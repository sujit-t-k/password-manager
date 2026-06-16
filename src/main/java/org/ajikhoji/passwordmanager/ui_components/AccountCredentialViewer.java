package org.ajikhoji.passwordmanager.ui_components;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.ajikhoji.passwordmanager.config.AppConfig;
import org.ajikhoji.passwordmanager.config.AppResources;
import org.ajikhoji.passwordmanager.config.DbConfig;
import org.ajikhoji.passwordmanager.model.AccountEntity;
import org.ajikhoji.passwordmanager.repository.OpenLinkButtonActionCustomizable;
import org.ajikhoji.passwordmanager.repository.TableFieldsPreferenceRememberable;
import org.ajikhoji.passwordmanager.security.EncryptionService;
import org.ajikhoji.passwordmanager.service.AccountCustomFieldService;
import org.ajikhoji.passwordmanager.service.AccountService;
import org.ajikhoji.passwordmanager.service.LabelService;
import org.ajikhoji.passwordmanager.service.SettingService;
import org.ajikhoji.passwordmanager.util.ClipboardCopyUtil;
import org.ajikhoji.passwordmanager.util.Utility;
import org.ajikhoji.passwordmanager.view.DetailedAccountInfoScreen;
import org.ajikhoji.passwordmanager.view.EditAccountScreen;
import org.ajikhoji.passwordmanager.view_secondary.AccountDeletionConfirmation;

import java.time.LocalDateTime;
import java.util.List;

import static org.ajikhoji.passwordmanager.util.ClipboardCopyUtil.copyText;

public class AccountCredentialViewer extends TableView<AccountEntity> {

    private final List<AccountEntity> allAccounts;
    private final EncryptionService encryptionService;

