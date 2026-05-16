package org.ajikhoji.passwordmanager.service;

import org.ajikhoji.passwordmanager.model.AccountCustomFieldEntity;
import org.ajikhoji.passwordmanager.repository.AccountCustomFieldRepo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountCustomFieldServiceImpl implements AccountCustomFieldService {

    private final AccountCustomFieldRepo repo;

    public AccountCustomFieldServiceImpl(final AccountCustomFieldRepo repo) {
        this.repo = repo;
    }

    @Override
    public void commit(final long accId, final List<AccountCustomFieldEntity> entitiesRetained) {
        final List<AccountCustomFieldEntity> oldEntities = this.getAccountCustomFieldsForAccountId(accId);

        final Map<String, AccountCustomFieldEntity> originalMap = new HashMap<>();
        for (AccountCustomFieldEntity e : oldEntities) {
            originalMap.put(e.getFieldName(), e);
        }

        final Map<String, AccountCustomFieldEntity> retainedMap = new HashMap<>();
        for (AccountCustomFieldEntity e : entitiesRetained) {
            retainedMap.put(e.getFieldName(), e);
        }

        final List<AccountCustomFieldEntity> toInsert = new ArrayList<>();
        final List<AccountCustomFieldEntity> toUpdate = new ArrayList<>();
        final List<AccountCustomFieldEntity> toDelete = new ArrayList<>();

        for (final Map.Entry<String, AccountCustomFieldEntity> entry : retainedMap.entrySet()) {
            final String fieldName = entry.getKey();
            final AccountCustomFieldEntity newEntity = entry.getValue();
            final AccountCustomFieldEntity oldEntity = originalMap.get(fieldName);

            if (oldEntity == null) {//if not available on old entries. then this is new => has to be inserted in DB
                toInsert.add(newEntity);
            } else if(!oldEntity.equals(newEntity)) {//if key exists but value doesn't match, then it has to be updated
                toUpdate.add(newEntity);
            }
        }

        for (final Map.Entry<String, AccountCustomFieldEntity> entry : originalMap.entrySet()) {
            final String fieldName = entry.getKey();
            if (!retainedMap.containsKey(fieldName)) {//if old key nor present in retained set, then this record has to be deleted
                toDelete.add(entry.getValue());
            }
        }

        if (!toDelete.isEmpty()) {
            repo.batchDeleteAccountCustomFields(accId, toDelete);
        }
        if (!toUpdate.isEmpty()) {
            repo.batchUpdateAccountCustomFields(accId, toUpdate);
        }
        if (!toInsert.isEmpty()) {
            repo.batchInsertAccountCustomFields(accId, toInsert);
        }
    }

    @Override
    public List<AccountCustomFieldEntity> getAccountCustomFieldsForAccountId(final long accId) {
        return repo.getAccountCustomFieldsForAccountId(accId);
    }

}
