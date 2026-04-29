package org.ajikhoji.passwordmanager.config;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import org.ajikhoji.passwordmanager.ui_components.AppFrame;

public class AppConfig {

    private static final String appName = System.getenv("APP_NAME");
    private static final double screenWidth, screenHeight, usableScreenWidth, usableScreenHeight;
    private static AppFrame appFrame;
    private static ObjectProperty<Pane> currDisplayPage = new SimpleObjectProperty<>(null);
    private static SideBarItem defaultSideMenuItem = SideBarItem.DASHBOARD;

    static {
        final Screen s = Screen.getPrimary();
        screenWidth = s.getBounds().getWidth();
        screenHeight = s.getBounds().getHeight();
        usableScreenWidth = s.getVisualBounds().getWidth();
        usableScreenHeight = s.getVisualBounds().getHeight();
    }

    public static String getAppName() {
        return appName;
    }

    public static double getScreenWidth() {
        return screenWidth;
    }

    public static double getScreenHeight() {
        return screenHeight;
    }

    public static double getVisualScreenWidth() {
        return usableScreenWidth;
    }

    public static double getVisualScreenHeight() {
        return usableScreenHeight;
    }

    public static AppFrame getAppFrame() {
        return appFrame;
    }

    public static void setAppFrame(AppFrame appFrame) {
        AppConfig.appFrame = appFrame;
    }

    public static void setCurrentDisplayPage(final Pane p) {
        currDisplayPage.set(p);
    }

    public static ObjectProperty<Pane> getCurrentDisplayPageProperty() {
        return currDisplayPage;
    }

    public static SideBarItem getDefaultSideMenuItem() {
        return defaultSideMenuItem;
    }

    public static void setDefaultSideMenuItem(SideBarItem defaultSideMenuItem) {
        AppConfig.defaultSideMenuItem = defaultSideMenuItem;
    }

    public static Scene getScene() {
        return appFrame.getScene();
    }

}
