package com.pblues.sportsshop.common.util;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderUtils {
    private static final String PREFIX = "ORD-";
    private static final AtomicInteger counter = new AtomicInteger(0);
    private static final Random random = new Random();

    public static String generateId() {
        String date = new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
        int seq = counter.incrementAndGet();
        String seqFormatted = String.format("%06d", seq);
        return PREFIX + date + "-" + seqFormatted + "-" + randomChars(3);
    }

    private static String randomChars(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
