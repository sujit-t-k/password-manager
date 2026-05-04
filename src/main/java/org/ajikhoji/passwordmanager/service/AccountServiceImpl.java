package org.ajikhoji.passwordmanager.service;

import org.ajikhoji.passwordmanager.exception.DatabaseOperationFailureException;
import org.ajikhoji.passwordmanager.exception.ValidationException;
import org.ajikhoji.passwordmanager.model.AccountEntity;
import org.ajikhoji.passwordmanager.repository.AccountRepo;
import org.ajikhoji.passwordmanager.util.TwoLevelLookupMap;

import java.util.List;

public class AccountServiceImpl implements AccountService {

    private final AccountRepo repo;
    /*
     * There can never be more than one record that holds different credential information with same account name and platform.
     * To ensure this while adding new account credential this becomes handy for fast-lookup.
     */
    private final TwoLevelLookupMap<String, String> nameAndPlatform;

    public AccountServiceImpl(final AccountRepo repo) {
        this.repo = repo;
        this.nameAndPlatform = new TwoLevelLookupMap<>();
    }

    @Override
    public void addNewAccountCredential(final AccountEntity newEntity) {
        final String accIName = newEntity.getAccName();
        final String platform = newEntity.getPlatform();
        if(!nameAndPlatform.register(accIName, platform)) {
            throw new ValidationException("""
                Cannot save account credential because there is already an existing account credential saved with same account name and platform
            """);
        }
        try {
            repo.addNewAccountCredential(newEntity);
        } catch (final DatabaseOperationFailureException on) {
            nameAndPlatform.unregister(accIName, platform);
            throw on;
        }
    }

    @Override
    public void deleteAccountCredential(final AccountEntity entityToDelete) {
        repo.deleteAccountCredential(entityToDelete);
        nameAndPlatform.unregister(entityToDelete.getAccName(), entityToDelete.getPlatform());
    }

    @Override
    public void updateAccountCredential(final AccountEntity originalEntity, final AccountEntity updatedEntity) {
        if(originalEntity.equals(updatedEntity)) {
            throw new ValidationException("No changes made to save");
        }
        repo.updateAccountCredential(updatedEntity);
        nameAndPlatform.unregister(originalEntity.getAccName(), originalEntity.getPlatform());
        nameAndPlatform.register(updatedEntity.getAccName(), updatedEntity.getPlatform());
    }

    @Override
    public List<AccountEntity> getAllAccountCredential() {
        return repo.getAllAccountCredential();
    }

}
