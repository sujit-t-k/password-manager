package org.ajikhoji.passwordmanager.config;

import javafx.application.HostServices;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.ajikhoji.passwordmanager.repository.*;
import org.ajikhoji.passwordmanager.security.EncryptionService;
import org.ajikhoji.passwordmanager.service.*;
import org.ajikhoji.passwordmanager.ui_components.AppFrame;

public class AppConfig {

    private static final String appName = System.getenv("APP_NAME");
    private static final double screenWidth, screenHeight, usableScreenWidth, usableScreenHeight;
    private static Stage primaryStage;
    private static AppFrame appFrame;
    private static HostServices hostServices;
    private static final ObjectProperty<Pane> currDisplayPage = new SimpleObjectProperty<>(null);
    private static SideBarItem defaultSideMenuItem = SideBarItem.DASHBOARD;
    private static EncryptionService encryptionService;
    private static final AppResources ar;

    static {
        final Screen s = Screen.getPrimary();
        screenWidth = s.getBounds().getWidth();
        screenHeight = s.getBounds().getHeight();
        usableScreenWidth = s.getVisualBounds().getWidth();
        usableScreenHeight = s.getVisualBounds().getHeight();
        ar = AppResources.getInstance();
    }

    public static AppResources getAppResources() {
        return ar;
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

    public static EncryptionService getEncryptionService() {
        return encryptionService;
    }

    public static AppFrame getAppFrame() {
        return appFrame;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setPrimaryStage(final Stage st) {
        primaryStage = st;
    }

    public static void setAppFrame(AppFrame appFrame) {
        AppConfig.appFrame = appFrame;
    }

    public static void setCurrentDisplayPage(final Pane p) {
        currDisplayPage.set(p);
    }

    public static void setHostServices(final HostServices hs) {
        hostServices = hs;
    }

    public static void openDocument(final String url) {
        hostServices.showDocument(url);
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

    public static void setEncryptionService(final EncryptionService es) {
        encryptionService = es;
    }

    public static Scene getScene() {
        return appFrame.getScene();
    }

}
