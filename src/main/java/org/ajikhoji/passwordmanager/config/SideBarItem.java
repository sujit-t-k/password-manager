package org.ajikhoji.passwordmanager.config;

import java.util.List;

public enum SideBarItem {

    DASHBOARD("Dashboard"),
    SETTING("Settings"),
    VIEW_ALL("View All Credentials"),
    ADD_NEW("Add New Credential"),
    ABOUT("About");

    public static List<SideBarItem> getAllSideBarItems() {
        return List.of(DASHBOARD, VIEW_ALL, ADD_NEW, SETTING, ABOUT);
    }

    private final String sideBarItemName;

    SideBarItem(String sideBarItem) {
        sideBarItemName = sideBarItem;
    }

    public String getSideBarItemName() {
        return sideBarItemName;
    }

}
