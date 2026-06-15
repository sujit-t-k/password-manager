package org.ajikhoji.passwordmanager.view;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import org.ajikhoji.passwordmanager.config.AppConfig;
import org.ajikhoji.passwordmanager.config.DbConfig;
import org.ajikhoji.passwordmanager.dto.LabelUsage;
import org.ajikhoji.passwordmanager.model.AccountCustomFieldEntity;
import org.ajikhoji.passwordmanager.model.AccountEntity;
import org.ajikhoji.passwordmanager.service.DashboardService;
import org.ajikhoji.passwordmanager.util.Utility;

import java.time.LocalDateTime;
import java.util.List;

public class DashboardScreen extends Pane {

    private final DashboardService dashboardService;
    private final VBox vbxParent;

    public DashboardScreen() {
        final ScrollPane sp = new ScrollPane();
        sp.getStyleClass().add("info-scroll");
        sp.setFitToWidth(true);

        vbxParent = new VBox();
        vbxParent.setStyle("-fx-padding: 20px;");

        final Label lblGreeting = new Label(
             String.format(
                  "%s %s",
                  Math.random() > 0.66D ? "Welcome" : Math.random() < 0.33D ? "Hello" : "Greetings,",
                  DbConfig.getSettingService().getUserName()
             )
        );
        lblGreeting.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        vbxParent.getChildren().add(lblGreeting);

        sp.setContent(vbxParent);
        getChildren().add(sp);
        sp.prefWidthProperty().bind(widthProperty());
        sp.prefHeightProperty().bind(heightProperty());

        dashboardService = DbConfig.getDashboardService();

        vbxParent.setSpacing(18.0D);
        addKpiMetrics();
        addAccountsAddedThisMonth();
        addRecentlyUsedAccounts();
        addMostUsedAccounts();
        addMostUsedLabels();
        addRecentlyModifiedAccounts();
    }

    private void addKpiMetrics() {
        final HBox hbxMetrics = new HBox(6.0D);

        final Label lblAccountsCount = new Label(String.valueOf(dashboardService.getTotalAccountCount()));
        lblAccountsCount.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-padding: 6px 0 0 0;");
        final Label lblAccounts = new Label("Total Accounts");
        lblAccounts.setAlignment(Pos.CENTER);
        lblAccounts.setTextAlignment(TextAlignment.CENTER);
        lblAccounts.setStyle("-fx-padding: 0 0 6px 0;");
        final VBox vbxTotalAccounts = new VBox(2.0D, lblAccountsCount, lblAccounts);
        vbxTotalAccounts.setAlignment(Pos.CENTER);
        vbxTotalAccounts.setStyle("-fx-background-color: #242424;");
        hbxMetrics.getChildren().add(vbxTotalAccounts);

        final Label lblAccountsAddedCount = new Label(String.valueOf(dashboardService.getAccountsAddedThisMonth().size()));
        lblAccountsAddedCount.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-padding: 6px 0 0 0;");
        final Label lblAccountsAdded = new Label("Accounts added this month");
        lblAccountsAdded.setWrapText(true);
        lblAccountsAdded.setAlignment(Pos.CENTER);
        lblAccountsAdded.setTextAlignment(TextAlignment.CENTER);
        lblAccountsAdded.setStyle("-fx-padding: 0 0 6px 0;");
        final VBox vbxTotalAccountsAdded = new VBox(2.0D, lblAccountsAddedCount, lblAccountsAdded);
        vbxTotalAccountsAdded.setAlignment(Pos.CENTER);
        vbxTotalAccountsAdded.setStyle("-fx-background-color: #242424;");
        hbxMetrics.getChildren().add(vbxTotalAccountsAdded);

        final Label lblLabelsCount = new Label(String.valueOf(dashboardService.getTotalLabelsCount()));
        lblLabelsCount.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-padding: 6px 0 0 0;");
        final Label lblLabels = new Label("Total Labels");
        lblLabels.setAlignment(Pos.CENTER);
        lblLabels.setTextAlignment(TextAlignment.CENTER);
        lblLabels.setStyle("-fx-padding: 0 0 6px 0;");
        final VBox vbxTotalLabels = new VBox(2.0D, lblLabelsCount, lblLabels);
        vbxTotalLabels.setAlignment(Pos.CENTER);
        vbxTotalLabels.setStyle("-fx-background-color: #242424;");
        hbxMetrics.getChildren().add(vbxTotalLabels);

        HBox.setHgrow(vbxTotalLabels, Priority.ALWAYS);
        HBox.setHgrow(vbxTotalAccounts, Priority.ALWAYS);
        HBox.setHgrow(vbxTotalAccountsAdded, Priority.ALWAYS);
        hbxMetrics.prefWidthProperty().bind(vbxParent.widthProperty().subtract(40));

        vbxParent.getChildren().add(hbxMetrics);
    }

