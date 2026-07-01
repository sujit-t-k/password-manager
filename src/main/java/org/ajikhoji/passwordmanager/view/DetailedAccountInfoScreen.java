package org.ajikhoji.passwordmanager.view;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.ajikhoji.passwordmanager.Launcher;
import org.ajikhoji.passwordmanager.config.AppConfig;
import org.ajikhoji.passwordmanager.config.AppResources;
import org.ajikhoji.passwordmanager.config.DbConfig;
import org.ajikhoji.passwordmanager.dto.AccountWithCustomFields;
import org.ajikhoji.passwordmanager.model.AccountCustomFieldEntity;
import org.ajikhoji.passwordmanager.model.AccountEntity;
import org.ajikhoji.passwordmanager.security.EncryptionService;
import org.ajikhoji.passwordmanager.util.Utility;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.ajikhoji.passwordmanager.util.ClipboardCopyUtil.copyText;

public class DetailedAccountInfoScreen {

    public static void show(final AccountEntity info, final List<AccountCustomFieldEntity> customFields) {
        new DetailedAccountInfoScreen(info, customFields);
    }

    public static GridPane[] getDetailedAccountInfoView(final AccountWithCustomFields existing, final AccountWithCustomFields imported) {
        final GridPane existingStat = getBaseGridPane();
        final GridPane importedStat = getBaseGridPane();

        final BiConsumer<String, GridPane> Title = (sectionTitle, parent) -> {
            final int rowsFilled = parent.getRowCount();
            final Label lblTitle = new Label(sectionTitle);
            lblTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            lblTitle.setAlignment(Pos.CENTER);
            lblTitle.setTextAlignment(TextAlignment.CENTER);

            final HBox hbxTitle = new HBox(lblTitle);
            parent.add(hbxTitle, 0, rowsFilled, 2, 1);
        };

        final AccountEntity existingAccount = existing.getAccountEntity();
        final AccountEntity importedAccount = imported.getAccountEntity();
        final EncryptionService encryptionService = AppConfig.getEncryptionService();

        Title.accept("Primary Account Info", existingStat);
        Title.accept("Primary Account Info", importedStat);

        addField(
 "Account Name/ID:", existingAccount.getAccName(), existingStat,
"Account Name/ID:", importedAccount.getAccName(), importedStat
        );
        addField(
"Password:", encryptionService.decrypt(existingAccount.getAccPassword()), existingStat,
"Password:", encryptionService.decrypt(importedAccount.getAccPassword()), importedStat
        );
        addField(
"Platform:", existingAccount.getPlatform(), existingStat,
"Platform:", importedAccount.getPlatform(), importedStat
        );
        addField(
"Link:", existingAccount.getLink(), existingStat,
"Link:", importedAccount.getLink(), importedStat
        );

        final Map<String, String> existingCustomFields = new HashMap<>();
        for(final AccountCustomFieldEntity customField : existing.getCustomFields()) {
            existingCustomFields.put(customField.getFieldName(), encryptionService.decrypt(customField.getFieldValue()));
        }

        final Map<String, String> importedCustomFields = new HashMap<>();
        final Set<String> commonCustomFields = new HashSet<>();
        for(final AccountCustomFieldEntity customField : imported.getCustomFields()) {
            importedCustomFields.put(customField.getFieldName(), encryptionService.decrypt(customField.getFieldValue()));
            if(existingCustomFields.containsKey(customField.getFieldName())) {
                commonCustomFields.add(customField.getFieldName());
            }
        }

        Title.accept(String.format("%sAdditional Account Info", existingCustomFields.isEmpty() ? "No " : ""), existingStat);
        Title.accept(String.format("%sAdditional Account Info", importedCustomFields.isEmpty() ? "No " : ""), importedStat);

        //add common fields first (they may have minimal difference)
        for(final String commonCustomField : commonCustomFields) {
            final String existingValue = existingCustomFields.get(commonCustomField);
            final String importedValue = importedCustomFields.get(commonCustomField);

            addField(commonCustomField, existingValue,existingStat, commonCustomField, importedValue, importedStat);
            importedCustomFields.remove(commonCustomField);
            existingCustomFields.remove(commonCustomField);
        }

        final List<Map.Entry<String, String>> remainingExistingCustomFields = new ArrayList<>(existingCustomFields.entrySet());
        final List<Map.Entry<String, String>> remainingImportedCustomFields = new ArrayList<>(importedCustomFields.entrySet());

        //add remaining (non-common) custom fields
        for(int i = 0; i < Math.max(remainingExistingCustomFields.size(), remainingImportedCustomFields.size()); ++i) {
            String fieldNameExisting = "", fieldValueExisting = "", fieldNameImported = "", fieldValueImported = "";

            if(i < remainingExistingCustomFields.size()) {
                fieldNameExisting = remainingExistingCustomFields.get(i).getKey();
                fieldValueExisting = remainingExistingCustomFields.get(i).getValue();
            }
            if(i < remainingImportedCustomFields.size()) {
                fieldNameImported = remainingImportedCustomFields.get(i).getKey();
                fieldValueImported = remainingImportedCustomFields.get(i).getValue();
            }

            addField(fieldNameExisting, fieldValueExisting, existingStat, fieldNameImported, fieldValueImported, importedStat);
        }

        return new GridPane[]{existingStat, importedStat};
    }

