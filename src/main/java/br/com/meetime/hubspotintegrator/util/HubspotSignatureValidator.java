package br.com.meetime.hubspotintegrator.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class HubspotSignatureValidator {

    public static boolean isValid(String signature, String requestBody, String clientSecret, String v1) {
        try {
            String combined = clientSecret + requestBody;

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(combined.getBytes(StandardCharsets.UTF_8));

            String expectedSignature = toHex(hash);

            return expectedSignature.equalsIgnoreCase(signature);
        } catch (Exception e) {
            return false;
        }
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