    public AccountCredentialViewer() {
        encryptionService = AppConfig.getEncryptionService();
        final AccountService accountService = DbConfig.getAccountService();
        allAccounts = accountService.getAllAccountCredential();
        setItems(FXCollections.observableArrayList(allAccounts));
        setEditable(false);
        setPlaceholder(new Label("No records available"));

        final TableColumn<AccountEntity, String> tcAccName = new TableColumn<>("Account ID/Name");
        final TableColumn<AccountEntity, String> tcAccPass = new TableColumn<>("Password");
        final TableColumn<AccountEntity, String> tcPlatform = new TableColumn<>("Platform");
        final TableColumn<AccountEntity, String> tcLink = new TableColumn<>("Link");
        final TableColumn<AccountEntity, LocalDateTime> tcCreated = new TableColumn<>("Added");
        final TableColumn<AccountEntity, Long> tcLabel = new TableColumn<>("Label");
        final TableColumn<AccountEntity, Void> tcControls = new TableColumn<>("Actions");
        final TableColumn<AccountEntity, LocalDateTime> tcLastUsed = new TableColumn<>("Last Used");
        final TableColumn<AccountEntity, LocalDateTime> tcRecentUpdated = new TableColumn<>("Recent updated");
        final TableColumn<AccountEntity, Integer> tcUsageCount = new TableColumn<>("Usage");

        tcAccName.setCellValueFactory(new PropertyValueFactory<>("accName"));
        tcAccPass.setCellValueFactory(new PropertyValueFactory<>("accPassword"));
        tcPlatform.setCellValueFactory(new PropertyValueFactory<>("platform"));
        tcLink.setCellValueFactory(new PropertyValueFactory<>("link"));
        tcLabel.setCellValueFactory(new PropertyValueFactory<>("labelId"));
        tcCreated.setCellValueFactory(new PropertyValueFactory<>("createdDateTime"));
        tcLastUsed.setCellValueFactory(new PropertyValueFactory<>("lastUsedDateTime"));
        tcRecentUpdated.setCellValueFactory(new PropertyValueFactory<>("recentUpdateDateTime"));
        tcUsageCount.setCellValueFactory(new PropertyValueFactory<>("usageCount"));

        tcControls.setSortable(false);
        tcControls.setPrefWidth(120.0D);
        tcControls.setResizable(false);
        tcLink.setSortable(false);
        tcLink.setPrefWidth(86.0D);
        tcLink.setResizable(false);
        tcAccPass.setSortable(false);
        tcAccPass.setPrefWidth(Utility.computeTextWidth("Password", Font.font(16.0D)) * 1.4D);
        tcAccPass.setResizable(false);

        final TableColumn<AccountEntity, ?>[] allFields = new TableColumn[] {tcControls, tcAccName,
                tcAccPass, tcPlatform, tcLabel, tcLink, tcCreated, tcLastUsed, tcRecentUpdated, tcUsageCount};

        final SettingService settingService = DbConfig.getSettingService();
        long fieldOrder = settingService.getTableFieldsOrder();
        boolean disableReordering = false;
        if(fieldOrder < 0) {
            fieldOrder *= -1;
            disableReordering = true;
        }
        for (int i = 0; i < allFields.length; ++i) {
            allFields[i].setId(String.valueOf(i));
            if(disableReordering) {
                allFields[i].setReorderable(false);
            }
        }

        int fieldsCount = TableFieldsPreferenceRememberable.getFieldsCount(fieldOrder);
        long order = Utility.reverse(Utility.getFieldOrderWithoutCount(fieldOrder));
        while (fieldsCount-- > 0) {
            final int fieldMappingId = (int) (order % 10);
            getColumns().add(allFields[fieldMappingId]);
            order /= 10;
        }

        final AppResources ar = AppConfig.getAppResources();
        tcAccName.setCellFactory(cellFactory -> new TableCell<>() {
            @Override
            public void updateItem(final String str, final boolean empty) {
            if(empty || str == null || str.isEmpty()) {
                setText(getIndex() < getTableView().getItems().size() ? "-" : null);
            } else {
                final ImageView imgViewCopy = new ImageView(ar.imgCopy);
                imgViewCopy.setFitHeight(20.0D);
                imgViewCopy.setFitWidth(20.0D);
                final Button btn = new Button("", imgViewCopy);
                btn.getStyleClass().add("btn-copy");
                btn.setPrefSize(24.0D, 24.0D);
                btn.setMaxSize(24.0D, 24.0D);
                final Tooltip tp = new Tooltip("Copy to clipboard");
                final Tooltip tpDone = new Tooltip("Copied!");
                Tooltip.install(btn, tp);
                btn.setOnAction(e -> {
                    copy(getIndex(), ClipboardCopyUtil.ContentType.ACCOUNT_ID);
                    imgViewCopy.setImage(ar.imgCopied);
                    btn.setGraphic(imgViewCopy);
                    Tooltip.uninstall(btn, tp);
                    Tooltip.install(btn, tpDone);
                });
                setOnMouseEntered(e -> {
                    if(getGraphic() != null) {
                        imgViewCopy.setImage(ar.imgCopy);
                        btn.setGraphic(imgViewCopy);
                        Tooltip.uninstall(btn, tpDone);
                        Tooltip.install(btn, tp);
                        setGraphic(null);
                    }
                });
                setOnMouseExited(e -> {
                    imgViewCopy.setImage(ar.imgCopy);
                    btn.setGraphic(imgViewCopy);
                    Tooltip.uninstall(btn, tpDone);
                    Tooltip.install(btn, tp);
                    setGraphic(null);
                });
                setOnMouseEntered(e -> {
                    setGraphic(btn);
                });
                setText(str);
            }
            }
        });

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
                    btnCopy.getStyleClass().add("btn-copy");
                    btnCopy.setPrefSize(24.0D, 24.0D);
                    btnCopy.setMaxSize(24.0D, 24.0D);
                    btnCopy.setOnAction(e -> {
                        if(!this.copied) {
                            copy(getIndex(), ClipboardCopyUtil.ContentType.PASSWORD);
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
                    final VBox vbx = new VBox(btnCopy);
                    vbx.setAlignment(Pos.CENTER);
                    setGraphic(vbx);
                }
                setOnMouseExited(e -> {
                    this.opened = false;
                    this.copied = false;
                });
            }
        });

