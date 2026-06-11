package org.ajikhoji.passwordmanager.repository;

public interface SettingRepo extends PasswordChangeable, TableFieldsPreferenceRememberable,
        OpenLinkButtonActionCustomizable, DataErasable {

    void updateHash(String hashedString);
    String getHashedValue();
    void updateSalt(String saltedstring);
    String getSalt();
    void updateUserName(String name);
    String getUserName();
    void updateHint(String hint);
    String getHint();
    boolean isSetupDone();

}
