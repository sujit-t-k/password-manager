package org.ajikhoji.passwordmanager.view;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.ajikhoji.passwordmanager.config.AppConfig;
import org.ajikhoji.passwordmanager.exception.ValidationException;
import org.ajikhoji.passwordmanager.util.Utility;
import org.ajikhoji.passwordmanager.validator.AccountInfoValidator;

import java.util.function.Consumer;

//view that appears for the very first time. This is to set up master password for this application
public class AppSetupScreen {

    private final VBox vbxParent;
    private String userName = "";
    private String password = "";
    private String hint = "";
    private final Consumer<String[]> postSuccessfulValidation;

    private AppSetupScreen(final Consumer<String[]> onSuccessfulValidation) {
        postSuccessfulValidation = onSuccessfulValidation;
        vbxParent = new VBox();
        vbxParent.setAlignment(Pos.CENTER);

        final Pane paneContent = AppConfig.getAppFrame().getPane();
        vbxParent.prefHeightProperty().bind(paneContent.heightProperty());
        vbxParent.prefWidthProperty().bind(paneContent.widthProperty());
        paneContent.getChildren().add(vbxParent);

        getUserName();
    }

    public static void init(final Consumer<String[]> onSuccessfulValidation) {
        Utility.setupAppTitleBar();
        new AppSetupScreen(onSuccessfulValidation);
    }

    private void getUserName() {
        vbxParent.getChildren().clear();
        vbxParent.setSpacing(AppConfig.getScreenHeight() * 0.1D);

        final Label lblHeader = new Label("Welcome to Ajikhoji's password manager");
        lblHeader.setStyle("-fx-font-size: 28px; -fx-font-wieght: bold;");
        vbxParent.getChildren().add(lblHeader);

        final HBox hbxCenter = new HBox();
        hbxCenter.setAlignment(Pos.CENTER);
        final Utility.EntryField ef = Utility.addLabeledTextField("How would you like yourself to be called?", "Enter your name here", 40, hbxCenter);
        ef.textProperty().set(userName);
        vbxParent.getChildren().add(hbxCenter);

        final Button btnNext = new Button("Next");
        btnNext.setStyle("-fx-font-size: 22px;");
        vbxParent.getChildren().add(btnNext);

        btnNext.setOnAction(e -> {
            final String name = ef.textProperty().get();
            if(name.isBlank()) {
                ef.errorMessageProperty().set("Name shall not be empty");
                return;
            }
            if(name.length() < 3) {
                ef.errorMessageProperty().set("Name should be at least 3 characters long");
                return;
            }
            userName = name;
            getMasterPassword();
        });
    }

    private void getMasterPassword() {
        vbxParent.getChildren().clear();
        vbxParent.setSpacing(AppConfig.getScreenHeight() * 0.06D);

        final Label lblHeader = new Label(String.format("Hello, %s!", userName));
        lblHeader.setStyle("-fx-font-size: 36px; -fx-font-wieght: bold;");
        final Label lblPrompt = new Label("Secure the app from the beginning!! Assign password to this app along with a hint.");
        final VBox vbxTop = new VBox(4.0D, lblHeader, lblPrompt);
        vbxTop.setAlignment(Pos.CENTER);
        final HBox hbxTop = new HBox(8.0D, vbxTop);
        hbxTop.setAlignment(Pos.CENTER);
        vbxParent.getChildren().add(hbxTop);

        final VBox vbxFields = new VBox(16.0D);
        final HBox hbxCenter = new HBox(vbxFields);
        hbxCenter.setAlignment(Pos.CENTER);
        final Utility.EntryField passwordField = Utility.addLabeledToggleablePasswordField("Create a password", 50, vbxFields);
        passwordField.textProperty().set(password);
        final Utility.EntryField hintField = Utility.addLabeledTextField("Enter hint questionnaire", 50, vbxFields);
        hintField.textProperty().set(hint);
        vbxParent.getChildren().add(hbxCenter);

        final Button btnNext = new Button("Next");
        btnNext.setStyle("-fx-font-size: 20px;");
        final Button btnBack = new Button("Previous");
        btnBack.setStyle("-fx-font-size: 20px;");

        final HBox hbxActions = new HBox(32.0D, btnBack, btnNext);
        hbxActions.setAlignment(Pos.CENTER);
        vbxParent.getChildren().add(hbxActions);

        btnBack.setOnAction(e -> {
            getUserName();
        });
        btnNext.setOnAction(e -> {
            final String pass = passwordField.textProperty().get();
            try {
                AccountInfoValidator.validateUnencryptedMasterPassword(pass);
            } catch (final ValidationException ve) {
                passwordField.errorMessageProperty().set(ve.getMessage());
                return;
            }
            final String hintString = hintField.textProperty().get();
            if(hintString.isBlank()) {
                hintField.errorMessageProperty().set("Hint shall not be empty");
                return;
            }
            if(hintString.length() < 2) {
                hintField.errorMessageProperty().set("Hint should be at least 2 characters long");
                return;
            }
            password = pass;
            hint = hintString;
            verifyPassword(false);
        });
    }

