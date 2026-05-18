package org.ajikhoji.passwordmanager.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.Base64;

public class HashUtil {

    public static String hashPassword(final String password, final byte[] salt) {
        try {
            final PBEKeySpec spec = new PBEKeySpec(
                    password.toCharArray(),
                    salt,
                    65536,
                    256
            );

            final SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            final byte[] hash = factory.generateSecret(spec).getEncoded();

            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Hashing failed", e);
        }
    }

}
