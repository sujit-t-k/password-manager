package org.ajikhoji.passwordmanager.view;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.ajikhoji.passwordmanager.Launcher;
import org.ajikhoji.passwordmanager.config.AppConfig;
import org.ajikhoji.passwordmanager.config.DbConfig;
import org.ajikhoji.passwordmanager.model.AccountCustomFieldEntity;
import org.ajikhoji.passwordmanager.model.AccountEntity;
import org.ajikhoji.passwordmanager.model.LabelEntity;
import org.ajikhoji.passwordmanager.security.EncryptionService;
import org.ajikhoji.passwordmanager.util.Utility;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

public class EditAccountScreen extends AccountInfoEditor {

    /*
     * Allows editing given account entity and its associated custom fields.
     * On successful validation, the onSave bi-consumer call is made.
     * Note that the bi-consumer returns updated information as a new object,
     * it does not perform changes/mutates the given instance of account entity and custom fields.
     */
    public static void show(
        final AccountEntity info,
        final List<AccountCustomFieldEntity> customFields,
        final BiConsumer<AccountEntity, List<AccountCustomFieldEntity>> onSave
    ) {
        final Stage st = new Stage();
        st.setTitle("Editing account credential");
        st.initOwner(AppConfig.getPrimaryStage());
        st.initModality(Modality.APPLICATION_MODAL);
        final EditAccountScreen screen = new EditAccountScreen(info, customFields, onSave, st);
        final Scene scene = new Scene(screen, AppConfig.getScreenWidth() * 0.8D, AppConfig.getScreenHeight() * 0.8D);
        screen.getStyleClass().add("pane-primary");
        screen.getStylesheets().add(Objects.requireNonNull(Launcher.class.getResource("style/dark-theme.css")).toExternalForm());
        st.setScene(scene);
        st.show();
    }

    private EditAccountScreen(
        final AccountEntity info,
        final List<AccountCustomFieldEntity> customFields,
        final BiConsumer<AccountEntity, List<AccountCustomFieldEntity>> onSave,
        final Stage stage
    ) {
        final EncryptionService encryptionService = AppConfig.getEncryptionService();
        final String oldPassword = encryptionService.decrypt(info.getAccPassword());
        final List<AccountCustomFieldEntity> oldCustomFields = customFields.stream()
            .map(entity ->
                new AccountCustomFieldEntity(
                    entity.getAccId(),
                    entity.getFieldName(),
                    encryptionService.decrypt(entity.getFieldValue())
                )
            )
            .toList();

        efName.textProperty().set(info.getAccName());
        efPassword.textProperty().set(oldPassword);
        efConfirmPassword.textProperty().set(oldPassword);
        efPlatform.textProperty().set(info.getPlatform());
        efLink.textProperty().set(info.getLink());
        cbxLabel.getSelectionModel().select(DbConfig.getLabelService().getLabelEntityById(info.getLabelId()));
        customFieldEditor.setAll(oldCustomFields);

        btnSave.setText("Save changes");
        lblHeader.setText("Editing account credential");

        btnSave.setOnAction(e -> {
            final String name = efName.textProperty().get();
            final String pass = efPassword.textProperty().get();
            final String platform = efPlatform.textProperty().get();
            final String link = efLink.textProperty().get();
            final LabelEntity label = cbxLabel.getValue();
            final List<AccountCustomFieldEntity> customFieldEntities = customFieldEditor.getAllCustomFieldData();

            boolean changeMade = !name.equals(info.getAccName()) || !pass.equals(oldPassword) || !platform.equals(info.getPlatform()) ||
                    label.getLabelId() != info.getLabelId() || oldCustomFields.size() != customFieldEntities.size() ||
                    ((link == null && info.getLink() != null) || (link != null && (info.getLink() == null || !info.getLink().equals(link))));
            if(!changeMade) {
                for(final AccountCustomFieldEntity oldEntity : oldCustomFields) {
                    if(customFieldEntities.stream()
                            .filter(entity ->
                                    entity.getFieldName().equals(oldEntity.getFieldName()) &&
                                    entity.getFieldValue().equals(oldEntity.getFieldValue())
                            ).toList().isEmpty()) {
                        changeMade = true;
                        break;
                    }
                }
            }
            if(changeMade) {
                final AccountEntity updatedEntity = AccountEntity.Builder()
                    .withAccountId(info.getAccId())
                    .withAccountName(name)
                    .withEncryptedAccountPassword(encryptionService.encrypt(pass))
                    .withAccountPlatform(platform)
                    .withLink(link)
                    .withLabelId(label.getLabelId())
                    .withUsageCount(info.getUsageCount())
                    .withCreatedDateTime(info.getCreatedDateTime())
                    .withLastUsedDateTime(info.getLastUsedDateTime())
                    .withLastUpdatedDateTime(LocalDateTime.now())
                    .build();
                final List<AccountCustomFieldEntity> encryptedCustomFieldEntities = customFieldEntities.stream()
                        .map(entity ->
                            new AccountCustomFieldEntity(
                                info.getAccId(),
                                entity.getFieldName(),
                                encryptionService.encrypt(entity.getFieldValue())
                            )
                        ).toList();
                try {
                    onSave.accept(updatedEntity, encryptedCustomFieldEntities);
                    stage.close();
                } catch (final Exception ex) {
                    Utility.showErrorAlert("Update operation aborted", "Something went wrong. Try again later.");
                }
            } else {
                Utility.showInformationAlert("Update operation aborted", "No changes detected");
            }
        });

        final Button btnClose = new Button("Close");
        btnClose.setOnAction(e -> stage.close());
        fpControls.getChildren().add(1, btnClose);
    }

}
