package com.galimimus.phishingmonitor.helpers;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.security.*;


public class Validation {
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    public static boolean validatePattern(String str, Pattern pattern) {
        return !pattern.matcher(str).matches();
    }

    public static String validateToken(String token) {
        System.out.println("validate token stated, token = " + token);
        DB db = new DB();
        db.connect();
        ArrayList<String> ips = db.getIPs();//TODO: ПЕРЕДЕЛАТЬ ЧТО БЫ ПРОВЕРКА БЫЛА ПО ГРУППАМ
        db.close();


        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        for (String ip : ips) {
            byte[] bytesHash = new byte[0];
            bytesHash = ip.getBytes(StandardCharsets.UTF_8);

            System.out.println("validate token stated, hashed ip = " + new String(md.digest(bytesHash)));


            if (new String(md.digest(bytesHash)).equals(token)) {
                return ip;
            }
        }
        return null;
    }

    public static String createToken(String ip) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] bytesOfMessage = ip.getBytes(StandardCharsets.UTF_8);
        return new String(md.digest(bytesOfMessage));
    }
}