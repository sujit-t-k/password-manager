package org.ajikhoji.passwordmanager.repository;

public interface DefaultScreenOnAppLaunchCustomizable {

    void saveDefaultScreenOnAppLaunch(final String screenName);
    String getDefaultScreenOnAppLaunch();

}
