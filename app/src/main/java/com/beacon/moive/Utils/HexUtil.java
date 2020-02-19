package com.beacon.moive.Utils;


public class HexUtil {

    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static String byte2hex(byte[] src, int offset, int len) {
        String hex = "";
        for (int i = 0; i < len; i++) {
            hex += Integer.toHexString((src[offset + i] & 0x000000FF) | 0xFFFFFF00).substring(6)
                    .toUpperCase();
        }
        return hex;
    }

}
