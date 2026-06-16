package org.ajikhoji.passwordmanager.ui_components;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.ajikhoji.passwordmanager.config.AppConfig;
import org.ajikhoji.passwordmanager.config.AppResources;
import org.ajikhoji.passwordmanager.config.DbConfig;
import org.ajikhoji.passwordmanager.dto.LabelUsage;
import org.ajikhoji.passwordmanager.model.LabelEntity;
import org.ajikhoji.passwordmanager.service.LabelService;
import org.ajikhoji.passwordmanager.util.Utility;
import org.ajikhoji.passwordmanager.view_secondary.LabelNamingWindow;

import java.util.function.Consumer;

public class LabelManager extends TableView<LabelUsage> {

    private Consumer<Integer> onLabelCountChange;

    public LabelManager() {
        final TableColumn<LabelUsage, String> tcLabelName = new TableColumn<>("Name");
        final TableColumn<LabelUsage, Integer> tcUsageCount = new TableColumn<>("Account associated");
        final TableColumn<LabelUsage, Void> tcActions = new TableColumn<>("Actions");

        tcLabelName.setCellValueFactory(new PropertyValueFactory<>("labelName"));
        tcUsageCount.setCellValueFactory(new PropertyValueFactory<>("usageCount"));

        tcLabelName.setReorderable(false);
        tcUsageCount.setReorderable(false);
        tcActions.setReorderable(false);
        tcActions.setSortable(false);
        tcLabelName.setMinWidth(Utility.computeTextWidth("Name", Font.font("Times New Roman", FontWeight.BOLD, 24.0D)) * 1.2D);
        tcUsageCount.setPrefWidth(Utility.computeTextWidth("Account associated", Font.font("Times New Roman", FontWeight.BOLD, 20.0D)) * 1.2D);
        tcActions.setMinWidth(Utility.computeTextWidth("Actions", Font.font("Times New Roman", FontWeight.BOLD, 20.0D)) * 1.2D);

        getColumns().add(tcLabelName);
        getColumns().add(tcUsageCount);
        getColumns().add(tcActions);

        tcUsageCount.setCellFactory(cellFactory -> new TableCell<>(){
            @Override
            public void updateItem(final Integer count, final boolean empty) {
                if(count == null) {
                    setGraphic(null);
                } else {
                    final VBox vbx = new VBox(new Label(String.valueOf(count)));
                    vbx.setAlignment(Pos.CENTER);
                    setGraphic(vbx);
                }
                setText(null);
            }
        });

        final AppResources ar = AppConfig.getAppResources();
        tcActions.setCellFactory(cellFactory -> new TableCell<>() {
            private final ImageView ivEdit = new ImageView(ar.imgEdit);
            private final ImageView ivDelete = new ImageView(ar.imgDelete);private final Button btnEdit = new Button();
            private final Button btnDelete = new Button();
            private final HBox hbxControls = new HBox(8.0D, btnEdit, btnDelete);

            {
                ivEdit.setFitHeight(20.0D);
                ivEdit.setPreserveRatio(true);
                ivDelete.setFitHeight(20.0D);
                ivDelete.setPreserveRatio(true);
                btnEdit.getStyleClass().add("btn-img");
                btnDelete.getStyleClass().add("btn-img-delete");
                Tooltip.install(btnEdit, new Tooltip("Modify / Edit"));
                Tooltip.install(btnDelete, new Tooltip("Delete"));
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
                        final LabelUsage data = LabelManager.this.getItems().get(idx);
                        if(data.getLabelName().equals(LabelEntity.DEFAULT_LABEL_NAME)) {
                            setGraphic(null);
                            return;
                        }

                        btnEdit.setOnAction(event -> {
                            final String oldName = data.getLabelName();
                            LabelNamingWindow.showExistingLabelNameEditingWindow(
                                (newName, window) -> {
                                    try {
                                        final LabelService ls = DbConfig.getLabelService();
                                        ls.updateLabel(new LabelEntity(data.getLabelId(), newName));
                                        getItems().set(idx, new LabelUsage(data.getLabelId(), newName, data.getUsageCount()));
                                        LabelManager.this.refresh();
                                        window.close();
                                    } catch (final Exception ex) {
                                        Utility.showErrorAlert("Operation failed", String.format("Error occurred while updating label name from %s to %s", oldName, newName));
                                    }
                                },
                                data.getLabelName()
                            );
                        });
                        btnDelete.setOnAction(event -> {
                            LabelNamingWindow.showDeleteConfirmationWindow(
                                data.getUsageCount(),
                                data.getLabelName(),
                                    (replacementLabelEntity, window) -> {
                                    final LabelService labelService = DbConfig.getLabelService();
                                    try {
                                        labelService.deleteLabel(new LabelEntity(data.getLabelId(), data.getLabelName()), replacementLabelEntity);
                                        getItems().remove(data);
                                        updateData();
                                        LabelManager.this.refresh();
                                        window.close();
                                    } catch (final Exception e) {
                                        e.printStackTrace();
                                        Utility.showErrorAlert("Delete operation failed", "Something went wrong.");
                                    }
                                }
                            );
                        });

                        btnEdit.setGraphic(ivEdit);
                        btnDelete.setGraphic(ivDelete);
                        setGraphic(hbxControls);
                    }
                }
            }
        });

        updateData();
    }

    private void updateData() {
        setItems(FXCollections.observableArrayList(DbConfig.getLabelService().getLabelUsageStatistics()));
        if(onLabelCountChange != null) {
            onLabelCountChange.accept(getLabelsCount());
        }
    }

    public int getLabelsCount() {
        return getItems().size();
    }

    public void setOnLabelCountChange(final Consumer<Integer> onLabelCountChange) {
        this.onLabelCountChange = onLabelCountChange;
    }

    public void triggerLabelCountChangeEvent() {
        if(this.onLabelCountChange != null) {
            this.onLabelCountChange.accept(getLabelsCount());
        }
    }

}
