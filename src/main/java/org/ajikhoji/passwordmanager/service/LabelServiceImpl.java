package org.ajikhoji.passwordmanager.service;

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
    public void deleteLabel(final LabelEntity entityToDelete) {
        final LabelEntity toDelete = idToEntity.get(entityToDelete.getLabelId());
        if(toDelete == null || !toDelete.equals(entityToDelete)) {
            throw new ValidationException(String.format("Label with id %d and info %s does not exists", entityToDelete.getLabelId(), entityToDelete.getLabelName()));
        }
        repo.deleteLabel(entityToDelete);
        idAndName.remove(entityToDelete.getLabelId(), entityToDelete.getLabelName());
    }

    @Override
    public List<LabelEntity> getAllLabels() {
        return repo.getAllLabels();
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
