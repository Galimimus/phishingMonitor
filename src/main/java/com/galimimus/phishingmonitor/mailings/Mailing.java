package com.galimimus.phishingmonitor.mailings;

import com.galimimus.phishingmonitor.StartApplication;
import com.galimimus.phishingmonitor.helpers.DB;
import com.galimimus.phishingmonitor.helpers.Validation;
import com.galimimus.phishingmonitor.models.Employee;
import com.galimimus.phishingmonitor.server.HTTPServer;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.mail.Session;
public class Mailing{
    protected String text;
    protected ArrayList<Employee> employees = new ArrayList<>();//constructor email + ip
    protected String theme;
    protected String host;
    protected int port;
    protected String URL_BASE = "http://localhost:8000/loh?";
    protected String URL_DOWNLOAD = "http://localhost:8000/log?file=";
    protected String URL_TOKEN_PART = "token=";
    protected String URL_MAIL_PART = "&mail=";
    protected int mailing_id;
    protected Path exe_generated = Path.of("dropper/x\u202egpj.exe");
    protected String from_email;
    protected String from_pass;
    protected Properties props;
    protected Session session;
    protected MimeMessage message;
    static final Logger log = Logger.getLogger(StartApplication.class.getName());

    public Mailing(String text, String recipients, String theme, String from_email, String from_pass, String smtp_server, int port) {//TODO: Проверка????
        this.from_email = from_email;
        this.from_pass = from_pass;
        this.text = text.replace("\n", "<br/>");
        employees = getRecipientsInfo(recipients);
        this.theme = theme;
        host = smtp_server;
        this.port = port;
        props = new Properties();
        props.put("mail.transport.protocol", "smtps");
        props.put("mail.smtps.host", smtp_server);//"smtp.gmail.com"
        props.put("mail.smtps.auth", "true");
        props.put("mail.smtp.sendpartial", "true");
    }
    public void Send(String to){

        if(Validation.validatePattern(to, Pattern.compile("^[_A-Za-z0-9]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"))){
            log.logp(Level.SEVERE, "Mailing", "Send", "Invalid Email");
            return;
        }
        try {
            Transport transport = session.getTransport();
            transport.connect(host, port, from_email, from_pass);
            transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
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




//7901kxxo2003z&0
// arkadiy.vasilevich.94@mail.ru
// arkadijvasilevic42@gmail.com
// rghn uyaa ieym tnid пароль приложений!!!