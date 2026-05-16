package org.ajikhoji.passwordmanager.repository;

import org.ajikhoji.passwordmanager.model.AccountCustomFieldEntity;

import java.util.List;

public interface AccountCustomFieldRepo {

    void addNewAccountCustomField(AccountCustomFieldEntity newEntity);
    void batchInsertAccountCustomFields(long accId, List<AccountCustomFieldEntity> entities);
    void deleteAccountCustomField(AccountCustomFieldEntity entityToDelete);
    void batchDeleteAccountCustomFields(long accId, List<AccountCustomFieldEntity> entities);
    List<AccountCustomFieldEntity> getAccountCustomFieldsForAccountId(long accId);
    void updateAccountCustomField(AccountCustomFieldEntity updatedEntity);
    void batchUpdateAccountCustomFields(long accId, List<AccountCustomFieldEntity> entities);

}
