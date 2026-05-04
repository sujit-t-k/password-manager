package org.ajikhoji.passwordmanager.repository;

import org.ajikhoji.passwordmanager.model.LabelEntity;

import java.util.List;

public interface LabelRepo {

    void addNewLabel(LabelEntity newEntity);
    void deleteLabel(LabelEntity entityToDelete);
    List<LabelEntity> getAllLabels();

}
