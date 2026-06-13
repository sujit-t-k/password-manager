package org.ajikhoji.passwordmanager.service;

import org.ajikhoji.passwordmanager.model.LabelEntity;

import java.util.List;

public interface LabelService {

    LabelEntity addNewLabel(final String labelName);
    void deleteLabel(final LabelEntity entityToDelete);
    List<LabelEntity> getAllLabels();
    List<LabelEntity> getUsedLabels();
    LabelEntity getLabelEntityById(final long id);
    LabelEntity getLabelEntityByName(final String labelValue);

}
