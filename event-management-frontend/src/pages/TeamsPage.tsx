import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import teamService from '@/services/teamService';
import eventService from '@/services/eventService';
import { useAuth } from '@/context/AuthContext';
import { useToast } from '@/components/ui/Toast';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Dialog } from '@/components/ui/Dialog';
import { Table, TableHeader, TableRow, TableHead, TableBody, TableCell } from '@/components/ui/Table';
import { TableSkeleton } from '@/components/ui/LoadingSkeleton';
import { Link } from 'react-router-dom';
import { Users, Plus, ArrowUpRight, Copy, Search, Check, Calendar, Archive, LayoutList } from 'lucide-react';
import { motion } from 'framer-motion';
import type { Team } from '@/types';

export const TeamsPage: React.FC = () => {
  const { user } = useAuth();
  const { success, error: toastError } = useToast();
  const queryClient = useQueryClient();

  const [search, setSearch] = useState('');
  const [adminTab, setAdminTab] = useState<'active' | 'archived'>('active');
  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [teamName, setTeamName] = useState('');
  const [eventId, setEventId] = useState<number | ''>('');
  const [copiedCode, setCopiedCode] = useState<string | null>(null);

  // Single endpoint — backend returns role-filtered data via JWT
  const { data: teams, isLoading: teamsLoading } = useQuery({
    queryKey: ['teamsList'],
    queryFn: teamService.getAllTeams,
  });

  // Fetch events for name lookup and dropdown
  const { data: events } = useQuery({
    queryKey: ['eventsList'],
    queryFn: eventService.getAllEvents,
  });

  const getEventTitle = (eId?: number) =>
    events?.find((e) => e.id === eId)?.title ?? 'N/A';

  const getEventDate = (eId?: number) =>
    events?.find((e) => e.id === eId)?.eventDate ?? null;

  const isExpired = (team: Team): boolean => {
    const eventDate = getEventDate(team.eventId);
    if (!eventDate) return false;
    return new Date() > new Date(eventDate);
  };

  const createTeamMutation = useMutation({
    mutationFn: () => {
      if (!user) throw new Error('Not authenticated');
      if (!eventId) throw new Error('Please select an event');
      return teamService.createTeam({
        teamName: teamName.trim(),
        leaderName: user.name,
        eventId: Number(eventId),
      });
    },
    onSuccess: (data) => {
      success(`Team "${data.teamName}" created! Invite Code: ${data.teamCode}`);
      setIsCreateOpen(false);
      setTeamName('');
      setEventId('');
      queryClient.invalidateQueries({ queryKey: ['teamsList'] });
    },
    onError: (err: any) => {
      toastError(err.response?.data?.message || 'Failed to create team.');
    },
  });

  const handleCreateTeam = (e: React.FormEvent) => {
    e.preventDefault();
    if (!teamName.trim()) { toastError('Please specify the team name.'); return; }
    if (!eventId) { toastError('Please select an event.'); return; }
    createTeamMutation.mutate();
  };

  const handleCopyCode = (code: string) => {
    navigator.clipboard.writeText(code);
    setCopiedCode(code);
    success(`Code "${code}" copied!`);
    setTimeout(() => setCopiedCode(null), 2500);
  };

  const searchFilter = (t: Team) =>
    t.teamName.toLowerCase().includes(search.toLowerCase()) ||
    t.leaderName.toLowerCase().includes(search.toLowerCase()) ||
    t.teamCode.toLowerCase().includes(search.toLowerCase()) ||
    getEventTitle(t.eventId).toLowerCase().includes(search.toLowerCase());

  // Admin: split by expiry
  const activeAdminTeams = teams?.filter((t) => !isExpired(t) && searchFilter(t)) ?? [];
  const archivedAdminTeams = teams?.filter((t) => isExpired(t) && searchFilter(t)) ?? [];

  // Student: backend only returns their joined active teams
  const studentTeams = teams?.filter(searchFilter) ?? [];

  const isAdmin = user?.role === 'ADMIN';

  return (
    <div className="space-y-8">
      {/* Header */}
      <div className="flex flex-col sm:flex-row justify-between sm:items-center gap-4">
        <div>
          <h1 className="text-3xl font-extrabold text-foreground">
            {isAdmin ? 'All Teams' : 'My Teams'}
          </h1>
          <p className="text-sm font-medium text-muted-foreground mt-1">
            {isAdmin
              ? 'Complete visibility into every team registered in the platform'
              : 'Teams you have created or joined'}
          </p>
        </div>

        {user?.role === 'STUDENT' && (
          <div className="flex items-center gap-2">
            <Button size="sm" onClick={() => setIsCreateOpen(true)} className="flex items-center gap-2">
              <Plus className="h-4 w-4" />
              Form Team
            </Button>
            <Link to="/teams/join">
              <Button size="sm" variant="outline" className="flex items-center gap-2">
                <ArrowUpRight className="h-4 w-4" />
                Join Team
              </Button>
            </Link>
          </div>
        )}
      </div>

      {/* Admin Tabs */}
      {isAdmin && (
        <div className="flex items-center gap-1 border-b border-border">
          <button
            onClick={() => setAdminTab('active')}
            className={`flex items-center gap-2 px-4 py-2.5 text-sm font-semibold border-b-2 transition-colors ${
              adminTab === 'active'
                ? 'border-primary text-primary'
                : 'border-transparent text-muted-foreground hover:text-foreground'
            }`}
          >
            <LayoutList className="h-4 w-4" />
            Active Teams
            <span className="ml-1 px-1.5 py-0.5 text-xs rounded-full bg-primary/10 text-primary font-bold">
              {activeAdminTeams.length}
            </span>
          </button>
          <button
            onClick={() => setAdminTab('archived')}
            className={`flex items-center gap-2 px-4 py-2.5 text-sm font-semibold border-b-2 transition-colors ${
              adminTab === 'archived'
                ? 'border-amber-500 text-amber-600'
                : 'border-transparent text-muted-foreground hover:text-foreground'
            }`}
          >
            <Archive className="h-4 w-4" />
            Archived Teams
            <span className="ml-1 px-1.5 py-0.5 text-xs rounded-full bg-amber-500/10 text-amber-600 font-bold">
              {archivedAdminTeams.length}
            </span>
          </button>
        </div>
      )}

      {/* Search Bar */}
      <div className="relative max-w-md w-full">
        <Search className="absolute left-3 top-3 h-4 w-4 text-muted-foreground/60" />
        <Input
          placeholder={isAdmin ? 'Search by name, leader, event, or code...' : 'Search your teams...'}
          className="pl-10"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
      </div>

      {/* Tables */}
      {teamsLoading ? (
        <TableSkeleton rows={5} />
      ) : isAdmin ? (
        <AdminTeamsTable
          teams={adminTab === 'active' ? activeAdminTeams : archivedAdminTeams}
          getEventTitle={getEventTitle}
          isArchived={adminTab === 'archived'}
          copiedCode={copiedCode}
          onCopy={handleCopyCode}
        />
      ) : (
        <StudentTeamsTable
          teams={studentTeams}
          getEventTitle={getEventTitle}
          userEmail={user?.email ?? ''}
          copiedCode={copiedCode}
          onCopy={handleCopyCode}
        />
      )}

      {/* Create Team Dialog (Students only) */}
      <Dialog
        isOpen={isCreateOpen}
        onClose={() => { setIsCreateOpen(false); setTeamName(''); setEventId(''); }}
        title="Form Collaborative Team"
      >
        <form onSubmit={handleCreateTeam} className="space-y-4">
          <Input
            label="Team / Guild Name"
            placeholder="e.g. Byte Busters, Tech Titans"
            value={teamName}
            onChange={(e) => setTeamName(e.target.value)}
            disabled={createTeamMutation.isPending}
          />
          <div className="space-y-1">
            <label className="block text-sm font-semibold text-foreground">Select Event</label>
            <div className="relative">
              <Calendar className="absolute left-3 top-2.5 h-4 w-4 text-muted-foreground/60" />
              <select
                className="w-full h-10 pl-9 pr-3 rounded-md border border-input bg-background text-sm focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring disabled:opacity-50"
                value={eventId}
                onChange={(e) => setEventId(Number(e.target.value))}
                disabled={createTeamMutation.isPending || !events}
              >
                <option value="" disabled>Select an event for your team</option>
                {events?.map((evt) => (
                  <option key={evt.id} value={evt.id}>{evt.title}</option>
                ))}
              </select>
            </div>
          </div>
          <Input label="Leader / Founder" value={user?.name || ''} disabled />
          <div className="flex justify-end gap-3 pt-4 border-t border-border">
            <Button type="button" variant="outline" disabled={createTeamMutation.isPending} onClick={() => setIsCreateOpen(false)}>
              Cancel
            </Button>
            <Button type="submit" isLoading={createTeamMutation.isPending}>
              Generate Team
            </Button>
          </div>
        </form>
      </Dialog>
    </div>
  );
};

