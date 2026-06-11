package org.ajikhoji.passwordmanager.util;

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
import org.ajikhoji.passwordmanager.repository.TableFieldsPreferenceRememberable;
import org.ajikhoji.passwordmanager.ui_components.ToggleableTextField;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

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
                    btn.getStyleClass().add("btn-table");
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

    private final static Clipboard cb = Clipboard.getSystemClipboard();
    public static void copyText(final String str) {
        final Map<DataFormat, Object> copyMap = new HashMap<>();
        copyMap.put(DataFormat.PLAIN_TEXT, str);
        cb.setContent(copyMap);
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
        final Node n = fieldName.contains("Password") || fieldName.contains("password") ? new ToggleableTextField() : new TextField();
        final StringProperty errorMessage = new SimpleStringProperty("");
        final Label lblLength = new Label("0 / " + maxLength);
        StringProperty textProperty = null;
        if(n instanceof TextField tf) {
            if(placeHolder != null) {
                tf.setPromptText(placeHolder);
            }
            textProperty = tf.textProperty();
            tf.setPrefColumnCount(maxLength);
            tf.setMaxWidth(Region.USE_PREF_SIZE);
        } else if (n instanceof ToggleableTextField ttf) {
            textProperty = ttf.getTextProperty();
        }
        final StringProperty tp = textProperty;
        tp.addListener((ol, ov, nv) -> {
            if (nv == null) {
                lblLength.setText("0 / " + maxLength);
                return;
            }
            errorMessage.set("");
            if (nv.length() > maxLength) {
                tp.set(ov);
                return;
            }
            lblLength.setText(nv.length() + " / " + maxLength);
        });

        final Label lblErrorReason = new Label();
        lblErrorReason.setStyle("-fx-text-fill: #D40F37; -fx-background-color: #240309; -fx-padding: 4px;");
        final VBox v = new VBox(6.0D, lbl, n, lblLength);
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
        if(n instanceof TextField tf) {
            return new EntryField(tf.textProperty(), errorMessage);
        }
        final ToggleableTextField ttf = (ToggleableTextField) n;
        return new EntryField(ttf.getTextProperty(), errorMessage);
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

}
