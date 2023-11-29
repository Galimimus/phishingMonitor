package com.galimimus.phishingmonitor.mailings;

public class EXEMailing extends Mailing implements Runnable{
    public EXEMailing(String text, String recipients, String theme, String from_email, String from_pass, String smtp_server, int port) {
        super(text, recipients, theme, from_email, from_pass, smtp_server, port);
    }

    @Override
    public void run() {

    }
}
