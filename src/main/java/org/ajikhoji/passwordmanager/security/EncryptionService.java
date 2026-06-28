package org.ajikhoji.passwordmanager.security;

public interface EncryptionService {
    String encrypt(String plainText);
    String decrypt(String cipherText);
    boolean matchesEncrypted(String encrypted1, String encrypted2);
}
