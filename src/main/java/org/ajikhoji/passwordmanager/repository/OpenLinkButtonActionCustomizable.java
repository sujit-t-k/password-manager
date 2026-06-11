package org.ajikhoji.passwordmanager.repository;

public interface OpenLinkButtonActionCustomizable {

    enum LinkActionOption {
        OPEN_IN_BROWSER,
        OPEN_IN_BROWSER_AND_COPY_ACC_ID,
        OPEN_IN_BROWSER_AND_COPY_ACC_PASS;
    }

    void saveOpenLinkAction(final LinkActionOption option);
    LinkActionOption getOpenLinkActionPreference();

}