        tcLink.setCellFactory(cf -> new TableCell<>() {
            private boolean copied = false, opened = false;
            @Override
            protected void updateItem(final String str, final boolean empty) {
                if(empty || str == null || str.isEmpty()) {
                    setText(getIndex() < getItems().size() ? "-" : null);
                    setGraphic(null);
                } else {
                    setText(null);
                    final ImageView imgViewCopy = new ImageView(ar.imgCopy);
                    imgViewCopy.setFitHeight(20.0D);
                    imgViewCopy.setFitWidth(20.0D);
                    final Button btnCopy = new Button("", imgViewCopy);
                    btnCopy.getStyleClass().add("btn-copy");
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
                    btnLink.getStyleClass().add("btn-link");
                    btnLink.setPrefSize(24.0D, 24.0D);
                    btnLink.setMaxSize(24.0D, 24.0D);
                    btnLink.setOnAction(e -> {
                        if(!this.opened) {
                            AppConfig.openDocument(str);
                            imgViewLink.setImage(ar.imgLinkActivated);
                            btnLink.setGraphic(imgViewLink);
                            this.opened = true;

                            final int idx = getIndex();
                            final AccountEntity currEntity = getItems().get(idx);
                            if(currEntity != null) {
                                final OpenLinkButtonActionCustomizable.LinkActionOption preference = settingService.getOpenLinkActionPreference();
                                if (preference.equals(OpenLinkButtonActionCustomizable.LinkActionOption.OPEN_IN_BROWSER_AND_COPY_ACC_ID)) {
                                    copy(getIndex(), ClipboardCopyUtil.ContentType.ACCOUNT_ID);
                                } else if (preference.equals(OpenLinkButtonActionCustomizable.LinkActionOption.OPEN_IN_BROWSER_AND_COPY_ACC_PASS)) {
                                    copy(getIndex(), ClipboardCopyUtil.ContentType.PASSWORD);
                                }
                            }

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

        tcCreated.setCellFactory(rowData -> new TableCell<>() {
            @Override
            public void updateItem(final LocalDateTime createdDate, final boolean empty) {
                super.updateItem(createdDate, empty);
                if(!empty) {
                    setText(Utility.getFormatedDateTimeString(createdDate));
                } else {
                    setText(null);
                }
            }
        });
        tcRecentUpdated.setCellFactory(rowData -> new TableCell<>() {
            @Override
            public void updateItem(final LocalDateTime recentUpdatedDate, final boolean empty) {
                super.updateItem(recentUpdatedDate, empty);
                if(!empty) {
                    if(recentUpdatedDate == null) {
                        setText("-");
                    } else {
                        setText(Utility.getFormatedDateTimeString(recentUpdatedDate));
                    }
                } else {
                    setText(null);
                }
            }
        });
        tcLastUsed.setCellFactory(rowData -> new TableCell<>() {
            @Override
            public void updateItem(final LocalDateTime lastUsedDate, final boolean empty) {
                super.updateItem(lastUsedDate, empty);
                if(!empty) {
                    if(lastUsedDate == null) {
                        setText("-");
                    } else {
                        setText(Utility.getFormatedDateTimeString(lastUsedDate));
                    }
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
                } else {
                    setText(null);
                }
            }
        });

        tcControls.setCellFactory(cellFactory -> new TableCell<>() {
            private final ImageView ivShowMore = new ImageView(ar.imgInfo);
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
                btnShowMore.getStyleClass().add("btn-img");
                btnEdit.getStyleClass().add("btn-img");
                btnDelete.getStyleClass().add("btn-img-delete");
                Tooltip.install(btnShowMore, new Tooltip("Show detailed information"));
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
                            AccountDeletionConfirmation.show(
                                data.getAccName(),
                                data.getPlatform(),
                                () -> {
                                    try {
                                        accountService.deleteAccountCredential(data);
                                        getItems().remove(data);
                                        AccountCredentialViewer.this.refresh();
                                    } catch (final Exception e) {
                                        Utility.showErrorAlert("Delete operation failed", "Something went wrong.");
                                    }
                                }
                            );
                        });
                        btnShowMore.setGraphic(ivShowMore);
                        btnEdit.setGraphic(ivEdit);
                        btnDelete.setGraphic(ivDelete);
                    }
                    setGraphic(hbxControls);
                }
            }
        });

        getColumns().addListener((ListChangeListener<TableColumn<AccountEntity, ?>>) change -> {
            long newOrder = 0;
            for (final TableColumn<AccountEntity, ?> field : AccountCredentialViewer.this.getColumns()) {
                newOrder = (newOrder * 10) + Long.parseLong(field.getId());
            }
            final long orderWithCount = TableFieldsPreferenceRememberable.getUpdatedFieldsCount(newOrder, AccountCredentialViewer.this.getColumns().size());
            settingService.saveTableFieldsOrderPreference(orderWithCount);
        });
    }

    private void copy(final int idx, final ClipboardCopyUtil.ContentType contentType) {
        final AccountEntity accountEntity = getItems().get(idx);
        final String content = switch (contentType) {
            case ACCOUNT_ID -> accountEntity.getAccName();
            case PASSWORD -> encryptionService.decrypt(accountEntity.getAccPassword());
            default -> accountEntity.getLink();
        };

        copyText(
            content,
            contentType,
            accountEntity.getAccId(),
            () -> {
                final AccountEntity updatedEntity = AccountEntity.Builder()
                        .withAccountId(accountEntity.getAccId())
                        .withAccountName(accountEntity.getAccName())
                        .withEncryptedAccountPassword(accountEntity.getAccPassword())
                        .withLabelId(accountEntity.getLabelId())
                        .withAccountPlatform(accountEntity.getPlatform())
                        .withLink(accountEntity.getLink())
                        .withCreatedDateTime(accountEntity.getCreatedDateTime())
                        .withLastUpdatedDateTime(accountEntity.getRecentUpdateDateTime())
                        .withUsageCount(accountEntity.getUsageCount() + 1)
                        .withLastUsedDateTime(LocalDateTime.now())
                        .build();
                try {
                    DbConfig.getAccountService().updateAccountCredential(accountEntity, updatedEntity);
                    getItems().set(idx, updatedEntity);
                    AccountCredentialViewer.this.refresh();
                } catch (final Exception ignored) { }
            }
        );
    }

    public List<AccountEntity> getAllAccounts() {
        return allAccounts;
    }

}
