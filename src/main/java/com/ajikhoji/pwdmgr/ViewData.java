package com.ajikhoji.pwdmgr;

import com.ajikhoji.db.AccountDetail;
import com.ajikhoji.db.DataHandler;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.util.Callback;
import javafx.util.Duration;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class ViewData {

    private final static Pane pane = new Pane();
    private final static Label lblStatus = new Label();
    private final static String[] detailsEdit = new String[5], detailsOriginal = new String[5];
    private final static BooleanProperty changeAvailable = new SimpleBooleanProperty(false),
            update = new SimpleBooleanProperty(false), rollbackEnable = new SimpleBooleanProperty(false);
    private static AccountDetail adEdit;
    private final static Clipboard cp = Clipboard.getSystemClipboard();

    public static void init() {
        final Font fontCell = Font.font(Resource.getInstance().fontTitle.getFamily(), 16.0D);
        final double DBL_GAP = 15.0D, DBL_PANE_INITIAL_WIDTH = ContentPane.paneContent.getPrefWidth() - 2*DBL_GAP, DBL_PANE_INITIAL_HEIGHT = ContentPane.paneContent.getPrefHeight() - 2*DBL_GAP;
        final BooleanProperty editAccountDetail = new SimpleBooleanProperty(false);

        //Status Bar
        lblStatus.setFont(fontCell);
        lblStatus.setMouseTransparent(true);
        lblStatus.setLayoutX(0.0D);
        lblStatus.setFont(fontCell);
        lblStatus.getStyleClass().add("lbl-status");
        lblStatus.heightProperty().addListener((ol, ov, nv) -> {
            pane.setMinHeight(pane.getPrefHeight() - nv.doubleValue() - 2*DBL_GAP);
            pane.setMaxHeight(pane.getPrefHeight() - nv.doubleValue() - 2*DBL_GAP);
            pane.setPrefHeight(pane.getPrefHeight() - nv.doubleValue() - 2*DBL_GAP);
        });

        final Timeline tlStatus = new Timeline(new KeyFrame(Duration.millis(3300.0D)));
        tlStatus.setOnFinished(e -> {
            lblStatus.setText("");
        });

        pane.setMinSize(DBL_PANE_INITIAL_WIDTH, DBL_PANE_INITIAL_HEIGHT);
        pane.setMaxSize(DBL_PANE_INITIAL_WIDTH, DBL_PANE_INITIAL_HEIGHT);
        pane.setPrefSize(DBL_PANE_INITIAL_WIDTH, DBL_PANE_INITIAL_HEIGHT);
        pane.getStyleClass().add("info-pane");

        ContentPane.paneContent.widthProperty().addListener((ol, ov, nv) -> {
            pane.setMinWidth(nv.doubleValue() - 2*DBL_GAP);
            pane.setMaxWidth(nv.doubleValue() - 2*DBL_GAP);
            pane.setPrefWidth(nv.doubleValue() - 2*DBL_GAP);
            lblStatus.setMinWidth(nv.doubleValue());
            lblStatus.setMaxWidth(nv.doubleValue());
            lblStatus.setPrefWidth(nv.doubleValue());
        });
        ContentPane.paneContent.heightProperty().addListener((ol, ov, nv) -> {
            pane.setMinHeight(nv.doubleValue() - 2*DBL_GAP - lblStatus.getHeight());
            pane.setMaxHeight(nv.doubleValue() - 2*DBL_GAP - lblStatus.getHeight());
            pane.setPrefHeight(nv.doubleValue() - 2*DBL_GAP - lblStatus.getHeight());
        });
        pane.setLayoutX(DBL_GAP);
        pane.setLayoutY(DBL_GAP);

        //Table
        final double DBL_MARGIN = 20.0D;
        final TableView<AccountDetail> tblView = new TableView<>();
        tblView.setEditable(false);
        tblView.setMinSize(DBL_PANE_INITIAL_WIDTH - 2*DBL_MARGIN, DBL_PANE_INITIAL_HEIGHT - 3*DBL_MARGIN - 33.6D);// this '33.6D' is to adjust for 'hbOptions' height being -1.0D at first when app loads
        tblView.setMaxSize(DBL_PANE_INITIAL_WIDTH - 2*DBL_MARGIN, DBL_PANE_INITIAL_HEIGHT - 3*DBL_MARGIN - 33.6D);
        tblView.setPrefSize(DBL_PANE_INITIAL_WIDTH - 2*DBL_MARGIN, DBL_PANE_INITIAL_HEIGHT - 3*DBL_MARGIN - 33.6D);
        tblView.setLayoutX(DBL_MARGIN);
        tblView.setLayoutY(DBL_MARGIN);
        final BiFunction<String, String, TableColumn<AccountDetail, String>> CreateTableColumn = (strColumnHeader, strMapTo) -> {
            final TableColumn<AccountDetail, String> col = new TableColumn<>(strColumnHeader);
            tblView.getColumns().add(col);
            col.setEditable(false);
            col.setCellFactory(cf -> new TableCell<>() {
                @Override
                protected void updateItem(final String str, final boolean empty) {
                    setFont(fontCell);
                    if(empty || str == null || str.isEmpty()) {
                        setText(getIndex() < tblView.getItems().size() ? "-" : null);
                    } else {
                        final ImageView imgViewCopy = new ImageView(Resource.getInstance().imgCopy);
                        imgViewCopy.setFitHeight(20.0D);
                        imgViewCopy.setFitWidth(20.0D);
                        final Button btn = new Button("", imgViewCopy);
                        btn.getStyleClass().add("btn-table");
                        btn.setPrefSize(24.0D, 24.0D);
                        btn.setMaxSize(24.0D, 24.0D);
                        final Tooltip tp = new Tooltip("Copy to clipboard");
                        final Tooltip tpDone = new Tooltip("Copied!");
                        Tooltip.install(btn, tp);
                        btn.setOnAction(e -> {
                            tlStatus.stop();
                            lblStatus.setText("Copied to clipboard: " + str);
//                            System.out.println("copy to clipboard : " + str);
                            copyText(str);
                            tlStatus.playFrom(Duration.ZERO);
                            imgViewCopy.setImage(Resource.getInstance().imgCopied);
                            btn.setGraphic(imgViewCopy);
                            Tooltip.uninstall(btn, tp);
                            Tooltip.install(btn, tpDone);
                            //TO-DO : copy 'str' to clipboard
                        });
                        setOnMouseEntered(e -> {
                            if(getGraphic() != null) {
                                imgViewCopy.setImage(Resource.getInstance().imgCopy);
                                btn.setGraphic(imgViewCopy);
                                Tooltip.uninstall(btn, tpDone);
                                Tooltip.install(btn, tp);
                                setGraphic(null);
                            }
                        });
                        setOnMouseExited(e -> {
                            imgViewCopy.setImage(Resource.getInstance().imgCopy);
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
            col.setCellValueFactory(new PropertyValueFactory<>(strMapTo));
            return col;
        };

        final TableColumn<AccountDetail, String> colAccID =  CreateTableColumn.apply("Account ID", "strAccName");
        final TableColumn<AccountDetail, String> colPassword = CreateTableColumn.apply(" Password ", "strPassword");
        final TableColumn<AccountDetail, String> colDomain = CreateTableColumn.apply("Domain Name", "strDomain");
        final TableColumn<AccountDetail, String> colLink = CreateTableColumn.apply("Link", "strLink");
        final TableColumn<AccountDetail, String> colPurpose = CreateTableColumn.apply("Purpose", "strPurpose");

        colAccID.setMinWidth(110.0D);
        colPassword.setMinWidth(130.0D);
        colDomain.setMinWidth(130.0D);
        colPurpose.setMinWidth(90.0D);

        colPassword.setSortable(false);
        colLink.setSortable(false);
        colLink.setPrefWidth(66.0D);
        colLink.setResizable(false);
        colLink.setCellFactory(cf -> new TableCell<>() {
            private boolean copied = false, opened = false;
            @Override
            protected void updateItem(final String str, final boolean empty) {
                setFont(fontCell);
                if(empty || str == null || str.isEmpty()) {
                    setText(getIndex() < tblView.getItems().size() ? "-" : null);
                    setAlignment(Pos.CENTER);
                    setGraphic(null);
                } else {
                    setText(null);
                    final ImageView imgViewCopy = new ImageView(Resource.getInstance().imgCopy);
                    imgViewCopy.setFitHeight(20.0D);
                    imgViewCopy.setFitWidth(20.0D);
                    final Button btnCopy = new Button("", imgViewCopy);
                    btnCopy.getStyleClass().add("btn-table");
                    btnCopy.setPrefSize(24.0D, 24.0D);
                    btnCopy.setMaxSize(24.0D, 24.0D);
                    btnCopy.setOnAction(e -> {
                        if(!this.copied) {
                            tlStatus.stop();
                            //System.out.println("copy to clipboard : " + str);
                            copyText(str);
                            lblStatus.setText("Copied to clipboard: " + str);
                            tlStatus.playFrom(Duration.ZERO);
                            //TO-DO : copy link to clipboard
                            imgViewCopy.setImage(Resource.getInstance().imgCopied);
                            btnCopy.setGraphic(imgViewCopy);
                            this.copied = true;
                            new Timeline(new KeyFrame(Duration.millis(1800.0D), eh -> {
                                imgViewCopy.setImage(Resource.getInstance().imgCopy);
                                btnCopy.setGraphic(imgViewCopy);
                                this.copied = false;
                            })).playFromStart();
                        }
                    });
                    final Tooltip tp = new Tooltip("Copy to clipboard");
                    Tooltip.install(btnCopy, tp);
                    final ImageView imgViewLink = new ImageView(Resource.getInstance().imgOpenLink);
                    imgViewLink.setFitHeight(20.0D);
                    imgViewLink.setFitWidth(20.0D);
                    final Button btnLink = new Button("", imgViewLink);
                    btnLink.getStyleClass().addAll("btn-table","btn-link");
                    btnLink.setPrefSize(24.0D, 24.0D);
                    btnLink.setMaxSize(24.0D, 24.0D);
                    btnLink.setOnAction(e -> {
                        if(!this.opened) {
                            System.out.println("open link : " + str);
                            HelloApplication.hs.showDocument(str);
                            imgViewLink.setImage(Resource.getInstance().imgOpened);
                            btnLink.setGraphic(imgViewLink);
                            //TO-DO : open link 'str'
                            this.opened = true;
                            new Timeline(new KeyFrame(Duration.millis(1800.0D), eh -> {
                                imgViewLink.setImage(Resource.getInstance().imgOpenLink);
                                btnLink.setGraphic(imgViewLink);
                                this.opened = false;
                            })).playFromStart();
                        }
                    });
                    final Tooltip tpLink = new Tooltip("Open link in web-browser");
                    Tooltip.install(btnLink, tpLink);
                    final Pane p = new Pane();
                    p.setMaxSize(60.0D, 28.0D);
                    p.setPrefSize(60.0D, 28.0D);
                    p.setMinSize(60.0D, 28.0D);
                    p.setStyle("-fx-background : transparent;");
                    p.getChildren().addAll(btnCopy, btnLink);
                    btnCopy.setLayoutX(4.0D);
                    btnCopy.setLayoutY(2.0D);
                    btnLink.setLayoutX(32.0D);
                    btnCopy.setLayoutY(2.0D);
                    setGraphic(p);
                }
                setOnMouseExited(e -> {
                    this.opened = false;
                    this.copied = false;
                });
            }
        });

        final TableColumn<AccountDetail, LocalDateTime> colDateCreated = new TableColumn<>("Date Created");
        colDateCreated.setCellValueFactory(new PropertyValueFactory<>("ldtDateCreated"));
        colDateCreated.setPrefWidth(190.0D);
        colDateCreated.setResizable(false);

        final TableColumn<AccountDetail, LocalDateTime> colDateModified = new TableColumn<>("Last Modified");
        colDateModified.setCellValueFactory(new PropertyValueFactory<>("ldtDateModified"));
        colDateModified.setPrefWidth(190.0D);
        colDateModified.setResizable(false);
        tblView.getColumns().addAll(colDateCreated, colDateModified);

        int order = Settings.getTableColumnOrder();
        colAccID.setId((order%10) + ":Account ID");
        order /= 10;
        colPassword.setId((order%10) + ": Password ");
        order /= 10;
        colDomain.setId((order%10) + ":Domain Name");
        order /= 10;
        colLink.setId((order%10) + ":Link");
        order /= 10;
        colPurpose.setId((order%10) + ":Purpose");
        order /= 10;
        colDateCreated.setId((order%10) + ":Date Created");
        order /= 10;
        colDateModified.setId(order + ":Last Modified");

        colDateCreated.setCellFactory(c -> new TableCell<>() {
            private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            @Override
            protected void updateItem(LocalDateTime ltd, boolean empty) {
                setFont(fontCell);
                super.updateItem(ltd, empty);
                this.setText((ltd == null || empty) ? ((getIndex() < tblView.getItems().size()) ? "-" : null) : dtf.format(ltd));
                this.setAlignment(Pos.CENTER);
            }
        });
        colDateModified.setCellFactory(c -> new TableCell<>() {
            private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            @Override
            protected void updateItem(LocalDateTime ltd, boolean empty) {
                setFont(fontCell);
                super.updateItem(ltd, empty);
                this.setText((ltd == null || empty) ? ((getIndex() < tblView.getItems().size()) ? "-" : null) : dtf.format(ltd));
                this.setAlignment(Pos.CENTER);
            }
        });

        final BooleanProperty showPassword = new SimpleBooleanProperty(!Settings.getPasswordViewPreference());

        final ImageView imgShowPassword = new ImageView(showPassword.get() ? Resource.getInstance().imgEyeOpened : Resource.getInstance().imgEyeClosed);
        imgShowPassword.setFitWidth(16.0D);
        imgShowPassword.setFitHeight(16.0D);
        imgShowPassword.getProperties().put("index", 1);

        showPassword.addListener((ol, ov, nv) -> {
            if(nv) {
                colPassword.setCellFactory(cf -> new TableCell<>() {
                    @Override
                    protected void updateItem(final String str, final boolean empty) {
                        setFont(fontCell);
                        if(empty || str == null || str.isEmpty()) {
                            setText(getIndex() < tblView.getItems().size() ? "-" : null);
                        } else {
                            final ImageView imgViewCopy = new ImageView(Resource.getInstance().imgCopy);
                            imgViewCopy.setFitHeight(20.0D);
                            imgViewCopy.setFitWidth(20.0D);
                            final Button btn = new Button("", imgViewCopy);
                            btn.getStyleClass().add("btn-table");
                            btn.setPrefSize(24.0D, 24.0D);
                            btn.setMaxSize(24.0D, 24.0D);
                            final Tooltip tp = new Tooltip("Copy to clipboard");
                            final Tooltip tpDone = new Tooltip("Copied!");
                            Tooltip.install(btn, tp);
                            btn.setOnAction(e -> {
                                tlStatus.stop();
                                lblStatus.setText("Copied to clipboard: " + str);
//                            System.out.println("copy to clipboard : " + str);
                                copyText(str);
                                tlStatus.playFrom(Duration.ZERO);
                                imgViewCopy.setImage(Resource.getInstance().imgCopied);
                                btn.setGraphic(imgViewCopy);
                                Tooltip.uninstall(btn, tp);
                                Tooltip.install(btn, tpDone);
                                //TO-DO : copy 'str' to clipboard
                            });
                            setOnMouseEntered(e -> {
                                if(getGraphic() != null) {
                                    imgViewCopy.setImage(Resource.getInstance().imgCopy);
                                    btn.setGraphic(imgViewCopy);
                                    Tooltip.uninstall(btn, tpDone);
                                    Tooltip.install(btn, tp);
                                    setGraphic(null);
                                }
                            });
                            setOnMouseExited(e -> {
                                imgViewCopy.setImage(Resource.getInstance().imgCopy);
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
                imgShowPassword.setImage(Resource.getInstance().imgEyeOpened);
            } else {
                colPassword.setCellFactory(c -> new TableCell<>() {
                    @Override
                    protected void updateItem(String pwd, boolean empty) {
                        super.updateItem(pwd, empty);
                        this.setText(pwd == null ? "" : "*".repeat(pwd.length()));
                        this.setAlignment(Pos.CENTER_LEFT);
                    }
                });
                imgShowPassword.setImage(Resource.getInstance().imgEyeClosed);
            }
        });
        showPassword.set(!showPassword.get());
        Label lbl = new Label();
        lbl.setGraphic(imgShowPassword);
        colPassword.setGraphic(lbl);
        lbl.setMouseTransparent(true);

        final ImageView imgViewEdit = new ImageView(Resource.getInstance().imgEdit), imgViewDelete = new ImageView(Resource.getInstance().imgDelete);
        imgViewEdit.setFitWidth(25.0D);
        imgViewEdit.setFitHeight(25.0D);
        imgViewDelete.setFitWidth(25.0D);
        imgViewDelete.setFitHeight(25.0D);
        final MenuItem editItem = new MenuItem("Edit", imgViewEdit);
        final MenuItem removeItem = new MenuItem("Delete", imgViewDelete);
        final ContextMenu rowMenu = new ContextMenu(editItem, removeItem);

        final ComboBox<String> cbSearch = new ComboBox<>(FXCollections.observableArrayList("Account ID", "Domain Name", "Purpose", "No filter"));
        final TextField tfSearch = new TextField();
        final Button btnEdit = new Button("EDIT");
        final Button btnDelete = new Button("DELETE");
        final Button btnRestore = new Button("RESTORE");
        final Button btnRollBack = new Button("ROLLBACK");

        btnRestore.setFont(fontCell);
        btnRestore.setDisable(true);
        btnRestore.setOnAction(e -> {
            btnRestore.setDisable(true);
            final int intCountRestored = DataHandler.getInstance().restoreDeletedInfo();
            lblStatus.setText(intCountRestored + " deleted account detail" + ((intCountRestored == 1) ? " have" : "s has") + " been restored.");
            tlStatus.playFrom(Duration.ZERO);
            applyFilter(tblView, cbSearch.getSelectionModel().getSelectedItem(), tfSearch.getText());
        });

        btnRollBack.setFont(fontCell);
        rollbackEnable.addListener((ol, ov, nv) -> {
            btnRollBack.setDisable(!rollbackEnable.get());
        });
        btnRollBack.setDisable(true);
        btnRollBack.setOnAction(e -> {
            btnRestore.setDisable(true);
            rollbackEnable.set(false);
            lblStatus.setText("ROLLBACK DONE : Restored to previous session state.");
            tlStatus.playFrom(Duration.ZERO);
            DataHandler.getInstance().rollBackToOriginalState();
            btnRollBack.setDisable(true);
            applyFilter(tblView, cbSearch.getSelectionModel().getSelectedItem(), tfSearch.getText());
        });

        btnEdit.setFont(fontCell);
        btnEdit.setDisable(true);
        btnEdit.setOnAction(e -> {
//            btnDelete.setDisable(true);
//            btnEdit.setDisable(true);
            detailsEdit[0] = adEdit.getStrAccName();
            detailsEdit[1] = adEdit.getStrPassword();
            detailsEdit[2] = adEdit.getStrDomain();
            detailsEdit[3] = (adEdit.getStrLink() == null) ? "" : adEdit.getStrLink();
            detailsEdit[4] = adEdit.getStrPurpose();
            editAccountDetail.set(true);
        });

        btnDelete.setFont(fontCell);
        btnDelete.setDisable(true);
        btnDelete.setOnAction(e -> {
            btnDelete.setDisable(true);
            btnEdit.setDisable(true);
            btnRestore.setDisable(false);
            DataHandler.getInstance().deleteAccountInfo(adEdit);
            applyFilter(tblView, cbSearch.getSelectionModel().getSelectedItem(), tfSearch.getText());
        });

        cbSearch.setPromptText("Search On");
        cbSearch.setCursor(Cursor.HAND);
        cbSearch.setPrefWidth(156.0D);

        cbSearch.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(item);
                setFont(fontCell);
            }
        });
        cbSearch.setCellFactory(new Callback<>() {
            @Override
            public ListCell<String> call(ListView<String> l) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(item);
                        setFont(fontCell);
                    }
                };
            }
        });
        cbSearch.getSelectionModel().selectedItemProperty().addListener((ol, ov, nv) -> {
            tfSearch.setText("");
            tfSearch.setDisable(nv.equals("No filter"));
        });
        tfSearch.setDisable(true);
        tfSearch.setFont(Font.font(Resource.getInstance().fontNormal.getFamily(), 16.0D));
        tfSearch.setPromptText("Search text...");
        tfSearch.textProperty().addListener((ol, ov, nv) -> {
            applyFilter(tblView, cbSearch.getSelectionModel().getSelectedItem(), nv);
        });

        final HBox hbOptions = new HBox(cbSearch, tfSearch, btnEdit, btnDelete, btnRestore, btnRollBack);
        hbOptions.setLayoutX(DBL_MARGIN);
        hbOptions.setSpacing(10.0D);
        hbOptions.setPrefHeight(33.6D);
        hbOptions.setMinHeight(33.6D);
        hbOptions.setMaxHeight(33.6D);

        final double DBL_OPTION_PANE_GAP = 8.0D;
        hbOptions.setSpacing(DBL_OPTION_PANE_GAP);

        hbOptions.maxWidthProperty().addListener((ol, ov, nv) -> {
            tfSearch.setMinWidth(nv.doubleValue() - 466.0D - 5.5D*DBL_OPTION_PANE_GAP);
            tfSearch.setMaxWidth(nv.doubleValue() - 466.0D - 5.5D*DBL_OPTION_PANE_GAP);
            tfSearch.setPrefWidth(nv.doubleValue() - 466.0D - 5.5D*DBL_OPTION_PANE_GAP);
        });
        btnRollBack.heightProperty().addListener((ol, ov, nv) -> {
            cbSearch.setPrefHeight(nv.doubleValue());
            tfSearch.setPrefHeight(nv.doubleValue());
        });

        tblView.setRowFactory(tableView -> {
            final TableRow<AccountDetail> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                btnEdit.setDisable(false);
                btnDelete.setDisable(false);
                adEdit = tableView.getSelectionModel().getSelectedItem();
            });
            editItem.setOnAction(e -> {
                //TO-DO: Go to edit area
                adEdit = tableView.getSelectionModel().getSelectedItem();
                btnEdit.setDisable(false);
                btnDelete.setDisable(false);
                detailsEdit[0] = adEdit.getStrAccName();
                detailsEdit[1] = adEdit.getStrPassword();
                detailsEdit[2] = adEdit.getStrDomain();
                detailsEdit[3] = (adEdit.getStrLink() == null) ? "" : adEdit.getStrLink();
                detailsEdit[4] = adEdit.getStrPurpose();
                editAccountDetail.set(true);
            });
            removeItem.setOnAction(e -> {
                btnRestore.setDisable(false);
                DataHandler.getInstance().deleteAccountInfo(tableView.getSelectionModel().getSelectedItem());
                applyFilter(tblView, cbSearch.getSelectionModel().getSelectedItem(), tfSearch.getText());
//                tableView.getItems().remove(tableView.getSelectionModel().getSelectedItem());
            });
            // only display context menu for non-empty rows:
            row.contextMenuProperty().bind(Bindings.when(row.emptyProperty()).then((ContextMenu) null).otherwise(rowMenu));
            return row;
        });

        tblView.setOnMouseClicked(e -> {
            if ((e.getPickResult().getIntersectedNode() instanceof Label && ((Label)(e.getPickResult().getIntersectedNode())).getText().equals(" Password "))
                || (e.getPickResult().getIntersectedNode() instanceof Text && ((Text)(e.getPickResult().getIntersectedNode())).getText().equals(" Password "))) {
                showPassword.set(!showPassword.get());
            }
        });

        btnRollBack.heightProperty().addListener((ol, ov, nv) -> {
            hbOptions.setMinHeight(nv.doubleValue());
            hbOptions.setMaxHeight(nv.doubleValue());
            hbOptions.setPrefHeight(nv.doubleValue());
        });

        pane.widthProperty().addListener((ol, ov, nv) -> {
            tblView.setMinWidth(nv.doubleValue() - 2*DBL_MARGIN);
            tblView.setMaxWidth(nv.doubleValue() - 2*DBL_MARGIN);
            tblView.setPrefWidth(nv.doubleValue() - 2*DBL_MARGIN);
            hbOptions.setMinWidth(nv.doubleValue() - 2*DBL_MARGIN);
            hbOptions.setMaxWidth(nv.doubleValue() - 2*DBL_MARGIN);
            hbOptions.setPrefWidth(nv.doubleValue() - 2*DBL_MARGIN);
        });
        pane.heightProperty().addListener((ol,ov, nv) -> {
            tblView.setMinHeight(nv.doubleValue() - 2*DBL_MARGIN - hbOptions.getPrefHeight() - DBL_MARGIN);
            tblView.setMaxHeight(nv.doubleValue() - 2*DBL_MARGIN - hbOptions.getPrefHeight() - DBL_MARGIN);
            tblView.setPrefHeight(nv.doubleValue() - 2*DBL_MARGIN - hbOptions.getPrefHeight() - DBL_MARGIN);
            hbOptions.setLayoutY(tblView.getLayoutY() + tblView.getPrefHeight() + DBL_MARGIN);
        });

        //Editor
        final VBox vb = new VBox();
        vb.setAlignment(Pos.CENTER);
        vb.setLayoutX(20.0D);
        pane.widthProperty().addListener((ol, ov, nv) -> {
            vb.setMinWidth(nv.doubleValue() - 40.0D);
            vb.setMaxWidth(nv.doubleValue() - 40.0D);
            vb.setPrefWidth(nv.doubleValue() - 40.0D);
        });
        pane.heightProperty().addListener((ol, ov, nv) -> {
            vb.setMinHeight(nv.doubleValue());
            vb.setMaxHeight(nv.doubleValue());
            vb.setPrefHeight(nv.doubleValue());
        });
        final Text txtEdit = getTextNode("Edit Account Detail", Font.font(Resource.getInstance().fontTitle.getFamily(), 20.0D));
        final Text txtAccName = getTextNode("Account ID", Font.font(Resource.getInstance().fontNormal.getFamily(), 16.0D));
        final Text txtAccPassword = getTextNode("Password", Font.font(Resource.getInstance().fontNormal.getFamily(), 16.0D));
        final Text txtAccDomain = getTextNode("Domain Name", Font.font(Resource.getInstance().fontNormal.getFamily(), 16.0D));
        final Text txtAccLink = getTextNode("Domain Link", Font.font(Resource.getInstance().fontNormal.getFamily(), 16.0D));
        final Text txtAccPurpose = getTextNode("Purpose", Font.font(Resource.getInstance().fontNormal.getFamily(), 16.0D));

        final TextField tfAccName = getTextFieldNode(0, Font.font(Resource.getInstance().fontNormal.getFamily(), 16.0D), 60, "The field 'Account ID' has reached maximum number of 60 characters.");
        final TextField tfPassword = getTextFieldNode(1, Font.font(Resource.getInstance().fontNormal.getFamily(), 16.0D), 60, "The field 'Password' has reached maximum number of 60 characters.");
        final TextField tfDomain = getTextFieldNode(2, Font.font(Resource.getInstance().fontNormal.getFamily(), 16.0D), 60, "The field 'Domain Name' has reached maximum number of 60 characters.");
        final TextField tfLink = getTextFieldNode(3, Font.font(Resource.getInstance().fontNormal.getFamily(), 16.0D), 300, "The field 'Domain Link' has reached maximum number of 300 characters.");
        final TextField tfPurpose = getTextFieldNode(4, Font.font(Resource.getInstance().fontNormal.getFamily(), 16.0D), 60, "The field 'Purpose' has reached maximum number of 60 characters.");

        final Button btnCancelEdit = new Button("CANCEL");
        btnCancelEdit.setOnAction(e -> editAccountDetail.set(false));

        final Button btnSaveEdit = new Button("SAVE");
        btnSaveEdit.setOnAction(e -> {
            if(detailsEdit[0].isEmpty()) {
                lblStatus.setText("Account ID should be filled.");
            } else if (detailsEdit[1].isEmpty()) {
                lblStatus.setText("The field 'Password' should not be empty.");
            } else if (detailsEdit[2].isEmpty()) {
                lblStatus.setText("The field 'Domain Name' should not be empty.");
            } else if(detailsEdit[4].isEmpty()) {
                lblStatus.setText("The field 'Purpose' should be filled.");
            } else if(detailsEdit[0].equals(detailsOriginal[0]) && detailsEdit[1].equals(detailsOriginal[1]) && detailsEdit[2].equals(detailsOriginal[2])
                         && detailsEdit[4].equals(detailsOriginal[4]) && !detailsEdit[3].equals(detailsOriginal[3])) {
                //TO-DO : Update Link
                final String strResult = DataHandler.getInstance().editAccInfo(adEdit, detailsEdit[0], detailsEdit[1], detailsEdit[2], detailsEdit[3], detailsEdit[4]);
                lblStatus.setText(strResult);
                if(strResult.equals("Account Detail modified successfully!")) {
                    tlStatus.playFrom(Duration.ZERO);
                    editAccountDetail.set(false);
                }
            } else {
                //TO-DO: Edit
                final String strResult = DataHandler.getInstance().editAccInfo(adEdit, detailsEdit[0], detailsEdit[1], detailsEdit[2], detailsEdit[3], detailsEdit[4]);
                lblStatus.setText(strResult);tlStatus.playFrom(Duration.ZERO);
                if(strResult.equals("Account Detail modified successfully!")) {
                    tlStatus.playFrom(Duration.ZERO);
                    editAccountDetail.set(false);
                }
            }
        });

        final VBox v1 = new VBox(txtAccName, tfAccName), v2 = new VBox(txtAccPassword, tfPassword), v3 = new VBox(txtAccDomain,tfDomain),
                v4 = new VBox(txtAccLink, tfLink), v5 = new VBox(txtAccPurpose, tfPurpose);
        final HBox h = new HBox(btnCancelEdit, btnSaveEdit);
        h.setAlignment(Pos.CENTER);

        vb.setVisible(false);

        vb.getChildren().addAll(txtEdit, v1, v2, v3, v4, v5, h);
        btnSaveEdit.widthProperty().addListener((ol, ov, nv) -> h.setSpacing((h.getWidth() - (btnCancelEdit.getWidth() + nv.doubleValue()))/3.0D));
        h.widthProperty().addListener((ol, ov, nv) -> h.setSpacing((nv.doubleValue() - (btnCancelEdit.getWidth() + btnSaveEdit.getWidth()))/3.0D));
        h.heightProperty().addListener((ol, ov, nv) -> vb.setSpacing((vb.getPrefHeight() - (txtEdit.getBoundsInLocal().getHeight() + 5*v4.getHeight() + nv.doubleValue()))/8.0D));
        vb.heightProperty().addListener((ol, ov, nv) -> vb.setSpacing((nv.doubleValue() - (txtEdit.getBoundsInLocal().getHeight() + 5*v4.getHeight() + h.getHeight()))/8.0D));

        editAccountDetail.addListener((ol, ov, nv) -> {
            tblView.setVisible(!nv);
            hbOptions.setVisible(!nv);
            vb.setVisible(nv);
            if(nv) {
                tfAccName.setText(detailsEdit[0]);
                tfPassword.setText(detailsEdit[1]);
                tfDomain.setText(detailsEdit[2]);
                tfLink.setText(detailsEdit[3]);
                tfPurpose.setText(detailsEdit[4]);
                System.arraycopy(detailsEdit, 0, detailsOriginal, 0, 5);
                lblStatus.setText("No modifications made");
                btnSaveEdit.setDisable(true);
                changeAvailable.set(false);
                tfAccName.requestFocus();
            } else {
                applyFilter(tblView, cbSearch.getSelectionModel().getSelectedItem(), tfSearch.getText());
            }
        });
        changeAvailable.addListener((ol, ov, nv) -> {
            btnSaveEdit.setDisable(!nv);
        });

        pane.getChildren().addAll(tblView, hbOptions, vb);
        ContentPane.paneContent.getChildren().add(pane);

        update.addListener((ol, ov, nv) -> {
            tblView.setItems(FXCollections.observableArrayList(DataHandler.getInstance().currentAccDetails));
            tblView.refresh();
        });

        tblView.getColumns().addListener((ListChangeListener<? super TableColumn<AccountDetail, ?>>) (l) -> {
            while(l.next()) {
                final Map<String, Integer> orderMapping = HashMap.newHashMap(7);
                for(final var x: l.getAddedSubList()) {
                    orderMapping.put(x.getId().split(":")[1], orderMapping.size() + 1);
                }
                if(orderMapping.size() == 7) {
                    final int newOrder = orderMapping.get("Last Modified")*1_000_000 + orderMapping.get("Date Created")*100_000
                            + orderMapping.get("Purpose")*10_000 + orderMapping.get("Link")*1_000
                            + orderMapping.get("Domain Name")*100 + orderMapping.get(" Password ")*10 + orderMapping.get("Account ID");
                    //TO-DO: Update 'order' value in settings
                    Settings.setTableColumnOrder(newOrder);
                }
            }
        });
        if(!DataHandler.getInstance().getViewTableColumnDefaultPreference()) {
            tblView.getColumns().sort(Comparator.comparingInt(x -> Integer.parseInt(x.getId().split(":")[0])));
        }
    }

    private static TextField getTextFieldNode(int detailPos, final Font ff, final int maxLen,final String strPrompt) {
        final TextField tf = new TextField(detailsEdit[detailPos]);
        tf.setFont(ff);
        tf.textProperty().addListener((olt, ovt, nvt) -> {
            if(nvt != null) {
                boolean maxLenCrossed = false;
                if (nvt.length() > maxLen) {
                    maxLenCrossed = true;
                    lblStatus.setText(strPrompt);
                    tf.textProperty().setValue(nvt.substring(0, maxLen));
                }
                if (ovt != null && ovt.length() <= maxLen) {
                    detailsEdit[detailPos] = tf.getText();
                    final String[] edited = new String[5];
                    int filled = 0;
                    if (!detailsEdit[0].equals(detailsOriginal[0])) {
                        edited[filled++] = "Account ID";
                    }
                    if (!detailsEdit[1].equals(detailsOriginal[1])) {
                        edited[filled++] = "Password";
                    }
                    if (!detailsEdit[2].equals(detailsOriginal[2])) {
                        edited[filled++] = "Domain Name";
                    }
                    if (!detailsEdit[3].equals(detailsOriginal[3])) {
                        edited[filled++] = "Domain Link";
                    }
                    if (!detailsEdit[4].equals(detailsOriginal[4])) {
                        edited[filled++] = "Purpose";
                    }
                    if (!maxLenCrossed) {
                        if (filled == 0) {
                            lblStatus.setText("No modifications made");
                        } else if (filled == 1) {
                            lblStatus.setText("Modified : " + edited[0]);
                        } else {
                            final StringBuilder sb = new StringBuilder("Modified : ");
                            for (int i = 0; i < filled - 2; ++i) {
                                sb.append(edited[i]).append(", ");
                            }
                            sb.append(edited[filled - 2]).append(" and ").append(edited[filled - 1]);
                            lblStatus.setText(sb.toString());
                        }
                    }
                    changeAvailable.set(filled != 0);
                }
            }
        });
        return tf;
    }

    private static void applyFilter(final TableView<AccountDetail> tv, final String strField, final String strFilterText) {
        if(strField == null || tv == null || strFilterText == null) return;
        switch (strField) {
            case "Account ID" ->
                    tv.setItems(FXCollections.observableArrayList(DataHandler.getInstance().currentAccDetails.stream()
                            .filter(ad -> ad.getStrAccName().toLowerCase().contains(strFilterText.toLowerCase())).sorted((ad1, ad2) -> {
                                if (ad1.getStrAccName().startsWith(strFilterText)) return -1;
                                else if (ad1.getStrAccName().endsWith(strFilterText)) return 1;
                                return ad1.getStrAccName().compareTo(ad2.getStrAccName());
                            }).toList()
                    ));
            case "Domain Name" ->
                    tv.setItems(FXCollections.observableArrayList(DataHandler.getInstance().currentAccDetails.stream()
                            .filter(ad -> ad.getStrDomain().toLowerCase().contains(strFilterText.toLowerCase())).sorted((ad1, ad2) -> {
                                if (ad1.getStrDomain().startsWith(strFilterText)) return -1;
                                else if (ad1.getStrDomain().endsWith(strFilterText)) return 1;
                                return ad1.getStrDomain().compareTo(ad2.getStrDomain());
                            }).toList()
                    ));
            case "Purpose" ->
                    tv.setItems(FXCollections.observableArrayList(DataHandler.getInstance().currentAccDetails.stream()
                            .filter(ad -> ad.getStrPurpose().toLowerCase().contains(strFilterText.toLowerCase())).sorted((ad1, ad2) -> {
                                if (ad1.getStrPurpose().startsWith(strFilterText)) return -1;
                                else if (ad1.getStrPurpose().endsWith(strFilterText)) return 1;
                                return ad1.getStrPurpose().compareTo(ad2.getStrPurpose());
                            }).toList()
                    ));
            default -> tv.setItems(FXCollections.observableArrayList(DataHandler.getInstance().currentAccDetails));
        }
        tv.refresh();
    }

    private static Text getTextNode(final String txt, final Font ff) {
        final Text t = new Text(txt);
        t.setFont(ff);
        t.getStyleClass().add("text-label");
        return t;
    }

    private static void copyText(final String str) {
        final Map<DataFormat, Object> copyMap = new HashMap<>();
        copyMap.put(DataFormat.PLAIN_TEXT, str);
        cp.setContent(copyMap);
    }

    public static void updateTable() {
        update.set(!update.get());
    }

    public static void enableRollBack() {
        rollbackEnable.set(true);
    }

    public static void disableRollBack() {
        rollbackEnable.set(false);
    }

    public static Label getStatusBar() {
        return lblStatus;
    }

    public static Pane getPane() {
        return pane;
    }
}
