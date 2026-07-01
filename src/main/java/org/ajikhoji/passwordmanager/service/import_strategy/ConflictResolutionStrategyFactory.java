package org.ajikhoji.passwordmanager.service.import_strategy;

public final class ConflictResolutionStrategyFactory {

    public static ConflictResolutionStrategy create(final ConflictResolutionType requestedType) {
        if(requestedType == null) {
            throw new NullPointerException("Conflict resolution request type cannot be null");
        }
        return switch (requestedType) {
            case IMPORT_NEW_ONLY -> new ImportNewOnlyStrategy();
            case IMPORT_LATEST_ONLY -> new ImportLatestOnlyStrategy();
            case REPLACE_EXISTING -> new ReplaceExistingStrategy();
            default -> throw new RuntimeException(String.format("Cannot resolve conflict resolution strategy for requested type: %s", requestedType.name()));
        };
    }

}
