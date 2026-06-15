package org.ajikhoji.passwordmanager.repository;

import org.ajikhoji.passwordmanager.model.AccountEntity;

import java.util.List;

public interface AccountRepo {

    void addNewAccountCredential(AccountEntity newEntity);
    void deleteAccountCredential(AccountEntity entityToDelete);
    List<AccountEntity> getAllAccountCredential();
    void updateAccountCredential(AccountEntity updatedEntity);

    //dashboard utility methods
    List<AccountEntity> getKMostUsedAccounts(int k);
    List<AccountEntity> getKRecentModifiedAccounts(int k);
    List<AccountEntity> getKRecentUsedAccounts(int k);
    List<AccountEntity> getAccountsAddedThisMonth();

}
