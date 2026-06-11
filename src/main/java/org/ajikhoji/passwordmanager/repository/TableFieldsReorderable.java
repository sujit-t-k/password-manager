package org.ajikhoji.passwordmanager.repository;

public interface TableFieldsReorderable {

    long DEFAULT_TABLE_FIELDS_ORDER = 60000124530L;

    void saveTableFieldsOrderPreference(final long preferenceOrder);
    long getTableFieldsOrder();

    static int getFieldsCount(final long order) {
        return (int) (order / 10_000_000_000L);
    }

    static long getUpdatedFieldsCount(final long order, final int fieldsCount) {
        return (order % 10_000_000_000L) + (fieldsCount * 10_000_000_000L);
    }

}