    private static void addField(final String fieldNameExisting, final String fieldValueExisting, final GridPane existingViewer,
                                 final String fieldNameImported, final String fieldValueImported, final GridPane importedViewer) {
        final Label lblFieldNameExisting = new Label(fieldNameExisting);
        final Label lblFieldValueExisting = new Label(fieldValueExisting);
        final Label lblFieldNameImported = new Label(fieldNameImported);
        final Label lblFieldValueImported = new Label(fieldValueImported);

        if(!fieldNameExisting.isBlank()) {
            existingViewer.addRow(existingViewer.getRowCount(), lblFieldNameExisting, lblFieldValueExisting);
        }
        if(!fieldNameImported.isBlank()) {
            importedViewer.addRow(importedViewer.getRowCount(), lblFieldNameImported, lblFieldValueImported);
        }
        if(!fieldNameExisting.equals(fieldNameImported)) {
            highlightDifference(lblFieldNameExisting, lblFieldValueExisting, lblFieldNameImported, lblFieldValueImported);
        } else if (!Utility.isSameValuedObject(fieldValueExisting, fieldValueImported)) {
            highlightDifference(lblFieldValueExisting, lblFieldValueImported);
        }
    }

    private static void highlightDifference(final Label... lbl) {
        for(final Label l : lbl) {
            l.setStyle("-fx-text-fill: #99ffaa;");
        }
    }

    private static GridPane getBaseGridPane() {
        final GridPane gp = new GridPane(10.0D, 20.0D);
        gp.setStyle("-fx-padding: 20px;");
        ColumnConstraints ccField = new ColumnConstraints();
        ccField.setHalignment(HPos.RIGHT);
        gp.getColumnConstraints().add(ccField);
        ColumnConstraints ccValue = new ColumnConstraints();
        ccValue.setHalignment(HPos.LEFT);
        ccValue.setHgrow(Priority.ALWAYS);
        gp.getColumnConstraints().add(ccValue);

        return gp;
    }

    public GridPane getDetailedAccountInfoView(final AccountEntity info, final List<AccountCustomFieldEntity> customFields) {
        final GridPane gp = getBaseGridPane();

        final String noValue = "-";
        final Consumer<String> Title = sectionTitle -> {
            final int rowsFilled = gp.getRowCount();
            final Label lblTitle = new Label(sectionTitle);
            lblTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            lblTitle.setAlignment(Pos.CENTER);
            lblTitle.setTextAlignment(TextAlignment.CENTER);

            final HBox hbxTitle = new HBox(lblTitle);
            gp.add(hbxTitle, 0, rowsFilled, 2, 1);
        };
        final BiConsumer<String, String> FieldInfo = (fieldName, fieldValue) -> {
            final int rowsFilled = gp.getRowCount();
            final Label lblField = new Label(fieldName);
            gp.add(lblField, 0, rowsFilled);

            if(fieldValue == null || fieldValue.equals(noValue) || fieldValue.isBlank()) {
                final Label lblValue = new Label(noValue);
                gp.add(lblValue, 1, rowsFilled);
            } else {
                final HBox hbxField = getCopyableLabel(fieldValue);
                gp.add(hbxField, 1, rowsFilled);
            }
        };

        final EncryptionService encryptionService = AppConfig.getEncryptionService();

        final String addedDate = Utility.getFormatedDateTimeString(info.getCreatedDateTime());
        final String lastModifiedDate = Utility.getFormatedDateTimeString(info.getRecentUpdateDateTime());
        final String recentlyUsedDate = Utility.getFormatedDateTimeString(info.getLastUsedDateTime());

        Title.accept("Primary Account Info");
        FieldInfo.accept("Account Name/ID:", info.getAccName());
        FieldInfo.accept("Password:", encryptionService.decrypt(info.getAccPassword()));
        FieldInfo.accept("Platform:", info.getPlatform());
        FieldInfo.accept("Label:", DbConfig.getLabelService().getLabelEntityById(info.getLabelId()).getLabelName());
        FieldInfo.accept("Link:", info.getLink() == null || info.getLink().isBlank() ? noValue : info.getLink());
        FieldInfo.accept("Added date:", addedDate.equals("NULL") ? noValue : addedDate);
        FieldInfo.accept("Recent modified date:", lastModifiedDate.equals("NULL") ? noValue : lastModifiedDate);
        FieldInfo.accept("Last used date:", recentlyUsedDate.equals("NULL") ? noValue : recentlyUsedDate);

        final HBox hbxSeparator = new HBox();
        hbxSeparator.setPrefHeight(1.25D);
        hbxSeparator.setStyle("-fx-background-color: #333333;");
        gp.add(hbxSeparator, 0, gp.getRowCount(), 2, 1);

        if(customFields.isEmpty()) {
            Title.accept("No additional account information available");
        } else {
            Title.accept("Additional Account Info");
            customFields.forEach(entity -> FieldInfo.accept(entity.getFieldName(), encryptionService.decrypt(entity.getFieldValue())));
        }

        return gp;
    }

