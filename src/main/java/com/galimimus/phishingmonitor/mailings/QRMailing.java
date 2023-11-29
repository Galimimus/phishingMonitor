package com.galimimus.phishingmonitor.mailings;

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
import com.google.zxing.qrcode.QRCodeWriter;import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;
import java.io.*;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.galimimus.phishingmonitor.helpers.Validation.createToken;

public class QRMailing extends Mailing implements Runnable{
    public QRMailing(String text, String recipients, String theme, String from_email, String from_pass, String smtp_server, int port) {
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

    public static void QR_gen(String content){
        try {
            String fileName = "qrcode/qrcode.png";
            int size = 250;

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, size, size);

            Path filePath = Paths.get(fileName);
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", filePath);
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
            QR_gen(url_base+createToken(emp.getIP()));
            FileDataSource fds = new FileDataSource("qrcode/qrcode.png");
            mbp.setDataHandler(new DataHandler(fds));
            mbp.setHeader("Content-ID","<qr>");
            mbp.setFileName("qrcode.png");

            multipart.addBodyPart(mbp);

            message.setContent(multipart, "text/html; charset=utf-8");
            message.setSubject(theme);
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(emp.getEmail()));

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Preparing done");
    }
}