/* ─── Admin Table Component ─────────────────────────────────── */

interface AdminTableProps {
  teams: Team[];
  getEventTitle: (id?: number) => string;
  isArchived: boolean;
  copiedCode: string | null;
  onCopy: (code: string) => void;
}

const AdminTeamsTable: React.FC<AdminTableProps> = ({ teams, getEventTitle, isArchived, copiedCode, onCopy }) => {
  if (teams.length === 0) {
    return (
      <div className="text-center py-16 border border-dashed border-border rounded-2xl bg-card">
        {isArchived
          ? <><Archive className="h-10 w-10 text-muted-foreground/50 mx-auto mb-3" /><h3 className="text-base font-bold text-foreground">No archived teams</h3><p className="text-sm text-muted-foreground mt-1">Teams from past events will appear here.</p></>
          : <><Users className="h-10 w-10 text-muted-foreground/50 mx-auto mb-3" /><h3 className="text-base font-bold text-foreground">No active teams</h3><p className="text-sm text-muted-foreground mt-1">No teams have been formed yet.</p></>
        }
      </div>
    );
  }

  return (
    <motion.div initial={{ opacity: 0, y: 15 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.3 }}>
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead>Team Name</TableHead>
            <TableHead>Event Name</TableHead>
            <TableHead>Team Leader</TableHead>
            <TableHead>Team Members</TableHead>
            <TableHead>Team Size</TableHead>
            <TableHead>Registration Date</TableHead>
            <TableHead>Status</TableHead>
            <TableHead>Invite Code</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {teams.map((team) => (
            <TableRow key={team.id}>
              <TableCell className="font-semibold text-foreground">{team.teamName}</TableCell>
              <TableCell>{getEventTitle(team.eventId)}</TableCell>
              <TableCell>{team.leaderName}</TableCell>
              <TableCell>
                <div className="flex flex-col gap-0.5 max-w-[180px]">
                  {team.members && team.members.length > 0
                    ? team.members.map((m) => (
                        <span key={m} className="text-xs text-muted-foreground truncate">{m}</span>
                      ))
                    : <span className="text-xs text-muted-foreground">—</span>
                  }
                </div>
              </TableCell>
              <TableCell>
                <div className="flex items-center gap-1.5">
                  <Users className="h-4 w-4 text-muted-foreground" />
                  <span className="font-semibold">{team.members?.length ?? 1}</span>
                </div>
              </TableCell>
              <TableCell className="text-sm text-muted-foreground">
                {team.createdAt
                  ? new Date(team.createdAt).toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' })
                  : '—'}
              </TableCell>
              <TableCell>
                {isArchived ? (
                  <span className="inline-flex items-center gap-1 px-2.5 py-0.5 text-xs font-bold bg-amber-500/10 text-amber-600 rounded-full border border-amber-500/20">
                    <Archive className="h-3 w-3" /> Archived
                  </span>
                ) : (
                  <span className="inline-flex items-center px-2.5 py-0.5 text-xs font-bold bg-emerald-500/10 text-emerald-600 rounded-full border border-emerald-500/20">
                    Active
                  </span>
                )}
              </TableCell>
              <TableCell>
                <button
                  onClick={() => onCopy(team.teamCode)}
                  className="flex items-center gap-1.5 font-mono text-primary text-sm hover:opacity-75 transition-opacity"
                >
                  {team.teamCode}
                  {copiedCode === team.teamCode
                    ? <Check className="h-4 w-4 text-green-500" />
                    : <Copy className="h-4 w-4" />}
                </button>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </motion.div>
  );
};

/* ─── Student Table Component ───────────────────────────────── */

interface StudentTableProps {
  teams: Team[];
  getEventTitle: (id?: number) => string;
  userEmail: string;
  copiedCode: string | null;
  onCopy: (code: string) => void;
}

const StudentTeamsTable: React.FC<StudentTableProps> = ({ teams, getEventTitle, userEmail, copiedCode, onCopy }) => {
  if (teams.length === 0) {
    return (
      <div className="text-center py-16 border border-dashed border-border rounded-2xl bg-card">
        <Users className="h-10 w-10 text-muted-foreground/50 mx-auto mb-3" />
        <h3 className="text-base font-bold text-foreground">You haven't joined any teams yet</h3>
        <p className="text-sm font-medium text-muted-foreground mt-1">
          Form a new team or enter an invite code to get started.
        </p>
      </div>
    );
  }

  return (
    <motion.div initial={{ opacity: 0, y: 15 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.3 }}>
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead>Team Name</TableHead>
            <TableHead>Event</TableHead>
            <TableHead>Leader</TableHead>
            <TableHead>Team Size</TableHead>
            <TableHead>Your Role</TableHead>
            <TableHead>Invite Code</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {teams.map((team) => {
            const youLeadThisTeam = team.leaderName === userEmail;
            return (
              <TableRow key={team.id}>
                <TableCell className="font-semibold text-foreground">{team.teamName}</TableCell>
                <TableCell>{getEventTitle(team.eventId)}</TableCell>
                <TableCell>{team.leaderName}</TableCell>
                <TableCell>
                  <div className="flex items-center gap-1.5">
                    <Users className="h-4 w-4 text-muted-foreground" />
                    <span className="font-medium">{team.members?.length ?? 1}</span>
                  </div>
                </TableCell>
                <TableCell>
                  {youLeadThisTeam ? (
                    <span className="inline-flex px-2.5 py-0.5 text-xs font-bold bg-primary/10 text-primary rounded-full border border-primary/20">
                      Leader
                    </span>
                  ) : (
                    <span className="inline-flex px-2.5 py-0.5 text-xs font-bold bg-secondary text-muted-foreground rounded-full border border-border">
                      Member
                    </span>
                  )}
                </TableCell>
                <TableCell>
                  <button
                    onClick={() => onCopy(team.teamCode)}
                    className="flex items-center gap-1.5 font-mono text-primary text-sm hover:opacity-75 transition-opacity"
                  >
                    {team.teamCode}
                    {copiedCode === team.teamCode
                      ? <Check className="h-4 w-4 text-green-500" />
                      : <Copy className="h-4 w-4" />}
                  </button>
                </TableCell>
              </TableRow>
            );
          })}
        </TableBody>
      </Table>
    </motion.div>
  );
};

export default TeamsPage;


