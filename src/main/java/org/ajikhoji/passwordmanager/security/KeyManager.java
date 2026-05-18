package org.ajikhoji.passwordmanager.security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class KeyManager {

    public static SecretKeySpec generateKey(String masterPassword) {
        try {
            byte[] salt = "some-fixed-salt".getBytes(); // improve later

            final PBEKeySpec spec = new PBEKeySpec(
                    masterPassword.toCharArray(),
                    salt,
                    65536,
                    256
            );

            final SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            final byte[] keyBytes = factory.generateSecret(spec).getEncoded();

            return new SecretKeySpec(keyBytes, "AES");
        } catch (Exception e) {
            throw new RuntimeException("Key generation failed", e);
        }
    }
}
