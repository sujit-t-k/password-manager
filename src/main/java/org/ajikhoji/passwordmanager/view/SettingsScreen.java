package org.ajikhoji.passwordmanager.view;

import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javafx.stage.StageStyle;
import org.ajikhoji.passwordmanager.Launcher;
import org.ajikhoji.passwordmanager.config.AppConfig;
import org.ajikhoji.passwordmanager.config.AppResources;
import org.ajikhoji.passwordmanager.config.DbConfig;
import org.ajikhoji.passwordmanager.config.SideBarItem;
import org.ajikhoji.passwordmanager.dto.AccountWithCustomFields;
import org.ajikhoji.passwordmanager.dto.ImportAnalyzeResult;
import org.ajikhoji.passwordmanager.exception.DatabaseOperationFailureException;
import org.ajikhoji.passwordmanager.exception.ValidationException;
import org.ajikhoji.passwordmanager.model.AccountCustomFieldEntity;
import org.ajikhoji.passwordmanager.model.LabelEntity;
import org.ajikhoji.passwordmanager.repository.OpenLinkButtonActionCustomizable;
import org.ajikhoji.passwordmanager.repository.TableFieldsPreferenceRememberable;
import org.ajikhoji.passwordmanager.security.AesEncryptionService;
import org.ajikhoji.passwordmanager.security.EncryptionService;
import org.ajikhoji.passwordmanager.security.KeyManager;
import org.ajikhoji.passwordmanager.service.CsvExportService;
import org.ajikhoji.passwordmanager.service.CsvImportService;
import org.ajikhoji.passwordmanager.service.SettingService;
import org.ajikhoji.passwordmanager.service.import_strategy.ConflictResolutionStrategy;
import org.ajikhoji.passwordmanager.service.import_strategy.ConflictResolutionStrategyFactory;
import org.ajikhoji.passwordmanager.service.import_strategy.ConflictResolutionType;
import org.ajikhoji.passwordmanager.ui_components.LabelManager;
import org.ajikhoji.passwordmanager.util.*;
import org.ajikhoji.passwordmanager.validator.AccountInfoValidator;

