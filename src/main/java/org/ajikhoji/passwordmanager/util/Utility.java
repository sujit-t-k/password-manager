package org.ajikhoji.passwordmanager.util;

import com.opencsv.CSVWriter;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.ajikhoji.passwordmanager.config.AppConfig;
import org.ajikhoji.passwordmanager.config.AppResources;
import org.ajikhoji.passwordmanager.config.DbConfig;
import org.ajikhoji.passwordmanager.exception.DatabaseOperationFailureException;
import org.ajikhoji.passwordmanager.model.AccountCustomFieldEntity;
import org.ajikhoji.passwordmanager.model.AccountEntity;
import org.ajikhoji.passwordmanager.repository.TableFieldsPreferenceRememberable;
import org.ajikhoji.passwordmanager.security.EncryptionService;
import org.ajikhoji.passwordmanager.ui_components.ToggleableTextField;

import java.io.File;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.ajikhoji.passwordmanager.util.ClipboardCopyUtil.copyText;

public class Utility {

    private Utility() {}

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    public static String getFormatedDateTimeString(final LocalDateTime ldt) {
        if(ldt == null) {
            return "NULL";
        }
        return dtf.format(ldt);
    }

    public static Timestamp getSqlTimeStampForLocalDateTime(final LocalDateTime ldt) {
        if(ldt == null) {
            return null;
        }
        return Timestamp.valueOf(ldt);
    }

    public static boolean isSameValuedObject(final Object obj1, final Object obj2) {
        if(obj1 == null) {
            return obj2 == null;
        } else if(obj2 == null) {
            return false;
        }
        return obj1.equals(obj2);
    }

    public static boolean isSameDateTimeValue(final LocalDateTime ldt1, final LocalDateTime ldt2) {
        if(ldt1 == null) {
            return ldt2 == null;
        } else if(ldt2 == null) {
            return false;
        }
        return ldt1.isEqual(ldt2);
    }

    public static void showErrorAlert(final String title, final String content) {
        final Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setContentText(content);
        a.show();
    }

    public static void showInformationAlert(final String title, final String content) {
        final Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setContentText(content);
        a.show();
    }

    public static <T> TableColumn<T, String> getCopyableTableColumn(final String tableHeader, final String mapToField) {
        final TableColumn<T, String> tc = new TableColumn<>(tableHeader);
        final AppResources ar = AppConfig.getAppResources();
        tc.setCellValueFactory(new PropertyValueFactory<>(mapToField));
        tc.setCellFactory(cellFactory -> new TableCell<>() {
            @Override
            public void updateItem(final String str, final boolean empty) {
            if(empty || str == null || str.isEmpty()) {
                setText(getIndex() < getTableView().getItems().size() ? "-" : null);
            } else {
                final ImageView imgViewCopy = new ImageView(ar.imgCopy);
                imgViewCopy.setFitHeight(20.0D);
                imgViewCopy.setFitWidth(20.0D);
                final Button btn = new Button("", imgViewCopy);
                btn.getStyleClass().add("btn-copy");
                btn.setPrefSize(24.0D, 24.0D);
                btn.setMaxSize(24.0D, 24.0D);
                final Tooltip tp = new Tooltip("Copy to clipboard");
                final Tooltip tpDone = new Tooltip("Copied!");
                Tooltip.install(btn, tp);
                btn.setOnAction(e -> {
                    copyText(str);
                    imgViewCopy.setImage(ar.imgCopied);
                    btn.setGraphic(imgViewCopy);
                    Tooltip.uninstall(btn, tp);
                    Tooltip.install(btn, tpDone);
                });
                setOnMouseEntered(e -> {
                    if(getGraphic() != null) {
                        imgViewCopy.setImage(ar.imgCopy);
                        btn.setGraphic(imgViewCopy);
                        Tooltip.uninstall(btn, tpDone);
                        Tooltip.install(btn, tp);
                        setGraphic(null);
                    }
                });
                setOnMouseExited(e -> {
                    imgViewCopy.setImage(ar.imgCopy);
                    btn.setGraphic(imgViewCopy);
                    Tooltip.uninstall(btn, tpDone);
                    Tooltip.install(btn, tp);
                    setGraphic(null);
                });
                setOnMouseEntered(e -> {
                    setGraphic(btn);
                });
                setText(str);
            }
            }
        });
        return tc;
    }

    public static <T> void autoFitColumnWidth(TableColumn<T, String> column) {
        Platform.runLater(() -> {
            double maxWidth = computeTextWidth(column.getText().repeat(2), Font.getDefault()) * 0.7D + 20.0;

            for (T item : column.getTableView().getItems()) {
                if (item != null) {
                    String cellData = column.getCellData(item);
                    if (cellData != null) {
                        double cellWidth = computeTextWidth(cellData.repeat(2), Font.getDefault()) * 0.7D + 25.0;
                        if (cellWidth > maxWidth) {
                            maxWidth = cellWidth;
                        }
                    }
                }
            }

            column.setPrefWidth(maxWidth);
        });
    }

