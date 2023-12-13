package com.galimimus.phishingmonitor.mailings;

import com.galimimus.phishingmonitor.helpers.DB;
import com.galimimus.phishingmonitor.models.Employee;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.galimimus.phishingmonitor.helpers.Validation.createToken;

public class URLMailing extends Mailing implements Runnable{

    public URLMailing(String text, String recipients, String theme) {
        super(text, recipients, theme);
    }

    @Override
    public void run() {
        log.logp(Level.INFO, "URLMailing", "run", "Starting url mailing");
        DB db = new DB();
        db.connect();
        mailing_id = db.getLastMailingId();
        mailing_id++;
        db.close();
        int total_sent = 0;
        for (Employee emp : employees){
            PrepareMail(emp);
            try {
                Thread.sleep(15);
            } catch (InterruptedException e) {
                log.logp(Level.SEVERE, "URLMailing", "run", e.toString());
                throw new RuntimeException(e);
            }
            Send(emp.getEmail(), emp.getIp());
            total_sent++;
        }
        if (!employees.isEmpty()) {
            com.galimimus.phishingmonitor.models.Mailing mailing = new com.galimimus.phishingmonitor.models.Mailing(employees.get(0).getDepartment().getId(), total_sent);
            db.connect();
            db.logMailing(mailing);
            db.close();
        }else {
            log.logp(Level.INFO, "URLMailing", "run", "Employees empty set");

        }
        log.logp(Level.INFO, "URLMailing", "run", "Url mailing done. Total messages sent = " + total_sent);
    }

    private void PrepareMail(Employee emp){
        session = Session.getDefaultInstance(props);
        message = new MimeMessage(session);
        Pattern pattern = Pattern.compile("<a href=\"");
        Matcher matcher = pattern.matcher(text);
        String tmp_text = text;
        while (matcher.find()) {
            StringBuilder sb = new StringBuilder(text);
            sb.insert(matcher.end(),URL_BASE+URL_TOKEN_PART+java.net.URLEncoder.encode(createToken(emp.getIp(), emp.getDepartment().getId()), StandardCharsets.UTF_16)+URL_MAIL_PART+mailing_id);
            tmp_text = String.valueOf(sb);
        }
        try {
            message.setSubject(theme);
            message.setContent(tmp_text, "text/html; charset=utf-8");
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(emp.getEmail()));

        } catch (MessagingException e) {
            log.logp(Level.SEVERE, "URLMailing", "PrepareMail", e.toString());
            throw new RuntimeException(e);
        }
        log.logp(Level.INFO, "URLMailing", "PrepareMail", "Preparing mail for " + emp.getEmail() + " done.");
    }
}
