package org.ajikhoji.passwordmanager.util;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.ajikhoji.passwordmanager.AppStartup;
import org.ajikhoji.passwordmanager.config.AppConfig;
import org.ajikhoji.passwordmanager.config.AppResources;
import org.ajikhoji.passwordmanager.config.DbConfig;
import org.ajikhoji.passwordmanager.config.DbHandler;
import org.ajikhoji.passwordmanager.dto.AccountWithCustomFields;
import org.ajikhoji.passwordmanager.dto.ImportAnalyzeResult;
import org.ajikhoji.passwordmanager.model.AccountCustomFieldEntity;
import org.ajikhoji.passwordmanager.model.AccountEntity;
import org.ajikhoji.passwordmanager.repository.TableFieldsPreferenceRememberable;
import org.ajikhoji.passwordmanager.service.CsvExportService;
import org.ajikhoji.passwordmanager.ui_components.ToggleableTextField;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.ajikhoji.passwordmanager.util.ClipboardCopyUtil.copyText;

public final class Utility {

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

    public static LocalDateTime getLocalDateTime(final String dateTimeString) {
        try {
            return LocalDateTime.parse(dateTimeString, dtf);
        } catch (final Exception e) {
            return null;
        }
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

    public static void exportAllCredentialDataAsCsv(final String filePath) {
        try {
            final List<AccountEntity> allAccounts = DbConfig.getAccountService().getAllAccountCredential();
            final List<AccountWithCustomFields> info = new ArrayList<>(allAccounts.size());

            for(final AccountEntity accountEntity : allAccounts) {
                final List<AccountCustomFieldEntity> allCustomFields = DbConfig.getAccountCustomFieldService().getAccountCustomFieldsForAccountId(accountEntity.getAccId());
                info.add(new AccountWithCustomFields(accountEntity, allCustomFields));
            }

            new CsvExportService().export(info, new File(filePath).toPath());
        } catch (final Exception e) {
            throw new RuntimeException(String.format("Export operation aborted: %s", e.getMessage()));
        }
    }

    public static void setupAppTitleBar() {
        final HBox hbxTitleBar = AppConfig.getAppFrame().getTitleBar();
        if(hbxTitleBar.getChildren().isEmpty()) {
            hbxTitleBar.setStyle("-fx-padding: 0 0 0 10px;");
            hbxTitleBar.getChildren().clear();
            hbxTitleBar.setAlignment(Pos.CENTER_LEFT);
            final Label lblAppTitle = new Label(AppConfig.getAppName());
            lblAppTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 0 0 0 4px;");
            final ImageView ivAppIcon = new ImageView(AppConfig.getAppResources().imgAppIcon);
            ivAppIcon.setSmooth(true);
            ivAppIcon.setFitHeight(30.0D);
            ivAppIcon.setPreserveRatio(true);
            hbxTitleBar.getChildren().addAll(ivAppIcon, lblAppTitle);
            AppConfig.getPrimaryStage().setTitle(lblAppTitle.getText());
        }
    }

    public static void deleteRecursively(Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static void resetApplication() {
        try {
            DbConfig.closeDb();
            deleteRecursively(Path.of(DbHandler.STR_DATABASE_PATH));
            AppStartup.initApp();
        } catch (final Exception e) {
            Utility.showErrorAlert("Reset failed", "Internal error occurred");
        }
    }

}