    public static double computeTextWidth(String text, Font font) {
        final Text helper = new Text(text);
        helper.setFont(font);
        return helper.getLayoutBounds().getWidth();
    }

    public record EntryField(StringProperty textProperty, StringProperty errorMessageProperty) {}

    public static EntryField addLabeledTextField(final String fieldName, final int maxLength, final Pane parent) {
        return addLabeledTextField(fieldName, null, maxLength, parent);
    }

    public static EntryField addLabeledTextField(final String fieldName, final String placeHolder, final int maxLength, final Pane parent) {
        final Label lbl = new Label(fieldName);
        final TextField tf = new TextField();
        final StringProperty errorMessage = new SimpleStringProperty("");
        final Label lblLength = new Label("0 / " + maxLength);
        if(placeHolder != null) {
            tf.setPromptText(placeHolder);
        }
        tf.setPrefColumnCount(maxLength);
        tf.setMaxWidth(Region.USE_PREF_SIZE);

        final StringProperty tp = tf.textProperty();
        tp.addListener((ol, ov, nv) -> {
            if (nv == null) {
                lblLength.setText("0 / " + maxLength);
                return;
            }
            errorMessage.set("");
            if (nv.length() > maxLength) {
                tp.set(nv.substring(0, maxLength));
                return;
            }
            lblLength.setText(nv.length() + " / " + maxLength);
        });

        final Label lblErrorReason = new Label();
        lblErrorReason.setStyle("-fx-text-fill: #D40F37; -fx-background-color: #240309; -fx-padding: 4px;");
        final VBox v = new VBox(6.0D, lbl, tf, lblLength);
        errorMessage.addListener((ol, ov, nv) -> {
            if (nv == null || nv.isBlank()) {
                v.getChildren().remove(lblErrorReason);
            } else {
                lblErrorReason.setText(errorMessage.getValue());
                if (!v.getChildren().contains(lblErrorReason)) {
                    v.getChildren().add(lblErrorReason);
                }
            }
        });
        parent.getChildren().add(v);

        return new EntryField(tp, errorMessage);
    }

    public static EntryField addLabeledToggleablePasswordField(final String fieldName, final int maxLength, final Pane parent) {
        return addLabeledToggleablePasswordField(fieldName, maxLength, false, parent);
    }

    public static EntryField addLabeledToggleablePasswordField(final String fieldName, final int maxLength, final boolean pasteOptionDisabled, final Pane parent) {
        final Label lbl = new Label(fieldName);
        final ToggleableTextField ttf = new ToggleableTextField(pasteOptionDisabled);
        final StringProperty errorMessage = new SimpleStringProperty("");
        final Label lblLength = new Label("0 / " + maxLength);
        ttf.setMaxLength(maxLength);

        final StringProperty tp = ttf.getTextProperty();
        tp.addListener((ol, ov, nv) -> {
            if (nv == null) {
                lblLength.setText("0 / " + maxLength);
                return;
            }
            errorMessage.set("");
            if (nv.length() > maxLength) {
                tp.set(nv.substring(0, maxLength));
                return;
            }
            lblLength.setText(nv.length() + " / " + maxLength);
        });

        final Label lblErrorReason = new Label();
        lblErrorReason.setStyle("-fx-text-fill: #D40F37; -fx-background-color: #240309; -fx-padding: 4px;");
        final VBox v = new VBox(6.0D, lbl, ttf, lblLength);
        errorMessage.addListener((ol, ov, nv) -> {
            if (nv == null || nv.isBlank()) {
                v.getChildren().remove(lblErrorReason);
            } else {
                lblErrorReason.setText(errorMessage.getValue());
                if (!v.getChildren().contains(lblErrorReason)) {
                    v.getChildren().add(lblErrorReason);
                }
            }
        });
        parent.getChildren().add(v);

        return new EntryField(tp, errorMessage);
    }

    public static boolean isFieldPresent(final int fieldNumber, long order) {
        int fieldsCount = TableFieldsPreferenceRememberable.getFieldsCount(order);
        while(fieldsCount-- > 0) {
            if(order % 10 == fieldNumber) {
                return true;
            }
            order /= 10;
        }
        return false;
    }

    public static long addField(final int fieldNumber, final long order) {
        final int fieldsCount = TableFieldsPreferenceRememberable.getFieldsCount(order);
        final long includedFieldOrder = order * 10 + fieldNumber;
        return TableFieldsPreferenceRememberable.getUpdatedFieldsCount(includedFieldOrder, fieldsCount + 1);
    }

