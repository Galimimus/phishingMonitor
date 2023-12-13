package com.galimimus.phishingmonitor.helpers;

import com.galimimus.phishingmonitor.StartApplication;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.security.*;


public class Validation {
    static final Logger log = Logger.getLogger(StartApplication.class.getName());

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean validatePattern(String str, Pattern pattern) {
        return !pattern.matcher(str).matches();
    }
    public static boolean validateSymbols(String str) {
        return str.contains("*") || str.contains("/") || str.contains(",") || str.contains("'")
                || str.contains("\"") || str.contains("`") || str.contains("&") || str.contains("--")
                || str.contains("|") || str.contains(":");
    }


    public static String validateToken(String token) {
        if(validatePattern(token, Pattern.compile(".{16}_\\d+$"))){
            log.logp(Level.WARNING, "Validation", "validateToken", "Incorrect token value, token = " + token);
            return null;
        }
        String hashed_ip = token.substring(0,16);
        String dep_id = token.substring(17);
        log.logp(Level.INFO, "Validation", "validateToken", "Start process token value, hashed_ip = " + hashed_ip+ " dep = "+dep_id);

        DB db = new DB();
        db.connect();
        ArrayList<String> ips = db.getIPs(Integer.parseInt(dep_id));
        db.close();


        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            log.logp(Level.SEVERE, "Validation", "validateToken", e.toString());
            throw new RuntimeException(e);
        }
        for (String ip : ips) {

            byte[] bytesHash;
            bytesHash = ip.getBytes(StandardCharsets.UTF_16);
            System.out.println(Arrays.toString(md.digest(bytesHash))+" "+new String(md.digest(bytesHash)) + " "+hashed_ip + token);


            if (new String(md.digest(bytesHash)).equals(hashed_ip)) {
                log.logp(Level.INFO, "Validation", "validateToken", "Token " + hashed_ip + " validated. ip = " + ip);
                return ip;
            }
        }
        log.logp(Level.WARNING, "Validation", "validateToken", "Token " + hashed_ip + " is not validated.");
        return null;
    }

    public static String createToken(String ip, int dep) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            log.logp(Level.SEVERE, "Validation", "createToken", e.toString());
            throw new RuntimeException(e);
        }
        byte[] bytesOfMessage = ip.getBytes(StandardCharsets.UTF_16);
        System.out.println(ip+" "+dep);
        System.out.println(new String(md.digest(bytesOfMessage))+"_"+dep);
        return new String(md.digest(bytesOfMessage))+"_"+dep;
    }
}