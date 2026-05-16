package org.ajikhoji.passwordmanager.ui_components;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import org.ajikhoji.passwordmanager.model.AccountCustomFieldEntity;
import org.ajikhoji.passwordmanager.model.AccountEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/*
 * ui component to be used in add and edit pages of account credential
 * This is responsible for managing UI level support of new field pair
 * inclusion, modification of existing field pair and deletion of such.
 */
public class AccountCustomFieldEditorOld extends GridPane {

    private final List<AccountCustomFieldEntity> allCustomFields;

    public AccountCustomFieldEditorOld() {
        allCustomFields = new ArrayList<>();
        setHgap(2.0D);
        setVgap(2.0D);
        setStyle("-fx-padding: 10px;");
        ColumnConstraints ccField = new ColumnConstraints();
        ccField.setHalignment(HPos.CENTER);
        ccField.setFillWidth(true);
        getColumnConstraints().add(ccField);
        ColumnConstraints ccValue = new ColumnConstraints();
        ccValue.setHalignment(HPos.CENTER);
        getColumnConstraints().add(ccValue);

        final Button btnAdd = new Button("Add new field");
        final Button btnClear = new Button("Clear all");
        final HBox hbxControl = new HBox(12.0D, btnClear, btnAdd);
        hbxControl.setAlignment(Pos.CENTER);
        this.add(hbxControl, 0, 0, 3, 1);

        btnAdd.setOnAction(e -> {
            new FieldEditor(
                getAllFieldNames(),
                (fieldName, fieldValue) -> {
                    allCustomFields.add(new AccountCustomFieldEntity(AccountEntity.UNDEFINED_ACCOUNT_ID, fieldName, fieldValue));
                    updateListUi();
                }
            );
        });
        btnClear.setOnAction(e -> {
            allCustomFields.clear();
            updateListUi();
        });
        updateListUi();
    }

    public AccountCustomFieldEditorOld(final List<AccountCustomFieldEntity> customFields) {
        this();
        allCustomFields.addAll(customFields);
        updateListUi();
    }

    private void updateListUi() {
        this.getChildren().remove(1, this.getChildren().size());

        this.addRow(1, getCenteredTextLabel("Name"), getCenteredTextLabel("Value"), getCenteredTextLabel("Action"));
        if(allCustomFields.isEmpty()) {
            this.add(getCenteredTextLabel("Nothing to show"), 0, 2, 3, 1);
        }

        boolean even = true;
        for(final AccountCustomFieldEntity e : allCustomFields) {
            final Label lblKey = new Label(e.getFieldName());
            final Label lblValue = new Label(e.getFieldValue());
            final Button btnEdit = new Button("Edit");
            btnEdit.setOnAction(ae -> {
                new FieldEditor(
                    getAllFieldNames(),
                    e.getFieldName(),
                    e.getFieldValue(),
                    (updatedName, updatedValue) -> {
                        e.setFieldName(updatedName);
                        e.setFieldValue(updatedValue);
                        lblKey.setText(updatedName);
                        lblValue.setText(updatedValue);
                    }
                );
            });
            final Button btnDelete = new Button("Delete");
            btnDelete.setOnAction(ae -> {
                allCustomFields.remove(e);
                updateListUi();
            });
            final HBox hbxControls = new HBox(6.0D, btnEdit, btnDelete);
            if(even) {
                hbxControls.setStyle("-fx-background-color: yellow; -fx-padding: 6px;");
                lblKey.setStyle("-fx-background-color: yellow; -fx-padding: 6px;");
                lblValue.setStyle("-fx-background-color: yellow; -fx-padding: 6px;");
            } else {
                hbxControls.setStyle("-fx-background-color: blue; -fx-padding: 6px;");
                lblKey.setStyle("-fx-background-color: blue; -fx-padding: 6px;");
                lblValue.setStyle("-fx-background-color: blue; -fx-padding: 6px;");
            }
            final int filledCount = this.getChildren().size();
            this.addRow(filledCount, lblKey, lblValue, hbxControls);
            even = !even;
        }
    }

    private Label getCenteredTextLabel(final String text) {
        final Label lbl = new Label(text);
        lbl.setAlignment(Pos.CENTER);
        return lbl;
    }

    private Set<String> getAllFieldNames() {
        return allCustomFields.stream().map(AccountCustomFieldEntity::getFieldName).collect(Collectors.toSet());
    }

    //NOTE: Newly added field pair will be assigned AccountEntity.UNDEFINED_ACCOUNT_ID by default
    public List<AccountCustomFieldEntity> getAllCustomFields() {
        return new ArrayList<>(allCustomFields);
    }

}
