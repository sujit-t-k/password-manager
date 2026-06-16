package org.ajikhoji.passwordmanager.view_secondary;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.ajikhoji.passwordmanager.config.AppConfig;

import java.util.Set;
import java.util.function.BiConsumer;

// a dialog box like window to request for values for new field pair or modification of existing field pair
public class CustomFieldEditor {

    //to be utilized for requesting new field key-value pair inclusion
    public CustomFieldEditor(final Set<String> allKeys, final BiConsumer<String, String> onSuccessfulNamingValidation) {
        this(allKeys, "", "", "Add new field", "Add", onSuccessfulNamingValidation);
    }

    //to be used for editing existing field key-value pair
    public CustomFieldEditor(final Set<String> allKeys, final String currKey, final String currValue, final BiConsumer<String, String> onSuccessfulNamingValidation) {
        this(allKeys, currKey, currValue, "Edit field property", "Save", onSuccessfulNamingValidation);
    }

    //base constructor for both add new field and edit existing field.
    private CustomFieldEditor(final Set<String> allKeys, final String currKey, final String currValue, final String windowTitle, final String btnText, final BiConsumer<String, String> onSuccessfulNamingValidation) {
        final Stage st = new Stage();
        st.setResizable(false);
        st.setTitle(windowTitle);
        st.initOwner(AppConfig.getPrimaryStage());
        st.initModality(Modality.APPLICATION_MODAL);

        GridPane gp = new GridPane(10.0D, 25.0D);
        gp.setStyle("-fx-padding: 20px;");
        ColumnConstraints ccField = new ColumnConstraints();
        ccField.setHalignment(HPos.RIGHT);
        gp.getColumnConstraints().add(ccField);
        ColumnConstraints ccValue = new ColumnConstraints();
        ccValue.setHalignment(HPos.LEFT);
        ccValue.setHgrow(Priority.ALWAYS);
        gp.getColumnConstraints().add(ccValue);

        final Label lblName = new Label("Field name:");
        gp.add(lblName, 0, 0);
        final TextField tfName = new TextField(currKey);
        gp.add(tfName, 1, 0);
        final Label lblValue = new Label("Field value:");
        gp.add(lblValue, 0, 1);
        final TextField tfValue = new TextField(currValue);
        gp.add(tfValue, 1, 1);
        final Label lblError = new Label();
        lblError.setStyle("-fx-text-fill: red;");
        final HBox hbxError = new HBox(lblError);
        hbxError.setAlignment(Pos.CENTER);
        gp.add(hbxError, 0, 2, 2, 1);
        final Button btnAdd = new Button(btnText);
        final Button btnCancel = new Button("Cancel");
        final HBox hbx = new HBox(18.0D, btnCancel, btnAdd);
        hbx.setAlignment(Pos.CENTER);
        gp.add(hbx, 0, 3, 2, 1);

        btnCancel.setOnAction(e -> st.close());
        btnAdd.setOnAction(e -> {
            final String name = tfName.getText();
            if(name == null || name.isBlank()) {
                lblError.setText("Name shall not be blank");
                return;
            }
            if(allKeys.contains(name) && !name.equals(currKey)) {
                lblError.setText("Field with this name already exists");
                return;
            }
            final String value = tfValue.getText();
            if(value == null || value.isBlank()) {
                lblError.setText("Value shall not be blank");
                return;
            }
            if(name.equals(currKey) && value.equals(currValue)) {
                lblError.setText("No changes made");
                return;
            }
            onSuccessfulNamingValidation.accept(name, value);
            st.close();
        });
        tfName.textProperty().addListener((ol, ov, nv) -> {
            lblError.setText("");
            if(nv != null && nv.length() > 50) {
                tfName.textProperty().set(nv.substring(0, 50));
            }
        });
        tfValue.textProperty().addListener((ol, ov, nv) -> {
            lblError.setText("");
            if(nv != null && nv.length() > 50) {
                tfValue.textProperty().set(nv.substring(0, 50));
            }
        });

        final Scene sc = new Scene(gp, 400, 260);
        st.setScene(sc);
        st.show();
    }

}