    private DetailedAccountInfoScreen(final AccountEntity info, final List<AccountCustomFieldEntity> customFields) {
        final Stage st = new Stage();
        st.setTitle("Viewing account credential");
        st.initOwner(AppConfig.getPrimaryStage());
        st.initModality(Modality.APPLICATION_MODAL);

        final Button btnClose = new Button("Close");
        btnClose.setStyle("-fx-font-size: 16px;");
        btnClose.setOnAction(e -> st.close());
        final HBox hbxControls = new HBox(8.0D, btnClose);
        hbxControls.setStyle("-fx-padding: 10px; -fx-border-width: 1px 0 0 0; -fx-border-stroke: #333333;");
        hbxControls.setAlignment(Pos.CENTER);

        final ScrollPane spContent = new ScrollPane(getDetailedAccountInfoView(info, customFields));
        spContent.setFitToWidth(true);
        spContent.getStyleClass().add("info-scroll");

        final BorderPane bpBase = new BorderPane();
        bpBase.setCenter(spContent);
        bpBase.setBottom(hbxControls);

        final Scene scene = new Scene(bpBase, AppConfig.getScreenWidth() * 0.4D, AppConfig.getScreenHeight() * 0.7D);
        bpBase.getStyleClass().add("pane-primary");
        bpBase.getStylesheets().add(Objects.requireNonNull(Launcher.class.getResource("style/dark-theme.css")).toExternalForm());
        st.setScene(scene);
        st.show();
    }

    private HBox getCopyableLabel(final String text) {
        final AppResources ar = AppConfig.getAppResources();
        final Label lbl = new Label(text);

        final ImageView imgViewCopy = new ImageView(ar.imgCopy);
        imgViewCopy.setFitHeight(20.0D);
        imgViewCopy.setFitWidth(20.0D);
        final Button btn = new Button("", imgViewCopy);
        btn.getStyleClass().add("btn-copy-full-info");

        final HBox hbx = new HBox(6.0D, lbl);
        hbx.setAlignment(Pos.CENTER_LEFT);

        final Tooltip tp = new Tooltip("Copy to clipboard");
        final Tooltip tpDone = new Tooltip("Copied!");
        Tooltip.install(btn, tp);
        btn.setOnAction(e -> {
            copyText(text);
            imgViewCopy.setImage(ar.imgCopied);
            btn.setGraphic(imgViewCopy);
            Tooltip.uninstall(btn, tp);
            Tooltip.install(btn, tpDone);
        });
        hbx.setOnMouseExited(e -> {
            imgViewCopy.setImage(ar.imgCopy);
            btn.setGraphic(imgViewCopy);
            Tooltip.uninstall(btn, tpDone);
            Tooltip.install(btn, tp);
            hbx.getChildren().remove(btn);
        });
        hbx.setOnMouseEntered(e -> {
            if(!hbx.getChildren().contains(btn)) {
                hbx.getChildren().add(0, btn);
            }
        });

        return hbx;
    }

}
