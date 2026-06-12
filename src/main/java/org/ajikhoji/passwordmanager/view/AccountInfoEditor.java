package org.ajikhoji.passwordmanager.view;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.util.StringConverter;
import org.ajikhoji.passwordmanager.config.DbConfig;
import org.ajikhoji.passwordmanager.model.LabelEntity;
import org.ajikhoji.passwordmanager.service.LabelService;
import org.ajikhoji.passwordmanager.ui_components.AccountCustomFieldEditor;
import org.ajikhoji.passwordmanager.ui_components.NewLabelNamingWindow;
import org.ajikhoji.passwordmanager.util.Utility;

public class AccountInfoEditor extends BorderPane {

    protected Utility.EntryField efName;
    protected Utility.EntryField efPassword;
    protected Utility.EntryField efConfirmPassword;
    protected Utility.EntryField efPlatform;
    protected Utility.EntryField efLink;
    protected final ChoiceBox<LabelEntity> cbxLabel;
    protected final AccountCustomFieldEditor customFieldEditor;
    protected final Button btnSave;
    protected final Label lblHeader;
    protected final FlowPane fpControls;
    
    public AccountInfoEditor() {
        cbxLabel = new ChoiceBox<>(FXCollections.observableArrayList(DbConfig.getLabelService().getAllLabels()));
        cbxLabel.setConverter(new StringConverter<>() {
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
        cbxLabel.getSelectionModel().select(DbConfig.getLabelService().getLabelEntityByName(LabelEntity.DEFAULT_LABEL_NAME));
        final Button btnAddNewLabel = new Button("Add Label");
        btnAddNewLabel.setOnAction(e -> {
            new NewLabelNamingWindow(
                (newLabelName, window) -> {
                    try {
                        final LabelService ls = DbConfig.getLabelService();
                        ls.addNewLabel(newLabelName);
                        cbxLabel.setItems(FXCollections.observableArrayList(ls.getAllLabels()));
                        final LabelEntity toBeSelected = ls.getLabelEntityByName(newLabelName);
                        if(toBeSelected != null) {
                            cbxLabel.getSelectionModel().select(toBeSelected);
                        } else {
                            cbxLabel.getSelectionModel().select(DbConfig.getLabelService().getLabelEntityByName(LabelEntity.DEFAULT_LABEL_NAME));
                        }
                        window.close();
                    } catch (final Exception ex) {
                        Utility.showErrorAlert("Operation aborted", String.format("Failed to add new category label %s", newLabelName));
                    }
                }
            );
        });
        final HBox hbxLabel = new HBox(10.0D, new Label("Category Label"), cbxLabel, btnAddNewLabel);
        hbxLabel.setAlignment(Pos.CENTER_LEFT);

        customFieldEditor = new AccountCustomFieldEditor();

        final BorderPane bpBase = this;
        bpBase.setStyle("-fx-background-color: #262626;");

        //top title display
        lblHeader = new Label();
        lblHeader.setAlignment(Pos.CENTER);
        lblHeader.setTextAlignment(TextAlignment.CENTER);
        lblHeader.prefWidthProperty().bind(bpBase.widthProperty());
        lblHeader.setStyle("-fx-background-color: #242424; -fx-padding: 8px 0px 6px 0px; -fx-font-size: 16px; -fx-font-weight: bold;");
        bpBase.setTop(lblHeader);

        //bottom controls pane
        btnSave = new Button("Save");
        final Button btnClear = new Button("Clear");
        btnClear.setOnAction(e -> {
            clearData();
        });
        fpControls = new FlowPane(btnClear, btnSave);
        fpControls.setHgap(12.0D);
        fpControls.setAlignment(Pos.CENTER);
        fpControls.setStyle("-fx-background-color: #202020; -fx-padding: 8px 0px 8px 0px;");
        bpBase.setBottom(fpControls);

        //middle area for showing input controls
        final VBox vbxCenter = new VBox(14.0D);
        vbxCenter.setPadding(new Insets(16.0D));
        vbxCenter.setStyle("-fx-background-color: #292929;");
        final ScrollPane spDetail = new ScrollPane(vbxCenter);
        spDetail.setFitToWidth(true);
        spDetail.setFitToHeight(true);
        bpBase.setCenter(spDetail);

        efName = Utility.addLabeledTextField("Account Name / ID", 90, vbxCenter);
        efPassword = Utility.addLabeledToggleablePasswordField("Password", 50, vbxCenter);
        efConfirmPassword = Utility.addLabeledToggleablePasswordField("Confirm Password", 50, true, vbxCenter);
        vbxCenter.getChildren().add(hbxLabel);
        efPlatform = Utility.addLabeledTextField("Platform", 90, vbxCenter);
        efLink = Utility.addLabeledTextField("Link", 300, vbxCenter);
        vbxCenter.getChildren().add(customFieldEditor);

        efName.textProperty().addListener((ol, ov, nv) -> {
            if(nv == null || nv.isBlank()) {
                efName.errorMessageProperty().set("Name should not be empty");
            }
            updateButton();
        });
        efPassword.textProperty().addListener((ol, ov, nv) -> {
            if(nv == null || nv.isBlank()) {
                efPassword.errorMessageProperty().set("Password should not be empty");
            }
            if(!efPassword.textProperty().get().equals(efConfirmPassword.textProperty().get())) {
                efConfirmPassword.errorMessageProperty().set("Password does not match");
            } else {
                efConfirmPassword.errorMessageProperty().set("");
            }
            updateButton();
        });
        efConfirmPassword.textProperty().addListener((ol, ov, nv) -> {
            if(!efPassword.textProperty().get().equals(efConfirmPassword.textProperty().get())) {
                efConfirmPassword.errorMessageProperty().set("Password does not match");
            } else {
                efConfirmPassword.errorMessageProperty().set("");
            }
            updateButton();
        });
        efPlatform.textProperty().addListener((ol, ov, nv) -> {
            if(nv == null || nv.isBlank()) {
                efPlatform.errorMessageProperty().set("Platform should not be empty");
            }
            updateButton();
        });
        updateButton();
    }

    private void updateButton() {
        btnSave.setDisable(
            !efName.errorMessageProperty().get().isEmpty() ||
            !efPassword.errorMessageProperty().get().isEmpty() ||
            !efConfirmPassword.errorMessageProperty().get().isEmpty() ||
            !efPlatform.errorMessageProperty().get().isEmpty()
        );
    }

    public void clearData() {
        efName.textProperty().set("");
        efPassword.textProperty().set("");
        efConfirmPassword.textProperty().set("");
        efPlatform.textProperty().set("");
        efLink.textProperty().set("");
        cbxLabel.getSelectionModel().select(DbConfig.getLabelService().getLabelEntityByName(LabelEntity.DEFAULT_LABEL_NAME));
    }

}
