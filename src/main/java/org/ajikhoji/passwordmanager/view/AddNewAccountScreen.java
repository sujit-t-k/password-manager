package org.ajikhoji.passwordmanager.view;

import org.ajikhoji.passwordmanager.config.AppConfig;
import org.ajikhoji.passwordmanager.config.DbConfig;
import org.ajikhoji.passwordmanager.exception.DatabaseOperationFailureException;
import org.ajikhoji.passwordmanager.exception.ValidationException;
import org.ajikhoji.passwordmanager.model.AccountCustomFieldEntity;
import org.ajikhoji.passwordmanager.model.AccountEntity;
import org.ajikhoji.passwordmanager.model.LabelEntity;
import org.ajikhoji.passwordmanager.security.EncryptionService;
import org.ajikhoji.passwordmanager.service.AccountCustomFieldService;
import org.ajikhoji.passwordmanager.service.AccountService;
import org.ajikhoji.passwordmanager.util.Utility;
import org.ajikhoji.passwordmanager.validator.AccountInfoValidator;
import org.ajikhoji.passwordmanager.viewmodel.AddNewAccountViewModel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AddNewAccountScreen extends AccountInfoEditor {

    private final AddNewAccountViewModel viewModel;

    public AddNewAccountScreen(final AddNewAccountViewModel viewModel, final Consumer<AccountEntity> onSuccessfulValidation) {
        this.viewModel = viewModel;
        efName.textProperty().set(viewModel.accName);
        efPassword.textProperty().set(viewModel.accPassword);
        efConfirmPassword.textProperty().set(viewModel.confirmPassword);
        efPlatform.textProperty().set(viewModel.platform);
        cbxLabel.getSelectionModel().select(viewModel.label);
        customFieldEditor.setAll(viewModel.customFields);

        lblHeader.setText("Add New Account Credential");
        btnSave.setText("Add");
        btnSave.setOnAction(e -> {
            try {
                AccountInfoValidator.validateAccountName(efName.textProperty().get());
                AccountInfoValidator.validateUnencryptedPassword(efPassword.textProperty().get());
                AccountInfoValidator.validatePlatform(efPlatform.textProperty().get());
                onSuccessfulValidation.accept(null);
            } catch (final ValidationException ex) {
                Utility.showErrorAlert("Validation Error", ex.getMessage());
                return;
            }
            try {
                final EncryptionService encryptionService = AppConfig.getEncryptionService();
                final String encryptedPassword = encryptionService.encrypt(efPassword.textProperty().get());
                final LabelEntity chosenLabel = cbxLabel.getSelectionModel().getSelectedItem();

                //save account info
                final AccountService accountService = DbConfig.getAccountService();
                final AccountEntity ae = AccountEntity
                        .Builder()
                        .withAccountName(efName.textProperty().get())
                        .withAccountPlatform(efPlatform.textProperty().get())
                        .withEncryptedAccountPassword(encryptedPassword)
                        .withLink(efLink.textProperty().get())
                        .withLabelId(chosenLabel.getLabelId())
                        .withCreatedDateTime(LocalDateTime.now())
                        .build();
                accountService.addNewAccountCredential(ae);

                //save custom account fields info
                final List<AccountCustomFieldEntity> allCustomFields = customFieldEditor.getAllCustomFieldData();
                if(!allCustomFields.isEmpty()) {
                    for(final AccountCustomFieldEntity entity : allCustomFields) {
                        entity.setFieldValue(encryptionService.encrypt(entity.getFieldValue()));
                    }
                    final AccountCustomFieldService accountCustomFieldService = DbConfig.getAccountCustomFieldService();
                    accountCustomFieldService.commit(ae.getAccId(), allCustomFields);
                }

                Utility.showInformationAlert("Success", "Information saved");
                clearData();
                viewModel.reset();
            } catch (final DatabaseOperationFailureException | ValidationException ex) {
                Utility.showErrorAlert("Operation failed", ex.getMessage());
            } catch (final Exception ex) {
                Utility.showErrorAlert("Operation failed", "Error occurred while saving the data.");
            }
        });
    }

    public void saveInfo() {
        viewModel.accName = efName.textProperty().get();
        viewModel.accPassword = efPassword.textProperty().get();
        viewModel.confirmPassword = efConfirmPassword.textProperty().get();
        viewModel.platform = efPlatform.textProperty().get();
        viewModel.link = efLink.textProperty().get();
        viewModel.label = cbxLabel.getSelectionModel().getSelectedItem();
        viewModel.customFields = new ArrayList<>(customFieldEditor.getAllCustomFieldData());
    }

}
