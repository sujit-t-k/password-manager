package org.ajikhoji.passwordmanager.service.import_strategy;

import org.ajikhoji.passwordmanager.dto.ImportAnalyzeResult;

public interface ConflictResolutionStrategy {

    void resolve(ImportAnalyzeResult result);

}
