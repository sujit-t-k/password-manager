package org.ajikhoji.passwordmanager.dto;

import org.ajikhoji.passwordmanager.config.AppConfig;
import org.ajikhoji.passwordmanager.config.DbConfig;
import org.ajikhoji.passwordmanager.model.AccountCustomFieldEntity;
import org.ajikhoji.passwordmanager.model.AccountEntity;
import org.ajikhoji.passwordmanager.security.EncryptionService;
import org.ajikhoji.passwordmanager.service.CsvExportService;
import org.ajikhoji.passwordmanager.util.Utility;

import java.util.List;

public final class AccountWithCustomFields {

    private final AccountEntity accountEntity;
    private final List<AccountCustomFieldEntity> customFields;

    public AccountWithCustomFields(final AccountEntity ae, final List<AccountCustomFieldEntity> cf) {
        accountEntity = ae;
        customFields = cf;
    }

    public int getCustomFieldsCount() {
        return customFields.size();
    }

    public String[] getAsCsvRowData() {
        final EncryptionService encryptionService = AppConfig.getEncryptionService();

        final String[] row = new String[CsvExportService.PRIMARY_FIELDS_EXPORT_COUNT + (getCustomFieldsCount() << 1)];
        row[0] = accountEntity.getAccName();
        row[1] = encryptionService.decrypt(accountEntity.getAccPassword());
        row[2] = accountEntity.getPlatform();
        row[3] = DbConfig.getLabelService().getLabelEntityById(accountEntity.getLabelId()).getLabelName();
        row[4] = CsvExportService.getFormattedString(accountEntity.getLink());
        row[5] = Utility.getFormatedDateTimeString(accountEntity.getCreatedDateTime());
        row[6] = CsvExportService.getFormattedString(Utility.getFormatedDateTimeString(accountEntity.getRecentUpdateDateTime()));
        for(int i = 0; i < getCustomFieldsCount(); ++i) {
            row[CsvExportService.PRIMARY_FIELDS_EXPORT_COUNT + (i << 1)] = customFields.get(i).getFieldName();
            row[CsvExportService.PRIMARY_FIELDS_EXPORT_COUNT + (i << 1) + 1] = encryptionService.decrypt(customFields.get(i).getFieldValue());
        }

        return row;
    }

    public AccountEntity getAccountEntity() {
        return accountEntity;
    }

    public List<AccountCustomFieldEntity> getCustomFields() {
        return customFields;
    }

    @Override
    public String toString() {
        return String.format("AccountWithCustomFields(%s, %s)", accountEntity, customFields);
    }

}
