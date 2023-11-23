package com.galimimus.phishingmonitor.mailings;

public class URLMailing extends Mailing implements Runnable{
    public URLMailing(String text, String recipients, String theme, String email) {
        super(text, recipients, theme, email);
    }

    @Override
    public void run() {

    }
}
