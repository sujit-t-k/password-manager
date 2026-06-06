package org.ajikhoji.passwordmanager.ui_components;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.ajikhoji.passwordmanager.config.AppConfig;
import org.ajikhoji.passwordmanager.config.AppResources;
import org.ajikhoji.passwordmanager.config.DbConfig;
import org.ajikhoji.passwordmanager.model.AccountEntity;
import org.ajikhoji.passwordmanager.security.EncryptionService;
import org.ajikhoji.passwordmanager.service.AccountCustomFieldService;
import org.ajikhoji.passwordmanager.service.AccountService;
import org.ajikhoji.passwordmanager.service.LabelService;
import org.ajikhoji.passwordmanager.util.Utility;
import org.ajikhoji.passwordmanager.view.DetailedAccountInfoScreen;
import org.ajikhoji.passwordmanager.view.EditAccountScreen;

import java.time.LocalDateTime;
import java.util.List;

import static org.ajikhoji.passwordmanager.util.Utility.copyText;

public class AccountCredentialViewer extends TableView<AccountEntity> {

    private final List<AccountEntity> allAccounts;

    public AccountCredentialViewer() {
        final AccountService accountService = DbConfig.getAccountService();
        allAccounts = accountService.getAllAccountCredential();
        setItems(FXCollections.observableArrayList(allAccounts));
        setEditable(false);
        setPlaceholder(new Label("No records available"));

        final TableColumn<AccountEntity, String> tcAccName = Utility.getCopyableTableColumn("Account ID/Name", "accName");
        final TableColumn<AccountEntity, String> tcAccPass = new TableColumn<>("Password");
        final TableColumn<AccountEntity, String> tcPlatform = new TableColumn<>("Platform");
        final TableColumn<AccountEntity, String> tcLink = Utility.getCopyableTableColumn("Link", "link");
        final TableColumn<AccountEntity, LocalDateTime> tcCreated = new TableColumn<>("Added");
        final TableColumn<AccountEntity, Long> tcLabel = new TableColumn<>("Label");
        final TableColumn<AccountEntity, Void> tcControls = new TableColumn<>("Actions");

        tcAccPass.setCellValueFactory(new PropertyValueFactory<>("accPassword"));
        tcPlatform.setCellValueFactory(new PropertyValueFactory<>("platform"));
        tcCreated.setCellValueFactory(new PropertyValueFactory<>("createdDateTime"));
        tcLabel.setCellValueFactory(new PropertyValueFactory<>("labelId"));

        tcAccPass.setSortable(false);
        tcLink.setSortable(false);
        tcLink.setPrefWidth(86.0D);
        tcLink.setResizable(false);
        tcAccPass.setPrefWidth(Utility.computeTextWidth("Password", Font.font(16.0D)) * 1.4D);
        tcAccPass.setResizable(false);

        getColumns().add(tcAccName);
        getColumns().add(tcAccPass);
        getColumns().add(tcPlatform);
        getColumns().add(tcLink);
        getColumns().add(tcCreated);
        getColumns().add(tcLabel);
        getColumns().add(tcControls);

        final AppResources ar = AppConfig.getAppResources();
        tcLink.setCellFactory(cf -> new TableCell<>() {
            private boolean copied = false, opened = false;
            @Override
            protected void updateItem(final String str, final boolean empty) {
                if(empty || str == null || str.isEmpty()) {
                    setText(getIndex() < getItems().size() ? "-" : null);
                    setAlignment(Pos.CENTER);
                    setGraphic(null);
                } else {
                    setText(null);
                    final ImageView imgViewCopy = new ImageView(ar.imgCopy);
                    imgViewCopy.setFitHeight(20.0D);
                    imgViewCopy.setFitWidth(20.0D);
                    final Button btnCopy = new Button("", imgViewCopy);
                    btnCopy.getStyleClass().add("btn-table");
                    btnCopy.setPrefSize(24.0D, 24.0D);
                    btnCopy.setMaxSize(24.0D, 24.0D);
                    btnCopy.setOnAction(e -> {
                        if(!this.copied) {
                            copyText(str);
                            imgViewCopy.setImage(ar.imgCopied);
                            btnCopy.setGraphic(imgViewCopy);
                            this.copied = true;
                            new Timeline(new KeyFrame(Duration.millis(1800.0D), eh -> {
                                imgViewCopy.setImage(ar.imgCopy);
                                btnCopy.setGraphic(imgViewCopy);
                                this.copied = false;
                            })).playFromStart();
                        }
                    });
                    final Tooltip tp = new Tooltip("Copy link to clipboard");
                    Tooltip.install(btnCopy, tp);
                    final ImageView imgViewLink = new ImageView(ar.imgLinkOpen);
                    imgViewLink.setFitHeight(20.0D);
                    imgViewLink.setFitWidth(20.0D);
                    final Button btnLink = new Button("", imgViewLink);
                    btnLink.getStyleClass().addAll("btn-table","btn-link");
                    btnLink.setPrefSize(24.0D, 24.0D);
                    btnLink.setMaxSize(24.0D, 24.0D);
                    btnLink.setOnAction(e -> {
                        if(!this.opened) {
                            AppConfig.openDocument(str);
                            imgViewLink.setImage(ar.imgLinkActivated);
                            btnLink.setGraphic(imgViewLink);
                            this.opened = true;
                            new Timeline(new KeyFrame(Duration.millis(1800.0D), eh -> {
                                imgViewLink.setImage(ar.imgLinkOpen);
                                btnLink.setGraphic(imgViewLink);
                                this.opened = false;
                            })).playFromStart();
                        }
                    });
                    final Tooltip tpLink = new Tooltip("Open link in web-browser");
                    Tooltip.install(btnLink, tpLink);
                    final HBox hbx = new HBox(10.0D, btnCopy, btnLink);
                    setGraphic(hbx);
                }
                setOnMouseExited(e -> {
                    this.opened = false;
                    this.copied = false;
                });
            }
        });

        final EncryptionService encryptionService = AppConfig.getEncryptionService();
        tcAccPass.setCellFactory(cf -> new TableCell<>() {
            private boolean copied = false, opened = false;
            private final ImageView imgViewCopy = new ImageView(ar.imgCopy);
            final Button btnCopy = new Button("", imgViewCopy);

            @Override
            protected void updateItem(final String str, final boolean empty) {
                if(empty || str == null || str.isEmpty()) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(null);
                    imgViewCopy.setFitHeight(20.0D);
                    imgViewCopy.setFitWidth(20.0D);
                    btnCopy.getStyleClass().add("btn-table");
                    btnCopy.setPrefSize(24.0D, 24.0D);
                    btnCopy.setMaxSize(24.0D, 24.0D);
                    btnCopy.setOnAction(e -> {
                        if(!this.copied) {
                            copyText(encryptionService.decrypt(str));
                            imgViewCopy.setImage(ar.imgCopied);
                            btnCopy.setGraphic(imgViewCopy);
                            this.copied = true;
                            new Timeline(new KeyFrame(Duration.millis(1800.0D), eh -> {
                                imgViewCopy.setImage(ar.imgCopy);
                                btnCopy.setGraphic(imgViewCopy);
                                this.copied = false;
                            })).playFromStart();
                        }
                    });
                    final Tooltip tp = new Tooltip("Copy password to clipboard");
                    Tooltip.install(btnCopy, tp);
                    final ImageView imgViewLink = new ImageView(ar.imgLinkOpen);
                    imgViewLink.setFitHeight(20.0D);
                    imgViewLink.setFitWidth(20.0D);
                    setGraphic(btnCopy);
                }
                setOnMouseExited(e -> {
                    this.opened = false;
                    this.copied = false;
                });
                setAlignment(Pos.CENTER);
            }
        });

