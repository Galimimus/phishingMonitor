package com.galimimus.phishingmonitor.mailings;

public class EXEMailing extends Mailing implements Runnable{
    public EXEMailing(String text, String recipients, String theme, String email) {
        super(text, recipients, theme, email);
    }

    @Override
    public void run() {

    }
}
