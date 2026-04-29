package org.ajikhoji.passwordmanager.view;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import org.ajikhoji.passwordmanager.config.AppConfig;

public class StartupView {

    private BorderPane bpBase;

    private StartupView() {
        bpBase = new BorderPane();
        Label lblTitle = new Label(AppConfig.getAppName());
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        bpBase.setCenter(lblTitle);
    }

    public Pane getBasePane() {
        return bpBase;
    }

    public static StartupView getNewInstance() {
        return new StartupView();
    }

}
