package com.ping.reptile.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @author: W.Z
 * @date: 2022/8/23 17:00
 * @desc:
 */
public class ParamsUtils {

    private static String[] attr = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};


    public static String cipher() {
        LocalDate date = LocalDate.now();
        String format = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String[] split = format.split("-");
        String salt = random(24);

        String iv = split[0] + split[1] + split[2];
        long milli = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        String enc = TripleDES.encrypt(salt, milli + "", iv);
        String str = salt + iv + enc;
        return strTobinary(str);

    }

    public static String random(int size) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < size; i++) {
            long index = Math.round(Math.random() * (attr.length - 1));
            str.append(attr[(int) index]);
        }
        return str.toString();
    }

    private static String strTobinary(String str) {
        StringBuilder sb = new StringBuilder();
        byte[] bytes = str.getBytes();
        for (int i = 0; i < bytes.length; i++) {
            if (i != 0) {
                sb.append(" ");
            }
            sb.append(Integer.toBinaryString(bytes[i]));
        }
        return sb.toString();
    }

    public static String getCaseNo(String caseNo) {
        String[] split = new String[2];
        if (caseNo.contains("〔")) {
            split = caseNo.split("〔");
        }
        if (caseNo.contains("（")) {
            split = caseNo.split("（");
        }
        if (caseNo.contains("【")) {
            split = caseNo.split("【");
        }
        if (caseNo.contains("[")) {
            split = caseNo.split("\\[");
        }
        return split[0];
    }
}
