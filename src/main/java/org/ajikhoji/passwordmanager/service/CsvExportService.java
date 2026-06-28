package org.ajikhoji.passwordmanager.service;


import com.opencsv.CSVWriter;
import org.ajikhoji.passwordmanager.dto.AccountWithCustomFields;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

public final class CsvExportService implements ExportService<AccountWithCustomFields> {

    public static final String
        accIdHeaderTitle = "Account ID/Name",
        passwordHeaderTitle = "Password",
        platformHeaderTitle = "Platform",
        labelHeaderTitle = "Label",
        linkHeaderTitle = "Link",
        addedDateTitle = "Added",
        lastModifiedTitle = "Last modified",
        nullField = "-";
    public static final int PRIMARY_FIELDS_EXPORT_COUNT = 7;

    @Override
    public void export(final Collection<AccountWithCustomFields> data, final Path destination) {
        int maxCustomFields = 0;
        for(final AccountWithCustomFields row : data) {
            maxCustomFields = Math.max(maxCustomFields, row.getCustomFieldsCount());
        }

        try {
            final File file = destination.toFile();
            final CSVWriter writer = new CSVWriter(new FileWriter(file));
            final String[] header = new String[PRIMARY_FIELDS_EXPORT_COUNT + (maxCustomFields << 1)];
            header[0] = accIdHeaderTitle;
            header[1] = passwordHeaderTitle;
            header[2] = platformHeaderTitle;
            header[3] = labelHeaderTitle;
            header[4] = linkHeaderTitle;
            header[5] = addedDateTitle;
            header[6] = lastModifiedTitle;
            for (int i = 0; i < maxCustomFields; ++i) {
                header[PRIMARY_FIELDS_EXPORT_COUNT + (i << 1)] = String.format("Custom Field Name %d", (i + 1));
                header[PRIMARY_FIELDS_EXPORT_COUNT + (i << 1) + 1] = String.format("Custom Field Value %d", (i + 1));
            }
            writer.writeNext(header);

            for (final AccountWithCustomFields row : data) {
                writer.writeNext(row.getAsCsvRowData());
            }

            writer.flush();
            writer.close();
        } catch (final IOException e) {
            throw new RuntimeException("Failed to initiate writer");
        } catch (final Exception e) {
            throw new RuntimeException("Data export operation failed");
        }
    }

    public static String getFormattedString(final String str) {
        if(str == null || str.equals("NULL")) {
            return nullField;
        }
        return str;
    }

}
