package org.ajikhoji.passwordmanager.ui_components;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.ajikhoji.passwordmanager.config.AppConfig;
import org.ajikhoji.passwordmanager.config.DbConfig;
import org.ajikhoji.passwordmanager.model.LabelEntity;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

public class NewLabelNamingWindow {

    public NewLabelNamingWindow(final BiConsumer<String, Stage> onSuccessfulNewNamingValidation) {
        final Stage st = new Stage();
        st.setResizable(false);
        st.setTitle("Add New Category Label");
        st.initOwner(AppConfig.getPrimaryStage());
        st.initModality(Modality.APPLICATION_MODAL);

        final Set<String> allAvailableLabels = new HashSet<>(DbConfig.getLabelService().getAllLabels().stream().map(LabelEntity::getLabelName).toList());

        GridPane gp = new GridPane(10.0D, 25.0D);
        gp.setStyle("-fx-padding: 20px;");
        ColumnConstraints ccField = new ColumnConstraints();
        ccField.setHalignment(HPos.RIGHT);
        gp.getColumnConstraints().add(ccField);
        ColumnConstraints ccValue = new ColumnConstraints();
        ccValue.setHalignment(HPos.LEFT);
        ccValue.setHgrow(Priority.ALWAYS);
        gp.getColumnConstraints().add(ccValue);

        final Label lblName = new Label("Category name:");
        gp.add(lblName, 0, 0);
        final TextField tfName = new TextField();
        gp.add(tfName, 1, 0);
        final Label lblError = new Label();
        lblError.setStyle("-fx-text-fill: red;");
        final HBox hbxError = new HBox(lblError);
        hbxError.setAlignment(Pos.CENTER);
        gp.add(hbxError, 0, 1, 2, 1);
        final Button btnAdd = new Button("Add");
        final Button btnCancel = new Button("Cancel");
        final HBox hbx = new HBox(18.0D, btnCancel, btnAdd);
        hbx.setAlignment(Pos.CENTER);
        gp.add(hbx, 0, 2, 2, 1);

        final String[] reservedNames = new String[]{"any", "all", "none", "every", "each", "no filter", "filter", "select", "apply", "applied"};//to avoid confusion at filtering labels in 'view all credentials' screen

        btnCancel.setOnAction(e -> st.close());
        btnAdd.setOnAction(e -> {
            final String name = tfName.getText();
            if(name == null || name.isBlank()) {
                lblError.setText("Name shall not be blank");
                return;
            }
            boolean isReservedName = false;
            for(final String reservedName : reservedNames) {
                if(name.toLowerCase().strip().equals(reservedName)) {
                    isReservedName = true;
                    break;
                }
            }
            if(isReservedName) {
                lblError.setText(String.format("Name '%s' not allowed", name));
                return;
            }
            if(allAvailableLabels.contains(name)) {
                lblError.setText("Category label already exists");
                return;
            }
            onSuccessfulNewNamingValidation.accept(name, st);
        });
        tfName.textProperty().addListener((ol, ov, nv) -> lblError.setText(""));

        final Scene sc = new Scene(gp, 400, 160);
        st.setScene(sc);
        st.show();
    }

}
