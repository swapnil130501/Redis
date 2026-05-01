package com.pm.resp;

public class RespEncoder {
    public static byte[] simpleString(String s) {
        return ("+" + s + "\r\n").getBytes();
    }

    public static byte[] error(String msg) {
        return ("-ERR " + msg + "\r\n").getBytes();
    }

    public static byte[] bulkString(String s) {
        return ("$" + s.length() + "\r\n" + s + "\r\n").getBytes();
    }

    public static byte[] integer(long n) {
        return (":" + n + "\r\n").getBytes();
    }

    public static byte[] nullBulk() {
        return "$-1\r\n".getBytes();
    }
}
