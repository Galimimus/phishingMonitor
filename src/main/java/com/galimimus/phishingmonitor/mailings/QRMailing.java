package com.galimimus.phishingmonitor.mailings;

import com.galimimus.phishingmonitor.helpers.DB;
import com.galimimus.phishingmonitor.helpers.SettingsSingleton;
import com.galimimus.phishingmonitor.models.Employee;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.activation.*;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;


import static com.galimimus.phishingmonitor.helpers.Validation.createToken;

public class QRMailing extends Mailing implements Runnable{
    public QRMailing(String text, String recipients, String theme) {
        super(text, recipients, theme);
    }

    @Override
    public void run() {
        log.logp(Level.INFO, "QRMailing", "run", "Starting qr mailing");
        DB db = new DB();
        db.connect();
        mailing_id = db.getLastMailingId();
        mailing_id++;
        db.close();
        int total_sent = 0;
        for (Employee emp : employees){
            PrepareMail(emp);
            try {
                System.out.println("Thread sleeps");
                Thread.sleep(15);
                System.out.println("Thread awake");
            } catch (InterruptedException e) {
                log.logp(Level.SEVERE, "QRMailing", "run", e.toString());
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
            log.logp(Level.INFO, "QRMailing", "run", "Employees empty set");

        }
        log.logp(Level.INFO, "QRMailing", "run", "Qr mailing done. Total messages sent = " + total_sent);
    }

    public static void QR_gen(String content){
        try {
            String fileName = "qrcode.png";
            int size = 250;

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, size, size);
            SettingsSingleton ss = SettingsSingleton.getInstance();
            Path filePath = Paths.get(ss.getWORKING_DIRECTORY(),"qrcode",fileName);
            if(!Files.exists(filePath)){
                Files.createFile(filePath);
            }
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", filePath);
        } catch (WriterException | IOException e) {
            log.logp(Level.SEVERE, "QRMailing", "QR_gen", e.toString());
            throw new RuntimeException(e);
        }
    }

    private void PrepareMail(Employee emp) {
        session = Session.getDefaultInstance(props);
        message = new MimeMessage(session);

        try {

            MimeMultipart multipart = new MimeMultipart("related");

            BodyPart mbp = new MimeBodyPart();
            String tmp_text ="<div>"+ text + "</div>\n<div><img src=\"cid:qr\"></div>\n";
            mbp.setContent(tmp_text, "text/html; charset=utf-8");

            multipart.addBodyPart(mbp);

            mbp = new MimeBodyPart();
            QR_gen(URL_BASE+URL_TOKEN_PART+java.net.URLEncoder.encode(createToken(emp.getIp(), emp.getDepartment().getId()), StandardCharsets.UTF_16)+URL_MAIL_PART+mailing_id);
            FileDataSource fds = new FileDataSource(Paths.get(ss.getWORKING_DIRECTORY(),"qrcode","qrcode.png").toFile());
            mbp.setDataHandler(new DataHandler(fds));
            mbp.setHeader("Content-ID","<qr>");
            mbp.setFileName("qrcode.png");

            multipart.addBodyPart(mbp);

            message.setContent(multipart, "text/html; charset=utf-8");
            message.setSubject(theme);
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(emp.getEmail()));

        } catch (MessagingException e) {
            log.logp(Level.SEVERE, "QRMailing", "PrepareMail", e.toString());
            throw new RuntimeException(e);
        }
        log.logp(Level.INFO, "QRMailing", "PrepareMail", "Preparing mail for " + emp.getEmail() + " done.");
    }
}
