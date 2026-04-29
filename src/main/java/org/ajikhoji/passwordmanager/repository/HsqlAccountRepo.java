package org.ajikhoji.passwordmanager.repository;

import org.ajikhoji.passwordmanager.model.AccountEntity;

import java.sql.Connection;
import java.util.List;

public class HsqlAccountRepo implements AccountRepo {

    private Connection conn;

    @Override
    public void addNewAccountCredential(final AccountEntity newEntity) {

    }

    @Override
    public void deleteAccountCredential(final AccountEntity entityToDelete) {

    }

    @Override
    public List<AccountEntity> getAllAccountCredential() {
        return List.of();
    }

    @Override
    public void updateAccountCredential(final AccountEntity updatedAccountEntity) {

    }

}
