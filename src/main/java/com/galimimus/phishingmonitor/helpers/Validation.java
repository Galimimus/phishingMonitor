package com.galimimus.phishingmonitor.helpers;

import java.util.regex.Pattern;

public class Validation {
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    public static boolean validatePattern(String str, Pattern pattern) {
        return !pattern.matcher(str).matches();
    }
}
