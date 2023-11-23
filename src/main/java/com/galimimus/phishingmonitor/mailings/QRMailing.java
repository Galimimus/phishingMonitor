package com.galimimus.phishingmonitor.mailings;

public class QRMailing extends Mailing implements Runnable{
    public QRMailing(String text, String recipients, String theme, String email) {
        super(text, recipients, theme, email);
    }

    @Override
    public void run() {

    }
}
