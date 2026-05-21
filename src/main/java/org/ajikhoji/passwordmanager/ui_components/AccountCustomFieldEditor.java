package org.ajikhoji.passwordmanager.ui_components;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.ajikhoji.passwordmanager.model.AccountCustomFieldEntity;
import java.util.List;

public class AccountCustomFieldEditor extends VBox {

    private final AccountCustomFieldViewer viewer;

    public AccountCustomFieldEditor() {
        viewer = new AccountCustomFieldViewer();

        final Label lblTitle = new Label("Additional information");
        final Button btnAdd = new Button("Add new field");
        final Button btnClear = new Button("Clear all");
        final HBox hbxControls = new HBox(12.0D, lblTitle, btnClear, btnAdd);
        hbxControls.setAlignment(Pos.BOTTOM_LEFT);

        setSpacing(8.0D);
        getChildren().addAll(hbxControls, viewer);

        btnAdd.setOnAction(e -> {
            new FieldEditor(
                viewer.getAllFieldNames(),
                viewer::addNewCustomField
            );
        });
        btnClear.setOnAction(e -> viewer.clear());
    }

    public AccountCustomFieldEditor(final List<AccountCustomFieldEntity> entries) {
        this();
        setAll(entries);
    }

    public void setAll(final List<AccountCustomFieldEntity> entries) {
        viewer.setAll(entries);
    }

    public List<AccountCustomFieldEntity> getAllCustomFieldData() {
        return viewer.getAllCustomFieldData();
    }

}
