package com.smarttender.common.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/** SHA-256 hashing utility for tender deduplication. */
public final class HashUtil {
    private HashUtil() {}

    public static String hashSha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    public static String buildTenderHashInput(String tenderId, String title,
                                               String org, String publishedDate) {
        return String.join("|", nullSafe(tenderId), nullSafe(title),
                nullSafe(org), nullSafe(publishedDate)).toLowerCase().trim();
    }

    private static String nullSafe(String s) { return s == null ? "" : s.trim(); }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
