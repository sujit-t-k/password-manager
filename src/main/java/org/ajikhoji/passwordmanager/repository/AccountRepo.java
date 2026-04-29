package org.ajikhoji.passwordmanager.repository;

import org.ajikhoji.passwordmanager.model.AccountEntity;

import java.util.List;

public interface AccountRepo {

    void addNewAccountCredential(AccountEntity newEntity);
    void deleteAccountCredential(AccountEntity entityToDelete);
    List<AccountEntity> getAllAccountCredential();
    void updateAccountCredential(AccountEntity updatedEntity);

}
