package org.ajikhoji.passwordmanager.util;

import org.ajikhoji.passwordmanager.config.AppConfig;
import org.ajikhoji.passwordmanager.config.DbConfig;
import org.ajikhoji.passwordmanager.dto.AccountWithCustomFields;
import org.ajikhoji.passwordmanager.dto.ImportAnalyzeResult;
import org.ajikhoji.passwordmanager.model.AccountCustomFieldEntity;
import org.ajikhoji.passwordmanager.model.AccountEntity;
import org.ajikhoji.passwordmanager.security.EncryptionService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ImportDataAnalyzer {

    private ImportDataAnalyzer() { }

    public enum ImportMethod {
        CSV
    }

    public static ImportAnalyzeResult getImportAnalytics(final List<AccountWithCustomFields> importedData, final ImportMethod importFrom) {
        final List<AccountEntity> existingAccounts = DbConfig.getAccountService().getAllAccountCredential();

        final Map<String, Map<String, AccountEntity>> existingAccountsLookup = new HashMap<>();
        for(final AccountEntity ae : existingAccounts) {
            existingAccountsLookup.putIfAbsent(ae.getAccName(), new HashMap<>());
            existingAccountsLookup.get(ae.getAccName()).put(ae.getPlatform(), ae);
        }

        final List<AccountWithCustomFields> newAccounts = new ArrayList<>();
        final List<ImportAnalyzeResult.ConflictAccount> conflictAccounts = new ArrayList<>();
        final List<AccountWithCustomFields> alreadyExisting = new ArrayList<>();

        for(final AccountWithCustomFields imported : importedData) {
            final AccountEntity importedAccount = imported.getAccountEntity();
            final AccountEntity existingAccount = existingAccountsLookup.getOrDefault(importedAccount.getAccName(), new HashMap<>()).get(importedAccount.getPlatform());
            //if imported account does not present in exiting account then it means this imported account has to be added into the db
            if(existingAccount == null) {
                newAccounts.add(imported);
                continue;
            }

            //if imported account is already present, determine whether it conflicts or not.
            //if account info doesn't match then consider it as 'conflict'
            final List<AccountCustomFieldEntity> existingCustomFields = DbConfig.getAccountCustomFieldService().getAccountCustomFieldsForAccountId(existingAccount.getAccId());
            final boolean conflict = !isAccountInfoEqual(existingAccount, existingCustomFields, importedAccount, imported.getCustomFields(), importFrom);

            if(conflict) {
                final AccountWithCustomFields existing = new AccountWithCustomFields(existingAccount, existingCustomFields);
                conflictAccounts.add(new ImportAnalyzeResult.ConflictAccount(existing, imported));
            } else {
                alreadyExisting.add(imported);
            }
        }

        return new ImportAnalyzeResult(newAccounts, conflictAccounts, alreadyExisting);
    }

    public static boolean isAccountInfoEqual(final AccountEntity existingAccount, final List<AccountCustomFieldEntity> existingCustomFields,
        final AccountEntity importedAccount, final List<AccountCustomFieldEntity> importedCustomFields, final ImportMethod importFrom) {

        final EncryptionService encryptionService = AppConfig.getEncryptionService();
        //majority conflict occurs on primary information, so check those earlier
        //link, added date, modified dates are ignored
        if(!(
            existingAccount.getAccName().equals(importedAccount.getAccName()) &&
            encryptionService.matchesEncrypted(existingAccount.getAccPassword(), importedAccount.getAccPassword()) &&
            existingAccount.getPlatform().equals(importedAccount.getPlatform())
        )) {
            return false;
        }

        //check for custom fields too
        if(existingCustomFields.size() != importedCustomFields.size()) {
            return false;
        }
        final TwoLevelLookupMap<String, String> importedCustomFieldsLookup = new TwoLevelLookupMap<>();
        for(final AccountCustomFieldEntity importedCustomField : importedCustomFields) {
            importedCustomFieldsLookup.register(importedCustomField.getFieldName(), encryptionService.decrypt(importedCustomField.getFieldValue()));
        }
        for(final AccountCustomFieldEntity existingCustomField : existingCustomFields) {
            if(!importedCustomFieldsLookup.unregister(existingCustomField.getFieldName(), encryptionService.decrypt(existingCustomField.getFieldValue()))) {
                return false;//occurs when current existing custom fields pair does not match with any of the imported custom field pair
            }
        }

        return importedCustomFieldsLookup.isEmpty();//when there are additional custom fields from import, then also account info is considered to be unequal
    }

}