    private TableView<AccountEntity> getTableView(final TableColumn<AccountEntity, ?> lastColumn) {
        final TableView<AccountEntity> tblView = new TableView<>();
        tblView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        final TableColumn<AccountEntity, String> accId = new TableColumn<>("Account ID/Name");
        accId.setCellValueFactory(new PropertyValueFactory<>("accName"));
        accId.setCellFactory(rowData -> new TableCell<>() {
            @Override
            public void updateItem(final String id, final boolean empty) {
                super.updateItem(id, empty);
                if(!empty) {
                    setText(id);
                    setCursor(Cursor.HAND);
                } else {
                    setText(null);
                    setCursor(Cursor.DEFAULT);
                }
            }
        });
        accId.setMinWidth(Utility.computeTextWidth(accId.getText(),  Font.font("Times New Roman", FontWeight.BOLD, 24.0D)) * 1.2D);
        tblView.getColumns().add(accId);

        final TableColumn<AccountEntity, String> accPlatform = new TableColumn<>("Platform");
        accPlatform.setCellValueFactory(new PropertyValueFactory<>("platform"));
        accPlatform.setCellFactory(rowData -> new TableCell<>() {
            @Override
            public void updateItem(final String platform, final boolean empty) {
                super.updateItem(platform, empty);
                if(!empty) {
                    setText(platform);
                    setCursor(Cursor.HAND);
                } else {
                    setText(null);
                    setCursor(Cursor.DEFAULT);
                }
            }
        });
        accPlatform.setMinWidth(Utility.computeTextWidth(accPlatform.getText(),  Font.font("Times New Roman", FontWeight.BOLD, 24.0D)) * 1.2D);
        tblView.getColumns().add(accPlatform);

        tblView.getColumns().add(lastColumn);

        return tblView;
    }

    private TableView<LabelUsage> getTableView() {
        final TableView<LabelUsage> tblView = new TableView<>();

        final TableColumn<LabelUsage, String> lblName = new TableColumn<>("Label Name");
        lblName.setCellValueFactory(new PropertyValueFactory<>("labelName"));
        lblName.setMinWidth(Utility.computeTextWidth(lblName.getText(),  Font.font("Times New Roman", FontWeight.BOLD, 24.0D)) * 1.2D);
        tblView.getColumns().add(lblName);

        final TableColumn<LabelUsage, Integer> usage = new TableColumn<>("Account associated");
        usage.setCellValueFactory(new PropertyValueFactory<>("usageCount"));
        usage.setCellFactory(rowData -> new TableCell<>() {
            @Override
            public void updateItem(final Integer count, final boolean empty) {
                super.updateItem(count, empty);
                if(!empty) {
                    final VBox vbx = new VBox(new Label(String.valueOf(count)));
                    vbx.setAlignment(Pos.CENTER);
                    setGraphic(vbx);
                } else {
                    setGraphic(null);
                }
            }
        });
        usage.setMinWidth(Utility.computeTextWidth(usage.getText(),  Font.font("Times New Roman", FontWeight.BOLD, 24.0D)) * 1.2D);
        tblView.getColumns().add(usage);

        return tblView;
    }

    private void addAccountsAddedThisMonth() {
        final List<AccountEntity> accounts = dashboardService.getAccountsAddedThisMonth();
        final VBox vbx = new VBox(4.0D);

        final Label lblTitle = new Label("Account added this month");
        lblTitle.setWrapText(true);
        lblTitle.setStyle("-fx-font-size: 18px;");
        vbx.getChildren().add(lblTitle);

        if(accounts.isEmpty()) {
            final Label lblNoAccounts = new Label("You haven't added any accounts this month.");
            vbx.getChildren().add(lblNoAccounts);
        } else {
            final TableColumn<AccountEntity, LocalDateTime> dateAdded = new TableColumn<>("Added on");
            dateAdded.setCellValueFactory(new PropertyValueFactory<>("createdDateTime"));
            dateAdded.setCellFactory(rowData -> new TableCell<>() {
                @Override
                public void updateItem(final LocalDateTime createdDate, final boolean empty) {
                    super.updateItem(createdDate, empty);
                    if(!empty) {
                        setText(Utility.getFormatedDateTimeString(createdDate));
                        setCursor(Cursor.HAND);
                    } else {
                        setText(null);
                        setCursor(Cursor.DEFAULT);
                    }
                }
            });

            final TableView<AccountEntity> tbl = getTableView(dateAdded);
            tbl.getSelectionModel().selectedItemProperty().addListener((ol, ov, nv) -> {
                if(nv != null) {
                    final List<AccountCustomFieldEntity> customFields = DbConfig.getAccountCustomFieldService().getAccountCustomFieldsForAccountId(nv.getAccId());
                    DetailedAccountInfoScreen.show(nv, customFields);
                    Platform.runLater(() -> {
                        tbl.getSelectionModel().clearSelection();
                        tbl.refresh();
                    });
                }
            });
            tbl.setItems(FXCollections.observableArrayList(accounts));
            tbl.setPrefHeight(Math.min(AppConfig.getScreenHeight() * 0.25D, (accounts.size() + 1) * 45.0D));

            vbx.getChildren().add(tbl);
        }
        vbxParent.getChildren().add(vbx);
    }

