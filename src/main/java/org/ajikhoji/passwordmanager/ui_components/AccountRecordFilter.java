package org.ajikhoji.passwordmanager.ui_components;

import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import org.ajikhoji.passwordmanager.config.AppConfig;
import org.ajikhoji.passwordmanager.config.DbConfig;
import org.ajikhoji.passwordmanager.model.AccountEntity;
import org.ajikhoji.passwordmanager.model.LabelEntity;
import org.ajikhoji.passwordmanager.service.LabelService;

import java.util.List;
import java.util.function.BiFunction;

public class AccountRecordFilter extends VBox {

    public AccountRecordFilter(final AccountCredentialViewer viewer) {
        final TextField tfGlobalSearch = new TextField();
        tfGlobalSearch.setPromptText("Search on account name, platform and label");

        final GridPane gpAdvancedFilter = new GridPane(6.0D, 12.0D);
        gpAdvancedFilter.setStyle("-fx-padding: 6px;");
        ColumnConstraints ccField = new ColumnConstraints();
        ccField.setHalignment(HPos.RIGHT);
        gpAdvancedFilter.getColumnConstraints().add(ccField);
        ColumnConstraints ccValue = new ColumnConstraints();
        ccValue.setHalignment(HPos.LEFT);
        ccValue.setHgrow(Priority.ALWAYS);
        gpAdvancedFilter.getColumnConstraints().add(ccValue);

        gpAdvancedFilter.add(new Label("Account Name/ID:"), 0, 0);
        final TextField tfAccountNameFilter = new TextField();
        tfAccountNameFilter.setPrefColumnCount(50);
        tfAccountNameFilter.setPrefWidth(USE_PREF_SIZE);
        gpAdvancedFilter.add(tfAccountNameFilter, 1, 0);

        gpAdvancedFilter.add(new Label("Platform:"), 0, 1);
        final TextField tfPlatformFilter = new TextField();
        tfPlatformFilter.setPrefColumnCount(50);
        tfPlatformFilter.setPrefWidth(USE_PREF_SIZE);
        gpAdvancedFilter.add(tfPlatformFilter, 1, 1);

        gpAdvancedFilter.add(new Label("Label:"), 0, 2);
        final ChoiceBox<LabelEntity> cbxLabelFilter = new ChoiceBox<>();
        cbxLabelFilter.setConverter(new StringConverter<>() {
            @Override
            public String toString(LabelEntity object) {
                return object.getLabelName();
            }

            @Override
            public LabelEntity fromString(String string) {
                return LabelEntity.NULL_LABEL;
            }
        });
        gpAdvancedFilter.add(cbxLabelFilter, 1, 2);

        final TitledPane tpAdvancedFilter = new TitledPane("Advanced Filter", gpAdvancedFilter);
        final Accordion advancedFilter = new Accordion();
        advancedFilter.getPanes().add(tpAdvancedFilter);

        final Label lblPrompt = new Label("Viewing filtered data.");
        lblPrompt.setStyle("-fx-text-fill: #FFB80F;");
        final ImageView ivWarning = new ImageView(AppConfig.getAppResources().imgWarning);
        ivWarning.setFitHeight(20.0D);
        ivWarning.setPreserveRatio(true);
        final HBox hbxWarning = new HBox(4.0D, ivWarning, lblPrompt);
        hbxWarning.setAlignment(Pos.CENTER_LEFT);
        final Button btnClear = new Button("Clear Filter");
        final BorderPane bpFilterAppliedNotificationPanel = new BorderPane();
        bpFilterAppliedNotificationPanel.setLeft(hbxWarning);
        bpFilterAppliedNotificationPanel.setRight(btnClear);

        setSpacing(10.0D);
        getChildren().addAll(tfGlobalSearch, advancedFilter);

        final LabelService labelService = DbConfig.getLabelService();
        final List<LabelEntity> allLabels = labelService.getUsedLabels();
        allLabels.addFirst(LabelEntity.NULL_LABEL);
        cbxLabelFilter.setItems(FXCollections.observableArrayList(allLabels));
        cbxLabelFilter.getSelectionModel().select(LabelEntity.NULL_LABEL);

        final BiFunction<String, String, Boolean> matches = (filterValue, originalValue) -> originalValue.toLowerCase().contains(filterValue.toLowerCase());

        final Runnable applyFilter = () -> {
            final String globalFilter = tfGlobalSearch.getText();
            final String accNameFilter = tfAccountNameFilter.getText();
            final String platformFilter = tfPlatformFilter.getText();
            final long labelFilter = cbxLabelFilter.getValue().getLabelId();

            final boolean allFilterFieldsEmpty =
                (globalFilter == null || globalFilter.isEmpty()) && (accNameFilter == null || accNameFilter.isEmpty()) &&
                (platformFilter == null || platformFilter.isEmpty()) && labelFilter == LabelEntity.NULL_LABEL.getLabelId();

            if(allFilterFieldsEmpty) {
                getChildren().remove(bpFilterAppliedNotificationPanel);
                viewer.setPlaceholder(new Label("No records available"));
            }

            final List<AccountEntity> allAccounts = viewer.getAllAccounts();
            final List<AccountEntity> filteredAccounts = allAccounts.stream()
                .filter(accountEntity -> {
                    if(
                        globalFilter != null && !globalFilter.isBlank() &&
                        !(matches.apply(globalFilter, accountEntity.getAccName()) ||
                          matches.apply(globalFilter, accountEntity.getPlatform()) ||
                          matches.apply(globalFilter, labelService.getLabelEntityById(accountEntity.getLabelId()).getLabelName())
                        )
                    ) {
                        return false;
                    }
                    if(accNameFilter != null && !accNameFilter.isBlank() && !matches.apply(accNameFilter, accountEntity.getAccName())) {
                        return false;
                    }
                    if(platformFilter != null && !platformFilter.isBlank() && !matches.apply(platformFilter, accountEntity.getPlatform())) {
                        return false;
                    }
                    if(labelFilter != LabelEntity.NULL_LABEL.getLabelId() && accountEntity.getLabelId() != labelFilter) {
                        return false;
                    }
                    return true;
                }).toList();
            viewer.setItems(FXCollections.observableArrayList(filteredAccounts));
            if(!allFilterFieldsEmpty && !getChildren().contains(bpFilterAppliedNotificationPanel)) {
                getChildren().add(bpFilterAppliedNotificationPanel);
                viewer.setPlaceholder(new Label("No matching records found"));
            }
        };

        tfGlobalSearch.textProperty().addListener((ol, ov, nv) -> applyFilter.run());
        tfAccountNameFilter.textProperty().addListener((ol, ov, nv) -> applyFilter.run());
        tfPlatformFilter.textProperty().addListener((ol, ov, nv) -> applyFilter.run());
        cbxLabelFilter.getSelectionModel().selectedItemProperty().addListener((ol, ov, nv) -> applyFilter.run());
        btnClear.setOnAction(e -> {
            tfGlobalSearch.clear();
            tfAccountNameFilter.clear();
            tfPlatformFilter.clear();
            cbxLabelFilter.setValue(LabelEntity.NULL_LABEL);
            applyFilter.run();
        });
    }

}