    private void verifyPassword(final boolean populateData) {
        vbxParent.getChildren().clear();
        vbxParent.setSpacing(AppConfig.getScreenHeight() * 0.1D);

        final Label lblHeader = new Label(String.format("You are only one step away, %s!", userName));
        lblHeader.setStyle("-fx-font-size: 24px; -fx-font-wieght: bold;");
        final Label lblPrompt = new Label("Enter your password to complete the setup");
        final VBox vbxTop = new VBox(4.0D, lblHeader, lblPrompt);
        vbxTop.setAlignment(Pos.CENTER);
        final HBox hbxTop = new HBox(8.0D, vbxTop);
        hbxTop.setAlignment(Pos.CENTER);
        vbxParent.getChildren().add(hbxTop);

        final VBox vbxFields = new VBox(16.0D);
        final HBox hbxCenter = new HBox(vbxFields);
        hbxCenter.setAlignment(Pos.CENTER);
        final Utility.EntryField passwordField = Utility.addLabeledToggleablePasswordField("Enter password to confirm", 50, true, vbxFields);
        final Label lblHint = new Label(String.format("Hint: %s", hint));
        lblHint.setStyle("-fx-font-size: 16px;");
        vbxFields.getChildren().add(lblHint);
        vbxParent.getChildren().add(hbxCenter);

        final Button btnNext = new Button("Done");
        btnNext.setStyle("-fx-font-size: 20px;");
        final Button btnBack = new Button("Previous");
        btnBack.setStyle("-fx-font-size: 20px;");

        final HBox hbxActions = new HBox(32.0D, btnBack, btnNext);
        hbxActions.setAlignment(Pos.CENTER);
        vbxParent.getChildren().add(hbxActions);

        btnBack.setOnAction(e -> {
            getMasterPassword();
        });
        btnNext.setOnAction(e -> {
            final String pass = passwordField.textProperty().get();
            if(!pass.equals(password)) {
                passwordField.errorMessageProperty().set("Password does not match");
                return;
            }
            showWaitingScreen();
        });
        if(populateData) {
            passwordField.textProperty().set(password);
        }
    }

    private void showWaitingScreen() {
        Platform.runLater(() -> {
            vbxParent.getChildren().clear();
            vbxParent.setSpacing(AppConfig.getScreenHeight() * 0.1D);

            final Label lblHeader = new Label("Almost there");
            lblHeader.setStyle("-fx-font-size: 32px; -fx-font-wieght: bold;");
            final Label lblPrompt = new Label("Setting things up");
            final VBox vbxTop = new VBox(4.0D, lblHeader, lblPrompt);
            vbxTop.setAlignment(Pos.CENTER);
            final HBox hbxTop = new HBox(8.0D, vbxTop);
            hbxTop.setAlignment(Pos.CENTER);
            vbxParent.getChildren().add(hbxTop);
        });

        new Thread(() -> {
            Platform.runLater(() -> {
                try {
                    postSuccessfulValidation.accept(new String[]{userName, hint, password});
                } catch (Exception e) {
                    Utility.showErrorAlert("Registration failed", "Something went wrong. Try again later.");
                    verifyPassword(true);
                }
            });
        }).start();
    }

}
