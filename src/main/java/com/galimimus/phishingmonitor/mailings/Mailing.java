package com.galimimus.phishingmonitor.mailings;

import com.galimimus.phishingmonitor.helpers.DB;
import com.galimimus.phishingmonitor.models.Employee;
import com.sun.mail.smtp.SMTPTransport;

import java.nio.file.Path;
import java.security.Security;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

import javax.mail.Session;
public class Mailing{
    String text;
    ArrayList<Employee> employees = new ArrayList<>();//constructor email + ip
    String theme;
    String email;
    String url_base = "http://localhost:8080/phishing_test/index.php?ip=";
    Path exe_generated = Path.of("/home/galimimus/IdeaProjects/phishingMonitor/dropper/x\u202egpj.exe");

    public Mailing(String text, String recipients, String theme, String email) {
        this.email = email;
        this.text = text;
        employees = getRecipientsInfo(recipients);
        this.theme = theme;
    }

    public Mailing(){
//7901kxxo2003z&0
        //arkadiy.vasilevich.94@mail.ru
    }
    public static void Send() throws AddressException, MessagingException {
        //"xexe@vkpr.store", "pass111", "gilegi1653@mainoj.com", "", "hello", "HZHZHZHHZHZ"

        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtps");
        props.put("mail.smtps.host", "smtp.mail.ru");
        props.put("mail.smtps.auth", "true");
        props.put("mail.smtp.sendpartial", "true");

        Session session = Session.getDefaultInstance(props);
        MimeMessage message = new MimeMessage(session);

//устанавливаем тему письма
        message.setSubject("тестовое письмо!");

//добавляем текст письма
        message.setText("Asta la vista, baby!");

//указываем получателя
        message.addRecipient(Message.RecipientType.TO, new InternetAddress("galimimus37@gmail.com"));

//указываем дату отправления
        message.setSentDate(new Date());
        //логин и пароль gmail пользователя
        String userLogin = "arkadijvasilevic42@gmail.com";
        String userPassword = "rghn uyaa ieym tnid";//пароль приложений!!!

//авторизуемся на сервере:
        Transport transport = session.getTransport();
        transport.connect("smtp.gmail.com", 465, userLogin, userPassword);

//отправляем сообщение:
        transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
    }

    private ArrayList<Employee> getRecipientsInfo(String recipients) {
        DB db = new DB();
        db.connect();
        ArrayList<Employee> res = db.getRecipients(recipients);
        db.close();
        return res;
    }

    /*    Properties prop;
        Session session;
        int hehe(){
            Properties prop = new Properties();
            prop.put("mail.smtp.auth", true);
            prop.put("mail.smtp.starttls.enable", "true");
            prop.put("mail.smtp.host", "smtp.mailtrap.io");
            prop.put("mail.smtp.port", "25");
            prop.put("mail.smtp.ssl.trust", "smtp.mailtrap.io");
            Session session = Session.getInstance(prop, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
            return 0;
        }*/
    public void StartEXEMailing(){//TODO: ТРИ RUNABLE КЛАССА ДЛЯ РАЗНЫХ ТИПОВ РАССЫЛКИ

    }
    public void StartQRMailing(){

    }
    public void StartURLMailing(){

    }
}
