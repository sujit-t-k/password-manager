package org.ajikhoji.passwordmanager.service;

import org.ajikhoji.passwordmanager.dto.LabelUsage;
import org.ajikhoji.passwordmanager.model.LabelEntity;

import java.util.List;

public interface LabelService {

    LabelEntity addNewLabel(final String labelName);
    void updateLabel(final LabelEntity entity);
    void deleteLabel(final LabelEntity entityToDelete, final LabelEntity replacementLabel);
    List<LabelEntity> getAllLabels();
    List<LabelEntity> getUsedLabels();
    List<LabelUsage> getLabelUsageStatistics();
    LabelEntity getLabelEntityById(final long id);
    LabelEntity getLabelEntityByName(final String labelValue);

}
