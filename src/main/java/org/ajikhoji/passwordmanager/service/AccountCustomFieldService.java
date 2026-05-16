package org.ajikhoji.passwordmanager.service;

import org.ajikhoji.passwordmanager.model.AccountCustomFieldEntity;

import java.util.List;

public interface AccountCustomFieldService {

    void commit(long accId, List<AccountCustomFieldEntity> entitiesRetained);
    List<AccountCustomFieldEntity> getAccountCustomFieldsForAccountId(long accId);

}
