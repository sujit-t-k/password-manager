package org.ajikhoji.passwordmanager.service.import_strategy;

//represents various choices applicable to handle conflict records during import operation
public enum ConflictResolutionType {

    IMPORT_LATEST_ONLY,
    REPLACE_EXISTING,
    IMPORT_NEW_ONLY,
    REVIEW_MANUALLY;

}
