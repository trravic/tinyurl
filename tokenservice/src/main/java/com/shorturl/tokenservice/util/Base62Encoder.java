package com.shorturl.tokenservice.util;

public class Base62Encoder {
    static private final int base = 62;
    static private final String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    static public String encode(long number) {
        StringBuilder stringBuilder = new StringBuilder(1);
        do {
            stringBuilder.insert(0, characters.charAt((int) (number % base)));
            number /= base;
        } while (number > 0);
        return stringBuilder.toString();
    }

    static public long decode(String number) throws IllegalArgumentException {
        long result = 0L;
        int length = number.length();
        for (int i = 0; i < length; i++) {
            char ch = number.charAt(length - i - 1);
            int index = characters.indexOf(ch);
            if (index == -1) {
                throw new IllegalArgumentException("Incorrect Request Url");
            }
            result += (long) Math.pow(base, i) * index;
        }
        return result;
    }
}
