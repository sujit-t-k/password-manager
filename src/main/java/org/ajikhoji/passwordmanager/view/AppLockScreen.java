package org.ajikhoji.passwordmanager.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.ajikhoji.passwordmanager.config.AppConfig;
import org.ajikhoji.passwordmanager.config.DbConfig;
import org.ajikhoji.passwordmanager.util.Utility;
import org.ajikhoji.passwordmanager.view_secondary.AppResetConfirmation;

import java.util.function.Function;

public class AppLockScreen {

    public static void init(final String hint, final Function<String, Boolean> onLoginAttempt) {
        Utility.setupAppTitleBar();

        final VBox vbxParent = new VBox(AppConfig.getScreenHeight() * 0.1D);
        vbxParent.setAlignment(Pos.CENTER);
        final Pane paneContent = AppConfig.getAppFrame().getPane();
        paneContent.getChildren().clear();
        vbxParent.prefHeightProperty().bind(paneContent.heightProperty());
        vbxParent.spacingProperty().bind(paneContent.heightProperty().divide(10));
        vbxParent.prefWidthProperty().bind(paneContent.widthProperty());

        final ScrollPane sp = new ScrollPane(vbxParent);
        sp.getStyleClass().add("info-scroll");
        sp.setFitToWidth(true);
        paneContent.getChildren().add(sp);

        final Label lblGreeting = new Label(String.format("%s, %s", Math.random() > 0.66D ? "Welcome" : Math.random() < 0.33D ? "Hello" : "Greetings", DbConfig.getSettingService().getUserName()));
        lblGreeting.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");
        lblGreeting.setWrapText(true);
        vbxParent.getChildren().add(lblGreeting);

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
        btnLogin.setStyle("-fx-font-size: 20px;");

        final Button btnPasswordLost = new Button("Lost password?");
        btnPasswordLost.setStyle("-fx-font-size: 20px;");
        btnLogin.prefWidthProperty().bind(btnPasswordLost.widthProperty());

        final VBox vbxControls = new VBox(12.0D, btnLogin, btnPasswordLost);
        vbxControls.setAlignment(Pos.CENTER);
        vbxParent.getChildren().add(vbxControls);

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

        btnPasswordLost.setOnAction(e -> AppResetConfirmation.show(Utility::resetApplication));
    }

}
