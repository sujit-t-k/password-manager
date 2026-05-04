package org.ajikhoji.passwordmanager.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/*
 * This data structure is used to maintain uniqueness of value (T + U) combined.
 * In other words, this structure provides O(1) look-up methods such that (T + U) value is always unique.
 * Note that this is not commutative: T + U <> U + T
 */
public class TwoLevelLookupMap<T, U> {

    private final Map<T, Set<U>> firstLevel;

    public TwoLevelLookupMap() {
        firstLevel = new HashMap<>();
    }

    /*
     * Returns false if (T + U) combined value is already registered.
     * Otherwise, returns true as (T + U) combined value is unique this time and it is recorded.
     */
    public boolean register(final T levelOneValue, final U levelTwoValue) {
        firstLevel.putIfAbsent(levelOneValue, new HashSet<>());
        final Set<U> secondLevel = firstLevel.get(levelOneValue);
        return secondLevel.add(levelTwoValue);
    }

    /*
     * Return true if the combined value of (T + U) is present and removes it, otherwise returns false.
     */
    public boolean unregister(final T levelOneValue, final U levelTwoValue) {
        final Set<U> secondLevel = firstLevel.getOrDefault(levelOneValue, new HashSet<>());
        return secondLevel.remove(levelTwoValue);
    }

}
