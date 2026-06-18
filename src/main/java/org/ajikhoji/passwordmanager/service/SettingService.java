package org.ajikhoji.passwordmanager.service;

import org.ajikhoji.passwordmanager.repository.*;

public interface SettingService extends PasswordChangeable, TableFieldsPreferenceRememberable,
        OpenLinkButtonActionCustomizable, DataErasable, DefaultScreenOnAppLaunchCustomizable {

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
