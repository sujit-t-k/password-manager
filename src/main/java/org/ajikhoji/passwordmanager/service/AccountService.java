package org.ajikhoji.passwordmanager.service;

import org.ajikhoji.passwordmanager.model.AccountEntity;

import java.util.List;

public interface AccountService {

    void addNewAccountCredential(final AccountEntity newEntity);
    void deleteAccountCredential(final AccountEntity entityToDelete);
    void updateAccountCredential(final AccountEntity originalEntity, final AccountEntity updatedEntity);
    List<AccountEntity> getAllAccountCredential();

}
