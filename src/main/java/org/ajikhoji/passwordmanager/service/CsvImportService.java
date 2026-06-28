package org.ajikhoji.passwordmanager.service;

import com.opencsv.CSVReader;
import org.ajikhoji.passwordmanager.config.AppConfig;
import org.ajikhoji.passwordmanager.config.DbConfig;
import org.ajikhoji.passwordmanager.dto.AccountWithCustomFields;
import org.ajikhoji.passwordmanager.exception.ValidationException;
import org.ajikhoji.passwordmanager.model.AccountCustomFieldEntity;
import org.ajikhoji.passwordmanager.model.AccountEntity;
import org.ajikhoji.passwordmanager.model.LabelEntity;
import org.ajikhoji.passwordmanager.security.EncryptionService;
import org.ajikhoji.passwordmanager.util.Utility;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class CsvImportService implements ImportService<AccountWithCustomFields> {

    @Override
    public List<AccountWithCustomFields> importFrom(final Path path) {
        if(path == null) {
            throw new NullPointerException("Import path shall not be null");
        }

        final File file = new File(path.toUri());
        if(!file.exists()) {
            throw new ValidationException(String.format("File not found: %s", file.getAbsolutePath()));
        }
        if(!file.isFile()) {
            throw new ValidationException("Target is not a file");
        }

        try(final CSVReader reader = new CSVReader(new FileReader(file))) {
            final Iterator<String[]> itr = reader.iterator();
            final EncryptionService encryptionService = AppConfig.getEncryptionService();
            final long unlabelledId = DbConfig.getLabelService().getLabelEntityByName(LabelEntity.DEFAULT_LABEL_NAME).getLabelId();

            if(itr.hasNext()) {
                final String[] header = itr.next();
                if(header.length < CsvExportService.PRIMARY_FIELDS_EXPORT_COUNT ||
                    !(
                        CsvExportService.accIdHeaderTitle.equals(header[0]) &&
                        CsvExportService.passwordHeaderTitle.equals(header[1]) &&
                        CsvExportService.platformHeaderTitle.equals(header[2]) &&
                        CsvExportService.labelHeaderTitle.equals(header[3]) &&
                        CsvExportService.linkHeaderTitle.equals(header[4]) &&
                        CsvExportService.addedDateTitle.equals(header[5]) &&
                        CsvExportService.lastModifiedTitle.equals(header[6])
                    )
                ) {
                    reader.close();
                    throw new RuntimeException("File not in expected format");
                }
                List<AccountWithCustomFields> allAccounts = new ArrayList<>();

                while(itr.hasNext()) {
                    final String[] curr = itr.next();
                    if(curr.length < CsvExportService.PRIMARY_FIELDS_EXPORT_COUNT) {
                        reader.close();
                        throw new RuntimeException("File not in expected format");
                    }
                    final String accName = curr[0];
                    final String plainPass = curr[1];
                    final String platform = curr[2];
                    final String label = curr[3];
                    final String link = getActualValue(curr[4]);
                    final LocalDateTime addedDateTime = Utility.getLocalDateTime(curr[5]);
                    final LocalDateTime recentModifiedDateTime = Utility.getLocalDateTime(getActualValue(curr[6]));

                    final AccountEntity ae = AccountEntity.Builder()
                        .withAccountName(accName)
                        .withEncryptedAccountPassword(encryptionService.encrypt(plainPass))
                        .withAccountPlatform(platform)
                        .withLink(link)
                        .withLabelId(unlabelledId)
                        .withCreatedDateTime(addedDateTime)
                        .withLastUpdatedDateTime(recentModifiedDateTime)
                        .build();

                    final List<AccountCustomFieldEntity> customFields = new ArrayList<>((curr.length - CsvExportService.PRIMARY_FIELDS_EXPORT_COUNT) / 2);
                    for(int i = CsvExportService.PRIMARY_FIELDS_EXPORT_COUNT; i + 1 < curr.length; i += 2) {
                        customFields.add(new AccountCustomFieldEntity(AccountEntity.UNDEFINED_ACCOUNT_ID, curr[i], encryptionService.encrypt(curr[i + 1])));
                    }

                    allAccounts.add(new AccountWithCustomFields(ae, customFields));
                }
                reader.close();
                return allAccounts;
            } else {
                reader.close();
                return Collections.emptyList();
            }
        } catch (final IOException e) {
            throw new ValidationException(String.format("File not found: %s", file.getAbsolutePath()));
        }
    }

    public static String getActualValue(final String val) {
        if(val != null && val.equals(CsvExportService.nullField)) {
            return null;
        }
        return val;
    }

}
