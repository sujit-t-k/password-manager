package org.ajikhoji.passwordmanager.service;

import org.ajikhoji.passwordmanager.model.AccountEntity;

import java.util.List;

public interface AccountService {

    void addNewAccountCredential(final AccountEntity newEntity);
    void deleteAccountCredential(final AccountEntity entityToDelete);
    void updateAccountCredential(final AccountEntity originalEntity, final AccountEntity updatedEntity);
    List<AccountEntity> getAllAccountCredential();

    //dashboard utility methods
    List<AccountEntity> getKMostUsedAccounts(int k);
    List<AccountEntity> getKRecentModifiedAccounts(int k);
    List<AccountEntity> getKRecentUsedAccounts(int k);
    List<AccountEntity> getAccountsAddedThisMonth();

}
