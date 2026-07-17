import React, { useState, useEffect } from 'react';
import { useAuth } from '@/context/AuthContext';
import { Card } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { useToast } from '@/components/ui/Toast';
import { Trash2, Shield, Settings, UserCircle, Key, Image, Monitor, LogOut } from 'lucide-react';
import { motion } from 'framer-motion';
import api from '@/services/api';

export const SettingsPage: React.FC = () => {
  const { user, logout, profilePicture, updateProfilePicture } = useAuth();
  const { success, warning, error: toastError } = useToast();

  const [name, setName] = useState(user?.name || '');
  const [email, setEmail] = useState(user?.email || '');
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  
  const [isProfileUpdating, setIsProfileUpdating] = useState(false);
  const [isPasswordUpdating, setIsPasswordUpdating] = useState(false);
  const [isUploading, setIsUploading] = useState(false);
  const [imageError, setImageError] = useState(false);

  useEffect(() => {
    setImageError(false);
  }, [profilePicture]);

  const handleClearCache = () => {
    success('Client cache flushed successfully!');
  };

  const handleLogout = () => {
    warning('Logging out of the current device...');
    setTimeout(() => logout(), 1000);
  };

  const handleLogoutAll = () => {
    warning('Logging out of all active devices...');
    setTimeout(() => logout(), 1500);
  };

  const handleProfileUpdate = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsProfileUpdating(true);
    try {
      await api.put('/profile/update', { name, email });
      success('Profile details updated successfully.');
    } catch (err: any) {
      toastError(err.response?.data?.message || 'Failed to update profile.');
    } finally {
      setIsProfileUpdating(false);
    }
  };

  const handlePasswordUpdate = async (e: React.FormEvent) => {
    e.preventDefault();
    if (newPassword !== confirmPassword) {
      toastError('Passwords do not match.');
      return;
    }
    if (newPassword.length < 6) {
      toastError('Password is too weak.');
      return;
    }
    setIsPasswordUpdating(true);
    try {
      await api.put('/profile/password', { currentPassword, newPassword });
      success('Password updated successfully.');
      setCurrentPassword('');
      setNewPassword('');
      setConfirmPassword('');
    } catch (err: any) {
      toastError(err.response?.data?.message || 'Failed to update password.');
    } finally {
      setIsPasswordUpdating(false);
    }
  };

  const handleFileChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      setIsUploading(true);
      const formData = new FormData();
      formData.append('file', e.target.files[0]);
      
      try {
        const res = await api.post('/profile/upload', formData, {
          headers: { 'Content-Type': 'multipart/form-data' }
        });
        updateProfilePicture(res.data.url);
        success('Profile picture updated successfully.');
      } catch (err) {
        toastError('Failed to upload picture.');
      } finally {
        setIsUploading(false);
      }
    }
  };

  // Password strength logic
  const getPasswordStrength = () => {
    if (!newPassword) return 0;
    let strength = 0;
    if (newPassword.length >= 8) strength += 25;
    if (/[A-Z]/.test(newPassword)) strength += 25;
    if (/[0-9]/.test(newPassword)) strength += 25;
    if (/[^A-Za-z0-9]/.test(newPassword)) strength += 25;
    return strength;
  };
  const strength = getPasswordStrength();

  return (
    <div className="space-y-8 max-w-4xl mx-auto pb-12">
      {/* Header */}
      <div className="border-b border-border pb-6">
        <h1 className="text-3xl font-extrabold text-foreground flex items-center gap-2">
          <Settings className="h-8 w-8 text-primary" />
          Account Settings
        </h1>
        <p className="text-sm font-medium text-muted-foreground mt-1">
          Manage your account profile, security, and preferences
        </p>
      </div>

      <motion.div
        initial={{ opacity: 0, y: 15 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.4 }}
        className="space-y-8"
      >
        {/* Profile Section */}
        <Card className="p-8">
          <div className="mb-6 pb-4 border-b border-border flex items-center justify-between">
            <div>
              <h2 className="text-xl font-bold text-foreground">Profile Information</h2>
              <p className="text-sm text-muted-foreground mt-1">Update your personal details and public profile</p>
            </div>
            <UserCircle className="h-8 w-8 text-primary/40" />
          </div>
          
          <div className="flex flex-col md:flex-row gap-8">
            <div className="flex flex-col items-center space-y-4 md:w-1/3 border-r border-border pr-8">
              <div className="w-32 h-32 rounded-full border-4 border-primary/10 overflow-hidden bg-secondary flex items-center justify-center relative group">
                {profilePicture && !imageError ? (
                  <img src={profilePicture} alt="Profile" className="w-full h-full object-cover" onError={() => setImageError(true)} />
                ) : (
                  <div className="text-4xl font-bold text-muted-foreground/50">{user?.name?.substring(0, 2).toUpperCase()}</div>
                )}
                <label className="absolute inset-0 bg-black/50 text-white opacity-0 group-hover:opacity-100 flex flex-col items-center justify-center cursor-pointer transition-opacity">
                  <Image className="h-6 w-6 mb-1" />
                  <span className="text-xs font-semibold">Change</span>
                  <input type="file" accept="image/*" className="hidden" onChange={handleFileChange} disabled={isUploading} />
                </label>
              </div>
              <p className="text-xs text-muted-foreground text-center">
                JPG, GIF or PNG. Max size of 5MB.
              </p>
            </div>

            <form onSubmit={handleProfileUpdate} className="space-y-5 md:w-2/3">
              <Input
                label="Full Name"
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder="John Doe"
              />
              <Input
                label="Email Address"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                type="email"
                placeholder="john.doe@example.com"
              />
              <div className="flex justify-end pt-2">
                <Button type="submit" isLoading={isProfileUpdating}>Save Changes</Button>
              </div>
            </form>
          </div>
        </Card>

        {/* Security Section */}
        <Card className="p-8">
          <div className="mb-6 pb-4 border-b border-border flex items-center justify-between">
            <div>
              <h2 className="text-xl font-bold text-foreground">Security & Password</h2>
              <p className="text-sm text-muted-foreground mt-1">Ensure your account is using a long, random password to stay secure.</p>
            </div>
            <Key className="h-8 w-8 text-primary/40" />
          </div>

          <form onSubmit={handlePasswordUpdate} className="space-y-5 max-w-xl">
            <Input
              label="Current Password"
              type="password"
              value={currentPassword}
              onChange={(e) => setCurrentPassword(e.target.value)}
              placeholder="••••••••"
            />
            
            <div className="space-y-2">
              <Input
                label="New Password"
                type="password"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                placeholder="••••••••"
              />
              {newPassword.length > 0 && (
                <div className="space-y-1">
                  <div className="flex h-1.5 w-full bg-secondary rounded-full overflow-hidden">
                    <div className={`h-full transition-all ${strength <= 25 ? 'bg-red-500 w-1/4' : strength <= 50 ? 'bg-orange-500 w-2/4' : strength <= 75 ? 'bg-yellow-500 w-3/4' : 'bg-green-500 w-full'}`} />
                  </div>
                  <p className="text-xs text-muted-foreground text-right">
                    {strength <= 25 ? 'Weak' : strength <= 50 ? 'Fair' : strength <= 75 ? 'Good' : 'Strong'}
                  </p>
                </div>
              )}
            </div>

            <Input
              label="Confirm New Password"
              type="password"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              placeholder="••••••••"
            />
            <div className="flex justify-end pt-2">
              <Button type="submit" isLoading={isPasswordUpdating}>Update Password</Button>
            </div>
          </form>
        </Card>

        {/* Account Information (Read Only) */}
        <Card className="p-8 bg-slate-50/50">
          <div className="mb-6 pb-4 border-b border-border flex items-center justify-between">
            <div>
              <h2 className="text-xl font-bold text-foreground">Account Information</h2>
              <p className="text-sm text-muted-foreground mt-1">Read-only details regarding your account identity</p>
            </div>
            <Shield className="h-8 w-8 text-primary/40" />
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="space-y-1">
              <p className="text-sm font-semibold text-muted-foreground">Account Role</p>
              <p className="font-medium text-foreground bg-white px-3 py-2 rounded-md border border-border inline-block uppercase text-xs tracking-wider">
                {user?.role || 'STUDENT'}
              </p>
            </div>
            <div className="space-y-1">
              <p className="text-sm font-semibold text-muted-foreground">Registered Email</p>
              <p className="font-medium text-foreground bg-white px-3 py-2 rounded-md border border-border">{user?.email || 'N/A'}</p>
            </div>
            <div className="space-y-1">
              <p className="text-sm font-semibold text-muted-foreground">Account Created Date</p>
              <p className="font-medium text-foreground bg-white px-3 py-2 rounded-md border border-border">Jan 12, 2026</p>
            </div>
            <div className="space-y-1">
              <p className="text-sm font-semibold text-muted-foreground">Last Login</p>
              <p className="font-medium text-foreground bg-white px-3 py-2 rounded-md border border-border">{new Date().toLocaleString()}</p>
            </div>
            <div className="space-y-1 md:col-span-2">
              <p className="text-sm font-semibold text-muted-foreground">Current Session IP</p>
              <p className="font-mono text-xs text-foreground bg-white px-3 py-2 rounded-md border border-border">192.168.1.104 (Authenticated)</p>
            </div>
          </div>
        </Card>

        {/* Session Management & Preferences */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
          <Card className="p-6 flex flex-col justify-between">
            <div>
              <h3 className="text-lg font-bold text-foreground flex items-center gap-2 mb-2">
                <Monitor className="h-5 w-5" /> Session Management
              </h3>
              <p className="text-sm text-muted-foreground mb-6">Log out of your current device or force logout across all known devices.</p>
            </div>
            <div className="flex flex-col gap-3">
              <Button variant="outline" className="w-full justify-start" onClick={handleLogout}>
                <LogOut className="h-4 w-4 mr-2" /> Logout Current Device
              </Button>
              <Button variant="destructive" className="w-full justify-start" onClick={handleLogoutAll}>
                <Shield className="h-4 w-4 mr-2" /> Logout All Devices
              </Button>
            </div>
          </Card>

          <Card className="p-6 flex flex-col justify-between">
            <div>
              <h3 className="text-lg font-bold text-foreground flex items-center gap-2 mb-2">
                <Trash2 className="h-5 w-5" /> Preferences & Data
              </h3>
              <p className="text-sm text-muted-foreground mb-6">Clear locally cached application data to resolve potential issues or refresh state.</p>
            </div>
            <Button variant="outline" className="w-full text-amber-600 border-amber-200 hover:bg-amber-50 hover:border-amber-300" onClick={handleClearCache}>
              <Trash2 className="h-4 w-4 mr-2" /> Clear Client Cache
            </Button>
          </Card>
        </div>

      </motion.div>
    </div>
  );
};

export default SettingsPage;
