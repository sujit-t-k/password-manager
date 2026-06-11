package org.ajikhoji.passwordmanager.view;

import javafx.event.EventType;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.ajikhoji.passwordmanager.Launcher;
import org.ajikhoji.passwordmanager.config.AppConfig;
import org.ajikhoji.passwordmanager.config.AppResources;
import org.ajikhoji.passwordmanager.config.DbConfig;
import org.ajikhoji.passwordmanager.exception.DatabaseOperationFailureException;
import org.ajikhoji.passwordmanager.exception.ValidationException;
import org.ajikhoji.passwordmanager.repository.TableFieldsReorderable;
import org.ajikhoji.passwordmanager.security.AesEncryptionService;
import org.ajikhoji.passwordmanager.security.EncryptionService;
import org.ajikhoji.passwordmanager.security.KeyManager;
import org.ajikhoji.passwordmanager.service.SettingService;
import org.ajikhoji.passwordmanager.util.HashUtil;
import org.ajikhoji.passwordmanager.util.SaltUtil;
import org.ajikhoji.passwordmanager.util.Utility;
import org.ajikhoji.passwordmanager.validator.AccountInfoValidator;

import javax.crypto.SecretKey;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class SettingsScreen extends Pane {

    public SettingsScreen() {
        final ScrollPane sp = new ScrollPane();
        sp.getStyleClass().add("info-scroll");
        sp.setFitToWidth(true);

        final VBox vbxContent = new VBox();
        vbxContent.setStyle("-fx-padding: 20px;");

        //general section where 'view all credential' page's view can be configured as per the user's preference
        final Label lblTitleGeneral = new Label("General");
        lblTitleGeneral.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        lblTitleGeneral.setAlignment(Pos.CENTER);
        lblTitleGeneral.setTextAlignment(TextAlignment.CENTER);

        final Label lblViewFields = new Label("Fields to appear in 'View all credentials' table");
        lblViewFields.setWrapText(true);
        lblViewFields.setStyle("-fx-font-weight: bold; -fx-padding: 6px 0 8px 0;");
        final FlowPane fpViewFields = new FlowPane(Orientation.HORIZONTAL, 12.0D, 6.0D);
        final String[] fieldNames = new String[]{"Account ID/Name", "Password", "Platform", "Label", "Link",
                "Added date", "Last used date", "Recent updated date", "Usage count", "Actions"};
        final CheckBox[] cbx = new CheckBox[fieldNames.length];

        final Label lblFieldOrder = new Label("Order of fields in 'View all credentials' table");
        lblFieldOrder.setWrapText(true);
        lblFieldOrder.setStyle("-fx-font-weight: bold; -fx-padding: 6px 0 8px 0;");
        final RadioButton rbCustom = new RadioButton("Custom field order (double click on a field header and then drag and drop to re-arrange)");
        rbCustom.setWrapText(true);
        final RadioButton rbDefault = new RadioButton("App default field order (field re-arrangement will be disabled)");
        rbDefault.setWrapText(true);
        rbDefault.addEventFilter(MouseEvent.ANY, e -> {
            if(rbDefault.isSelected()) {
                e.consume();
            }
        });
        rbCustom.addEventFilter(MouseEvent.ANY, e -> {
            if(rbCustom.isSelected()) {
                e.consume();
            }
        });

        final VBox vbxGeneral = new VBox(12.0D, lblTitleGeneral, lblViewFields, fpViewFields, lblFieldOrder, new VBox(6.0D, rbDefault, rbCustom));
        
        //personal section where app password, hint and username can be changed
        final GridPane gpSecurity = new GridPane(8.0D, 16.0D);
        gpSecurity.setStyle("-fx-padding: 20px 0 0 0;");
        final ColumnConstraints ccField = new ColumnConstraints();
        ccField.setHalignment(HPos.RIGHT);
        gpSecurity.getColumnConstraints().add(ccField);
        final ColumnConstraints ccValue = new ColumnConstraints();
        ccValue.setHalignment(HPos.LEFT);
        gpSecurity.getColumnConstraints().add(ccValue);
        final ColumnConstraints ccEdit = new ColumnConstraints();
        ccEdit.setHalignment(HPos.LEFT);

        final AppResources ar = AppConfig.getAppResources();
        final Supplier<Button> EditButton = () -> {
            final ImageView imgViewCopy = new ImageView(ar.imgEdit);
            imgViewCopy.setFitHeight(20.0D);
            imgViewCopy.setFitWidth(20.0D);
            final Button btnEdit = new Button("", imgViewCopy);
            btnEdit.getStyleClass().add("btn-table-edit");
            return btnEdit;
        };
        final Function<String, Label> SettingInfoLabel = text -> {
            final Label lbl = new Label(text);
            lbl.setStyle("-fx-font-size: 16px");
            return lbl;
        };

        final Label lblTitleSecurity = new Label("Personal & Security");
        lblTitleSecurity.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        lblTitleSecurity.setAlignment(Pos.CENTER);
        lblTitleSecurity.setTextAlignment(TextAlignment.CENTER);
        gpSecurity.add(new HBox(lblTitleSecurity), 0, 0, 3, 1);

        final Label lblUsernameField = SettingInfoLabel.apply("Username");
        final Label lblUsernameValue = SettingInfoLabel.apply("");
        final Button btnUsernameEdit = EditButton.get();
        gpSecurity.addRow(gpSecurity.getRowCount(), lblUsernameField, lblUsernameValue, btnUsernameEdit);

        final Label lblHintField = SettingInfoLabel.apply("Hint");
        final Label lblHintValue = SettingInfoLabel.apply("");
        final Button btnHintEdit = EditButton.get();
        gpSecurity.addRow(gpSecurity.getRowCount(), lblHintField, lblHintValue, btnHintEdit);

        final Label lblPasswordField = SettingInfoLabel.apply("Password");
        final Label lblPasswordValue = SettingInfoLabel.apply("Sensitive, not shown");
        final Button btnPasswordEdit = EditButton.get();
        gpSecurity.addRow(gpSecurity.getRowCount(), lblPasswordField, lblPasswordValue, btnPasswordEdit);

        vbxContent.getChildren().addAll(vbxGeneral, gpSecurity);
        sp.setContent(vbxContent);
        getChildren().add(sp);
        sp.prefWidthProperty().bind(widthProperty());
        sp.prefHeightProperty().bind(heightProperty());

        final SettingService settingService = DbConfig.getSettingService();
        lblHintValue.setText(settingService.getHint());
        lblUsernameValue.setText(settingService.getUserName());

        btnHintEdit.setOnAction(e -> editHint(lblHintValue));
        btnUsernameEdit.setOnAction(e -> editUsername(lblUsernameValue));
        btnPasswordEdit.setOnAction(e -> editPassword());

        final long initialOrder = Math.abs(settingService.getTableFieldsOrder());
        System.out.println(settingService.getTableFieldsOrder());
        for(int i = 0; i < fieldNames.length; ++i) {
            final int fieldMappingNumber = (i + 1) % 10;
            cbx[i] = new CheckBox(fieldNames[i]);
            cbx[i].setSelected(Utility.isFieldPresent(fieldMappingNumber, initialOrder));
            if(i < 2 || i == 9) {//because Account ID, password and action fields cannot be omitted.
                cbx[i].setSelected(true);
                cbx[i].setDisable(true);
            } else {
                cbx[i].selectedProperty().addListener((ol, ov, nv) -> {
                    final long oldFieldOrder = settingService.getTableFieldsOrder();
                    final boolean isDefaultOptionSelected = oldFieldOrder < 0;
                    final long fieldOrder = Math.abs(oldFieldOrder);
                    if(nv) {
                        final long newOrderWithThisField = (isDefaultOptionSelected ? -1 : 1) * Utility.addField(fieldMappingNumber, fieldOrder);
                        settingService.saveTableFieldsOrderPreference(newOrderWithThisField);
                    } else {
                        final long newOrderWithoutThisField = (isDefaultOptionSelected ? -1 : 1) * Utility.removeField(fieldMappingNumber, fieldOrder);
                        settingService.saveTableFieldsOrderPreference(newOrderWithoutThisField);
                    }
                });
            }
            fpViewFields.getChildren().add(cbx[i]);
        }

        final Supplier<Long> FieldOrderCalculator = () -> {
            int count = 0;
            long order = 0;
            for(int i = 0; i < fieldNames.length; ++i) {
                final int fieldOrderMapping = (i + 1) % 10;
                if(cbx[i].isSelected()) {
                    ++count;
                    order = (order * 10) + fieldOrderMapping;
                }
            }
            return TableFieldsReorderable.getUpdatedFieldsCount(order, count);
        };

        rbDefault.setOnAction(e -> {
            rbDefault.setSelected(true);
            rbCustom.setSelected(false);
            settingService.saveTableFieldsOrderPreference(-FieldOrderCalculator.get());
        });
        rbCustom.setOnAction(e -> {
            rbCustom.setSelected(true);
            rbDefault.setSelected(false);
            settingService.saveTableFieldsOrderPreference(FieldOrderCalculator.get());
        });
        if(settingService.getTableFieldsOrder() < 0) {
            rbDefault.setSelected(true);
        } else {
            rbCustom.setSelected(true);
        }
    }

    private boolean isPasswordCorrect(final String plainPassword) {
        final SettingService settingService = DbConfig.getSettingService();
        final String saltString = settingService.getSalt();
        final byte[] salt = SaltUtil.getSaltValue(saltString);
        final String passwordHash = HashUtil.hashPassword(plainPassword, salt);
        final String storedPasswordHash = settingService.getHash();
        return passwordHash.equals(storedPasswordHash);
    }

    private void editHint(final Label lblHint) {
        final Stage st = new Stage();
        final VBox vbxFields = new VBox();
        vbxFields.setStyle("-fx-padding: 6px 6px 6px 6px;");
        final Utility.EntryField hintField = Utility.addLabeledTextField("Enter hint questionnaire", "New hint goes here", 50, vbxFields);
        final Utility.EntryField password = Utility.addLabeledTextField("Enter password", 50, vbxFields);
        final Button btnClose = new Button("Close");
        final Button btnApply = new Button("Apply");
        final HBox hbxControls = new HBox(12.0D, btnClose, btnApply);
        hbxControls.setAlignment(Pos.CENTER);
        vbxFields.getChildren().add(hbxControls);

        btnApply.setOnAction(e -> {
            final String hint = hintField.textProperty().get();
            if(hint == null || hint.isBlank()) {
                hintField.errorMessageProperty().set("Hint cannot be blank");
                return;
            }
            if(hint.equals(lblHint.getText())) {
                hintField.errorMessageProperty().set("New hint cannot be same as current hint");
                return;
            }
            if(isPasswordCorrect(password.textProperty().get())) {
                try {
                    DbConfig.getSettingService().setHint(hint);
                    lblHint.setText(hint);
                    st.close();
                    Utility.showInformationAlert("Operation Success", "Hint modified");
                } catch (final Exception ex) {
                    Utility.showErrorAlert("Operation Failed", "Internal Error occurred");
                }
            } else {
                Utility.showErrorAlert("Operation Failed", "Incorrect password entered");
                password.errorMessageProperty().set("Incorrect password entered");
            }
        });
        btnClose.setOnAction(e -> st.close());
        showSettingEditor(st, vbxFields, "Change passwor hint");
    }

    private void editUsername(final Label lblUsername) {
        final Stage st = new Stage();
        final VBox vbxFields = new VBox();
        vbxFields.setStyle("-fx-padding: 6px 6px 6px 6px;");
        final Utility.EntryField userNameField = Utility.addLabeledTextField("Enter user name to be displayed", 50, vbxFields);
        final Utility.EntryField password = Utility.addLabeledTextField("Enter password", 50, vbxFields);
        final Button btnClose = new Button("Close");
        final Button btnApply = new Button("Apply");
        final HBox hbxControls = new HBox(12.0D, btnClose, btnApply);
        hbxControls.setAlignment(Pos.CENTER);
        vbxFields.getChildren().add(hbxControls);

        btnApply.setOnAction(e -> {
            final String userName = userNameField.textProperty().get();
            if(userName == null || userName.isBlank()) {
                userNameField.errorMessageProperty().set("User name cannot be blank");
                return;
            }
            if(userName.equals(lblUsername.getText())) {
                userNameField.errorMessageProperty().set("New user name should not be same as current user name");
                return;
            }
            if(isPasswordCorrect(password.textProperty().get())) {
                try {
                    DbConfig.getSettingService().setUserName(userName);
                    lblUsername.setText(userName);
                    st.close();
                    Utility.showInformationAlert("Operation Success", "User name modified");
                } catch (final Exception ex) {
                    Utility.showErrorAlert("Operation Failed", "Internal Error occurred");
                }
            } else {
                password.errorMessageProperty().set("Incorrect password entered");
                Utility.showErrorAlert("Operation Failed", "Incorrect password entered");
            }
        });
        btnClose.setOnAction(e -> st.close());
        showSettingEditor(st, vbxFields, "Change user name");
    }

    private void editPassword() {
        final Stage st = new Stage();
        final VBox vbxFields = new VBox();
        vbxFields.setStyle("-fx-padding: 6px 6px 6px 6px;");
        final Utility.EntryField password = Utility.addLabeledTextField("Enter current password", 50, vbxFields);
        final Utility.EntryField newPassword = Utility.addLabeledTextField("Enter new password", 50, vbxFields);
        final Utility.EntryField newPasswordConfirm = Utility.addLabeledTextField("Confirm new password", 50, vbxFields);
        final Button btnClose = new Button("Close");
        final Button btnApply = new Button("Apply");
        final HBox hbxControls = new HBox(12.0D, btnClose, btnApply);
        hbxControls.setAlignment(Pos.CENTER);
        vbxFields.getChildren().add(hbxControls);

        btnApply.setOnAction(e -> {
            final String oldPass = password.textProperty().get();
            final String pass = newPassword.textProperty().get();
            final String confirmPass = newPasswordConfirm.textProperty().get();
            if(confirmPass == null || pass == null || !pass.equals(confirmPass)) {
                newPasswordConfirm.errorMessageProperty().set("New password does not match");
                return;
            }
            try {
                AccountInfoValidator.validateUnencryptedMasterPassword(pass);
            } catch (final ValidationException ve) {
                newPassword.errorMessageProperty().set(ve.getMessage());
                return;
            }
            if(pass.equals(oldPass)) {
                newPassword.errorMessageProperty().set("New password cannot be same as current password");
                return;
            }
            if(isPasswordCorrect(oldPass)) {
                try {
                    //generate new salt and hash master password with the new salt
                    final byte[] newSalt = SaltUtil.generateSalt();
                    final String passwordHash = HashUtil.hashPassword(pass, newSalt);
                    final String saltedString = SaltUtil.getAsString(newSalt);

                    final EncryptionService oldService = AppConfig.getEncryptionService();
                    final SecretKey key = KeyManager.generateKey(pass, newSalt);
                    final EncryptionService newService = new AesEncryptionService(key);

                    //changePassword() migrates all old-key encrypted data to new-key encrypted data, then stores new salt and hash in DB.
                    DbConfig.getSettingService().changePassword(oldService, newService, passwordHash, saltedString);
                    //to use this new master password-based encryption service further in this session.
                    AppConfig.setEncryptionService(newService);
                    st.close();
                    Utility.showInformationAlert("Operation Success", "Password changed");
                } catch (final DatabaseOperationFailureException dex) {
                    Utility.showErrorAlert("Operation Failed", dex.getMessage());
                } catch (final Exception ex) {
                    Utility.showErrorAlert("Operation Failed", "Internal Error occurred");
                }
            } else {
                password.errorMessageProperty().set("Incorrect password entered");
                Utility.showErrorAlert("Operation Failed", "Incorrect password entered");
            }
        });
        btnClose.setOnAction(e -> st.close());
        showSettingEditor(st, vbxFields, "Change app password");
    }

    private void showSettingEditor(final Stage st, final Pane paneBase, final String windowTitle) {
        st.setResizable(false);
        st.setTitle(windowTitle);
        st.initOwner(AppConfig.getPrimaryStage());
        st.initModality(Modality.APPLICATION_MODAL);

        paneBase.getStyleClass().add("pane-primary");
        paneBase.getStylesheets().add(Objects.requireNonNull(Launcher.class.getResource("style/dark-theme.css")).toExternalForm());

        final Scene sc = new Scene(paneBase);
        st.setScene(sc);
        st.show();
    }

}
