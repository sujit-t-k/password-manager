package org.ajikhoji.passwordmanager.dto;

import org.ajikhoji.passwordmanager.model.AccountEntity;

import java.time.LocalDateTime;
import java.util.List;

public final class ImportAnalyzeResult {

    public record ConflictAccount(AccountWithCustomFields alreadyAvailableAccount, AccountWithCustomFields importedAccount) {
        public boolean isImportedAccountLatest() {
            final AccountEntity already = alreadyAvailableAccount.getAccountEntity();
            final AccountEntity imported = importedAccount.getAccountEntity();

            final LocalDateTime importedModified = imported.getRecentUpdateDateTime();
            final LocalDateTime alreadyModified = already.getRecentUpdateDateTime();

            final LocalDateTime importedAdded = imported.getCreatedDateTime();
            final LocalDateTime alreadyAdded = already.getCreatedDateTime();

            if(importedModified == null) {
                if(alreadyModified == null) {//CASE - 1: no account has ever been modified
                    return importedAdded.isAfter(alreadyAdded);
                } else {//CASE - 2: existing account is modified but imported is not
                    return importedAdded.isAfter(alreadyModified);
                }
            } else if (alreadyModified == null) {//CASE - 3: existing has never been modified whereas imported is modified
                return importedModified.isAfter(alreadyAdded);
            } else {//CASE - 4: both accounts have been modified
                return importedModified.isAfter(alreadyModified);
            }
        }

        @Override
        public String toString() {
            return String.format("ConflictRecord(%s, %s)", alreadyAvailableAccount, importedAccount);
        }
    }

    //accounts that are not present in current database
    private final List<AccountWithCustomFields> newAccounts;

    //accounts that are present in database and have conflicts
    private final List<ConflictAccount> conflictAccounts;

    //accounts that are already present in the database and every detail matches exactly
    private final List<AccountWithCustomFields> alreadyAvailableAccounts;

    public ImportAnalyzeResult(final List<AccountWithCustomFields> newAccounts, final List<ConflictAccount> conflictAccounts, final List<AccountWithCustomFields> alreadyAvailableAccounts) {
        this.newAccounts = newAccounts;
        this.conflictAccounts = conflictAccounts;
        this.alreadyAvailableAccounts = alreadyAvailableAccounts;
    }

    public List<AccountWithCustomFields> getNewAccounts() {
        return newAccounts;
    }

    public List<ConflictAccount> getConflictAccounts() {
        return conflictAccounts;
    }

    public List<AccountWithCustomFields> getAlreadyAvailableAccounts() {
        return alreadyAvailableAccounts;
    }

}
