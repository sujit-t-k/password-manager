package org.ajikhoji.passwordmanager.service;

import org.ajikhoji.passwordmanager.repository.DataErasable;
import org.ajikhoji.passwordmanager.repository.OpenLinkButtonActionCustomizable;
import org.ajikhoji.passwordmanager.repository.PasswordChangeable;
import org.ajikhoji.passwordmanager.repository.TableFieldsPreferenceRememberable;

public interface SettingService extends PasswordChangeable, TableFieldsPreferenceRememberable,
        OpenLinkButtonActionCustomizable, DataErasable {

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
