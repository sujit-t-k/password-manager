package org.ajikhoji.passwordmanager.service.import_strategy;

import org.ajikhoji.passwordmanager.config.DbConfig;
import org.ajikhoji.passwordmanager.dto.AccountWithCustomFields;
import org.ajikhoji.passwordmanager.dto.ImportAnalyzeResult;
import org.ajikhoji.passwordmanager.exception.DatabaseOperationFailureException;
import org.ajikhoji.passwordmanager.service.AccountCustomFieldService;
import org.ajikhoji.passwordmanager.service.AccountService;

public final class ImportNewOnlyStrategy implements ConflictResolutionStrategy {

    @Override
    public void resolve(ImportAnalyzeResult result) {
        try {
            try {
                DbConfig.setAutoCommit(false);
                final AccountService accountService = DbConfig.getAccountService();
                final AccountCustomFieldService accountCustomFieldService = DbConfig.getAccountCustomFieldService();

                //add new records
                for (final AccountWithCustomFields newAccount : result.getNewAccounts()) {
                    accountService.addNewAccountCredential(newAccount.getAccountEntity());
                    accountCustomFieldService.commit(newAccount.getAccountEntity().getAccId(), newAccount.getCustomFields());
                }

                DbConfig.commit();
                result.setImportedRecordCount(result.getNewAccounts().size());
            } catch (final Exception ex) {
                throw new DatabaseOperationFailureException("Error occurred in importing data");
            } finally {
                DbConfig.setAutoCommit(true);
            }
        } catch (final Exception e) {
            throw new DatabaseOperationFailureException("Internal Error occurred");
        }
    }

}