import javax.crypto.SecretKey;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
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

        final Label lblLink = new Label("On clicking open link button of link field in 'View all credentials' table");
        lblLink.setWrapText(true);
        lblLink.setStyle("-fx-font-weight: bold; -fx-padding: 6px 0 8px 0;");
        final RadioButton rbOpen = new RadioButton("Open the link in new tab of system's default browser");
        rbOpen.setWrapText(true);
        final RadioButton rbCopyAccId = new RadioButton("Open in browser and copy account id/name to clipboard");
        rbCopyAccId.setWrapText(true);
        final RadioButton rbCopyPass = new RadioButton("Open in browser and copy account password to clipboard");
        rbCopyPass.setWrapText(true);
        rbOpen.addEventFilter(MouseEvent.ANY, e -> {
            if(rbOpen.isSelected()) {
                e.consume();
            }
        });
        rbCopyAccId.addEventFilter(MouseEvent.ANY, e -> {
            if(rbCopyAccId.isSelected()) {
                e.consume();
            }
        });
        rbCopyPass.addEventFilter(MouseEvent.ANY, e -> {
            if(rbCopyPass.isSelected()) {
                e.consume();
            }
        });

        final Label lblDefaultScreen = new Label("Default screen to show up after app launch");
        lblDefaultScreen.setWrapText(true);
        lblDefaultScreen.setStyle("-fx-font-weight: bold; -fx-padding: 6px 0 8px 0;");
        final RadioButton rbViewAll = new RadioButton("View all credentials");
        rbViewAll.setWrapText(true);
        final RadioButton rbDashboard = new RadioButton("Dashboard");
        rbDashboard.setWrapText(true);
        rbDashboard.addEventFilter(MouseEvent.ANY, e -> {
            if(rbDashboard.isSelected()) {
                e.consume();
            }
        });
        rbViewAll.addEventFilter(MouseEvent.ANY, e -> {
            if(rbViewAll.isSelected()) {
                e.consume();
            }
        });

        final VBox vbxGeneral = new VBox(18.0D,
            lblTitleGeneral,
            new VBox(3.0D, lblViewFields, fpViewFields),
            new VBox(3.0D, lblFieldOrder, new VBox(6.0D, rbDefault, rbCustom)),
            new VBox(3.0D, lblLink, new VBox(6.0D, rbOpen, rbCopyAccId, rbCopyPass)),
            new VBox(3.0D, lblDefaultScreen, new VBox(6.0D, rbDashboard, rbViewAll))
        );

        //labels sections
        final LabelManager lblManager = new LabelManager();
        final Label lblTitleLabels = new Label();
        lblTitleLabels.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        final VBox vbxLabel = new VBox(4.0D, lblTitleLabels, lblManager);
        vbxLabel.setStyle("-fx-padding: 20px 0 0 0;");

        lblManager.setOnLabelCountChange(updatedCount -> {
            lblManager.setPrefHeight(Math.min(500.0D, 55.0D * (updatedCount + 1)));
            lblTitleLabels.setText(String.format("Labels (%d/%d)", updatedCount, LabelEntity.MAX_LABEL_CAP));
        });
        lblManager.triggerLabelCountChangeEvent();

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
            btnEdit.getStyleClass().add("btn-img");
            return btnEdit;
        };
        final Function<String, Label> SettingInfoLabel = text -> {
            final Label lbl = new Label(text);
            lbl.setStyle("-fx-font-size: 16px");
            return lbl;
        };

        final Label lblTitleSecurity = new Label("Personal & Security");
        lblTitleSecurity.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
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

        //data section
        final GridPane gpData = new GridPane(8.0D, 16.0D);
        gpData.setStyle("-fx-padding: 20px 0 0 0;");
        final ColumnConstraints ccDataDescription = new ColumnConstraints();
        ccDataDescription.setHalignment(HPos.LEFT);
        gpData.getColumnConstraints().add(ccDataDescription);
        final ColumnConstraints ccDataAction = new ColumnConstraints();
        ccDataAction.setHalignment(HPos.LEFT);
        gpData.getColumnConstraints().add(ccDataAction);

        final Label lblData = new Label("Data");
        lblData.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        gpData.add(new HBox(lblData), 0, 0, 2, 1);
        final Button btnClearData = new Button("Clear data");
        gpData.addRow(1, new Label("Deletes all stored account credential"), btnClearData);
        final Button btnExport = new Button("Export as CSV");
        gpData.addRow(2, new Label("Exports all stored credential as a single CSV file"), btnExport);
        final Button btnImport = new Button("Import from CSV");
        gpData.addRow(3, new Label("Import data from previously exported CSV file"), btnImport);

        vbxContent.getChildren().addAll(vbxGeneral, vbxLabel, gpSecurity, gpData);
        sp.setContent(vbxContent);
        getChildren().add(sp);
        sp.prefWidthProperty().bind(widthProperty());
        sp.prefHeightProperty().bind(heightProperty());

        //data population and functionalities of all section starts from here
        final SettingService settingService = DbConfig.getSettingService();
        lblHintValue.setText(settingService.getHint());
        lblUsernameValue.setText(settingService.getUserName());

        btnHintEdit.setOnAction(e -> editHint(lblHintValue));
        btnUsernameEdit.setOnAction(e -> editUsername(lblUsernameValue));
        btnPasswordEdit.setOnAction(e -> editPassword());

        btnClearData.setOnAction(e -> showClearDataConfirmation());
        btnExport.setOnAction(e -> showExportDataDialog());
        btnImport.setOnAction(e -> showImportDialog());

        final long initialOrder = Math.abs(settingService.getTableFieldsOrder());
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
            return TableFieldsPreferenceRememberable.getUpdatedFieldsCount(order, count);
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

        rbOpen.setOnAction(e -> {
            rbOpen.setSelected(true);
            rbCopyAccId.setSelected(false);
            rbCopyPass.setSelected(false);
            settingService.saveOpenLinkAction(OpenLinkButtonActionCustomizable.LinkActionOption.OPEN_IN_BROWSER);
        });
        rbCopyAccId.setOnAction(e -> {
            rbOpen.setSelected(false);
            rbCopyAccId.setSelected(true);
            rbCopyPass.setSelected(false);
            settingService.saveOpenLinkAction(OpenLinkButtonActionCustomizable.LinkActionOption.OPEN_IN_BROWSER_AND_COPY_ACC_ID);
        });
        rbCopyPass.setOnAction(e -> {
            rbOpen.setSelected(false);
            rbCopyAccId.setSelected(false);
            rbCopyPass.setSelected(true);
            settingService.saveOpenLinkAction(OpenLinkButtonActionCustomizable.LinkActionOption.OPEN_IN_BROWSER_AND_COPY_ACC_PASS);
        });
        final OpenLinkButtonActionCustomizable.LinkActionOption savedOption = settingService.getOpenLinkActionPreference();
        rbOpen.setSelected(savedOption.equals(OpenLinkButtonActionCustomizable.LinkActionOption.OPEN_IN_BROWSER));
        rbCopyAccId.setSelected(savedOption.equals(OpenLinkButtonActionCustomizable.LinkActionOption.OPEN_IN_BROWSER_AND_COPY_ACC_ID));
        rbCopyPass.setSelected(savedOption.equals(OpenLinkButtonActionCustomizable.LinkActionOption.OPEN_IN_BROWSER_AND_COPY_ACC_PASS));

        rbDashboard.setOnAction(e -> {
            rbDashboard.setSelected(true);
            rbViewAll.setSelected(false);
            settingService.saveDefaultScreenOnAppLaunch(SideBarItem.DASHBOARD.name());
        });
        rbViewAll.setOnAction(e -> {
            rbViewAll.setSelected(true);
            rbDashboard.setSelected(false);
            settingService.saveDefaultScreenOnAppLaunch(SideBarItem.VIEW_ALL.name());
        });
        if(settingService.getDefaultScreenOnAppLaunch().equals(SideBarItem.DASHBOARD.name())) {
            rbDashboard.setSelected(true);
        } else {
            rbViewAll.setSelected(true);
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
        final Utility.EntryField password = Utility.addLabeledToggleablePasswordField("Enter password", 50, true, vbxFields);
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
        final Utility.EntryField password = Utility.addLabeledToggleablePasswordField("Enter password", 50, true, vbxFields);
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
        final Utility.EntryField password = Utility.addLabeledToggleablePasswordField("Enter current password", 50, true, vbxFields);
        final Utility.EntryField newPassword = Utility.addLabeledToggleablePasswordField("Enter new password", 50, vbxFields);
        final Utility.EntryField newPasswordConfirm = Utility.addLabeledToggleablePasswordField("Confirm new password", 50, true, vbxFields);
        final Button btnClose = new Button("Close");
        final Button btnApply = new Button("Apply");
        final HBox hbxControls = new HBox(12.0D, btnClose, btnApply);
        hbxControls.setAlignment(Pos.CENTER);
        vbxFields.getChildren().add(hbxControls);

        btnApply.setOnAction(e -> {
            final String oldPass = password.textProperty().get();
            final String pass = newPassword.textProperty().get();
            final String confirmPass = newPasswordConfirm.textProperty().get();
            if(pass == null || !pass.equals(confirmPass)) {
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

    private void showClearDataConfirmation() {
        final Stage st = new Stage();
        final VBox vbxFields = new VBox(16.0D);
        vbxFields.setStyle("-fx-padding: 16px;");

        if(DbConfig.getAccountService().getAllAccountCredential().isEmpty()) {
            final Label lblPrompt = new Label("No data available for clearance");
            lblPrompt.setStyle("-fx-font-size: 20px;");
            vbxFields.getChildren().add(lblPrompt);

            final Button btnOk = new Button("Close");
            btnOk.setStyle("-fx-font-size: 16px;");
            final HBox hbOk = new HBox(btnOk);
            vbxFields.getChildren().add(hbOk);

            btnOk.setOnAction(e -> st.close());
            showSettingEditor(st, vbxFields, "Clear data");

            return;
        }

        final Label lblPrompt = new Label("Deletes all account credentials and its associated custom field " +
                "information stored in this app. Setting preferences will be retained.");
        vbxFields.getChildren().add(lblPrompt);

        final Utility.EntryField password = Utility.addLabeledToggleablePasswordField("Enter app password to proceed", 50, true, vbxFields);

        final Image imgWarning = AppConfig.getAppResources().imgWarning;
        final Label lblWarning = new Label("This action cannot be undone. Deleted data cannot be recovered.");
        lblWarning.setStyle("-fx-text-fill: #FFB80F; -fx-font-size: 16px;");
        lblWarning.setAlignment(Pos.CENTER);
        lblWarning.setTextAlignment(TextAlignment.CENTER);
        lblWarning.prefWidthProperty().bind(vbxFields.widthProperty());
        final ImageView ivWarning = new ImageView(imgWarning);
        ivWarning.setFitHeight(25.0D);
        ivWarning.setPreserveRatio(true);
        lblWarning.setGraphic(ivWarning);
        vbxFields.getChildren().add(lblWarning);

        final Button btnProceed = new Button("Clear data");
        btnProceed.getStyleClass().add("btn-important-decision-warning");
        btnProceed.setStyle("-fx-font-size: 16px;");
        final HBox hbxControls = new HBox(btnProceed);
        hbxControls.setAlignment(Pos.CENTER);
        vbxFields.getChildren().add(hbxControls);

        btnProceed.setOnAction(e -> {
            if(isPasswordCorrect(password.textProperty().get())) {
                try {
                    DbConfig.getSettingService().clearAccountCredentialData();
                    st.close();
                    Utility.showInformationAlert("Operation Success", "Data cleared!!");
                    ClipboardCopyUtil.clear();
                } catch (final Exception ex) {
                    Utility.showErrorAlert("Operation Failed", "Internal Error occurred");
                }
            } else {
                password.errorMessageProperty().set("Incorrect password entered");
                Utility.showErrorAlert("Operation Failed", "Incorrect password entered");
            }
        });

        showSettingEditor(st, vbxFields, "Clear data");
    }

    private String saveFilePath = "";
    private String pathWithNoPrefix = "";
    private String shortenedPath = "";
    private Tooltip locationToolTip = null;
    private void showExportDataDialog() {
        final Stage st = new Stage();
        final VBox vbxFields = new VBox(16.0D);
        vbxFields.setStyle("-fx-padding: 16px;");

        if(DbConfig.getAccountService().getAllAccountCredential().isEmpty()) {
            final Label lblPrompt = new Label("No data available to export");
            lblPrompt.setStyle("-fx-font-size: 20px;");
            vbxFields.getChildren().add(lblPrompt);

            final Button btnOk = new Button("Close");
            btnOk.setStyle("-fx-font-size: 16px;");
            final HBox hbOk = new HBox(btnOk);
            vbxFields.getChildren().add(hbOk);

            btnOk.setOnAction(e -> st.close());
            showSettingEditor(st, vbxFields, "Export data");

            return;
        }

        final Label lblPrompt = new Label("Export all stored account credentials and custom field information to a CSV file.");
        vbxFields.getChildren().add(lblPrompt);

        final Utility.EntryField password = Utility.addLabeledToggleablePasswordField("Enter app password to continue", 50, true, vbxFields);

        final Label lblFileLocation = new Label("File location with name");
        final String locationNotSelected = "Selected file location with name will be displayed here. Once selected, click here to view expanded path.";
        shortenedPath = locationNotSelected;
        pathWithNoPrefix = locationNotSelected;
        final TextField tfLocationValue = new TextField(locationNotSelected);
        tfLocationValue.setEditable(false);
        HBox.setHgrow(tfLocationValue, Priority.ALWAYS);
        final Button btnLocate = new Button("Browse...");
        final VBox vbxFileLocation = new VBox(6.0D, lblFileLocation, new HBox(6.0D, tfLocationValue, btnLocate));;
        vbxFields.getChildren().add(vbxFileLocation);

        final Image imgWarning = AppConfig.getAppResources().imgWarning;
        final Label lblWarning = new Label("The CSV file to be exported will contain unencrypted passwords.\n" +
                "Anyone with access to the file can view your credentials.");
        lblWarning.setStyle("-fx-text-fill: #FFB80F; -fx-font-size: 16px;");
        lblWarning.setAlignment(Pos.CENTER);
        lblWarning.setTextAlignment(TextAlignment.CENTER);
        lblWarning.prefWidthProperty().bind(vbxFields.widthProperty());
        final ImageView ivWarning = new ImageView(imgWarning);
        ivWarning.setFitHeight(25.0D);
        ivWarning.setPreserveRatio(true);
        lblWarning.setGraphic(ivWarning);
        vbxFields.getChildren().add(lblWarning);

        final Button btnProceed = new Button("Export");
        btnProceed.getStyleClass().add("btn-important-decision-warning");
        btnProceed.setStyle("-fx-font-size: 16px;");
        final HBox hbxControls = new HBox(btnProceed);
        hbxControls.setAlignment(Pos.CENTER);
        vbxFields.getChildren().add(hbxControls);

        btnLocate.setOnAction(e -> {
            final FileChooser chooser = new FileChooser();

            chooser.setTitle("Export all account credentials");
            chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
            "CSV Files",
            "*.csv"
                )
            );

            final File file = chooser.showSaveDialog(AppConfig.getPrimaryStage());
            if(file != null) {
                saveFilePath = file.getAbsolutePath();
                pathWithNoPrefix = saveFilePath;

                if(pathWithNoPrefix.startsWith("\\\\?\\")) {//when file path is too long Windows OS adds '\\?\' as prefix. use it while saving data but don't show it to user.
                    pathWithNoPrefix = pathWithNoPrefix.substring(4);
                }
                final int count = (int) pathWithNoPrefix.chars().filter(c -> c == '\\').count();

                if(count > 2) {
                    final int firstOccurrenceIdx = pathWithNoPrefix.indexOf('\\');
                    final int secondOccurrenceIdx = pathWithNoPrefix.indexOf('\\', firstOccurrenceIdx + 1);
                    final int lastOccurrenceIdx = pathWithNoPrefix.lastIndexOf('\\');
                    shortenedPath = String.format("%s...%s", pathWithNoPrefix.substring(0, secondOccurrenceIdx + 1), pathWithNoPrefix.substring(lastOccurrenceIdx));
                } else {
                    shortenedPath = pathWithNoPrefix;
                }
                tfLocationValue.setText(shortenedPath);

                if(locationToolTip == null) {
                    locationToolTip = new Tooltip();
                    tfLocationValue.setTooltip(locationToolTip);
                }
                locationToolTip.setText(pathWithNoPrefix);
            }
        });
        tfLocationValue.focusedProperty().addListener((ol, ov, nv) -> {
            if(nv) {
                tfLocationValue.setText(pathWithNoPrefix);
            } else {
                tfLocationValue.setText(shortenedPath);
            }
        });

        btnProceed.setOnAction(e -> {
            if(tfLocationValue.getText().equals(locationNotSelected) || saveFilePath == null || saveFilePath.isBlank()) {
                Utility.showErrorAlert("Validation Error", "Select valid save location");
                return;
            }
            if(isPasswordCorrect(password.textProperty().get())) {
                showExportDataProcessingInfo();
                st.close();
            } else {
                password.errorMessageProperty().set("Incorrect password entered");
                Utility.showErrorAlert("Operation Failed", "Incorrect password entered");
            }
        });

        showSettingEditor(st, vbxFields, "Export data");
    }

    private void showExportDataProcessingInfo() {
        final Stage st = new Stage();
        final Scene appScene = AppConfig.getScene();
        final ColorAdjust ca = new ColorAdjust();
        ca.setBrightness(-0.4D);
        appScene.getRoot().setEffect(ca);

        st.initStyle(StageStyle.UNDECORATED);
        final VBox vbxFields = new VBox(16.0D);
        vbxFields.setStyle("-fx-padding: 16px; -fx-border-width: 2px; -fx-border-color: #565656;");

        final Label lblPrompt = new Label("Data export in progress. Please wait for a moment.");
        vbxFields.getChildren().add(lblPrompt);

        showSettingEditor(st, vbxFields, "Exporting data");

        try {
            Utility.exportAllCredentialDataAsCsv(saveFilePath);
            showExportSuccessDialog();
        } catch (final Exception e) {
            Utility.showErrorAlert("Operation Failed", "Internal Error occurred");
        } finally {
            st.close();
            appScene.getRoot().setEffect(null);
        }
    }

    private void showExportSuccessDialog() {
        final Stage st = new Stage();
        final VBox vbxInfo = new VBox(16.0D);
        vbxInfo.setStyle("-fx-padding: 16px;");

        final Label lblPrompt = new Label("Your data has been exported successfully.");
        vbxInfo.getChildren().add(lblPrompt);

        final Label lblLocation = new Label("Location");
        final Label lblLocationValue = new Label(pathWithNoPrefix);
        lblLocationValue.setWrapText(true);
        Tooltip.install(lblLocationValue, new Tooltip(pathWithNoPrefix));
        final VBox vbxLocation = new VBox(4.0D, lblLocation, lblLocationValue);
        vbxInfo.getChildren().add(vbxLocation);

        final Image imgWarning = AppConfig.getAppResources().imgWarning;
        final Label lblWarning = new Label("This file contains unencrypted passwords.");
        lblWarning.setStyle("-fx-text-fill: #FFB80F; -fx-font-size: 16px;");
        lblWarning.setAlignment(Pos.CENTER);
        lblWarning.setTextAlignment(TextAlignment.CENTER);
        lblWarning.prefWidthProperty().bind(vbxInfo.widthProperty());
        final ImageView ivWarning = new ImageView(imgWarning);
        ivWarning.setFitHeight(25.0D);
        ivWarning.setPreserveRatio(true);
        lblWarning.setGraphic(ivWarning);

        final Label lblTip = new Label("Store it securely and delete it when no longer needed.");
        vbxInfo.getChildren().add(lblWarning);
        final VBox vbxWarning = new VBox(4.0D, lblWarning, lblTip);
        vbxInfo.getChildren().add(vbxWarning);

        final Button btnOpen = new Button("View in File Explorer");
        final Button btnClose = new Button("Close");
        btnOpen.setStyle("-fx-font-size: 16px;");
        btnClose.setStyle("-fx-font-size: 16px;");
        final HBox hbxControls = new HBox(16.0D, btnOpen, btnClose);
        hbxControls.setAlignment(Pos.CENTER);
        vbxInfo.getChildren().add(hbxControls);

        vbxInfo.setPrefWidth(Math.clamp(500.0D, AppConfig.getScreenWidth() * 0.35D, AppConfig.getScreenWidth()));

        btnOpen.setOnAction(e -> {
            try {
                new ProcessBuilder(
                        "explorer.exe",
                        "/select,",
                        pathWithNoPrefix
                ).start();
            } catch (final Exception ex) {
                Utility.showErrorAlert("Permission denied", "System does not allow to open file explorer.");
            }
        });
        btnClose.setOnAction(e -> st.close());

        showSettingEditor(st, vbxInfo, "Export completed");
    }

    private void showImportDialog() {
        final Stage st = new Stage();
        final VBox vbx = new VBox(5.0D);
        vbx.setStyle("-fx-padding: 20px;");

        final Label lblPrompt = new Label("Please wait for a moment...");
        lblPrompt.setStyle("-fx-font-size: 16px;");
        final ProgressBar pb = new ProgressBar();
        vbx.getChildren().addAll(lblPrompt, pb);
        try {
            final FileChooser fileChooser = new FileChooser();
            final File file = fileChooser.showOpenDialog(AppConfig.getPrimaryStage());

            if(file == null) {
                return;
            }

            showSettingEditor(st, vbx, "Analyzing data");
            final List<AccountWithCustomFields> importedData = new CsvImportService().importFrom(file.toPath());
            final ImportAnalyzeResult result = ImportDataAnalyzer.getImportAnalytics(importedData, ImportDataAnalyzer.ImportMethod.CSV);

            if(result.getNewAccounts().isEmpty() && result.getConflictAccounts().isEmpty()) {
                Utility.showInformationAlert("Nothing to import", "Either file is empty or all records are available already.");
                return;
            }
            showImportAnalysisResultDialog(result);
        } catch (final RuntimeException e) {
            Utility.showErrorAlert("Import failed", e.getMessage());
        } catch (final Exception e) {
            Utility.showErrorAlert("Import failed", "Internal Error occurred");
        } finally {
            st.close();
        }
    }

    private ConflictResolutionType selectedType;
    private void showImportAnalysisResultDialog(final ImportAnalyzeResult result) {
        selectedType = ConflictResolutionType.IMPORT_LATEST_ONLY;//for safe fallback
        final Stage st = new Stage();
        final VBox vbx = new VBox(12.0D);
        vbx.setStyle("-fx-padding: 16px;");

        final int conflictRecordsCount = result.getConflictAccounts().size();
        final VBox vbxAnalysisResult = new VBox(
            6.0D,
            new Label(String.format("Total records found: %d", result.getTotalRecordsCount())),
            new Label(String.format("%d new", result.getNewAccounts().size())),
            new Label(String.format("%d duplicates", result.getAlreadyAvailableAccounts().size())),
            new Label(String.format("%d conflicts", conflictRecordsCount))
        );
        vbx.getChildren().add(vbxAnalysisResult);

        if(conflictRecordsCount > 0) {
            final Label lblPrompt = new Label("Choose how to proceed with conflict records:");

            final VBox vbxChoices = new VBox(3.0D);
            final RadioButton rbImportLatest = getRadioButton("Import only the latest records (recommended)", () -> selectedType = ConflictResolutionType.IMPORT_LATEST_ONLY);
            final RadioButton rbReplace = getRadioButton("Import all conflicting records (overrides some of existing records)", () -> selectedType = ConflictResolutionType.REPLACE_EXISTING);
            final RadioButton rbNewOnly = getRadioButton("Ignore all conflicting records (skips all conflict records)", () -> selectedType = ConflictResolutionType.IMPORT_NEW_ONLY);
            final RadioButton rbReview = getRadioButton("Review conflicts individually", () -> selectedType = ConflictResolutionType.REVIEW_MANUALLY);
            groupRadioButtons(rbImportLatest, rbReplace, rbNewOnly, rbReview);
            rbImportLatest.setSelected(true);

            vbxChoices.getChildren().addAll(rbImportLatest, rbReplace, rbNewOnly, rbReview);
            vbx.getChildren().addAll(lblPrompt, vbxChoices);
        }

        final HBox hbxControls = new HBox(16.0D);
        hbxControls.setAlignment(Pos.CENTER);
        final Button btnCancel = new Button("Cancel");
        final Button btnProceed = new Button("Proceed");
        hbxControls.getChildren().addAll(btnCancel, btnProceed);
        vbx.getChildren().add(hbxControls);

        showSettingEditor(st, vbx, "Analysis Summary");

        btnCancel.setOnAction(e -> st.close());
        btnProceed.setOnAction(e -> {
            if(selectedType.equals(ConflictResolutionType.REVIEW_MANUALLY)) {
                showManualReviewDialog(result);
                st.close();
            } else {
                try {
                    final ConflictResolutionStrategy strategy = ConflictResolutionStrategyFactory.create(selectedType);
                    strategy.resolve(result);
                    Utility.showInformationAlert("Import successful", String.format("%d records imported", result.getImportedRecordCount()));
                } catch (final Exception ex) {
                    Utility.showErrorAlert("Error occurred","Please restart the app and try again");
                } finally {
                    st.close();
                }
            }
        });
    }

    private int conflictIdx = 0;
    private static final byte YET_TO_DECIDE = 3, KEEP_EXISTING_RECORD = 7, REPLACE_BY_IMPORTED_RECORD = 15;
    private void showManualReviewDialog(final ImportAnalyzeResult result) {
        conflictIdx = 0;
        final Stage st = new Stage();
        st.setMinWidth(AppConfig.getVisualScreenWidth() * 0.8D);
        st.setMinHeight(AppConfig.getVisualScreenHeight() * 0.8D);
        final BorderPane bpBase = new BorderPane();
        bpBase.setStyle("-fx-padding: 20px 12px 20px 12px;");

        final int totalConflicts = result.getConflictAccounts().size();
        final Label lblTitle = new Label();
        lblTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        lblTitle.setTextAlignment(TextAlignment.CENTER);
        final HBox hbxTitle = new HBox(lblTitle);
        hbxTitle.setAlignment(Pos.CENTER);
        bpBase.setTop(hbxTitle);

        final VBox vbxExisting = new VBox(12.0D);
        vbxExisting.setStyle("-fx-padding: 4px;");
        vbxExisting.setAlignment(Pos.CENTER);
        final Label lblExisting = new Label("Already available");
        lblExisting.setTextAlignment(TextAlignment.CENTER);
        lblExisting.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        final ScrollPane existingAccountInfo = new ScrollPane();
        existingAccountInfo.setFitToWidth(true);
        existingAccountInfo.getStyleClass().add("info-scroll");
        final String decisionButtonDefaultStyle = "-fx-font-size: 16px;";
        final Button btnExisting = new Button("Keep this");
        btnExisting.setStyle(decisionButtonDefaultStyle);
        final HBox hbxExistingControl = new HBox(btnExisting);
        hbxExistingControl.setAlignment(Pos.CENTER);
        hbxExistingControl.setStyle("-fx-padding: 4px;");
        vbxExisting.getChildren().addAll(lblExisting, existingAccountInfo, hbxExistingControl);

        final VBox vbxImported = new VBox(12.0D);
        vbxImported.setStyle("-fx-padding: 4px;");
        vbxImported.setAlignment(Pos.CENTER);
        final Label lblImported = new Label("From Import file");
        lblImported.setTextAlignment(TextAlignment.CENTER);
        lblImported.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        final ScrollPane importedAccountInfo = new ScrollPane();
        importedAccountInfo.setFitToWidth(true);
        importedAccountInfo.getStyleClass().add("info-scroll");
        final Button btnImported = new Button("Import this");
        btnImported.setStyle(decisionButtonDefaultStyle);
        final HBox hbxImportedControl = new HBox(btnImported);
        hbxImportedControl.setAlignment(Pos.CENTER);
        hbxImportedControl.setStyle("-fx-padding: 4px;");
        vbxImported.getChildren().addAll(lblImported, importedAccountInfo, hbxImportedControl);

        final HBox hbxComparison = new HBox(12.0D, vbxExisting, vbxImported);
        HBox.setHgrow(vbxExisting, Priority.ALWAYS);
        HBox.setHgrow(vbxImported, Priority.ALWAYS);
        final Label lblDecision = new Label();
        lblDecision.setTextAlignment(TextAlignment.CENTER);
        lblDecision.setStyle("-fx-font-size: 18px; -fx-text-fill: #FFCE0C");
        final VBox vbxComparison = new VBox(12.0D, hbxComparison, lblDecision);
        vbxComparison.setAlignment(Pos.CENTER);
        bpBase.setCenter(vbxComparison);

        final HBox hbxControls = new HBox(16.0D);
        hbxControls.setAlignment(Pos.CENTER);
        bpBase.setBottom(hbxControls);

        final Button btnPrevious = new Button("Previous");
        final Button btnNext = new Button("Next");
        if(totalConflicts > 1) {
            hbxControls.getChildren().add(btnPrevious);
        }
        hbxControls.getChildren().add(btnNext);

        showSettingEditor(st, bpBase, String.format("Import Conflict Resolution - %s", AppConfig.getAppName()));

        final byte[] decision = new byte[totalConflicts];
        Arrays.fill(decision, YET_TO_DECIDE);

        final String selectedOptionButtonStyle = "-fx-font-size: 16px; -fx-text-fill: #163005; -fx-background-color: #B5E61D;";
        final Runnable updateDecisionLabel = () -> {
            lblDecision.setText(
                switch (decision[conflictIdx]) {
                    case REPLACE_BY_IMPORTED_RECORD -> "Selected option: Import record";
                    case KEEP_EXISTING_RECORD -> "Selected option: Keep existing record";
                    default -> "Select one of the option to proceed";
                }
            );
            btnExisting.setStyle(decisionButtonDefaultStyle);
            btnImported.setStyle(decisionButtonDefaultStyle);
        };
        final Consumer<Button> highlightSelectedOptionButton = selectedButton -> {
              updateDecisionLabel.run();
              selectedButton.setStyle(selectedOptionButtonStyle);
              btnNext.setDisable(false);
        };
        final Runnable updatePageContent = () -> {
            lblTitle.setText(String.format("Conflict %d of %d", conflictIdx + 1, totalConflicts));
            btnPrevious.setDisable(conflictIdx == 0);

            final AccountWithCustomFields existingAccountWithCustomFields = result.getConflictAccounts().get(conflictIdx).alreadyAvailableAccount();
            final AccountWithCustomFields importedAccountWithCustomFields = result.getConflictAccounts().get(conflictIdx).importedAccount();
            final GridPane[] accountInfoView = DetailedAccountInfoScreen.getDetailedAccountInfoView(existingAccountWithCustomFields, importedAccountWithCustomFields);
            existingAccountInfo.setContent(accountInfoView[0]);
            importedAccountInfo.setContent(accountInfoView[1]);

            btnNext.setText(conflictIdx + 1 == totalConflicts ? "Proceed to import" : "Next");
            btnNext.setDisable(conflictIdx >= 0 && conflictIdx <= totalConflicts - 1 && decision[conflictIdx] == YET_TO_DECIDE);

            updateDecisionLabel.run();
            if(decision[conflictIdx] == KEEP_EXISTING_RECORD) {
                highlightSelectedOptionButton.accept(btnExisting);
            } else if (decision[conflictIdx] == REPLACE_BY_IMPORTED_RECORD) {
                highlightSelectedOptionButton.accept(btnImported);
            }
        };
        final Runnable gotoNextPage = () -> {
            if(decision[conflictIdx] == YET_TO_DECIDE) {
                return;
            }
            if(conflictIdx + 1 == totalConflicts) {
                System.out.println(Arrays.toString(decision));
            } else {
                conflictIdx = Math.min(totalConflicts - 1, conflictIdx + 1);
                updatePageContent.run();
            }
        };
        btnPrevious.setOnAction(e -> {
            conflictIdx = Math.max(0, conflictIdx - 1);
            updatePageContent.run();
        });
        btnNext.setOnAction(e -> gotoNextPage.run());
        btnImported.setOnAction(e -> {
            decision[conflictIdx] = REPLACE_BY_IMPORTED_RECORD;
            highlightSelectedOptionButton.accept(btnImported);
        });
        btnExisting.setOnAction(e -> {
            decision[conflictIdx] = KEEP_EXISTING_RECORD;
            highlightSelectedOptionButton.accept(btnExisting);
        });

        st.setOnCloseRequest(e -> showImportAnalysisResultDialog(result));
        st.setResizable(true);
        updatePageContent.run();
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

    private RadioButton getRadioButton(final String text, final Runnable onSelection) {
        final RadioButton rb = new RadioButton(text);
        rb.addEventFilter(MouseEvent.ANY, e -> {
            if(rb.isSelected()) {
                e.consume();
            }
        });
        rb.setOnAction(e -> {
            rb.setSelected(true);
            onSelection.run();
        });
        return rb;
    }

    private void groupRadioButtons(final RadioButton... rbs) {
        for (final RadioButton rb : rbs) {
            rb.selectedProperty().addListener((ol, ov, nv) -> {
                if(nv) {
                    for (final RadioButton other : rbs) {
                        if(other != rb) {
                            other.setSelected(false);
                        }
                    }
                }
            });
        }
    }

}
