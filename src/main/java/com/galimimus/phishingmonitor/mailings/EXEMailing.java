package com.galimimus.phishingmonitor.mailings;

import com.galimimus.phishingmonitor.helpers.DB;
import com.galimimus.phishingmonitor.helpers.EXEGenerator;
import com.galimimus.phishingmonitor.helpers.SettingsSingleton;
import com.galimimus.phishingmonitor.models.Employee;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.galimimus.phishingmonitor.helpers.Validation.createToken;

public class EXEMailing extends Mailing implements Runnable{
    public EXEMailing(String text, String recipients, String theme) {
        super(text, recipients, theme);
    }

    @Override
    public void run(){
    log.logp(Level.INFO, "EXEMailing", "run", "Starting exe mailing");
    DB db = new DB();
    db.connect();

    mailing_id = db.getLastMailingId();
    mailing_id++;

    db.close();

    int total_sent = 0;

    EXEGenerator exe_gen = new EXEGenerator();

    for (Employee emp : employees){
    String filename = "Document_"+Calendar.getInstance().getTimeInMillis();//+"\u202excod.exe";//docx
    exe_gen.EXE_gen(filename, URL_BASE+URL_TOKEN_PART+createToken(emp.getIp(), emp.getDepartment().getId())+URL_MAIL_PART+mailing_id);
    PrepareMail(emp, filename);

    try {
        Thread.sleep(15);
    } catch (InterruptedException e) {
        log.logp(Level.SEVERE, "EXEMailing", "run", e.toString());
        throw new RuntimeException(e);
    }
    Send(emp.getEmail());
    total_sent++;
    }
    com.galimimus.phishingmonitor.models.Mailing mailing = new com.galimimus.phishingmonitor.models.Mailing(employees.get(0).getDepartment().getId(), total_sent);
        db.connect();
        db.logMailing(mailing);
        db.close();
        log.logp(Level.INFO, "EXEMailing", "run", "Exe mailing done. Total messages sent = " + total_sent);
}

    private void PrepareMail(Employee emp, String filename){
        session = Session.getDefaultInstance(props);
        message = new MimeMessage(session);
        Pattern pattern = Pattern.compile("<a href=");
        Matcher matcher = pattern.matcher(text);
        String tmp_text = text;
        while (matcher.find()) {
            StringBuilder sb = new StringBuilder(text);
            sb.insert(matcher.end(),URL_DOWNLOAD+filename );
            tmp_text = String.valueOf(sb);
        }
        try {
            message.setSubject(theme);
            message.setContent(tmp_text, "text/html; charset=utf-8");
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(emp.getEmail()));
        } catch (MessagingException e) {
            log.logp(Level.SEVERE, "EXEMailing", "PrepareMail", e.toString());
            throw new RuntimeException(e);
        }
        log.logp(Level.INFO, "EXEMailing", "PrepareMail", "Preparing mail for " + emp.getEmail() + " done.");
    }
}
