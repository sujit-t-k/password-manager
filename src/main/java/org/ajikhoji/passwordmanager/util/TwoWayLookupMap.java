package org.ajikhoji.passwordmanager.util;

import java.util.HashMap;
import java.util.Map;

/*
 * This data structure ensures that each of T and U are unique and not null.
 * It is not that T and U should be different but rather set of all T values should be unique
 * and same follows to that of U values.
 * Apart from uniqueness, this data structure provides  O(1) look-up methods
 * which can be used to retrieve corresponding value of their associated pair.
 * Technically, U can be looked-up in O(1) by using T and the reverse is also possible.
 */
public class TwoWayLookupMap<T, U> {

    private final Map<T, U> firstTypeKeyToSecondTypeKey;
    private final Map<U, T> secondTypeKeyToFirstTypeKey;

    public TwoWayLookupMap() {
        firstTypeKeyToSecondTypeKey = new HashMap<>();
        secondTypeKeyToFirstTypeKey = new HashMap<>();
    }

    public U getSecondValueByFirstKey(final T firstKey) {
        return firstTypeKeyToSecondTypeKey.get(firstKey);
    }

    public boolean firstKeyExists(final T keyOne) {
        return getSecondValueByFirstKey(keyOne) != null;
    }

    public T getFirstValueBySecondKey(final U secondKey) {
        return secondTypeKeyToFirstTypeKey.get(secondKey);
    }

    public boolean secondKeyExists(final U keyTwo) {
        return getFirstValueBySecondKey(keyTwo) != null;
    }

    public boolean put(final T keyOne, final U keyTwo) {
        if(keyOne == null || keyTwo == null || firstKeyExists(keyOne) || secondKeyExists(keyTwo)) {
            return false;
        }
        firstTypeKeyToSecondTypeKey.put(keyOne, keyTwo);
        secondTypeKeyToFirstTypeKey.put(keyTwo, keyOne);
        return true;
    }

    public boolean remove(final T keyOne, final U keyTwo) {
        return firstTypeKeyToSecondTypeKey.remove(keyOne, keyTwo) || secondTypeKeyToFirstTypeKey.remove(keyTwo, keyOne);
    }

    public void clear() {
        firstTypeKeyToSecondTypeKey.clear();
        secondTypeKeyToFirstTypeKey.clear();
    }

}
