package org.ajikhoji.passwordmanager.service;

import org.ajikhoji.passwordmanager.dto.LabelUsage;
import org.ajikhoji.passwordmanager.exception.DatabaseOperationFailureException;
import org.ajikhoji.passwordmanager.exception.EntityNotExistException;
import org.ajikhoji.passwordmanager.exception.ValidationException;
import org.ajikhoji.passwordmanager.model.LabelEntity;
import org.ajikhoji.passwordmanager.repository.LabelRepo;
import org.ajikhoji.passwordmanager.util.TwoWayLookupMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LabelServiceImpl implements LabelService {

    private final Map<Long, LabelEntity> idToEntity;
    private final TwoWayLookupMap<Long, String> idAndName;
    private final LabelRepo repo;

    public LabelServiceImpl(final LabelRepo repo) {
        idToEntity = new HashMap<>();
        idAndName = new TwoWayLookupMap<>();
        this.repo = repo;
        getAllLabels();
    }

    @Override
    public LabelEntity addNewLabel(final String labelName) {
        if(idAndName.secondKeyExists(labelName)) {
            throw new ValidationException(String.format("Label with name '%s' already exists", labelName));
        }
        final LabelEntity newEntity = new LabelEntity(labelName);
        repo.addNewLabel(newEntity);
        idAndName.put(newEntity.getLabelId(), labelName);
        return newEntity;
    }

    @Override
    public void updateLabel(final LabelEntity entity) {
        repo.updateLabel(entity);
        idAndName.remove(entity.getLabelId(), idAndName.getSecondValueByFirstKey(entity.getLabelId()));
        idAndName.put(entity.getLabelId(), entity.getLabelName());
        idToEntity.put(entity.getLabelId(), entity);
    }

    @Override
    public void deleteLabel(final LabelEntity entityToDelete, final LabelEntity replacementLabel) {
        final LabelEntity toDelete = idToEntity.get(entityToDelete.getLabelId());
        if(toDelete == null || !toDelete.equals(entityToDelete)) {
            throw new ValidationException(String.format("Label with id %d and info %s does not exists", entityToDelete.getLabelId(), entityToDelete.getLabelName()));
        }
        repo.deleteLabel(entityToDelete, replacementLabel);
        idAndName.remove(entityToDelete.getLabelId(), entityToDelete.getLabelName());
    }

    @Override
    public List<LabelEntity> getAllLabels() {
        final List<LabelEntity> allLabels = repo.getAllLabels();
        idToEntity.clear();
        idAndName.clear();
        for(final LabelEntity le : allLabels) {
            idToEntity.put(le.getLabelId(), le);
            idAndName.put(le.getLabelId(), le.getLabelName());
        }
        return allLabels;
    }

    @Override
    public List<LabelEntity> getUsedLabels() {
        return repo.getUsedLabels();
    }

    @Override
    public List<LabelUsage> getLabelUsageStatistics() {
        return repo.getLabelUsageStatistics();
    }

    @Override
    public LabelEntity getLabelEntityById(long id) {
        final LabelEntity required = idToEntity.get(id);
        if(required == null) {
            throw new EntityNotExistException(String.format("Label entity with id %s not found", id));
        }
        return required;
    }

    @Override
    public LabelEntity getLabelEntityByName(final String labelValue) {
        final Long id = idAndName.getFirstValueBySecondKey(labelValue);
        if(id == null) {
            throw new EntityNotExistException(String.format("No LabelEntity has label name as %s", labelValue));
        }
        return getLabelEntityById(id);
    }

}