    private void addRecentlyUsedAccounts() {
        final List<AccountEntity> accounts = dashboardService.getKRecentlyUsedAccounts(5);
        final VBox vbx = new VBox(4.0D);

        final Label lblTitle = new Label("Recently used accounts");
        lblTitle.setWrapText(true);
        lblTitle.setStyle("-fx-font-size: 18px;");
        vbx.getChildren().add(lblTitle);

        if(accounts.isEmpty()) {
            final Label lblNoAccounts = new Label("No accounts have been used recently.");
            vbx.getChildren().add(lblNoAccounts);
        } else {
            final TableColumn<AccountEntity, LocalDateTime> dateRecentlyUsed = new TableColumn<>("Last used");
            dateRecentlyUsed.setCellValueFactory(new PropertyValueFactory<>("lastUsedDateTime"));
            dateRecentlyUsed.setCellFactory(rowData -> new TableCell<>() {
                @Override
                public void updateItem(final LocalDateTime recentUsedDateTime, final boolean empty) {
                    super.updateItem(recentUsedDateTime, empty);
                    if(!empty) {
                        setText(Utility.getFormatedDateTimeString(recentUsedDateTime));
                        setCursor(Cursor.HAND);
                    } else {
                        setText(null);
                        setCursor(Cursor.DEFAULT);
                    }
                }
            });

            final TableView<AccountEntity> tbl = getTableView(dateRecentlyUsed);
            tbl.getSelectionModel().selectedItemProperty().addListener((ol, ov, nv) -> {
                if(nv != null) {
                    final List<AccountCustomFieldEntity> customFields = DbConfig.getAccountCustomFieldService().getAccountCustomFieldsForAccountId(nv.getAccId());
                    DetailedAccountInfoScreen.show(nv, customFields);
                    Platform.runLater(() -> {
                        tbl.getSelectionModel().clearSelection();
                        tbl.refresh();
                    });
                }
            });
            tbl.setItems(FXCollections.observableArrayList(accounts));
            tbl.setPrefHeight(Math.min(AppConfig.getScreenHeight() * 0.25D, (accounts.size() + 1) * 45.0D));

            vbx.getChildren().add(tbl);
        }
        vbxParent.getChildren().add(vbx);
    }

    private void addMostUsedAccounts() {
        final List<AccountEntity> accounts = dashboardService.getKMostUsedAccount(5);
        final VBox vbx = new VBox(4.0D);

        final Label lblTitle = new Label("Most used accounts");
        lblTitle.setWrapText(true);
        lblTitle.setStyle("-fx-font-size: 18px;");
        vbx.getChildren().add(lblTitle);

        if(accounts.isEmpty()) {
            final Label lblNoAccounts = new Label("No usage data available yet.");
            vbx.getChildren().add(lblNoAccounts);
        } else {
            final TableColumn<AccountEntity, Integer> usage = new TableColumn<>("Usage Count");
            usage.setCellValueFactory(new PropertyValueFactory<>("usageCount"));
            usage.setCellFactory(rowData -> new TableCell<>() {
                @Override
                public void updateItem(final Integer count, final boolean empty) {
                    super.updateItem(count, empty);
                    if(!empty) {
                        final VBox vbx = new VBox(new Label(String.valueOf(count)));
                        vbx.setAlignment(Pos.CENTER);
                        setGraphic(vbx);
                        setCursor(Cursor.HAND);
                    } else {
                        setGraphic(null);
                        setCursor(Cursor.DEFAULT);
                    }
                }
            });
            usage.setMinWidth(Utility.computeTextWidth(usage.getText(),  Font.font("Times New Roman", FontWeight.BOLD, 24.0D)) * 1.2D);

            final TableView<AccountEntity> tbl = getTableView(usage);
            tbl.getSelectionModel().selectedItemProperty().addListener((ol, ov, nv) -> {
                if(nv != null) {
                    final List<AccountCustomFieldEntity> customFields = DbConfig.getAccountCustomFieldService().getAccountCustomFieldsForAccountId(nv.getAccId());
                    DetailedAccountInfoScreen.show(nv, customFields);
                    Platform.runLater(() -> {
                        tbl.getSelectionModel().clearSelection();
                        tbl.refresh();
                    });
                }
            });
            tbl.setItems(FXCollections.observableArrayList(accounts));
            tbl.setPrefHeight(Math.min(AppConfig.getScreenHeight() * 0.25D, (accounts.size() + 1) * 45.0D));

            vbx.getChildren().add(tbl);
        }
        vbxParent.getChildren().add(vbx);
    }

