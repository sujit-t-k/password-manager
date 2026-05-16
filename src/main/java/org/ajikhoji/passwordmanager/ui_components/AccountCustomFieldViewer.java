package org.ajikhoji.passwordmanager.ui_components;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import org.ajikhoji.passwordmanager.config.AppConfig;
import org.ajikhoji.passwordmanager.config.AppResources;
import org.ajikhoji.passwordmanager.model.AccountCustomFieldEntity;
import org.ajikhoji.passwordmanager.model.AccountEntity;
import org.ajikhoji.passwordmanager.util.Utility;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AccountCustomFieldViewer extends TableView<AccountCustomFieldEntity> {

    final ObservableList<AccountCustomFieldEntity> allFields = FXCollections.observableArrayList();

    public AccountCustomFieldViewer() {
        final AppResources ar = AppConfig.getAppResources();
        final TableColumn<AccountCustomFieldEntity, String> tcFieldName = Utility.getCopyableTableColumn("Name", "fieldName");
        final TableColumn<AccountCustomFieldEntity, String> tcFieldValue = Utility.getCopyableTableColumn("Value", "fieldValue");
        final TableColumn<AccountCustomFieldEntity, Void> tcControls = new TableColumn<>("Actions");

        tcControls.setSortable(false);
        tcFieldValue.setSortable(false);
        tcControls.setPrefWidth(80.0D);
        tcControls.setResizable(false);
        setPlaceholder(new Label("No custom field available to show"));
        setEditable(false);
        setItems(allFields);

        getColumns().add(tcFieldName);
        getColumns().add(tcFieldValue);
        getColumns().add(tcControls);

        tcControls.setCellFactory(cellFactory -> new TableCell<>() {
            private final ImageView ivEdit = new ImageView(ar.imgEdit);
            private final ImageView ivDelete = new ImageView(ar.imgDelete);
            private final Button btnEdit = new Button();
            private final Button btnDelete = new Button();
            private final HBox hbxControls = new HBox(8.0D, btnEdit, btnDelete);

            {
                ivEdit.setFitHeight(20.0D);
                ivEdit.setPreserveRatio(true);
                ivDelete.setFitHeight(20.0D);
                ivDelete.setPreserveRatio(true);
                btnEdit.getStyleClass().add("btn-table-edit");
                btnEdit.setStyle("-fx-padding: 3px;");
                btnDelete.getStyleClass().add("btn-table-delete");
                btnDelete.setStyle("-fx-padding: 3px;");
                Tooltip.uninstall(btnEdit, new Tooltip("Modify / Edit"));
                Tooltip.uninstall(btnDelete, new Tooltip("Delete"));
                hbxControls.setAlignment(Pos.CENTER);
            }

            @Override
            public void updateItem(final Void item, final boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    final int idx = getIndex();
                    if(idx > -1) {
                        final AccountCustomFieldEntity data = AccountCustomFieldViewer.this.getItems().get(idx);
                        btnEdit.setOnAction(event -> {
                            new FieldEditor(
                                    getAllFieldNames(),
                                    data.getFieldName(),
                                    data.getFieldValue(),
                                    (updatedName, updatedValue) -> {
                                        data.setFieldName(updatedName);
                                        data.setFieldValue(updatedValue);
                                        AccountCustomFieldViewer.this.refresh();
                                        adjustTableViewHeight();
                                    }
                            );
                        });
                        btnDelete.setOnAction(event -> {
                            allFields.remove(data);
                        });
                        btnEdit.setGraphic(ivEdit);
                        btnDelete.setGraphic(ivDelete);
                    }
                    setGraphic(hbxControls);
                }
            }
        });

        allFields.addListener((javafx.collections.ListChangeListener<? super AccountCustomFieldEntity>) change -> {
            adjustTableViewHeight();
            Utility.autoFitColumnWidth(tcFieldName);
            Utility.autoFitColumnWidth(tcFieldValue);
        });
        adjustTableViewHeight();
        Utility.autoFitColumnWidth(tcFieldName);
        Utility.autoFitColumnWidth(tcFieldValue);
        setMinHeight(TableView.USE_PREF_SIZE);
    }

    private void adjustTableViewHeight() {
        Platform.runLater(() -> {
            // Force JavaFX to apply CSS and layout the current state of the table
            applyCss();
            layout();

            // 1. Try to find the actual rendered header height from CSS
            var header = lookup(".column-header-background");
            double headerHeight = (header instanceof javafx.scene.layout.Region r) ? r.getHeight() : 42.0;

            // 2. Try to find the actual height of a rendered row
            var row = lookup(".table-row-cell");
            double rowHeight = (row instanceof javafx.scene.layout.Region r) ? r.getHeight() : 36.0;

            int numRows = getItems().size();

            double calculatedHeight = headerHeight + (numRows * rowHeight) * 1.1F;
            setPrefHeight(Math.max(Math.min(calculatedHeight, AppConfig.getScreenHeight() * 0.4D), 120.0D));//min 120px, max 40% of screen
        });
    }

    public Set<String> getAllFieldNames() {
        return allFields.stream().map(AccountCustomFieldEntity::getFieldName).collect(Collectors.toSet());
    }

    public void addNewCustomField(final String name, final String value) {
        if(getAllFieldNames().contains(name)) {
            return;
        }
        allFields.add(new AccountCustomFieldEntity(AccountEntity.UNDEFINED_ACCOUNT_ID, name, value));
    }

    public void clear() {
        allFields.clear();
    }

    public List<AccountCustomFieldEntity> getAllCustomFieldData() {
        return allFields.stream().toList();
    }

    //deletes all the entities available and adds the given list of entities to the view
    public void setAll(final List<AccountCustomFieldEntity> entities) {
        clear();
        allFields.addAll(entities);
    }

}
