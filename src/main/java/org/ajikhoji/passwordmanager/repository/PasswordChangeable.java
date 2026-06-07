package org.ajikhoji.passwordmanager.repository;

import org.ajikhoji.passwordmanager.security.EncryptionService;

public interface PasswordChangeable {

    void changePassword(final EncryptionService oldService, final EncryptionService newService, final String hash, final String salt);

}
