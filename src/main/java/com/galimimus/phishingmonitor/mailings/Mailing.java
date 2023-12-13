package com.galimimus.phishingmonitor.mailings;

import com.galimimus.phishingmonitor.StartApplication;
import com.galimimus.phishingmonitor.helpers.DB;
import com.galimimus.phishingmonitor.helpers.SettingsSingleton;
import com.galimimus.phishingmonitor.helpers.Validation;
import com.galimimus.phishingmonitor.models.Employee;

import java.util.ArrayList;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;


import static com.galimimus.phishingmonitor.logic.Statistic.countEmployeeRaiting;

public class Mailing{
    protected final String text;
    protected ArrayList<Employee> employees = new ArrayList<>();//constructor email + ip
    protected final String theme;
    protected final String host;
    protected final int port;
    final SettingsSingleton ss = SettingsSingleton.getInstance();
    protected final String URL_BASE = "http://"+ss.getHTTP_SERVER_HOST()+":"+ss.getHTTP_SERVER_PORT()+"/"+ss.getHTTP_SERVER_URL_HANDLE()+"?";
    protected final String URL_DOWNLOAD = "http://"+ss.getHTTP_SERVER_HOST()+":"+ss.getHTTP_SERVER_PORT()+"/"+ss.getHTTP_SERVER_EXE_HANDLE()+"?file=";
    protected final String URL_TOKEN_PART = "token=";
    protected final String URL_MAIL_PART = "&mail=";
    protected int mailing_id;
    protected final String from_email;
    protected final String from_pass;
    protected final Properties props;
    protected Session session;
    protected MimeMessage message;
    static final Logger log = Logger.getLogger(StartApplication.class.getName());
    public Mailing(String text, String recipients, String theme) {
        this.from_email = ss.getUSER_EMAIL();
        this.from_pass = ss.getUSER_APP_PASS();
        this.text = text.replace("\n", "<br/>");
        employees = getRecipientsInfo(recipients);
        this.theme = theme;
        host = ss.getMAIL_SMTP_SERVER();
        this.port = ss.getMAIL_SMTP_PORT();
        props = new Properties();
        props.put("mail.transport.protocol", "smtps");
        props.put("mail.smtps.host", host);//"smtp.gmail.com"
        props.put("mail.smtps.auth", "true");
        props.put("mail.smtp.sendpartial", "true");
    }
    public void Send(String to, String ip){

        if(Validation.validatePattern(to, Pattern.compile("^[_A-Za-z0-9]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"))){
            log.logp(Level.SEVERE, "Mailing", "Send", "Invalid Email = " +to);
            return;
        }
        if(Validation.validatePattern(ip, Pattern.compile("\\d+.\\d+.\\d+.\\d+"))){
            log.logp(Level.SEVERE, "Mailing", "Send", "Invalid ip = " + ip);
            return;
        }
        try {
            Transport transport = session.getTransport();
            transport.connect(host, port, from_email, from_pass);
            transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
            countEmployeeRaiting(ip);
        } catch (MessagingException e) {
            log.logp(Level.SEVERE, "Mailing", "Send", e.toString());
            throw new RuntimeException(e);
        }
    }

    private ArrayList<Employee> getRecipientsInfo(String recipients) {
        DB db = new DB();
        db.connect();
        ArrayList<Employee> res = db.getRecipients(recipients);
        db.close();
        return res;
    }

}