    private void addMostUsedLabels() {
        final List<LabelUsage> labels = dashboardService.getKMostUsedLabels(5);
        final VBox vbx = new VBox(4.0D);

        final Label lblTitle = new Label("Most used labels");
        lblTitle.setWrapText(true);
        lblTitle.setStyle("-fx-font-size: 18px;");
        vbx.getChildren().add(lblTitle);

        if(labels.isEmpty()) {
            final Label lblNoAccounts = new Label("No usage data available yet.");
            vbx.getChildren().add(lblNoAccounts);
        } else {
            final TableColumn<LabelUsage, Integer> usage = new TableColumn<>("Usage Count");
            usage.setCellValueFactory(new PropertyValueFactory<>("usageCount"));
            usage.setCellFactory(rowData -> new TableCell<>() {
                @Override
                public void updateItem(final Integer count, final boolean empty) {
                    super.updateItem(count, empty);
                    if(!empty) {
                        final VBox vbx = new VBox(new Label(String.valueOf(count)));
                        vbx.setAlignment(Pos.CENTER);
                        setGraphic(vbx);
                        setCursor(Cursor.HAND);
                    } else {
                        setGraphic(null);
                        setCursor(Cursor.DEFAULT);
                    }
                }
            });

            final TableView<LabelUsage> tbl = getTableView();
            tbl.getSelectionModel().selectedItemProperty().addListener((ol, ov, nv) -> {
                Platform.runLater(() -> {
                    tbl.getSelectionModel().clearSelection();
                    tbl.refresh();
                });
            });
            tbl.setItems(FXCollections.observableArrayList(labels));
            tbl.setPrefHeight(Math.min(AppConfig.getScreenHeight() * 0.25D, (labels.size() + 1) * 45.0D));

            vbx.getChildren().add(tbl);
        }
        vbxParent.getChildren().add(vbx);
    }

    private void addRecentlyModifiedAccounts() {
        final List<AccountEntity> accounts = dashboardService.getKRecentlyModifiedAccounts(5);
        final VBox vbx = new VBox(4.0D);

        final Label lblTitle = new Label("Recently modified accounts");
        lblTitle.setWrapText(true);
        lblTitle.setStyle("-fx-font-size: 18px;");
        vbx.getChildren().add(lblTitle);

        if(accounts.isEmpty()) {
            final Label lblNoAccounts = new Label("No account has been modified.");
            vbx.getChildren().add(lblNoAccounts);
        } else {
            final TableColumn<AccountEntity, LocalDateTime> dateModified = new TableColumn<>("Last modified");
            dateModified.setCellValueFactory(new PropertyValueFactory<>("recentUpdateDateTime"));
            dateModified.setCellFactory(rowData -> new TableCell<>() {
                @Override
                public void updateItem(final LocalDateTime recentUsedDateTime, final boolean empty) {
                    super.updateItem(recentUsedDateTime, empty);
                    if(!empty) {
                        setText(Utility.getFormatedDateTimeString(recentUsedDateTime));
                        setCursor(Cursor.HAND);
                    } else {
                        setText(null);
                        setCursor(Cursor.DEFAULT);
                    }
                }
            });

            final TableView<AccountEntity> tbl = getTableView(dateModified);
            tbl.getSelectionModel().selectedItemProperty().addListener((ol, ov, nv) -> {
                if(nv != null) {
                    final List<AccountCustomFieldEntity> customFields = DbConfig.getAccountCustomFieldService().getAccountCustomFieldsForAccountId(nv.getAccId());
                    DetailedAccountInfoScreen.show(nv, customFields);
                    Platform.runLater(() -> {
                        tbl.getSelectionModel().clearSelection();
                        tbl.refresh();
                    });
                }
            });
            tbl.setItems(FXCollections.observableArrayList(accounts));
            tbl.setPrefHeight(Math.min(AppConfig.getScreenHeight() * 0.25D, (accounts.size() + 1) * 45.0D));

            vbx.getChildren().add(tbl);
        }
        vbxParent.getChildren().add(vbx);
    }

}
