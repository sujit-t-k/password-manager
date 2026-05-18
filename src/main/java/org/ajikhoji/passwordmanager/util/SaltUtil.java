package org.ajikhoji.passwordmanager.util;

import java.security.SecureRandom;
import java.util.Base64;

public class SaltUtil {

    public static byte[] generateSalt() {
        final byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    public static String getAsString(final byte[] salt) {
        return Base64.getEncoder().encodeToString(salt);
    }

    public static byte[] getSaltValue(final String encodedSaltedString) {
        return Base64.getDecoder().decode(encodedSaltedString);
    }

}
