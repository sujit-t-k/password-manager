package org.ajikhoji.passwordmanager.repository;

import org.ajikhoji.passwordmanager.dto.LabelUsage;
import org.ajikhoji.passwordmanager.model.LabelEntity;

import java.util.List;

public interface LabelRepo {

    void addNewLabel(LabelEntity newEntity);
    void updateLabel(LabelEntity labelEntity);
    void deleteLabel(LabelEntity entityToDelete, LabelEntity replacementLabel);
    List<LabelEntity> getAllLabels();
    List<LabelEntity> getUsedLabels();
    List<LabelUsage> getLabelUsageStatistics();

}
