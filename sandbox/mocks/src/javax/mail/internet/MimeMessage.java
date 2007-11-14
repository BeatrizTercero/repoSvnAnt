package javax.mail.internet;

import javax.mail.Session;

public class MimeMessage {
    public MimeMessage(Session s) {}
    public void addHeader(String s1, String s2) {}
    public void setContent(MimeMultipart attachments) {}
    public void setSubject(String subject, String charset) {}
    
    public void setFrom(InternetAddress internetAddress) {
    }
    public void setReplyTo(InternetAddress[] internetAddresses) {
    }
    public void setRecipients(int i, InternetAddress[] ia) {
    }
}