    public static long removeField(final int fieldNumber, long order) {
        int fieldsCount = TableFieldsPreferenceRememberable.getFieldsCount(order);

        long newOrderReversed = 0;
        int retainedCount = 0;
        while(fieldsCount-- > 0) {
            final int d = (int) (order % 10);
            if(d != fieldNumber) {
                newOrderReversed = newOrderReversed * 10 + d;
                ++retainedCount;
            }
            order /= 10;
        }
        final long newOrder = reverse(newOrderReversed);
        return TableFieldsPreferenceRememberable.getUpdatedFieldsCount(newOrder, retainedCount);
    }

    public static long reverse(long num) {
        long res = 0;
        while(num > 0) {
            res = (res * 10) + (num % 10);
            num /= 10;
        }
        return res;
    }

    public static long getFieldOrderWithoutCount(final long order) {
        return order % 10_000_000_000L;
    }

    public static void exportAllCredentialDataAsCsv(final String filePath, final ProgressBar pb) {
        pb.setProgress(0.0D);

        final EncryptionService encryptionService = AppConfig.getEncryptionService();
        final Map<Long, String> labelIdToLabelName = new HashMap<>();
        final Function<Long, String> LabelName = id -> {
            String labelName = labelIdToLabelName.get(id);
            if(labelName == null) {
                labelName = DbConfig.getLabelService().getLabelEntityById(id).getLabelName();
                labelIdToLabelName.put(id, labelName);
            }
            return labelName;
        };

        final class AccountWithCustomFields {
            final AccountEntity accountEntity;
            final List<AccountCustomFieldEntity> customFields;

            public AccountWithCustomFields(final AccountEntity ae, final List<AccountCustomFieldEntity> cf) {
                accountEntity = ae;
                customFields = cf;
            }

            int getCustomFieldsCount() {
                return customFields.size();
            }

            String[] getAsCsvRowData() {
                final String[] row = new String[5 + (getCustomFieldsCount() << 1)];
                row[0] = accountEntity.getAccName();
                row[1] = encryptionService.decrypt(accountEntity.getAccPassword());
                row[2] = accountEntity.getPlatform();
                row[3] = LabelName.apply(accountEntity.getLabelId());
                row[4] = accountEntity.getLink();
                for(int i = 0; i < getCustomFieldsCount(); ++i) {
                    row[5 + (i << 1)] = customFields.get(i).getFieldName();
                    row[5 + (i << 1) + 1] = encryptionService.decrypt(customFields.get(i).getFieldValue());
                }
                return row;
            }

        }

        try {
            final List<AccountEntity> allAccounts = DbConfig.getAccountService().getAllAccountCredential();
            final AccountWithCustomFields[] info = new AccountWithCustomFields[allAccounts.size()];
            final double maxProgressForDataLoad = 0.25D;
            int maxCustomFields = 0;

            for(int i = 0; i < info.length; ++i) {
                final List<AccountCustomFieldEntity> allCustomFields = DbConfig.getAccountCustomFieldService().getAccountCustomFieldsForAccountId(allAccounts.get(i).getAccId());
                info[i] = new AccountWithCustomFields(allAccounts.get(i), allCustomFields);
                pb.setProgress((maxProgressForDataLoad * (i + 1)) / allAccounts.size());
                maxCustomFields = Math.max(maxCustomFields, info[i].getCustomFieldsCount());
            }

            final CSVWriter writer = new CSVWriter(new FileWriter(filePath));
            final String[] header = new String[5 + (maxCustomFields << 1)];
            header[0] = "Account ID/Name";
            header[1] = "Password";
            header[2] = "Platform";
            header[3] = "Label";
            header[4] = "Link";
            for(int i = 0; i < maxCustomFields; ++i) {
                header[5 + (i << 1)] = String.format("Custom Field Name %d", (i + 1));
                header[5 + (i << 1) + 1] = String.format("Custom Field Value %d", (i + 1));
            }
            writer.writeNext(header);

            final double maxProgressForDataWrite = 0.9D;
            final double progressLength = maxProgressForDataWrite - maxProgressForDataLoad;
            for (int i = 0; i < info.length; ++i) {
                writer.writeNext(info[i].getAsCsvRowData());
                pb.setProgress(maxProgressForDataLoad + ((progressLength * (i + 1)) / info.length));
            }

            writer.flush();
            writer.close();
            pb.setProgress(1.0D);
        } catch (final Exception e) {
            throw new RuntimeException(String.format("Export operation aborted: %s", e.getMessage()));
        }
    }

}
