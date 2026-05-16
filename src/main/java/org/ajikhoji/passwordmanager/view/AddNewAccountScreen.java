package org.ajikhoji.passwordmanager.view;

import org.ajikhoji.passwordmanager.exception.ValidationException;
import org.ajikhoji.passwordmanager.model.AccountEntity;
import org.ajikhoji.passwordmanager.util.Utility;
import org.ajikhoji.passwordmanager.validator.AccountInfoValidator;

import java.util.function.Consumer;

public class AddNewAccountScreen extends AccountInfoEditor {

    public AddNewAccountScreen(Consumer<AccountEntity> onSuccessfulValidation) {
        lblHeader.setText("Add New Account Credential");
        btnSave.setText("Add");
        btnSave.setOnAction(e -> {
            try {
                AccountInfoValidator.validateAccountName(sspName.get());
                AccountInfoValidator.validateUnencryptedPassword(sspPassword.get());
                AccountInfoValidator.validatePlatform(sspPlatform.get());
                onSuccessfulValidation.accept(null);
            } catch (final ValidationException ex) {
                Utility.showErrorAlert("Validation Error", ex.getMessage());
            }
        });
    }

}
