package org.ajikhoji.passwordmanager.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.ajikhoji.passwordmanager.config.AppConfig;
import org.ajikhoji.passwordmanager.util.Utility;
import java.util.function.Function;

public class AppLockScreen {

    public static void init(final String hint, final Function<String, Boolean> onLoginAttempt) {
        //setting up app title bar
        final HBox hbxTitleBar = AppConfig.getAppFrame().getTitleBar();
        hbxTitleBar.getChildren().clear();
        hbxTitleBar.setAlignment(Pos.CENTER_LEFT);
        final Label lblAppTitle = new Label(AppConfig.getAppName());
        lblAppTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 0 0 0 14px;");
        hbxTitleBar.getChildren().add(lblAppTitle);
        AppConfig.getPrimaryStage().setTitle(lblAppTitle.getText());

        final VBox vbxParent = new VBox(AppConfig.getScreenHeight() * 0.1D);
        vbxParent.setAlignment(Pos.CENTER);
        final Pane paneContent = AppConfig.getAppFrame().getPane();
        vbxParent.prefHeightProperty().bind(paneContent.heightProperty());
        vbxParent.prefWidthProperty().bind(paneContent.widthProperty());
        paneContent.getChildren().add(vbxParent);

        final Label lblHeader = new Label("Welcome to Ajikhoji's password manager");
        lblHeader.setStyle("-fx-font-size: 28px; -fx-font-wieght: bold;");
        vbxParent.getChildren().add(lblHeader);

        final VBox vbxCenter = new VBox(12.0D);
        final Utility.EntryField pass = Utility.addLabeledToggleablePasswordField("Enter password", 50, vbxCenter);
        final Label lblHint = new Label(String.format("Hint: %s", hint));
        lblHint.setStyle("-fx-font-size: 16px;");
        vbxCenter.getChildren().add(lblHint);
        vbxCenter.setAlignment(Pos.CENTER);
        final HBox hbxCenter = new HBox(vbxCenter);
        hbxCenter.setAlignment(Pos.CENTER);
        vbxParent.getChildren().add(hbxCenter);

        final Button btnLogin = new Button("Login");
        btnLogin.setStyle("-fx-font-size: 22px;");
        vbxParent.getChildren().add(btnLogin);

        btnLogin.setOnAction(e -> {
            final String password = pass.textProperty().get();

            if(password == null || password.isBlank()) {
                pass.errorMessageProperty().set("Enter valid password");
                return;
            }

            if(!onLoginAttempt.apply(password)) {
                pass.errorMessageProperty().set(Math.random() < 0.5D ? "Wrong password" : "Incorrect password entered");
            }
        });
    }

}
