package org.ajikhoji.passwordmanager.service;

import org.ajikhoji.passwordmanager.repository.PasswordChangeable;
import org.ajikhoji.passwordmanager.repository.TableFieldsReorderable;

public interface SettingService extends PasswordChangeable, TableFieldsReorderable {

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