        tcCreated.setCellFactory(rowData -> new TableCell<>() {
            @Override
            public void updateItem(final LocalDateTime createdDate, final boolean empty) {
                super.updateItem(createdDate, empty);
                if(!empty) {
                    setText(Utility.getFormatedDateTimeString(createdDate));
                    setAlignment(Pos.CENTER);
                } else {
                    setText(null);
                }
            }
        });

        final LabelService labelService = DbConfig.getLabelService();
        tcLabel.setCellFactory(rowData -> new TableCell<>() {
            @Override
            public void updateItem(final Long labelId, final boolean empty) {
                super.updateItem(labelId, empty);
                if(!empty) {
                    setText(labelService.getLabelEntityById(labelId).getLabelName());
                    setAlignment(Pos.CENTER);
                } else {
                    setText(null);
                }
            }
        });

        tcControls.setCellFactory(cellFactory -> new TableCell<>() {
            private final ImageView ivShowMore = new ImageView(ar.imgLinkOpen);
            private final ImageView ivEdit = new ImageView(ar.imgEdit);
            private final ImageView ivDelete = new ImageView(ar.imgDelete);
            private final Button btnShowMore = new Button();
            private final Button btnEdit = new Button();
            private final Button btnDelete = new Button();
            private final HBox hbxControls = new HBox(8.0D, btnShowMore, btnEdit, btnDelete);

            {
                ivShowMore.setFitHeight(20.0D);
                ivShowMore.setPreserveRatio(true);
                ivEdit.setFitHeight(20.0D);
                ivEdit.setPreserveRatio(true);
                ivDelete.setFitHeight(20.0D);
                ivDelete.setPreserveRatio(true);
                btnShowMore.getStyleClass().add("btn-table-edit");
                btnShowMore.setStyle("-fx-padding: 3px;");
                btnEdit.getStyleClass().add("btn-table-edit");
                btnEdit.setStyle("-fx-padding: 3px;");
                btnDelete.getStyleClass().add("btn-table-delete");
                btnDelete.setStyle("-fx-padding: 3px;");
                Tooltip.install(btnShowMore, new Tooltip("Show more"));
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
                        final AccountEntity data = AccountCredentialViewer.this.getItems().get(idx);
                        btnShowMore.setOnAction(event -> {
                            final AccountCustomFieldService service = DbConfig.getAccountCustomFieldService();
                            DetailedAccountInfoScreen.show(data, service.getAccountCustomFieldsForAccountId(data.getAccId()));
                        });
                        btnEdit.setOnAction(event -> {
                            final AccountCustomFieldService customFieldService = DbConfig.getAccountCustomFieldService();
                            final AccountService accountService = DbConfig.getAccountService();
                            EditAccountScreen.show(
                                data,
                                customFieldService.getAccountCustomFieldsForAccountId(data.getAccId()),
                                (updatedAccountEntity, updatedCustomFields) -> {
                                    accountService.updateAccountCredential(data, updatedAccountEntity);
                                    customFieldService.commit(data.getAccId(), updatedCustomFields);
                                    getItems().set(idx, updatedAccountEntity);
                                    AccountCredentialViewer.this.refresh();
                                }
                            );
                        });
                        btnDelete.setOnAction(event -> {
                            final AccountService accountService = DbConfig.getAccountService();
                            try {
                                accountService.deleteAccountCredential(data);
                                getItems().remove(data);
                                AccountCredentialViewer.this.refresh();
                            } catch (final Exception e) {
                                Utility.showErrorAlert("Delete operation failed", "Something went wrong.");
                            }
                        });
                        btnShowMore.setGraphic(ivShowMore);
                        btnEdit.setGraphic(ivEdit);
                        btnDelete.setGraphic(ivDelete);
                    }
                    setGraphic(hbxControls);
                }
            }
        });
    }

    public List<AccountEntity> getAllAccounts() {
        return allAccounts;
    }

}
