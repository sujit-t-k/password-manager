package org.ajikhoji.passwordmanager.util;

import java.util.*;

/*
 * This data structure is used to maintain uniqueness of value (T + U) combined.
 * In other words, this structure provides O(1) look-up methods such that (T + U) value is always unique.
 * Note that this is not commutative: T + U <> U + T. Also note that this implementation is not thread-safe.
 */
public class TwoLevelLookupMap<T, U> {

    public record Entry<T, U>(T key, U  value) {}

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
        final Set<U> secondLevel = firstLevel.get(levelOneValue);
        if (secondLevel == null) return false;
        boolean removed = secondLevel.remove(levelTwoValue);
        if (secondLevel.isEmpty()) {
            firstLevel.remove(levelOneValue);
        }
        return removed;
    }

    /*
     * Return true if the combined value of (T + U) is present, otherwise returns false.
     */
    public boolean contains(final T levelOneValue, final U levelTwoValue) {
        final Set<U> secondLevel = firstLevel.get(levelOneValue);
        return secondLevel != null && secondLevel.contains(levelTwoValue);
    }

    /*
     * Returns all pairs of (T, U) that are registered
     */
    public Set<Entry<T, U>> getPairSet() {
        final Set<Entry<T, U>> required = new HashSet<>();

        for(final Map.Entry<T, Set<U>> e : firstLevel.entrySet()) {
            for(final U value : e.getValue()) {
                required.add(new Entry<>(e.getKey(), value));
            }
        }

        return required;
    }

    public void clear() {
        for(final Set<U> sets : firstLevel.values()) {
            sets.clear();
        }
        firstLevel.clear();
    }

}
