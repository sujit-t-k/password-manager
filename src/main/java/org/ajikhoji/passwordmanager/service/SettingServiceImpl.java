package org.ajikhoji.passwordmanager.service;

import org.ajikhoji.passwordmanager.repository.SettingRepo;
import org.ajikhoji.passwordmanager.security.EncryptionService;

public class SettingServiceImpl implements SettingService {
    private final SettingRepo repo;

    public SettingServiceImpl(final SettingRepo repo) {
        this.repo = repo;
    }

    @Override
    public void setHash(final String hashedString) {
        repo.updateHash(hashedString);
    }

    @Override
    public String getHash() {
        return repo.getHashedValue();
    }

    @Override
    public void setSalt(final String saltedstring) {
        repo.updateSalt(saltedstring);
    }

    @Override
    public String getSalt() {
        return repo.getSalt();
    }

    @Override
    public void setUserName(final String name) {
        repo.updateUserName(name);
    }

    @Override
    public String getUserName() {
        return repo.getUserName();
    }

    @Override
    public void setHint(final String hint) {
        repo.updateHint(hint);
    }

    @Override
    public String getHint() {
        return repo.getHint();
    }

    @Override
    public boolean isSetupDone() {
        return repo.isSetupDone();
    }

    @Override
    public void changePassword(final EncryptionService oldService, final EncryptionService newService, final String hash, final String salt) {
        repo.changePassword(oldService, newService, hash, salt);
    }

}
