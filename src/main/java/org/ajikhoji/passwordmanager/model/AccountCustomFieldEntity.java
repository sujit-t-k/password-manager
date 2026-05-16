package org.ajikhoji.passwordmanager.model;

public class AccountCustomFieldEntity {

    private long accId = AccountEntity.UNDEFINED_ACCOUNT_ID;
    private String fieldName;
    private String fieldValue;

    public AccountCustomFieldEntity(long accId, String fieldName, String fieldValue) {
        this.accId = accId;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public long getAccId() {
        return accId;
    }

    public void setAccId(long accId) {
        this.accId = accId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    @Override
    public String toString() {
        return String.format("AccountCustomFieldEntity (account id = %d, field key = %s, field value = %s)", accId, fieldName, fieldValue);
    }

}
