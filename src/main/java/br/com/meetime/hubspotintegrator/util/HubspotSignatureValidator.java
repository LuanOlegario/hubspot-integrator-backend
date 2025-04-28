package br.com.meetime.hubspotintegrator.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class HubspotSignatureValidator {
    public static boolean isValid(String signature, String requestBody, String clientSecret) {
        try {
            String algorithm = "HmacSHA256";
            Mac hasher = Mac.getInstance(algorithm);
            hasher.init(new SecretKeySpec(clientSecret.getBytes(), algorithm));

            byte[] hash = hasher.doFinal(requestBody.getBytes());
            String expectedSignature = Base64.getEncoder().encodeToString(hash);

            return expectedSignature.equals(signature);
        } catch (Exception e) {
            return false;
        }
    }
}

