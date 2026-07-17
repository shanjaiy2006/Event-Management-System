import React, { useState, useRef } from 'react';
import { useMutation } from '@tanstack/react-query';
import certificateService from '@/services/certificateService';
import { Card } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { useToast } from '@/components/ui/Toast';
import { Award, Sparkles, Download } from 'lucide-react';

export const CertificatePage: React.FC = () => {
 const { success, error: toastError } = useToast();
 const [studentName, setStudentName] = useState('');
 const [studentEmail, setStudentEmail] = useState('');
 const certificateRef = useRef<HTMLDivElement>(null);

 // Issue Certificate mutation
 const issueMutation = useMutation({
 mutationFn: () => certificateService.generateCertificate(studentName.trim(), studentEmail.trim()),
 onSuccess: (msg) => {
 success(msg || 'Certificate generated and email dispatched successfully!');
 },
 onError: (err: any) => {
 console.error(err);
 const errMsg = err.response?.data?.message || 'Failed to generate certificate. Verify student credentials.';
 toastError(errMsg);
 },
 });

 const handleIssue = (e: React.FormEvent) => {
 e.preventDefault();
 if (!studentName.trim() || !studentEmail.trim()) {
 toastError('Please supply both the name and email variables.');
 return;
 }
 issueMutation.mutate();
 };

 // Local image download using html-to-image or standard print emulator
 const handleLocalDownload = () => {
 if (!studentName.trim()) {
 toastError('Please fill in the Student Name before exporting.');
 return;
 }
 // Leverage window.print or a clean print simulation since canvas/screenshot libraries add size bloat
    const printContent = certificateRef.current?.innerHTML;
    if (printContent) {
      const printWindow = window.open('', '_blank');
      if (printWindow) {
        printWindow.document.write(`
        <html>
        <head>
          <title>Certificate - ${studentName}</title>
          <style>
            @import url('https://fonts.googleapis.com/css2?family=Cinzel:wght@700&family=Great+Vibes&family=Inter:wght@400;600&display=swap');
            body { margin: 0; padding: 40px; font-family: 'Inter', sans-serif; display: flex; justify-content: center; align-items: center; min-height: 100vh; background-color: #f1f5f9; }
            .cert-box { border: 20px solid #1e3a8a; padding: 60px; text-align: center; background: white; max-width: 900px; width: 100%; box-shadow: 0 10px 25px -5px rgba(0,0,0,0.1); border-radius: 4px; position: relative; }
            .inner-border { border: 2px solid #e2e8f0; padding: 40px; }
            .logos { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; }
            .logo-placeholder { width: 120px; height: 60px; background-color: #f8fafc; border: 1px dashed #cbd5e1; display: flex; align-items: center; justify-content: center; color: #64748b; font-size: 12px; font-weight: bold; }
            h1 { font-family: 'Cinzel', serif; color: #1e3a8a; margin-bottom: 10px; font-size: 42px; text-transform: uppercase; letter-spacing: 2px; }
            h3 { color: #64748b; font-size: 16px; text-transform: uppercase; letter-spacing: 4px; margin-top: 0; margin-bottom: 40px; }
            .presented { color: #475569; font-size: 18px; margin-bottom: 20px; }
            .name { font-family: 'Great Vibes', cursive; font-size: 64px; color: #0f172a; margin: 20px 0; border-bottom: 2px solid #e2e8f0; padding-bottom: 10px; display: inline-block; min-width: 400px; }
            .desc { font-size: 18px; line-height: 1.8; color: #475569; max-width: 700px; margin: 30px auto; }
            .details { font-weight: 600; color: #0f172a; }
            .footer { display: flex; justify-content: space-between; align-items: flex-end; margin-top: 80px; }
            .signature { border-top: 1px solid #cbd5e1; padding-top: 10px; width: 200px; color: #64748b; font-size: 14px; font-weight: 600; text-transform: uppercase; letter-spacing: 1px; }
            .seal { width: 100px; height: 100px; border-radius: 50%; background: #1e3a8a; color: white; display: flex; align-items: center; justify-content: center; font-size: 12px; font-weight: bold; font-family: 'Cinzel', serif; text-align: center; border: 4px double white; box-shadow: 0 0 0 4px #1e3a8a; margin: 0 auto; }
            .cert-id { position: absolute; bottom: 20px; left: 30px; font-size: 12px; color: #94a3b8; }
            .qr-code { width: 80px; height: 80px; border: 2px solid #e2e8f0; position: absolute; bottom: 20px; right: 30px; display: flex; align-items: center; justify-content: center; font-size: 10px; color: #94a3b8; }
          </style>
        </head>
        <body>
          <div class="cert-box">
            <div class="inner-border">
              <div class="logos">
                <div class="logo-placeholder">UNIVERSITY LOGO</div>
                <div class="logo-placeholder">EVENTIQ LOGO</div>
              </div>
              
              <h1>Certificate of Achievement</h1>
              <h3>Awarded for Excellence</h3>
              
              <div class="presented">This certificate is proudly presented to</div>
              
              <div class="name">${studentName || 'Student Name'}</div>
              
              <div class="desc">
                For successfully participating and demonstrating excellence in the event.<br/>
                Completed on <span class="details">${new Date().toLocaleDateString()}</span>.
              </div>
              
              <div class="footer">
                <div class="signature">Digital Signature</div>
                <div class="seal">OFFICIAL<br/>SEAL</div>
                <div class="signature">Event Coordinator</div>
              </div>
            </div>
            
            <div class="cert-id">Certificate ID: CERT-${Math.random().toString(36).substr(2, 9).toUpperCase()}</div>
            <div class="qr-code">QR CODE</div>
          </div>
          <script>
            window.onload = function() { window.print(); window.close(); }
          </script>
        </body>
        </html>
        `);
        printWindow.document.close();
      }
    }
  };

  return (
    <div className="space-y-8">
      {/* Header */}
      <div>
        <h1 className="text-3xl font-extrabold text-foreground">Smart Certificate Workspace</h1>
        <p className="text-sm font-medium text-muted-foreground mt-1">
          Issue verified event completions to students. Generates dynamic PDF templates and emails notifications.
        </p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-5 gap-8">
        {/* Left: Input parameters */}
        <div className="lg:col-span-2 space-y-6">
          <Card className="p-6">
            <h2 className="text-lg font-bold text-foreground mb-4 flex items-center gap-2">
              <Award className="h-5 w-5 text-primary" />
              Recipient Details
            </h2>

            <form onSubmit={handleIssue} className="space-y-4">
              <Input
                label="Student Name"
                placeholder="e.g. John Doe"
                value={studentName}
                onChange={(e) => setStudentName(e.target.value)}
                disabled={issueMutation.isPending}
              />

              <Input
                label="Student Email Address"
                placeholder="e.g. john@gmail.com"
                type="email"
                value={studentEmail}
                onChange={(e) => setStudentEmail(e.target.value)}
                disabled={issueMutation.isPending}
              />

              <div className="flex gap-2 pt-2">
                <Button
                  type="submit"
                  className="flex-1 flex items-center justify-center gap-2"
                  isLoading={issueMutation.isPending}
                >
                  <Sparkles className="h-4.5 w-4.5" />
                  Issue Certificate
                </Button>
                
                <Button
                  type="button"
                  variant="outline"
                  size="icon"
                  className="h-10 w-10 flex-shrink-0"
                  onClick={handleLocalDownload}
                >
                  <Download className="h-5 w-5" />
                </Button>
              </div>
            </form>
          </Card>

          <div className="flex items-start gap-3 p-4 rounded-xl border border-blue-500/20 bg-blue-500/10 text-blue-600 text-xs font-semibold leading-relaxed">
            <Sparkles className="h-5 w-5 flex-shrink-0 mt-0.5" />
            <span>
              The certificate will be emailed directly to the student's registered email address.
            </span>
          </div>
        </div>

        {/* Right: Live Preview Panel */}
        <div className="lg:col-span-3">
          <Card className="p-0 overflow-hidden relative border border-border h-full flex flex-col justify-between">
            <div className="px-6 py-4 border-b border-border bg-secondary/20">
              <h2 className="text-sm font-bold text-foreground">Interactive Certificate Preview</h2>
            </div>

            <div className="flex-1 p-8 flex items-center justify-center bg-slate-100 min-h-[400px]">
              <div 
                ref={certificateRef}
                className="w-full max-w-lg aspect-[1.414] bg-white border-[16px] border-blue-900 p-8 flex flex-col justify-between items-center text-center text-slate-800 shadow-xl relative rounded-sm"
              >
                {/* Visual border overlay */}
                <div className="absolute inset-2 border-2 border-slate-200 pointer-events-none" />

                <div className="flex justify-between w-full px-4 mb-4">
                  <div className="w-16 h-8 bg-slate-100 border border-slate-200 text-[6px] font-bold text-slate-400 flex items-center justify-center">UNI LOGO</div>
                  <div className="w-16 h-8 bg-slate-100 border border-slate-200 text-[6px] font-bold text-slate-400 flex items-center justify-center">EVENTIQ LOGO</div>
                </div>

                <div className="space-y-1">
                  <h1 className="text-2xl font-serif text-blue-900 tracking-wider uppercase">Certificate of Achievement</h1>
                  <h3 className="text-[10px] font-bold text-slate-500 tracking-widest uppercase">Awarded for Excellence</h3>
                </div>

                <div className="space-y-2 mt-4">
                  <p className="text-[10px] font-medium text-slate-600">This certificate is proudly presented to</p>
                  <div className="text-3xl font-serif text-slate-900 border-b-2 border-slate-200 px-6 py-2 inline-block min-w-[250px] italic">
                    {studentName || 'Student Name'}
                  </div>
                </div>

                <p className="text-[10px] font-medium text-slate-600 max-w-sm leading-relaxed px-4 mt-4">
                  For successfully participating and demonstrating excellence in the event.<br/>
                  Completed on <span className="font-bold text-slate-900">{new Date().toLocaleDateString()}</span>.
                </p>

                <div className="flex justify-between items-end w-full px-4 mt-8 text-[8px] text-slate-500 font-bold uppercase tracking-wider">
                  <div className="text-center border-t border-slate-300 pt-1 w-24">Digital Signature</div>
                  
                  <div className="w-16 h-16 rounded-full bg-blue-900 text-white flex items-center justify-center text-[7px] text-center border-[3px] border-double border-white outline-double outline-2 outline-blue-900 shrink-0 mx-2 shadow-sm font-serif">
                    OFFICIAL<br/>SEAL
                  </div>

                  <div className="text-center border-t border-slate-300 pt-1 w-24">Event Coordinator</div>
                </div>

                {/* Footer details */}
                <div className="absolute bottom-4 left-4 text-[7px] text-slate-400 font-mono">
                  ID: CERT-XXXXXXXXX
                </div>
                <div className="absolute bottom-4 right-4 w-10 h-10 border border-slate-200 flex items-center justify-center text-[5px] text-slate-400">
                  QR CODE
                </div>
              </div>
            </div>
          </Card>
        </div>
      </div>
    </div>
  );
};

export default CertificatePage;
