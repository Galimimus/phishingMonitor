package com.galimimus.phishingmonitor.mailings;

import com.galimimus.phishingmonitor.models.Employee;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.galimimus.phishingmonitor.helpers.Validation.createToken;

public class URLMailing extends Mailing implements Runnable{
    public URLMailing(String text, String recipients, String theme, String from_email, String from_pass, String smtp_server, int port) {
        super(text, recipients, theme, from_email, from_pass, smtp_server, port);
    }

    @Override
    public void run() {
        System.out.println("mailing started");//TODO: Запись инфы в бд и файл лога
        for (Employee emp : employees){
            PrepareMail(emp);
            System.out.println(emp.getEmail());
            try {
                System.out.println("Thread sleeps");
                Thread.sleep(15);
                System.out.println("Thread awake");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


            Send(emp.getEmail());
            System.out.println("mailing continuing");
        }
        System.out.println("mailing done");
    }

    private void PrepareMail(Employee emp){
        session = Session.getDefaultInstance(props);
        message = new MimeMessage(session);
        Pattern pattern = Pattern.compile("<a href=\"");
        Matcher matcher = pattern.matcher(text);
        String tmp_text = text;
        while (matcher.find()) {
            StringBuffer sb = new StringBuffer(text);
            sb.insert(matcher.end(),url_base+createToken(emp.getIP()));
            tmp_text = String.valueOf(sb);
        }
        try {
            message.setSubject(theme);
            message.setContent(tmp_text, "text/html; charset=utf-8");
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(emp.getEmail()));

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Preparing done");

    }
}
