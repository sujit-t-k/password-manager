package org.ajikhoji.passwordmanager.security;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.SecretKey;
import java.util.Base64;
import java.security.SecureRandom;

public class AesEncryptionService implements EncryptionService {

    private final SecretKey key;

    public AesEncryptionService(SecretKey key) {
        this.key = key;
    }

    @Override
    public String encrypt(String plainText) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            byte[] iv = new byte[16];
            new SecureRandom().nextBytes(iv);

            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);

            final byte[] encrypted = cipher.doFinal(plainText.getBytes());
            final byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    @Override
    public String decrypt(String cipherText) {
        try {
            final byte[] decoded = Base64.getDecoder().decode(cipherText);

            final byte[] iv = new byte[16];
            final byte[] encrypted = new byte[decoded.length - 16];

            System.arraycopy(decoded, 0, iv, 0, 16);
            System.arraycopy(decoded, 16, encrypted, 0, encrypted.length);

            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

            final byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }

    @Override
    public boolean matchesEncrypted(String encrypted1, String encrypted2) {
        if(encrypted1 == null || encrypted2 == null) {
            return false;
        }
        return decrypt(encrypted1).equals(decrypt(encrypted2));
    }

}
