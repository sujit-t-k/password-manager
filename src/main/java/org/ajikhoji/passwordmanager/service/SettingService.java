package org.ajikhoji.passwordmanager.service;

import org.ajikhoji.passwordmanager.repository.PasswordChangeable;

public interface SettingService extends PasswordChangeable {

    void setHash(String hashedString);
    String getHash();
    void setSalt(String saltedstring);
    String getSalt();
    void setUserName(String name);
    String getUserName();
    void setHint(String hint);
    String getHint();
    boolean isSetupDone();

}
