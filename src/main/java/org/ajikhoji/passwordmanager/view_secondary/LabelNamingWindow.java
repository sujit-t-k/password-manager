package org.ajikhoji.passwordmanager.view_secondary;

import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.ajikhoji.passwordmanager.Launcher;
import org.ajikhoji.passwordmanager.config.AppConfig;
import org.ajikhoji.passwordmanager.config.DbConfig;
import org.ajikhoji.passwordmanager.model.LabelEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;

public class LabelNamingWindow {

    public static void showNewLabelNamingWindow(final BiConsumer<String, Stage> onSuccessfulNewNamingValidation) {
        show(onSuccessfulNewNamingValidation, null, "Add New Label");
    }

    public static void showExistingLabelNameEditingWindow(final BiConsumer<String, Stage> onSuccessfulNewNamingValidation, final String currentLabelName) {
        show(onSuccessfulNewNamingValidation, currentLabelName, "Rename label");
    }

    private static LabelEntity selectedEntity;
    public static void showDeleteConfirmationWindow(final int associatedAccountCount, final String labelName, final BiConsumer<LabelEntity, Stage> onDeleteConfirmation) {
        selectedEntity = null;
        final Stage st = new Stage();

        final VBox vbxBase = new VBox(12.0D);
        vbxBase.setStyle("-fx-padding: 16px;");

        if(associatedAccountCount > 0) {
            final Label lblAccountsAssociated = new Label(String.format("Account(s) associated: %d", associatedAccountCount));
            vbxBase.getChildren().add(lblAccountsAssociated);

            final Label lblReassignment = new Label("These accounts will be reassigned to:");
            final ChoiceBox<LabelEntity> cbxAvailableLabels = new ChoiceBox<>();
            cbxAvailableLabels.setConverter(new StringConverter<>() {
                @Override
                public String toString(LabelEntity object) {
                    if(object == null) {
                        return null;
                    }
                    return object.getLabelName();
                }
                @Override
                public LabelEntity fromString(String string) {
                    return null;
                }
            });

            final List<LabelEntity> labels = DbConfig.getLabelService().getAllLabels().stream().filter(e -> !e.getLabelName().equals(labelName)).toList();
            cbxAvailableLabels.setItems(FXCollections.observableArrayList(labels));
            try {
                cbxAvailableLabels.setValue(labels.stream().filter(e -> e.getLabelName().equals(LabelEntity.DEFAULT_LABEL_NAME)).toList().getFirst());
            } catch (final Exception e) {
                if(!labels.isEmpty()) {
                    cbxAvailableLabels.setValue(labels.getFirst());
                }
            }
            final VBox vbxReassignment = new VBox(4.0D, lblReassignment, cbxAvailableLabels);
            vbxBase.getChildren().add(vbxReassignment);

            cbxAvailableLabels.getSelectionModel().selectedItemProperty().addListener((ol, ov, nv) -> selectedEntity = nv);
        }

        final Image imgWarning = AppConfig.getAppResources().imgWarning;
        final Label lblWarning = new Label("This action cannot be undone.");
        lblWarning.setStyle("-fx-text-fill: #FFB80F; -fx-font-size: 16px;");
        lblWarning.setAlignment(Pos.CENTER);
        lblWarning.setTextAlignment(TextAlignment.CENTER);
        lblWarning.prefWidthProperty().bind(vbxBase.widthProperty());
        final ImageView ivWarning = new ImageView(imgWarning);
        ivWarning.setFitHeight(25.0D);
        ivWarning.setPreserveRatio(true);
        lblWarning.setGraphic(ivWarning);
        vbxBase.getChildren().add(lblWarning);

        final Button btnDelete = new Button("Delete");
        btnDelete.getStyleClass().add("btn-important-decision-warning");
        btnDelete.setStyle("-fx-font-size: 16px;");
        final HBox hbxControls = new HBox(btnDelete);
        hbxControls.setAlignment(Pos.CENTER);
        vbxBase.getChildren().add(hbxControls);
        vbxBase.setPrefWidth(Math.clamp(500.0D, AppConfig.getScreenWidth() * 0.35D, AppConfig.getScreenWidth() * 0.96D));

        btnDelete.setOnAction(e -> onDeleteConfirmation.accept(selectedEntity, st));

        show(st, String.format("Delete label: %s", labelName), vbxBase);
    }

    private static void show(final BiConsumer<String, Stage> onSuccessfulNewNamingValidation, final String existingLabelName, final String appTitle) {
        final Stage st = new Stage();
        st.setResizable(false);
        st.setTitle(existingLabelName == null ? "Add New Category Label" : "Update label name");
        st.initOwner(AppConfig.getPrimaryStage());
        st.initModality(Modality.APPLICATION_MODAL);

        final Set<String> allAvailableLabels = new HashSet<>(DbConfig.getLabelService().getAllLabels().stream().map(LabelEntity::getLabelName).toList());
        final GridPane gp = new GridPane(10.0D, 25.0D);
        gp.setStyle("-fx-padding: 20px;");
        ColumnConstraints ccField = new ColumnConstraints();
        ccField.setHalignment(HPos.RIGHT);
        gp.getColumnConstraints().add(ccField);
        ColumnConstraints ccValue = new ColumnConstraints();
        ccValue.setHalignment(HPos.LEFT);
        ccValue.setHgrow(Priority.ALWAYS);
        gp.getColumnConstraints().add(ccValue);

        int row = 0;

        if(existingLabelName != null) {
            final Label lblCurrentName = new Label("Current name:");
            gp.add(lblCurrentName, 0, row);
            final Label lblCurrentValue = new Label(existingLabelName);
            gp.add(lblCurrentValue, 1, row++);
        }

        final Label lblName = new Label(existingLabelName == null ? "Label name:" : "New name:");
        gp.add(lblName, 0, row);
        final TextField tfName = new TextField();
        gp.add(tfName, 1, row++);
        final Label lblError = new Label();
        lblError.setStyle("-fx-text-fill: red;");
        final HBox hbxError = new HBox(lblError);
        hbxError.setAlignment(Pos.CENTER);
        gp.add(hbxError, 0, row++, 2, 1);
        final Button btnAdd = new Button(existingLabelName == null ? "Add" : "Update");
        final Button btnCancel = new Button("Cancel");
        final HBox hbx = new HBox(18.0D, btnCancel, btnAdd);
        hbx.setAlignment(Pos.CENTER);
        gp.add(hbx, 0, row, 2, 1);

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
                lblError.setText("Label name already exists");
                return;
            }
            onSuccessfulNewNamingValidation.accept(name, st);
        });
        tfName.textProperty().addListener((ol, ov, nv) -> lblError.setText(""));

        show(st, appTitle, gp);
    }

    private static void show(final Stage st, final String appTitle, final Pane base) {
        st.setResizable(false);
        st.setTitle(appTitle);
        st.initOwner(AppConfig.getPrimaryStage());
        st.initModality(Modality.APPLICATION_MODAL);

        base.getStyleClass().add("pane-primary");
        base.getStylesheets().add(Objects.requireNonNull(Launcher.class.getResource("style/dark-theme.css")).toExternalForm());

        final Scene sc = new Scene(base);
        st.setScene(sc);
        st.show();
    }

}
