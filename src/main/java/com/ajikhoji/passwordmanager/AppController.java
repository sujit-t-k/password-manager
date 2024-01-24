package com.ajikhoji.passwordmanager;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class AppController {

    @FXML
    private Pane paneBase;
    @FXML
    private ScrollPane paneContent;
    @FXML
    private Circle circleClose, circleMinimize;
    @FXML
    private VBox vBoxOptions;
    private Stage stage;
    private double dbl_screen_loc_x, dbl_screen_loc_y;
    private final double DBL_SCREEN_WIDTH = Screen.getPrimary().getVisualBounds().getWidth(), DBL_SCREEN_HEIGHT = Screen.getPrimary().getVisualBounds().getHeight();
    private boolean blnUpdateMouseIcon = true, blnShowPasswordViewTable = true, blnShowPasswordFieldViewTable = true,
                    blnShowPwdKeyEventAdded = false, blnInViewTable = false, blnViewTableUpdate = false;
    private Timeline t = new Timeline();
    private DataBaseHandler dbh;
    private ObservableList<AccountDetail> entries;
    private ImageView imgShowPassword = null;
    private TableColumn<AccountDetail, String> colAccPwd = null;
    private final HashMap<Integer, boolean[]> hMapIDRecordUpdated = new HashMap<>();
    private StagesPaneContainer spc = null;
    private final SimpleBooleanProperty blnEnableUpdateButton = new SimpleBooleanProperty(), blnEnableDeleteButton = new SimpleBooleanProperty(), blnEnableRevertAllButton = new SimpleBooleanProperty();

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            this.dbh = new DataBaseHandler();
            this.dbh.initialize();
            this.stage = (Stage) this.paneBase.getScene().getWindow();
            this.dbl_screen_loc_x = stage.getX();
            this.dbl_screen_loc_y = stage.getY();
            Tooltip tipClose = new Tooltip("CLOSE"), tipMinimize = new Tooltip("MINIMIZE");
            tipClose.setStyle("-fx-background: rgb(255,201,14);-fx-text-fill: rgba(30,30,30);-fx-background-color: rgba(255,201,14,0.8);-fx-font-size:12px;");
            tipMinimize.setStyle("-fx-background: rgb(255,201,14);-fx-text-fill: rgba(30,30,30);-fx-background-color: rgba(255,201,14,0.8);-fx-font-size:12px;");
            Tooltip.install(this.circleClose, tipClose);
            Tooltip.install(this.circleMinimize, tipMinimize);
            this.circleMinimize.setOnMouseEntered(e -> {
                this.circleMinimize.setRadius(14.0D);
                this.circleMinimize.setFill(Color.web("#3BCEFC"));
            });
            this.circleMinimize.setOnMouseExited(e -> {
                this.circleMinimize.setRadius(11.0D);
                this.circleMinimize.setFill(Color.web("#1E90FF"));
            });
            this.circleMinimize.setOnMousePressed(e -> {
                this.stage.setIconified(true);
            });
            this.circleClose.setOnMouseEntered(e -> {
                this.circleClose.setRadius(14.0D);
                this.circleClose.setFill(Color.web("#FFA16E"));
            });
            this.circleClose.setOnMouseExited(e -> {
                this.circleClose.setRadius(11.0D);
                this.circleClose.setFill(Color.web("#FF1F1F"));
            });
            this.circleClose.setOnMousePressed(e -> {
                this.dbh.closeServer();
                Platform.exit();
            });
            this.spc = new StagesPaneContainer();
            this.spc.buildStagesPane();
            final Text txtWelcome = this.getTextNode("Welcome!",136.0D, 196.0D, 120.0D, "Calibri", "#3E0942", true),
                    txtVersion = this.getTextNode("v 1.0.3",355.0D, 296.0D, 20.0D, "Calibri", "#eeddbb", true);
            final Rectangle rectAppTitle = new Rectangle(193.5D, 225.0D, 400.0D, 90.0D);
            rectAppTitle.setFill(Color.web("#3E0942"));
            this.paneContent.setContent(new Pane(txtWelcome, rectAppTitle, txtVersion,
                this.getTextNode("Password Manager",220.0D, 266.0D, 40.0D, "Consolas", "#eeddbb", true)));
            final Timeline tStarting = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(txtWelcome.scaleXProperty(), 0.3D)),
                    new KeyFrame(Duration.ZERO, new KeyValue(txtWelcome.scaleYProperty(), 0.3D)),
                    new KeyFrame(Duration.millis(900.0D), new KeyValue(txtWelcome.scaleXProperty(), 1.2D)),
                    new KeyFrame(Duration.millis(900.0D), new KeyValue(txtWelcome.scaleYProperty(), 1.2D)),
                    new KeyFrame(Duration.millis(1200.0D), new KeyValue(txtWelcome.scaleXProperty(), 1.0D)),
                    new KeyFrame(Duration.millis(1200.0D), new KeyValue(txtWelcome.scaleYProperty(), 1.0D))
            );
            tStarting.playFromStart();
            final AtomicReference<Pane> previousPaneSelected = new AtomicReference<>();
            final BiFunction<String, Image, Pane> buildMenuItem = (txt, img) -> {
               Label lblMenu = new Label(txt);
               lblMenu.setFont(new Font("Times New Roman", 16.0D));
               lblMenu.setLayoutX(0.0d);
               lblMenu.setLayoutY(75.0D);
               lblMenu.setPrefWidth(100.0D);
               lblMenu.setAlignment(Pos.CENTER);
               lblMenu.setTextFill(Color.web("#C8BFE7"));
               ColorAdjust ca = new ColorAdjust();
               ca.setHue(-0.37362D);
               ca.setSaturation(0.86742D);
               ca.setBrightness(0.763D);
               ImageView imgMenu = new ImageView(img);
               imgMenu.setEffect(ca);
               imgMenu.setFitWidth(50.0D);
               imgMenu.setFitHeight(50.0D);
               imgMenu.setLayoutX(25.0D);
               imgMenu.setLayoutY(20.0D);
                Pane paneMenu = new Pane(lblMenu, imgMenu);
                paneMenu.setBackground(new Background(new BackgroundFill(Color.web("#61045a"), CornerRadii.EMPTY, Insets.EMPTY)));
                paneMenu.setOnMouseEntered(e-> {
                    if(previousPaneSelected.get() ==  null || !previousPaneSelected.get().equals(paneMenu)) {
                        ca.setHue(0.26362D);
                        ca.setBrightness(0.84);
                        lblMenu.setTextFill(Color.web("#E7D272"));
                    }
                });
                paneMenu.setOnMouseExited(e-> {
                    if(previousPaneSelected.get() ==  null || !previousPaneSelected.get().equals(paneMenu)) {
                        ca.setHue(-0.37362D);
                        ca.setBrightness(0.763D);
                        lblMenu.setTextFill(Color.web("#C8BFE7"));
                    }
                });
                paneMenu.setOnMouseClicked(e-> {
                    if(previousPaneSelected.get() != paneMenu && !txt.isEmpty()) {
                        if (previousPaneSelected.get() != null) {
                            previousPaneSelected.get().setBackground(new Background(new BackgroundFill(Color.web("#61045a"), CornerRadii.EMPTY, Insets.EMPTY)));
                            ((Label) (previousPaneSelected.get().getChildren().get(0))).setTextFill(Color.web("#C8BFE7"));
                            ColorAdjust temp = ((ColorAdjust) (((ImageView) (previousPaneSelected.get().getChildren().get(1))).getEffect()));
                            temp.setHue(-0.37362D);
                            temp.setSaturation(0.86742D);
                            temp.setBrightness(0.763D);
                        }
                        paneMenu.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
                        lblMenu.setTextFill(Color.web("#61045a"));
                        ca.setHue(-0.33);
                        ca.setContrast(0.0D);
                        ca.setBrightness(0.4D);
                        this.setContent(txt);
                        previousPaneSelected.set(paneMenu);
                    }
                });
                paneMenu.setPrefSize(100.0D, 100.0D);
                paneMenu.setMinSize(100.0D, 102.4D);
                paneMenu.setMaxSize(100.0D, 100.0D);
               return paneMenu;
            };
            final String[] strOptions = {"View Table", "Add New Data", "Help / FAQs", "About",""};//"Settings",
            final Image[] imgOptions = {new Image(Objects.requireNonNull(PasswordManagerApp.class.getResource("images/tab/viewtable.png")).toExternalForm()),
                    new Image(Objects.requireNonNull(PasswordManagerApp.class.getResource("images/tab/addnewdata.png")).toExternalForm()),
                    new Image(Objects.requireNonNull(PasswordManagerApp.class.getResource("images/tab/help.png")).toExternalForm()),
                    new Image(Objects.requireNonNull(PasswordManagerApp.class.getResource("images/tab/about.png")).toExternalForm()),
                    new Image(InputStream.nullInputStream())
            };
            for(int i = 0; i < 5; i++) this.vBoxOptions.getChildren().add(buildMenuItem.apply(strOptions[i],imgOptions[i]));
        });
    }

    private class StagesPaneContainer {
        private final Circle[] cStages = new Circle[4];
        private final Text[] txtStages = new Text[4];
        private final Text[] txtStagesProcessName = new Text[4];
        private Timeline tds = new Timeline(), tlLineExpand = new Timeline();
        private final DropShadow ds = new DropShadow();

        private final double DBL_LINE_ONE_TO_TWO_END_X = 117.0D,DBL_LINE_TWO_TO_THREE_END_X = 257.0D, DBL_LINE_THREE_TO_FOUR_END_X = 397.0D;

        private final Line lineOneToTwo = new Line(23.0D, 0.0D, DBL_LINE_ONE_TO_TWO_END_X, 0.0D),
                lineTwoToThree = new Line(163.0D, 0.0D, DBL_LINE_TWO_TO_THREE_END_X, 0.0D),
                lineThreeToFour = new Line(303.0D, 0.0D, DBL_LINE_THREE_TO_FOUR_END_X, 0.0D);

        private Pane paneStages;
        private char charPrevStage = '0';
        private final String strStagePassedClr = "#2E1038", strCurrentStageNumberClr = "#FFFD55", strProgressBarEmptyClr = "#CA32FF", strCurrentStageCircleClr = "#BA2CA6",
                       strPassedNumberClr = "#61045a", strStageTextClrNormal = "#CEECDB";

        private void buildStagesPane() {
            if(this.paneStages == null) {
                this.paneStages = new Pane();
                this.paneStages.setLayoutX(110.0D);
                this.paneStages.setLayoutY(400.0D);
                this.txtStagesProcessName[0] = getTextNode("Enter Data", -30.0D, 34.0D, 14.0D, "Calibri", "#012210", false);
                this.txtStagesProcessName[1] = getTextNode("Verify", 122.0D, 34.0D, 14.0D, "Calibri", "#012210", false);
                this.txtStagesProcessName[2] = getTextNode("Confirm", 256.0D, 34.0D, 14.0D, "Calibri", "#012210", false);
                this.txtStagesProcessName[3] = getTextNode("Finish", 404.0D, 34.0D, 14.0D, "Calibri", "#012210", false);
                this.paneStages.getChildren().addAll(txtStagesProcessName[0], txtStagesProcessName[1], txtStagesProcessName[2], txtStagesProcessName[3]);
                double circleX = 0.0D;
                Line lProgress = new Line(20.0D, 0.0D, 400.0D, 0.0D);
                lProgress.setStroke(Color.web(this.strProgressBarEmptyClr));
                lProgress.setStrokeWidth(3.0D);
                this.paneStages.getChildren().add(lProgress);
                for (int i = 0; i < cStages.length; i++) {
                    this.cStages[i] = new Circle(20.0D);
                    this.cStages[i].setFill(Color.web(this.strProgressBarEmptyClr));//"#61045a"
                    this.cStages[i].setLayoutX(circleX);
                    this.cStages[i].setStroke(Color.web(this.strStagePassedClr));
                    this.cStages[0].setStrokeWidth(3.0D);
                    this.txtStages[i] = getTextNode((i + 1) + "", circleX - 6.0D, 8.0D, 22.0D, "Arial", this.strStageTextClrNormal, false);
                    this.paneStages.getChildren().addAll(this.cStages[i], this.txtStages[i]);
                    circleX += 140.0D;
                }
                this.lineOneToTwo.setStrokeWidth(3.0D);
                this.lineOneToTwo.setStroke(Color.web(this.strProgressBarEmptyClr));
                this.lineTwoToThree.setStrokeWidth(3.0D);
                this.lineTwoToThree.setStroke(Color.web(this.strProgressBarEmptyClr));
                this.lineThreeToFour.setStrokeWidth(3.0D);
                this.lineThreeToFour.setStroke(Color.web(this.strProgressBarEmptyClr));
                this.ds.setColor(Color.web("#61045a"));
                this.ds.setRadius(12.0D);
                this.ds.setBlurType(BlurType.GAUSSIAN);
                this.tds = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(ds.radiusProperty(), 12.0D)),
                        new KeyFrame(Duration.ZERO, new KeyValue(ds.colorProperty(), Color.web("#61045a"))),
                        new KeyFrame(Duration.millis(800.0D), new KeyValue(ds.radiusProperty(), 4.0D)),
                        new KeyFrame(Duration.millis(800.0D), new KeyValue(ds.colorProperty(), Color.web("#A60BF2"))));
                this.tds.setCycleCount(Timeline.INDEFINITE);
                this.tds.setAutoReverse(true);
                this.paneStages.getChildren().addAll(this.lineOneToTwo, this.lineTwoToThree, this.lineThreeToFour);
            }
        }
        public void gotoStage(char charCurrentStage) {
            this.tds.stop();
            this.tlLineExpand.stop();
            this.tlLineExpand.getKeyFrames().clear();
            switch (charCurrentStage) {
                case '1' -> {
                    if (charPrevStage == '2') {
                        this.cStages[1].setEffect(null);
                        this.cStages[1].setFill(Color.web(this.strProgressBarEmptyClr));
                        this.txtStages[1].setFill(Color.web(this.strStageTextClrNormal));
                        this.lineOneToTwo.setStroke(Color.web(this.strStagePassedClr));
                        this.tlLineExpand.getKeyFrames().add(new KeyFrame(Duration.millis(800.0D), new KeyValue(this.lineOneToTwo.endXProperty(), this.lineOneToTwo.getStartX())));
                        this.tlLineExpand.setOnFinished(e -> {
                            this.cStages[0].setEffect(this.ds);
                            this.cStages[0].setFill(Color.web(this.strCurrentStageCircleClr));
                            this.txtStages[0].setFill(Color.web(this.strCurrentStageNumberClr));
                            this.lineOneToTwo.setStroke(Color.web(this.strProgressBarEmptyClr));
                            this.lineOneToTwo.setEndX(this.DBL_LINE_ONE_TO_TWO_END_X);
                            this.tds.playFromStart();
                        });
                        this.tlLineExpand.playFromStart();
                    } else if (charPrevStage == '3') {
                        this.cStages[2].setEffect(null);
                        this.cStages[2].setFill(Color.web(this.strProgressBarEmptyClr));
                        this.txtStages[2].setFill(Color.web(this.strStageTextClrNormal));
                        this.cStages[1].setEffect(this.ds);
                        this.cStages[1].setFill(Color.web(this.strCurrentStageCircleClr));
                        this.txtStages[1].setFill(Color.web(this.strCurrentStageNumberClr));
                        this.lineTwoToThree.setStroke(Color.web(this.strStagePassedClr));
                        this.tlLineExpand.getKeyFrames().addAll(new KeyFrame(Duration.millis(600.0D), new KeyValue(this.lineTwoToThree.endXProperty(), this.lineTwoToThree.getStartX())),
                                                                new KeyFrame(Duration.millis(600.0D), e -> {
                                                                    this.cStages[1].setEffect(null);
                                                                    this.cStages[1].setFill(Color.web(this.strProgressBarEmptyClr));
                                                                    this.txtStages[1].setFill(Color.web(this.strStageTextClrNormal));
                                                                    this.lineTwoToThree.setStroke(Color.web(this.strProgressBarEmptyClr));
                                                                    this.lineTwoToThree.setEndX(this.DBL_LINE_TWO_TO_THREE_END_X);
                                                                    this.lineOneToTwo.setStroke(Color.web(this.strStagePassedClr));
                                                                    this.cStages[0].setEffect(this.ds);
                                                                    this.cStages[0].setFill(Color.web(this.strCurrentStageCircleClr));
                                                                    this.txtStages[0].setFill(Color.web(this.strCurrentStageNumberClr));
                                                                }), new KeyFrame(Duration.millis(600.0D), new KeyValue(this.lineOneToTwo.endXProperty(), this.lineOneToTwo.getEndX())),
                                                                new KeyFrame(Duration.millis(1200.0D), new KeyValue(this.lineOneToTwo.endXProperty(), this.lineOneToTwo.getStartX()))
                                );
                        this.tlLineExpand.setOnFinished(e -> {
                            this.lineOneToTwo.setStroke(Color.web(this.strProgressBarEmptyClr));
                            this.lineOneToTwo.setEndX(this.DBL_LINE_ONE_TO_TWO_END_X);
                            this.tds.playFromStart();
                        });this.tlLineExpand.setDelay(Duration.millis(300.0D));
                        this.tlLineExpand.playFromStart();
                    } else {
                        this.paneStages.setLayoutY(400.0D);
                        this.lineOneToTwo.setStroke(Color.web(this.strProgressBarEmptyClr));
                        this.lineTwoToThree.setStroke(Color.web(this.strProgressBarEmptyClr));
                        this.lineThreeToFour.setStroke(Color.web(this.strProgressBarEmptyClr));
                        for (int i = 1; i < this.cStages.length; i++) {
                            this.cStages[i].setFill(Color.web(this.strProgressBarEmptyClr));
                            this.cStages[i].setStroke(Color.web(this.strStagePassedClr));
                            this.txtStages[i].setFill(Color.web(this.strStageTextClrNormal));
                        }
                        this.cStages[0].setStroke(Color.web("#61045a"));
                        this.cStages[0].setFill(Color.web("#F21CB6"));
                        this.cStages[0].setStrokeWidth(3.0D);
                        this.txtStages[0].setFill(Color.web("#61045a"));
                        this.cStages[0].setEffect(this.ds);
                        this.tds.playFromStart();
                    }
                }
                case '2' -> {
                    this.cStages[0].setEffect(null);
                    this.cStages[0].setFill(Color.web(this.strStagePassedClr));
                    this.txtStages[0].setFill(Color.web(this.strPassedNumberClr));
                    this.lineOneToTwo.setStroke(Color.web(this.strStagePassedClr));
                    this.lineTwoToThree.setStroke(Color.web(this.strProgressBarEmptyClr));
                    if(this.lineOneToTwo.getEndX() == this.DBL_LINE_ONE_TO_TWO_END_X) this.lineOneToTwo.setEndX(this.lineOneToTwo.getStartX());
                    this.tlLineExpand.getKeyFrames().add(new KeyFrame(Duration.millis(800.0D), new KeyValue(this.lineOneToTwo.endXProperty(), this.DBL_LINE_ONE_TO_TWO_END_X)));
                    this.tlLineExpand.setOnFinished(e-> {
                        this.cStages[1].setEffect(this.ds);
                        this.cStages[1].setFill(Color.web(this.strCurrentStageCircleClr));
                        this.txtStages[1].setFill(Color.web(this.strCurrentStageNumberClr));
                        this.tds.playFromStart();
                    });
                    this.tlLineExpand.playFromStart();
                }
                case '3' -> {
                    this.cStages[1].setEffect(null);
                    this.cStages[1].setFill(Color.web(this.strStagePassedClr));
                    this.txtStages[1].setFill(Color.web(this.strPassedNumberClr));
                    this.lineTwoToThree.setStroke(Color.web(this.strStagePassedClr));
                    this.lineTwoToThree.setEndX(this.lineTwoToThree.getStartX());
                    this.tlLineExpand.getKeyFrames().add(new KeyFrame(Duration.millis(800.0D), new KeyValue(this.lineTwoToThree.endXProperty(), this.DBL_LINE_TWO_TO_THREE_END_X)));
                    this.tlLineExpand.setOnFinished(e-> {
                        this.cStages[2].setEffect(this.ds);
                        this.cStages[2].setFill(Color.web(this.strCurrentStageCircleClr));
                        this.txtStages[2].setFill(Color.web(this.strCurrentStageNumberClr));
                        this.tds.playFromStart();
                    });
                    this.tlLineExpand.playFromStart();
                }
                case '4' -> {
                    this.cStages[2].setEffect(null);
                    this.cStages[2].setFill(Color.web(this.strStagePassedClr));
                    this.txtStages[2].setFill(Color.web(this.strPassedNumberClr));
                    this.lineTwoToThree.setStroke(Color.web(this.strStagePassedClr));
                    this.lineTwoToThree.setEndX(this.DBL_LINE_TWO_TO_THREE_END_X);
                    this.lineThreeToFour.setStroke(Color.web(this.strStagePassedClr));
                    this.lineThreeToFour.setEndX(this.lineThreeToFour.getStartX());
                    this.tlLineExpand.getKeyFrames().add(new KeyFrame(Duration.millis(800.0D), new KeyValue(this.lineThreeToFour.endXProperty(), this.DBL_LINE_THREE_TO_FOUR_END_X)));
                    this.tlLineExpand.setOnFinished(e-> {
                        this.cStages[3].setEffect(this.ds);
                        this.cStages[3].setFill(Color.web(this.strCurrentStageCircleClr));
                        this.txtStages[3].setFill(Color.web(this.strCurrentStageNumberClr));
                        this.tds.playFromStart();
                    });
                    this.tlLineExpand.playFromStart();
                }
            }
            this.charPrevStage = charCurrentStage;
        }
        private Pane getStagesPane() {
            return this.paneStages;
        }
    }

    private boolean blnNewAccountDataNextClicked = false;
    private String strNewAccId = "", strNewAccPwd = "", strNewAccDomain = "", strNewAccPurpose = "";
    private final String strColorTabTitle = "#231024", strColorFieldText = "#231024", strColorButton = "#381938",
            strColorButtonText = "#E8E8E8", strColorButtonTextHovered = "#FFC604", strColorPaneBackground = "#A349A4",
    strColorButtonDisabled = "#020202", strColorButtonDisabledText = "#612B61";
    private final Image imgInfo = new Image(Objects.requireNonNull(PasswordManagerApp.class.getResource("images/info.png")).toExternalForm()),
    imgEyeOpen = new Image(Objects.requireNonNull(PasswordManagerApp.class.getResource("images/eye_open.png")).toExternalForm()),
    imgEyeClose = new Image(Objects.requireNonNull(PasswordManagerApp.class.getResource("images/eye_close.png")).toExternalForm());

    public void setContent(final String strOptionSelected) {
        if (t.getStatus() == Animation.Status.RUNNING) {
            t.stop();
            t.getKeyFrames().clear();
        }
        this.paneContent.setContent(null);
        this.blnInViewTable = false;
        this.hMapIDRecordUpdated.clear();
        Pane content = null;
        switch (strOptionSelected) {
            case "View Table" -> {
                this.blnInViewTable = true;
                this.blnShowPasswordFieldViewTable = true;
                content = new Pane((getAccountDetailsPane()));
            }
            case "Add New Data" -> {
                content = new Pane(blnNewAccountDataNextClicked ? this.getVerifyNewAccountDetailsPane() : this.getNewAccountDetailsEntryPane());
            }
            case "Help / FAQs" -> {
                content = new Pane(this.getHelpAndFAQsPane());
            }
            case "Settings" -> {
                content = new Pane(this.showSettingsPane());
            }
            case "About" -> {
                //TextArea txtAbout = new TextArea("       Author : Sujit T K \n   E-Mail : tksujit1705@gmail.com\n            App Version : 1.0.2v");
                TextArea txtAbout = new TextArea("Author : Sujit T K\nE-Mail : tksujit1705@gmail.com\nApp Version : 1.0.3v");
                txtAbout.setLayoutX(190.0D);
                txtAbout.setLayoutY(170.0D);
                txtAbout.setPrefSize(400.0D, 130.0D);
                txtAbout.setEditable(false);
                txtAbout.setStyle("-fx-control-inner-background: " + strColorPaneBackground + ";" +
                        "-fx-text-fill: #3A083E;" +
                        "-fx-font-size : 24px;");
                content = new Pane(txtAbout);
            }
        }
        this.paneContent.setContent(content);
    }

    private Rectangle rectPrevPrompt = null;
    private Text txtPrevPrompt = null;

    private void markAsUpdated(final int ID, final boolean accNameUpdated, final boolean accPwdUpdated, final boolean accDomainUpdated, final boolean accPurposeUpdated) {
        boolean[] b = new boolean[]{accNameUpdated, accPwdUpdated, accDomainUpdated, accPurposeUpdated};
        if(this.hMapIDRecordUpdated.containsKey(ID)) {
            boolean[] alreadyAvailableData = this.hMapIDRecordUpdated.get(ID);
            for(int i = 0; i < 4; i++) b[i] = b[i] || alreadyAvailableData[i];
        }
        this.hMapIDRecordUpdated.put(ID, b);
        this.blnEnableUpdateButton.set(true);
    }

    private void deMarkFromUpdate(final int ID, final String strFieldName) {
        if(hMapIDRecordUpdated.containsKey(ID)) {
            boolean[] b = new boolean[]{strFieldName.equals("Account Name"), strFieldName.equals("Password"),
                                        strFieldName.equals("Domain"), strFieldName.equals("Purpose")};
            boolean blnAllColumnsRemoved = true;
            if (this.hMapIDRecordUpdated.containsKey(ID)) {
                final boolean[] alreadyAvailableData = this.hMapIDRecordUpdated.get(ID);
                for (int i = 0; i < 4; i++) {
                    if(b[i]) b[i] = false;
                    else {
                        if(b[i] = alreadyAvailableData[i]) blnAllColumnsRemoved = false;
                    }
                }
            }
            if(blnAllColumnsRemoved) this.hMapIDRecordUpdated.remove(ID);
            else this.hMapIDRecordUpdated.put(ID, b);
            this.blnEnableUpdateButton.set(!this.hMapIDRecordUpdated.isEmpty());
        }
    }
    private Pane getHelpAndFAQsPane() {
        Pane paneHelpAndFAQs = new Pane();
        paneHelpAndFAQs.setId("Pane Help And FAQs");
        paneHelpAndFAQs.setPrefSize(676.0D, 410.0D);
        Rectangle rectFAQs = new Rectangle(676.0D, 410.0D);
        rectFAQs.setArcWidth(20.0D);
        rectFAQs.setArcHeight(20.0D);
        paneHelpAndFAQs.setShape(rectFAQs);
        paneHelpAndFAQs.setLayoutX(60.0D);
        paneHelpAndFAQs.setLayoutY(46.0D);
        paneHelpAndFAQs.setBackground(new Background(new BackgroundFill(Color.web(strColorPaneBackground), CornerRadii.EMPTY, Insets.EMPTY)));
        paneHelpAndFAQs.getChildren().add(this.getHelpTreeView());
        return paneHelpAndFAQs;
    }

    private TreeView<Pane> getHelpTreeView() {
        final double DBL_QUESTION_FONT_SIZE = 18.0D, DBL_ANSWER_FONT_SIZE = 14.0D;
        final String STR_QUESTION_FONT_FAMILY = "Calibri", STR_QUESTION_CLR = "#010203" , STR_ANSWER_FONT_FAMILY = "Calibri", STR_ANSWER_CLR = "#010203";
        String STR_ANSWER_PANE_BG = "#99ff33";
        TreeItem<Pane> tItemAddNewAccountText = new TreeItem<>(new Pane(this.getTextNode("How to add/store details of an account?", 10.0D, 14.0D, DBL_QUESTION_FONT_SIZE, STR_QUESTION_FONT_FAMILY, STR_QUESTION_CLR , false)));
        Pane paneAddNewAccountDetail = new Pane();
        paneAddNewAccountDetail.setPrefWidth(400.0D);
        Text txtStep1 = this.getTextNode("STEP - 1 : Go to \"Add New Data\" tab available on left panel of the app.", 20.0D, 20.0D, DBL_ANSWER_FONT_SIZE, STR_ANSWER_FONT_FAMILY, STR_ANSWER_CLR, false)
                ,txtStep2 = this.getTextNode("STEP - 2 : Enter the details of the account to be stored.", 20.0D, 360.0D, DBL_ANSWER_FONT_SIZE, STR_ANSWER_FONT_FAMILY, STR_ANSWER_CLR, false)
                ,txtStep3 = this.getTextNode("STEP - 3 : Re-enter the account name and password for the verification of correctness of previousely entered information. Note that the account name and password entered in STEP - 1" +
                " should match with that of entered in STEP - 2.", 20.0D, 705.0D, DBL_ANSWER_FONT_SIZE, STR_ANSWER_FONT_FAMILY, STR_ANSWER_CLR, false)
                ,txtStep4 = this.getTextNode("STEP - 4 : Check the account domain and purpose. You can modify the domain and purpose if you want. The account domain and purpose entered in this step will be considered as final" +
                " and need not to be same as given in STEP - 1.", 20.0D, 1080.0D, DBL_ANSWER_FONT_SIZE, STR_ANSWER_FONT_FAMILY, STR_ANSWER_CLR, false)
                ,txtStep5 = this.getTextNode("STEP - 5 : You will get the \"Success\" message if your account detail is successfully stored by the application.", 20.0D, 1460.0D, DBL_ANSWER_FONT_SIZE, STR_ANSWER_FONT_FAMILY, STR_ANSWER_CLR, false)
                ,txtAddNewAccountDummy = this.getTextNode("", 20.0D, 1800.0D, DBL_ANSWER_FONT_SIZE, STR_ANSWER_FONT_FAMILY, STR_ANSWER_CLR, false);
        txtStep3.setWrappingWidth(540.0D);
        txtStep4.setWrappingWidth(540.0D);
        txtStep5.setWrappingWidth(540.0D);
        paneAddNewAccountDetail.setBackground(new Background(new BackgroundFill(Color.web(STR_ANSWER_PANE_BG), CornerRadii.EMPTY, Insets.EMPTY)));
        paneAddNewAccountDetail.getChildren().addAll(txtStep1, txtStep2, txtStep3, txtStep4, txtStep5, txtAddNewAccountDummy);
        TreeItem<Pane> tItemAddNewAccountPane = new TreeItem<>(paneAddNewAccountDetail);
        tItemAddNewAccountText.getChildren().add(tItemAddNewAccountPane);
        TreeView<Pane> tViewHelp = new TreeView<>();
        TreeItem<Pane> tItemAddNewInfo = new TreeItem<>(new Pane(this.getTextNode("Adding new Account Detail", 10.0D, 14.0D, DBL_QUESTION_FONT_SIZE, STR_QUESTION_FONT_FAMILY, STR_QUESTION_CLR , false)));
        tItemAddNewInfo.getChildren().addAll(tItemAddNewAccountText,
                getTreeItemForHelp("What is the maximum limit\\length of the each field in \"Add New Account\" form?",
                "Each input field has a maximum length of 90 characters including whitespace characters.\n",DBL_QUESTION_FONT_SIZE,STR_QUESTION_FONT_FAMILY,
                STR_QUESTION_CLR,DBL_ANSWER_FONT_SIZE,STR_ANSWER_FONT_FAMILY,STR_ANSWER_CLR,STR_ANSWER_PANE_BG),
                getTreeItemForHelp("Are all fields are mandatory to be filled in \"Add New Account\" form?",
                "Yes, you must have to enter values for all fields.\n",DBL_QUESTION_FONT_SIZE,STR_QUESTION_FONT_FAMILY,
                STR_QUESTION_CLR,DBL_ANSWER_FONT_SIZE,STR_ANSWER_FONT_FAMILY,STR_ANSWER_CLR,STR_ANSWER_PANE_BG),
                getTreeItemForHelp("Why is there no conditions set up on Account ID and Password like setting minimum number of characters and restricting some characters?",
                        "Acknowledging the fact that you can store wide ranges of account details, there was no limitations setup to provide support for storing account details of any type."
                                + "\nFor example, an account detail of type GMail has a condition setup by Google where the password should be of at least eight characters, but enforcing this rule may restrict adding account details of PIN type and other"
                                + " short length passwords where you may need to store a password that can be of less than eight characters." +
                                "\n\nNOTE: This is an app for storing passwords and no new account of any domain can be created using this app. Restrictions for password can be setup by identifying/tracking their domain name" +
                                " and type, but it may possess threat to User's privacy. To safeguard user's privacy, tracking is not done and hence no limitations was set on password field.\n",DBL_QUESTION_FONT_SIZE,STR_QUESTION_FONT_FAMILY,
                        STR_QUESTION_CLR,DBL_ANSWER_FONT_SIZE,STR_ANSWER_FONT_FAMILY,STR_ANSWER_CLR,STR_ANSWER_PANE_BG),
                getTreeItemForHelp("Why paste option for \"Account ID\" is disabled in STEP - 2 but enabled in STEP - 1?",
                        "You can not only paste text on Account ID in STEP - 1, you can also paste text in Domain and Purpose." +
                                " As mentioned previousely, STEP - 2 is for verifying CORECTNESS of entered details. The text you may pasted/typed in STEP - 1 may contain typo or even it can be entirely wrong." +
                                " To ensure the correctness, you are forced to re-enter the details in STEP - 2 where you are MANUALLY EXPECTED to enter the detail. By doing so, you will identify mistakes/typos you have done previousely.\n",DBL_QUESTION_FONT_SIZE,STR_QUESTION_FONT_FAMILY,
                        STR_QUESTION_CLR,DBL_ANSWER_FONT_SIZE,STR_ANSWER_FONT_FAMILY,STR_ANSWER_CLR,STR_ANSWER_PANE_BG),
                getTreeItemForHelp("I have entered correct Account ID and password in STEP - 2, but app shows \"Username or Password does not match\".",
                        "As stated already under \"How to add/store details of an account?\", it is evident that the Account ID and password MUST be SAME in STEP - 1 as well as STEP - 2." +
                                "\n\nYou might have entered correct Account ID and Password in Step - 2, but may have typed/pasted wrong Account ID or password in STEP - 1. If so, go back to STEP - 1 and change it over there." +
                                "\n\nSome user might feel that going back to STEP - 1 and entering the correct details is unnecessary while the same has been entered in STEP - 2, but do remember that this 'unnecessary' step has been" +
                                " setup to again confirm correctness of the modified details. Also notice that the user may need to undergo this 'unnecessary' step only when they commit mistake." +
                                "\n\nMoreover, this system was aimed to drastically reduce the errors committed by the user by not allowing them to store wrong details.\n",DBL_QUESTION_FONT_SIZE,STR_QUESTION_FONT_FAMILY,
                        STR_QUESTION_CLR,DBL_ANSWER_FONT_SIZE,STR_ANSWER_FONT_FAMILY,STR_ANSWER_CLR,STR_ANSWER_PANE_BG));
        ImageView[] imgViewAddNewAccount = new ImageView[5];
        for(int i = 0; i < 5; i++) {
            imgViewAddNewAccount[i] = new ImageView(new Image(Objects.requireNonNull(PasswordManagerApp.class.getResource("images/help/add_new_acc_" + (i+1) + "_final.png")).toExternalForm()));
            paneAddNewAccountDetail.getChildren().add(imgViewAddNewAccount[i]);
            imgViewAddNewAccount[i].setFitWidth(440.0D);
            imgViewAddNewAccount[i].setFitHeight(300.0D);
            imgViewAddNewAccount[i].setLayoutX(20.0D);
        }
        imgViewAddNewAccount[0].setLayoutY(30.0D);
        imgViewAddNewAccount[1].setLayoutY(370.0D);
        imgViewAddNewAccount[2].setLayoutY(750.0D);
        imgViewAddNewAccount[3].setLayoutY(1125.0D);
        imgViewAddNewAccount[4].setLayoutY(1490.0D);

        STR_ANSWER_PANE_BG = "#9EFCFF";
        TreeItem<Pane> tItemView = new TreeItem<>(new Pane(this.getTextNode("View/Update/Delete account detail", 10.0D, 14.0D, DBL_QUESTION_FONT_SIZE, STR_QUESTION_FONT_FAMILY, STR_QUESTION_CLR , false)));
        tItemView.getChildren().addAll(getTreeItemForHelp("How to view the account details that were added already?",
                "You can view the already entered account details by selecting \"View Table\" from left pane of the app. You can also update the detail of any account or remove particular account detail if you wish to.\n",DBL_QUESTION_FONT_SIZE,STR_QUESTION_FONT_FAMILY,
                STR_QUESTION_CLR,DBL_ANSWER_FONT_SIZE,STR_ANSWER_FONT_FAMILY,STR_ANSWER_CLR,STR_ANSWER_PANE_BG),
                getTreeItemForHelp("How to edit a detail and update it?",
                        "All the available details will be listed in the table. You can edit any data/value of the table among four fields : Account ID, Password, Domain and Purpose " +
                                " by following the following steps :\n\n" +
                                "\t\tSTEP - 1 : Double click on the exact value (or cell of the table) which you would like to modify.\n" +
                                "\t\tSTEP - 2 : Now, that particular value becomes editable. You can now modify the value.\n" +
                                "\t\tSTEP - 3 : Once you have modified the value, press Enter Key to apply changes temporarily.\n" +
                                "\t\tSTEP - 4 : If you do not wish to apply changes, just click on any other field to loose the focus.\n" +
                                "\t\tSTEP - 5 : The value which you have modified will be highlighted in different color.\n" +
                                "\t\tSTEP - 6 : To permanently apply the changes, simply click on \"UPDATE\" button.\n\n" +
                                "\tNOTE : You can also update values of multiple fields of a particular account and modify a single field for multiple accounts simultaneosuely.\n",DBL_QUESTION_FONT_SIZE,STR_QUESTION_FONT_FAMILY,
                        STR_QUESTION_CLR,DBL_ANSWER_FONT_SIZE,STR_ANSWER_FONT_FAMILY,STR_ANSWER_CLR,STR_ANSWER_PANE_BG),
                getTreeItemForHelp("How to delete a particular record of an account Detail?",
                        "Just select the row or record of the account detail you wish to delete by simply clicking on any of the value of that row. Then, click on \"DELETE\" button to permanently delete the record.\n",DBL_QUESTION_FONT_SIZE,STR_QUESTION_FONT_FAMILY,
                        STR_QUESTION_CLR,DBL_ANSWER_FONT_SIZE,STR_ANSWER_FONT_FAMILY,STR_ANSWER_CLR,STR_ANSWER_PANE_BG),
                getTreeItemForHelp("Can I hide my password from the table whenever required?",
                        "Of course, you can hide all password(s) by pressing the \"Password\" Header of the table or you can click on the eye symbol." +
                                "It will hide the password by replacing all the characters using asterick * . You can also trigger this event by using CTRL + D." +
                                "You can reveal the password by once again pressing on \"Password\" Column Header or closed-eye icon or again pressing CTRL + D.\n\n" +
                                "The above method provides partial privacy. If you would like to entirely hide the \"Password\" column from the table, you can do it by" +
                                " pressing the CTRL + H. You can make use of the same key combination to unhide the Password column.\n",DBL_QUESTION_FONT_SIZE,STR_QUESTION_FONT_FAMILY,
                        STR_QUESTION_CLR,DBL_ANSWER_FONT_SIZE,STR_ANSWER_FONT_FAMILY,STR_ANSWER_CLR,STR_ANSWER_PANE_BG),
                getTreeItemForHelp("How does \"Revert all\" button functions?",
                        "The 'Revert All' option just simply undo the changes made by updation and deletion made by the user in the current session." +
                                " It recovers the deleted records and undo the changes made by restoring all the records to the original form in the way it was present when the current session was initiated.\n\n" +
                                "Here, \"session\" denotes the runtime of the application. A session is initiated when the application is launched by the user and the session is destroyed when the user closes the app.\n\n" +
                                "\tNOTE : It does not removes the record(s) that were newly added.\n",DBL_QUESTION_FONT_SIZE,STR_QUESTION_FONT_FAMILY,
                        STR_QUESTION_CLR,DBL_ANSWER_FONT_SIZE,STR_ANSWER_FONT_FAMILY,STR_ANSWER_CLR,STR_ANSWER_PANE_BG),
                getTreeItemForHelp("Is there any way to copy the Account ID and Password from the table?",
                        "Unlike the scenario of \"Add New Data\" where you are restricted to copy and paste the Account ID and password in STEP - 2, you can actually COPY the Account ID" +
                                " and password you want. It is for this purpose the application is intended for. You can copy the text by double-clicking on it, which will select the whole text and make it as editable." +
                                " Now, right click and select 'Copy' from Dropdown Menu or simply press CTRL + C to copy the text.\n\nIt is understandable that this COPY method is quite long process and" +
                                " not much user-friendly. The development processes are still going on and you'll get better user experience in upcoming version of the app.\n",DBL_QUESTION_FONT_SIZE,STR_QUESTION_FONT_FAMILY,
                        STR_QUESTION_CLR,DBL_ANSWER_FONT_SIZE,STR_ANSWER_FONT_FAMILY,STR_ANSWER_CLR,STR_ANSWER_PANE_BG),
                getTreeItemForHelp("How does \"Search by Domain\" filter works?",
                        "The record(s) that have Domain value matching(starting) with the text you have entered in the text box is filtered by ignoring the case of the alphabets.\n",DBL_QUESTION_FONT_SIZE,STR_QUESTION_FONT_FAMILY,
                        STR_QUESTION_CLR,DBL_ANSWER_FONT_SIZE,STR_ANSWER_FONT_FAMILY,STR_ANSWER_CLR,STR_ANSWER_PANE_BG));

        STR_ANSWER_PANE_BG = "#F7FF95";
        TreeItem<Pane> tItemMisc = new TreeItem<>(new Pane(this.getTextNode("Miscellaneous", 10.0D, 14.0D, DBL_QUESTION_FONT_SIZE, STR_QUESTION_FONT_FAMILY, STR_QUESTION_CLR , false)));
        tItemMisc.getChildren().addAll(getTreeItemForHelp("Where did my data gets stored?",
                "Your data gets stored within the 'data' folder. It is stored securely so that no one can access data from it directly.\n",DBL_QUESTION_FONT_SIZE,STR_QUESTION_FONT_FAMILY,
                STR_QUESTION_CLR,DBL_ANSWER_FONT_SIZE,STR_ANSWER_FONT_FAMILY,STR_ANSWER_CLR,STR_ANSWER_PANE_BG),
                getTreeItemForHelp("What will happen if I rename/delete the \"data\" folder?",
                        "If you rename or delete the folder, the application considers the folder to be missing and again creates folder (when new session starts) with name \"data\". It will consider data only from 'data' folder and if you moved data to any other folder or renamed the folder, then the app will not recognize datas from it.\n",DBL_QUESTION_FONT_SIZE,STR_QUESTION_FONT_FAMILY,
                        STR_QUESTION_CLR,DBL_ANSWER_FONT_SIZE,STR_ANSWER_FONT_FAMILY,STR_ANSWER_CLR,STR_ANSWER_PANE_BG),
                getTreeItemForHelp("How can I share this application to others?",
                        "Simply share this application (PasswordManager.jar file) by sending it to target user. Do not send \"data\" folder to them." +
                                "\nSYSTEM REQUIREMENTS :\n\t1.) Windows 10 or above\n\t2.) Java Runtime Environment (JRE) installed.\n",DBL_QUESTION_FONT_SIZE,STR_QUESTION_FONT_FAMILY,
                        STR_QUESTION_CLR,DBL_ANSWER_FONT_SIZE,STR_ANSWER_FONT_FAMILY,STR_ANSWER_CLR,STR_ANSWER_PANE_BG),
                getTreeItemForHelp("Can I view my data in another device if I copy my \"data\" folder to that device?",
                        "Yeah, you can view your data in another device if you have this app in that device and ensure to paste the \"data\" folder in the same location as the application." +
                                "\n\nNOTE : Do not share your \"data\" folder unnecessarily to others as they can view those data and they can share it publicly to anyone (possess great threat to your security)" +
                                " for which you alone would be considered responsible. Sharing of the \"data\" folder is generally not encouraged to do so unless you really need to do.\n",DBL_QUESTION_FONT_SIZE,STR_QUESTION_FONT_FAMILY,
                        STR_QUESTION_CLR,DBL_ANSWER_FONT_SIZE,STR_ANSWER_FONT_FAMILY,STR_ANSWER_CLR,STR_ANSWER_PANE_BG),
                getTreeItemForHelp("What can be expected in next version of this application?",
                        "You'll get control over the application like setting up a password for the application and customizations such as changing themes and fonts of the application." +
                                " Also, security will be improved by implementing encryption and decryption algorithms. \"Settings\" tab will be included in next version of the application.\n",DBL_QUESTION_FONT_SIZE,STR_QUESTION_FONT_FAMILY,
                        STR_QUESTION_CLR,DBL_ANSWER_FONT_SIZE,STR_ANSWER_FONT_FAMILY,STR_ANSWER_CLR,STR_ANSWER_PANE_BG),
                getTreeItemForHelp("On which technologies this app has been built on?",
                        "This application is primarily built by using Java and runs on top of JVM. The application is based on MVC (Model-View-Controller) architecture and built using Java FX, Java Swing, FXML, CSS and Hyper SQL.\n",DBL_QUESTION_FONT_SIZE,STR_QUESTION_FONT_FAMILY,
                        STR_QUESTION_CLR,DBL_ANSWER_FONT_SIZE,STR_ANSWER_FONT_FAMILY,STR_ANSWER_CLR,STR_ANSWER_PANE_BG),
                getTreeItemForHelp("What is the cost of resources for creating the application?",
                        "This application was developed by a soul(single) developer. All it costed was about 40 hours of dedication and hard work.\n",DBL_QUESTION_FONT_SIZE,STR_QUESTION_FONT_FAMILY,
                        STR_QUESTION_CLR,DBL_ANSWER_FONT_SIZE,STR_ANSWER_FONT_FAMILY,STR_ANSWER_CLR,STR_ANSWER_PANE_BG),
                getTreeItemForHelp("I would like to support/contribute for the development of this application. How can I do?",
                        "You are welcomed to give support or contribute for the development of this application. Here's what you can do:" +
                                "\n1.) Share this application with your friends and neighbours." +
                                "\n2.) Reach out to the only-developer of this application by contacting through mail provided in \"About\" tab." +
                                " You can share your ideas, convey any correction/suggestions or can contribute by any means that'll support for the improvement of overall application.\n",DBL_QUESTION_FONT_SIZE,STR_QUESTION_FONT_FAMILY,
                        STR_QUESTION_CLR,DBL_ANSWER_FONT_SIZE,STR_ANSWER_FONT_FAMILY,STR_ANSWER_CLR,STR_ANSWER_PANE_BG));


        TreeItem<Pane> tItemRoot = new TreeItem<>();
        tItemRoot.getChildren().addAll(tItemAddNewInfo,tItemView,tItemMisc);
        tViewHelp.setRoot(tItemRoot);
        tViewHelp.setShowRoot(false);
        tViewHelp.setPrefSize(630.0D, 370.0D);
        tViewHelp.setLayoutX(20.0D);
        tViewHelp.setLayoutY(20.0D);
        return tViewHelp;
    }

    private TreeItem<Pane> getTreeItemForHelp(final String strQues, final String strAns, final double DBL_QUESTION_FONT_SIZE, final String STR_QUESTION_FONT_FAMILY, final String STR_QUESTION_CLR,
                                              final double DBL_ANSWER_FONT_SIZE, final String STR_ANSWER_FONT_FAMILY, final String STR_ANSWER_CLR, final String STR_ANSWER_PANE_BG) {
        Text txtQuestion = this.getTextNode(strQues, 10.0D, 10.0D, DBL_QUESTION_FONT_SIZE, STR_QUESTION_FONT_FAMILY, STR_QUESTION_CLR , false);
        txtQuestion.setWrappingWidth(540.0D);
        TreeItem<Pane> tItmLimitation = new TreeItem<>(new Pane(txtQuestion));
        Text txtAnswer = this.getTextNode(strAns, 10.0D, 24.0D, DBL_ANSWER_FONT_SIZE, STR_ANSWER_FONT_FAMILY, STR_ANSWER_CLR , false);
        txtAnswer.setWrappingWidth(540.0D);
        Pane paneLimitationsAnswer = new Pane();
        paneLimitationsAnswer.setMaxWidth(578.0D);
        paneLimitationsAnswer.setBackground(new Background(new BackgroundFill(Color.web(STR_ANSWER_PANE_BG), CornerRadii.EMPTY, Insets.EMPTY)));
        paneLimitationsAnswer.getChildren().add(txtAnswer);
        TreeItem<Pane> tItmAnsLimitation = new TreeItem<>(paneLimitationsAnswer);
        tItmLimitation.getChildren().add(tItmAnsLimitation);
        return tItmLimitation;
    }

    private Pane getAccountDetailsPane() {
        this.blnEnableUpdateButton.set(false);
        this.blnEnableDeleteButton.set(false);
        Pane paneAccDetails = new Pane();
        paneAccDetails.setId("Pane Show Existing Account Info");
        paneAccDetails.setPrefSize(676.0D, 410.0D);
        Rectangle rectAccDetails = new Rectangle(676.0D, 410.0D);
        rectAccDetails.setArcWidth(20.0D);
        rectAccDetails.setArcHeight(20.0D);
        paneAccDetails.setShape(rectAccDetails);
        paneAccDetails.setLayoutX(60.0D);
        paneAccDetails.setLayoutY(56.0D);
        paneAccDetails.setBackground(new Background(new BackgroundFill(Color.web(strColorPaneBackground), CornerRadii.EMPTY, Insets.EMPTY)));

        this.entries = FXCollections.observableArrayList(this.dbh.getAllDetails());

        TableView<AccountDetail> tblDetails = new TableView<AccountDetail>();
        tblDetails.setEditable(true);
        TableColumn<AccountDetail, String> colAccName = new TableColumn<>("Account Name");
        tblDetails.getColumns().add(colAccName);
        colAccName.setEditable(true);
        colAccName.setCellFactory(col -> new EditableStringTableCell());
        colAccName.setCellValueFactory(new PropertyValueFactory<>("accName"));
        colAccName.setPrefWidth(colAccName.getPrefWidth()*1.2D);
        /*colAccName.setOnEditCommit((TableColumn.CellEditEvent<AccountDetail, String> t) -> {
            (t.getTableView().getItems().get(t.getTablePosition().getRow())).setAccName(t.getNewValue());
        });*/
        colAccPwd = new TableColumn<>("Password");
        colAccPwd.setCellValueFactory(new PropertyValueFactory<>("accPwd"));
        tblDetails.getColumns().add(colAccPwd);
        colAccPwd.setSortable(false);
        imgShowPassword = new ImageView(this.imgEyeOpen);
        imgShowPassword.setFitWidth(16.0D);
        imgShowPassword.setFitHeight(16.0D);
        imgShowPassword.getProperties().put("index", 1);
        Label lbl = new Label();
        lbl.setGraphic(imgShowPassword);
        colAccPwd.setGraphic(lbl);
        colAccPwd.setMinWidth(90.0D);
        lbl.setOnMouseClicked(e-> {
            this.togglePasswordView();
        });
        tblDetails.setOnMouseClicked(e-> {
            if(e.getTarget() instanceof Text) {
                if (((Text) e.getTarget()).getText().equals("Password")) this.togglePasswordView();
            }
        });
        tblDetails.setOnSort(null);
        colAccPwd.setCellFactory(col -> new EditableStringTableCell());
        if(!this.blnShowPwdKeyEventAdded) {
            this.blnShowPwdKeyEventAdded = true;
            this.paneBase.getScene().addEventHandler(KeyEvent.KEY_RELEASED, e -> {
                if (this.blnInViewTable) {
                    if (e.getCode() == KeyCode.D && e.isControlDown()) {
                        this.togglePasswordView();
                    } else if (e.getCode() == KeyCode.H && e.isControlDown()) {
                        colAccPwd.setVisible(blnShowPasswordFieldViewTable = !blnShowPasswordFieldViewTable);
                    }
                }
            });
        } else {
            this.togglePasswordView();
            this.togglePasswordView();
        }
        TableColumn<AccountDetail, String> colAccDomain = new TableColumn<>("Domain");
        colAccDomain.setCellValueFactory(new PropertyValueFactory<>("accDomain"));
        tblDetails.getColumns().add(colAccDomain);
        colAccDomain.setPrefWidth(colAccDomain.getPrefWidth()*1.2D);
        colAccDomain.setCellFactory(col -> new EditableStringTableCell());
        TableColumn<AccountDetail, String> colAccPurpose = new TableColumn<>("Purpose");
        colAccPurpose.setCellValueFactory(new PropertyValueFactory<>("accPurpose"));
        tblDetails.getColumns().add(colAccPurpose);
        colAccPurpose.setPrefWidth(colAccPurpose.getPrefWidth()*1.2D);
        colAccPurpose.setCellFactory(col -> new EditableStringTableCell());
        TableColumn<AccountDetail, LocalDateTime> colDataAdded = new TableColumn<>("Added");
        colDataAdded.setCellValueFactory(new PropertyValueFactory<>("dateCreated"));
        colDataAdded.setCellFactory(c -> new TableCell<>() {
            private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

            @Override
            protected void updateItem(LocalDateTime ltd, boolean empty) {
                super.updateItem(ltd, empty);
                this.setText((ltd == null || empty) ? ((getIndex() < entries.size()) ? "-" : null) : dtf.format(ltd));
                this.setAlignment(Pos.CENTER);
            }
        });
        colDataAdded.setPrefWidth(170.0D);
        colDataAdded.setResizable(false);
        tblDetails.getColumns().add(colDataAdded);
        TableColumn<AccountDetail, LocalDateTime> colDataLastModified = new TableColumn<>("Last Modified");
        colDataLastModified.setCellValueFactory(new PropertyValueFactory<>("dateLastModified"));
        colDataLastModified.setPrefWidth(170.0D);
        colDataLastModified.setResizable(false);
        tblDetails.getColumns().add(colDataLastModified);
        colDataLastModified.setCellFactory(c -> new TableCell<>() {
            private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

            @Override
            protected void updateItem(LocalDateTime ltd, boolean empty) {
                super.updateItem(ltd, empty);
                this.setText((ltd == null || empty) ? ((getIndex() < entries.size()) ? "-" : null) : dtf.format(ltd));
                this.setAlignment(Pos.CENTER);
            }
        });
        tblDetails.setPrefWidth(620.0D);
        tblDetails.setLayoutX(25.0D);
        tblDetails.setPrefHeight(330.0D);
        tblDetails.setLayoutY(15.0D);
        tblDetails.setItems(this.entries);
        Rectangle rectUpdate = new Rectangle(344.0D, 360.0D, 80.0D, 40.0D);
        rectUpdate.setFill(Color.web(this.strColorButtonDisabled));
        rectUpdate.setArcWidth(15.0D);
        rectUpdate.setArcHeight(15.0D);
        Text txtUpdate = this.getTextNode("Update", 360.0D, 384.0D, 16.0D, "Times New Roman", this.strColorButtonDisabledText, false);
        txtUpdate.setMouseTransparent(true);
        rectUpdate.setOnMouseEntered(e -> {
            if(this.blnViewTableUpdate && this.blnEnableUpdateButton.get()) {
                txtUpdate.setFill(Color.web(this.strColorButtonTextHovered));
                txtUpdate.setScaleX(1.2D);
                txtUpdate.setScaleY(1.2D);
            }
        });
        rectUpdate.setOnMouseExited(e -> {
            if (this.blnEnableUpdateButton.get()) {
                txtUpdate.setFill(Color.web(this.strColorButtonText));
                txtUpdate.setScaleX(1.0D);
                txtUpdate.setScaleY(1.0D);
            }
        });
        rectUpdate.setOnMouseReleased(e -> {
            if (this.blnViewTableUpdate && e.getButton() == MouseButton.PRIMARY && this.blnEnableUpdateButton.get()) {
                this.blnEnableRevertAllButton.set(true);
                this.blnViewTableUpdate = false;
                this.blnEnableUpdateButton.set(false);
                /*AccountDetail[] arrDetails = new AccountDetail[this.entries.size()];
                for(int i = 0; i < this.entries.size(); i++) {
                    arrDetails[i] = new AccountDetail(this.entries.get(i));
                }
                dbh.updateAccountDetails(arrDetails);*/
                int[] ID = new int[this.hMapIDRecordUpdated.size()];
                final String[] strAccName = new String[this.hMapIDRecordUpdated.size()],
                         strAccPwd = new String[this.hMapIDRecordUpdated.size()],
                         strAccDomain = new String[this.hMapIDRecordUpdated.size()],
                         strAccPurpose = new String[this.hMapIDRecordUpdated.size()],
                         strLastDateModified = new String[this.hMapIDRecordUpdated.size()];
                int filled = -1;
                for (AccountDetail entry : this.entries) {
                    if (this.hMapIDRecordUpdated.containsKey(entry.getUniqueID())) {
                        ID[++filled] = entry.getUniqueID();
                        strLastDateModified[filled] = entry.getDateLastModified().toString().replace("T"," ");
                        if (this.hMapIDRecordUpdated.get(ID[filled])[0]) strAccName[filled] = entry.getAccName();
                        if (this.hMapIDRecordUpdated.get(ID[filled])[1]) strAccPwd[filled] = entry.getAccPwd();
                        if (this.hMapIDRecordUpdated.get(ID[filled])[2]) strAccDomain[filled] = entry.getAccDomain();
                        if (this.hMapIDRecordUpdated.get(ID[filled])[3]) strAccPurpose[filled] = entry.getAccPurpose();
                    }
                }
                final int intTotalChangesMade = this.dbh.updateAccountDetails(ID, strAccName, strAccPwd, strAccDomain, strAccPurpose, strLastDateModified);
                this.showPrompt(paneAccDetails, "Successfully updated : " + intTotalChangesMade + " changes made.");
                this.hMapIDRecordUpdated.clear();
                tblDetails.refresh();
            }
        });
        this.blnEnableUpdateButton.addListener((ol, oldValue, newValue) -> {
            if (newValue) {
                rectUpdate.setFill(Color.web(this.strColorButton));
                txtUpdate.setFill(Color.web(this.strColorButtonText));
            } else {
                rectUpdate.setFill(Color.web(this.strColorButtonDisabled));
                txtUpdate.setFill(Color.web(this.strColorButtonDisabledText));
                txtUpdate.setScaleY(1.0D);
                txtUpdate.setScaleX(1.0D);
            }
        });
        Rectangle rectDelete = new Rectangle(445.0D, 360.0D, 80.0D, 40.0D);
        rectDelete.setFill(Color.web(this.strColorButtonDisabled));
        rectDelete.setArcWidth(15.0D);
        rectDelete.setArcHeight(15.0D);
        Text txtDelete = this.getTextNode("Delete", 465.0D, 384.0D, 16.0D, "Times New Roman", this.strColorButtonDisabledText, false);
        txtDelete.setMouseTransparent(true);
        rectDelete.setOnMouseEntered(e -> {
            if(!this.entries.isEmpty() && this.blnEnableDeleteButton.get()) {
                txtDelete.setFill(Color.web(this.strColorButtonTextHovered));
                txtDelete.setScaleY(1.2D);
                txtDelete.setScaleX(1.2D);
            }
        });
        rectDelete.setOnMouseExited(e -> {
            if(this.blnEnableDeleteButton.get()) {
                txtDelete.setFill(Color.web(this.strColorButtonText));
                txtDelete.setScaleY(1.0D);
                txtDelete.setScaleX(1.0D);
            }
        });
        rectDelete.setOnMouseReleased(e -> {
            if (!tblDetails.getItems().isEmpty() && e.getButton() == MouseButton.PRIMARY && !tblDetails.getSelectionModel().getSelectedCells().isEmpty() && this.blnEnableDeleteButton.get()) {
                this.blnEnableRevertAllButton.set(true);
                AccountDetail adRemove = tblDetails.getItems().get(tblDetails.getSelectionModel().getSelectedCells().get(0).getRow());
                this.dbh.deleteDetail(adRemove.getUniqueID());
                tblDetails.getItems().remove(adRemove);
                //tblDetails.getItems().remove(tblDetails.getSelectionModel().getSelectedCells().get(0).getRow());
                this.entries.remove(adRemove);
                this.showPrompt(paneAccDetails, "Selected Account Detail deleted successfully.");
                //System.out.println(entries.get(tblDetails.getSelectionModel().getSelectedCells().get(0).getRow()).getUniqueID());
                //tblDetails.setItems(newValue.isEmpty() ? this.entries : FXCollections.observableArrayList(this.entries.stream().filter(ad -> ad.getAccDomain().startsWith(newValue)).toList()));
            }
        });
        tblDetails.getSelectionModel().selectedItemProperty().addListener((ov, oldvalue, newValue) -> {
            this.blnEnableDeleteButton.set(newValue != null);
        });
        this.blnEnableDeleteButton.addListener((ol, oldValue, newValue) -> {
            if (newValue) {
                rectDelete.setFill(Color.web(this.strColorButton));
                txtDelete.setFill(Color.web(this.strColorButtonText));
            } else {
                rectDelete.setFill(Color.web(this.strColorButtonDisabled));
                txtDelete.setFill(Color.web(this.strColorButtonDisabledText));
                txtDelete.setScaleY(1.0D);
                txtDelete.setScaleX(1.0D);
            }
        });
        Text txtFilterDomain = this.getTextNode("Search on domain :", 25.0D, 370.0D, 14.0D, "Arial", this.strColorTabTitle, false);
        txtFilterDomain.setFont(Font.font("Arial", FontWeight.NORMAL, FontPosture.ITALIC, 16.0D));
        TextField txtFilterByDomain = this.getTextField(25.0D, 374.D, 300.0D, 24.0D);
        txtFilterByDomain.textProperty().addListener((ol, oldValue, newValue) -> {
            tblDetails.setItems(newValue.isEmpty() ? this.entries : FXCollections.observableArrayList(this.entries.stream().filter(ad -> ad.getAccDomain().toLowerCase().startsWith(newValue.toLowerCase())).toList()));
            tblDetails.refresh();
        });
        Rectangle rectRevert = new Rectangle(545.0D, 360.0D, 100.0D, 40.0D);
        rectRevert.setFill(Color.web(this.strColorButtonDisabled));
        rectRevert.setArcWidth(15.0D);
        rectRevert.setArcHeight(15.0D);
        Text txtRevert = this.getTextNode("Revert All", 562.0D, 384.0D, 16.0D, "Times New Roman", this.strColorButtonDisabledText, false);
        txtRevert.setMouseTransparent(true);
        rectRevert.setOnMouseEntered(e -> {
            if(this.blnEnableRevertAllButton.get()) {
                txtRevert.setFill(Color.web(this.strColorButtonTextHovered));
                txtRevert.setScaleY(1.2D);
                txtRevert.setScaleX(1.2D);
            }
        });
        rectRevert.setOnMouseExited(e -> {
            if(this.blnEnableRevertAllButton.get()) {
                txtRevert.setFill(Color.web(this.strColorButtonText));
                txtRevert.setScaleY(1.0D);
                txtRevert.setScaleX(1.0D);
            }
        });
        rectRevert.setOnMouseReleased(e -> {
            if (e.getButton() == MouseButton.PRIMARY && this.blnEnableRevertAllButton.get()) {
                this.blnEnableRevertAllButton.set(false);
                tblDetails.getItems().clear();
                this.entries.clear();
                this.hMapIDRecordUpdated.clear();
                dbh.restoreOriginalData();
                this.entries = FXCollections.observableArrayList(dbh.getAllDetails());
                txtFilterByDomain.setText("");
                tblDetails.setItems(entries);
                tblDetails.refresh();
                this.showPrompt(paneAccDetails, "All changes reverted successfully!");
            }
        });
        this.blnEnableRevertAllButton.addListener((ol, oldValue, newValue) -> {
            if (newValue) {
                rectRevert.setFill(Color.web(this.strColorButton));
                txtRevert.setFill(Color.web(this.strColorButtonText));
            } else {
                rectRevert.setFill(Color.web(this.strColorButtonDisabled));
                txtRevert.setFill(Color.web(this.strColorButtonDisabledText));
                txtRevert.setScaleY(1.0D);
                txtRevert.setScaleX(1.0D);
            }
        });
        Rectangle rectCorner = new Rectangle(629.0D, 329.0D, 16.0D, 16.0D);
        rectCorner.setFill(Color.web("#3A083E"));
        rectCorner.setMouseTransparent(true);

        paneAccDetails.getChildren().addAll(tblDetails, rectUpdate, txtUpdate, rectDelete, txtDelete, rectRevert, txtRevert, txtFilterDomain, txtFilterByDomain, rectCorner);
        return paneAccDetails;
    }
/*
    private EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent e) {

        }
    };
*/
    private void togglePasswordView() {
        blnShowPasswordViewTable = !blnShowPasswordViewTable;
        if(blnShowPasswordViewTable) {
            this.imgShowPassword.setImage(this.imgEyeOpen);
            colAccPwd.setCellFactory(col -> new EditableStringTableCell());
        } else {
            this.imgShowPassword.setImage(this.imgEyeClose);
            colAccPwd.setCellFactory(c -> new TableCell<>() {
                @Override
                protected void updateItem(String pwd, boolean empty) {
                    super.updateItem(pwd, empty);
                    this.setText(pwd == null ? "" : "*".repeat(pwd.length()));
                    this.setAlignment(Pos.CENTER);
                }
            });
        }
    }

    private void showPrompt(final Pane paneParent, final String strPrompt) {
        if (t.getStatus() == Animation.Status.RUNNING) {
            t.pause();
            t.playFrom(Duration.millis(2499));
        }
        Text txtPrompt = this.getTextNode(strPrompt, 0.0D, paneParent.getId().equals("Pane Show Existing Account Info") ? -23.0D : -28.0D, 16.0D, "Calibri", "#F36DF5", false);
        txtPrompt.setTextAlignment(TextAlignment.CENTER);
        txtPrompt.setWrappingWidth(paneParent.getWidth());
        Rectangle r = new Rectangle(0.0D, paneParent.getId().equals("Pane Show Existing Account Info") ? -45.0D : -50.0D , paneParent.getWidth(), 35.0D);
        r.setArcWidth(20.0D);
        r.setArcHeight(20.0D);
        r.setFill(Color.web(strColorTabTitle));
        this.rectPrevPrompt = r;
        this.txtPrevPrompt = txtPrompt;
        t.getKeyFrames().addAll((new KeyFrame(Duration.millis(2500.0D), new KeyValue(r.yProperty(), -130.0D))),
                new KeyFrame(Duration.millis(2500.0D), new KeyValue(txtPrompt.yProperty(), -78.0D)),
                new KeyFrame(Duration.millis(2500.0D), new KeyValue(r.opacityProperty(), 0.2D)),
                new KeyFrame(Duration.millis(2500.0D), new KeyValue(txtPrompt.opacityProperty(), 0.2D)),
                new KeyFrame(Duration.millis(2500.0D), eh -> {
                    paneParent.getChildren().removeAll(r, txtPrompt);
                }));
        paneParent.getChildren().addAll(r, txtPrompt);
        t.setOnFinished(eh -> {
            t.getKeyFrames().clear();
        });
        t.setDelay(Duration.millis(2500.0D));
        t.playFromStart();
    }

    private Pane showSettingsPane() {
        final TabPane paneSettingsTab = new TabPane();
        paneSettingsTab.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        paneSettingsTab.setLayoutX(20.0D);
        paneSettingsTab.setLayoutY(20.0D);
        Rectangle paneSettingsShape = new Rectangle(526.0D, 370.0D);
        paneSettingsShape.setArcWidth(20.0D);
        paneSettingsShape.setArcHeight(20.0D);

        final Supplier<Pane> getSecurityPane = () -> {
            final Pane paneSecurity = new Pane();
            paneSecurity.setStyle("-fx-border-color : " + this.strColorTabTitle + "; -fx-border-width : 1.00px;");
            paneSecurity.setPrefSize(590.0D, 290.0D);
            final Text txtAppPassword = this.getTextNode("Your App Password : ", 60.0D, 60.0D, 22.0D, "Calibri", this.strColorTabTitle, false);
            final TextField txtPassword = this.getTextField(260.0D, 33.0D, 110.0D, 24.0D);
            txtPassword.textProperty().addListener((ol, ov, nv)-> {
                nv = nv.replace("[^0-9]","");
                if(nv.length() > 8)  nv = nv.substring(0, 8);
                txtPassword.setText(nv);
            });
            txtPassword.setFont(new Font("Calibri", 20.0D));
            txtPassword.setAlignment(Pos.CENTER);
            txtPassword.setText("501769");
            txtPassword.setMouseTransparent(true);
            final Rectangle rectChangePassword = new Rectangle(400.0D, 34.0D, 140.0D, 34.0D);
            rectChangePassword.setFill(Color.web(this.strColorTabTitle));
            final Text textChangePassword = this.getTextNode("Change Password", 420.0D, 55.0D, 14.0D, "Calibri", strColorButtonText, false);
            textChangePassword.setMouseTransparent(true);
            rectChangePassword.setOnMouseEntered(e-> {
                textChangePassword.setFill(Color.web(strColorButtonTextHovered));
                textChangePassword.setScaleX(1.2D);
                textChangePassword.setScaleY(1.2D);
                rectChangePassword.setStroke(Color.web("#FFC604"));
            });
            rectChangePassword.setOnMouseExited(e -> {
                textChangePassword.setFill(Color.web(strColorButtonText));
                textChangePassword.setScaleX(1.0D);
                textChangePassword.setScaleY(1.0D);
                rectChangePassword.setStroke(null);
            });
            rectChangePassword.setOnMouseClicked(e-> {
                txtPassword.setMouseTransparent(false);
                txtPassword.requestFocus();
                rectChangePassword.setVisible(false);
                textChangePassword.setVisible(false);
            });
            final Rectangle rectApply = new Rectangle(400.0D, 34.0D, 140.0D, 34.0D);
            rectApply.setFill(Color.web(this.strColorTabTitle));
            final Text txtApply = this.getTextNode("Apply", 420.0D, 55.0D, 14.0D, "Calibri", strColorButtonText, false);
            textChangePassword.setMouseTransparent(true);
            rectApply.setOnMouseEntered(e-> {
                txtApply.setFill(Color.web(strColorButtonTextHovered));
                txtApply.setScaleX(1.2D);
                txtApply.setScaleY(1.2D);
                rectApply.setStroke(Color.web("#FFC604"));
            });
            rectApply.setOnMouseExited(e -> {
                txtApply.setFill(Color.web(strColorButtonText));
                txtApply.setScaleX(1.0D);
                txtApply.setScaleY(1.0D);
                rectApply.setStroke(null);
            });
            rectApply.setOnMouseClicked(e-> {
                txtPassword.setMouseTransparent(true);
                rectApply.setVisible(true);
                textChangePassword.setVisible(true);
            });
            paneSecurity.getChildren().addAll(txtAppPassword, txtPassword, rectApply, textChangePassword);
            paneSecurity.setBackground(new Background(new BackgroundFill(Color.web("#BE55BF"), CornerRadii.EMPTY, Insets.EMPTY)));
            return paneSecurity;
        };
        final Supplier<Pane> getAppearancePane = () -> {
            final Pane paneAppearance = new Pane();
            paneAppearance.setPrefSize(590.0D, 290.0D);
           // paneAppearance.setBackground(new Background(new BackgroundFill(Color.web("#c9280e"), CornerRadii.EMPTY, Insets.EMPTY)));
            return paneAppearance;
        };

        final Tab tabAppearance = new Tab("Appearance", getAppearancePane.get()), tabSecurity = new Tab("Security", getSecurityPane.get());
        paneSettingsTab.getTabs().addAll(tabAppearance, tabSecurity);
        paneSettingsTab.getSelectionModel().select(1);
        /*Pane paneSettings = new Pane();


        Text txtAppearance = this.getTextNode("Appearance", 75.0D, 47.2D, 26.0D, "Calibri", strColorTabTitle, true);

        Rectangle rectAppearanceBorder = new Rectangle(50.0D, 40.0D, 540.0D, 130.0D);
        rectAppearanceBorder.setFill(Color.web(strColorPaneBackground));
        rectAppearanceBorder.setStroke(Color.web(strColorTabTitle));
        rectAppearanceBorder.setArcWidth(20.0D);
        rectAppearanceBorder.setArcHeight(20.0D);
        Rectangle rectTextBack = new Rectangle(70.0D, 20.0D, 140.0D, 40.0D);
        rectTextBack.setFill(Color.web(strColorPaneBackground));

        Text txtTheme = this.getTextNode("Theme : ", 105.0D, 90.0D, 18.0D, "Calibri", strColorTabTitle, false);

        RadioButton chkboxThemeViolet = new RadioButton("Violet Veil");
        chkboxThemeViolet.setFont(new Font("Calibri", 14.0D));
        chkboxThemeViolet.setLayoutX(185.0D);
        chkboxThemeViolet.setLayoutY(75.0D);
        RadioButton chkboxThemeDark = new RadioButton("Darkened Enclave");
        chkboxThemeDark.setFont(new Font("Calibri", 14.0D));
        chkboxThemeDark.setLayoutX(290.0D);
        chkboxThemeDark.setLayoutY(75.0D);
        chkboxThemeViolet.selectedProperty().addListener((ol, ov, nv) -> {
            chkboxThemeDark.setSelected(!nv);
        });
        chkboxThemeDark.selectedProperty().addListener((ol, ov, nv) -> {
            chkboxThemeViolet.setSelected(!nv);
        });

        Slider sdOpacity = new Slider();
        sdOpacity.setLayoutX(150.0D);
        sdOpacity.setLayoutY(120.0D);
        sdOpacity.setMin(0.0D);
        sdOpacity.setMax(1.0D);
        sdOpacity.setMinSize(400.0D, 30.0D);
        sdOpacity.setPrefSize(400.0D, 30.0D);
        sdOpacity.setMaxSize(400.0D, 30.0D);

        Text txtOpacity = this.getTextNode("Opacity : ", 75.0D, 139.0D, 18.0D, "Calibri", strColorTabTitle, false);

        paneSettings.getChildren().addAll(rectAppearanceBorder, rectTextBack, txtAppearance, txtTheme, chkboxThemeViolet, chkboxThemeDark, sdOpacity, txtOpacity);
*/

        final Pane paneSettings = new Pane(paneSettingsTab);
        paneSettings.setShape(paneSettingsShape);
        paneSettings.setId("Pane Settings");
        paneSettings.setPrefSize(636.0D, 364.0D);
        paneSettings.setLayoutX(77.0D);
        paneSettings.setLayoutY(66.0D);
        paneSettings.setBackground(new Background(new BackgroundFill(Color.web(strColorPaneBackground), CornerRadii.EMPTY, Insets.EMPTY)));
        return paneSettings;
    }

    private Pane showSettingsPaneOld() {
        Pane paneSettings = new Pane();
        paneSettings.setId("Pane Settings");
        paneSettings.setPrefSize(636.0D, 364.0D);
        Rectangle paneSettingsShape = new Rectangle(526.0D, 370.0D);
        paneSettingsShape.setArcWidth(20.0D);
        paneSettingsShape.setArcHeight(20.0D);
        paneSettings.setShape(paneSettingsShape);
        paneSettings.setLayoutX(77.0D);
        paneSettings.setLayoutY(66.0D);
        paneSettings.setBackground(new Background(new BackgroundFill(Color.web(strColorPaneBackground), CornerRadii.EMPTY, Insets.EMPTY)));

        Text txtAppearance = this.getTextNode("Appearance", 75.0D, 47.2D, 26.0D, "Calibri", strColorTabTitle, true);

        Rectangle rectAppearanceBorder = new Rectangle(50.0D, 40.0D, 540.0D, 130.0D);
        rectAppearanceBorder.setFill(Color.web(strColorPaneBackground));
        rectAppearanceBorder.setStroke(Color.web(strColorTabTitle));
        rectAppearanceBorder.setArcWidth(20.0D);
        rectAppearanceBorder.setArcHeight(20.0D);
        Rectangle rectTextBack = new Rectangle(70.0D, 20.0D, 140.0D, 40.0D);
        rectTextBack.setFill(Color.web(strColorPaneBackground));

        Text txtTheme = this.getTextNode("Theme : ", 105.0D, 90.0D, 18.0D, "Calibri", strColorTabTitle, false);

        RadioButton chkboxThemeViolet = new RadioButton("Violet Veil");
        chkboxThemeViolet.setFont(new Font("Calibri", 14.0D));
        chkboxThemeViolet.setLayoutX(185.0D);
        chkboxThemeViolet.setLayoutY(75.0D);
        RadioButton chkboxThemeDark = new RadioButton("Darkened Enclave");
        chkboxThemeDark.setFont(new Font("Calibri", 14.0D));
        chkboxThemeDark.setLayoutX(290.0D);
        chkboxThemeDark.setLayoutY(75.0D);
        chkboxThemeViolet.selectedProperty().addListener((ol, ov, nv) -> {
            chkboxThemeDark.setSelected(!nv);
        });
        chkboxThemeDark.selectedProperty().addListener((ol, ov, nv) -> {
            chkboxThemeViolet.setSelected(!nv);
        });

        Slider sdOpacity = new Slider();
        sdOpacity.setLayoutX(150.0D);
        sdOpacity.setLayoutY(120.0D);
        sdOpacity.setMin(0.0D);
        sdOpacity.setMax(1.0D);
        sdOpacity.setMinSize(400.0D, 30.0D);
        sdOpacity.setPrefSize(400.0D, 30.0D);
        sdOpacity.setMaxSize(400.0D, 30.0D);

        Text txtOpacity = this.getTextNode("Opacity : ", 75.0D, 139.0D, 18.0D, "Calibri", strColorTabTitle, false);

        paneSettings.getChildren().addAll(rectAppearanceBorder, rectTextBack, txtAppearance, txtTheme, chkboxThemeViolet, chkboxThemeDark, sdOpacity, txtOpacity);

        return paneSettings;
    }

    private Pane getVerifyNewAccountDetailsPane() {
        Pane paneVerifyDetails = new Pane();
        paneVerifyDetails.setId("Pane Add New Account Stage 2 & Stage 3");
        paneVerifyDetails.setPrefSize(636.0D, 364.0D);
        Rectangle paneVerifyDetailsShape = new Rectangle(526.0D, 370.0D);
        paneVerifyDetailsShape.setArcWidth(20.0D);
        paneVerifyDetailsShape.setArcHeight(20.0D);
        paneVerifyDetails.setShape(paneVerifyDetailsShape);
        paneVerifyDetails.setLayoutX(77.0D);
        paneVerifyDetails.setLayoutY(66.0D);
        paneVerifyDetails.setBackground(new Background(new BackgroundFill(Color.web(strColorPaneBackground), CornerRadii.EMPTY, Insets.EMPTY)));
        Line lBorder = new Line(40.0D, 64.0D, 590.0D, 64.0D);
        lBorder.setFill(Color.web(strColorTabTitle));
        Timeline tl = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(lBorder.scaleXProperty(), 0.0D)),
                new KeyFrame(Duration.millis(200.0D), new KeyValue(lBorder.scaleXProperty(), 0.2D)),
                new KeyFrame(Duration.millis(600.0D), new KeyValue(lBorder.scaleXProperty(), 1.0D)));
        Text lblVerifyAccInfo = this.getTextNode("Verify New Account Info:", 40.0D, 50.0D, 26.0D, "Arial", strColorTabTitle, true);
        Text lblVerifyAccDetail = this.getTextNode("Kindly re-enter your account's ID and Password to be added for verification of correctness of given information.", 40.0D, 110.0D, 16.0D, "Arial", strColorTabTitle, false);
        lblVerifyAccDetail.setWrappingWidth(540.0D);
        Text lblVerifyAccLoginID = this.getTextNode("Account ID : ", 73.0D, 180.0D, 18.0D, "Times New Roman", strColorFieldText, false);
        Text lblVerifyAccPwdID = this.getTextNode("Password : ", 87.0D, 230.0D, 18.0D, "Times New Roman", strColorFieldText, false);
        TextField txtFieldVerifyLoginID = new TextField() {
            @Override
            public void cut() {
            }

            @Override
            public void copy() {
            }

            @Override
            public void paste() {
            }
        };
        ChangeListener<String> changeListenerTextFieldOne = (ov, oldValue, newValue) -> {
            if(newValue.length() > 90) {
                this.showPrompt(paneVerifyDetails, "Maximum length reached : Account Password length can be of maximum 90 characters.");
                txtFieldVerifyLoginID.setText(oldValue);
            }
        };
        txtFieldVerifyLoginID.textProperty().addListener(changeListenerTextFieldOne);
        txtFieldVerifyLoginID.setLayoutX(180.0D);
        txtFieldVerifyLoginID.setLayoutY(158.0D);
        txtFieldVerifyLoginID.setPrefWidth(400.0D);
        txtFieldVerifyLoginID.setPrefHeight(30.0D);
        PasswordField pwdFieldVerify = new PasswordField() {
            @Override
            public void cut() {
            }

            @Override
            public void copy() {
            }

            @Override
            public void paste() {
            }
        };
        ChangeListener<String> changeListenerTextFieldTwo = (ov, oldValue, newValue) -> {
            if(newValue.length() > 90) {
                this.showPrompt(paneVerifyDetails, "Maximum length reached : Account Password length can be of maximum 90 characters.");
                pwdFieldVerify.setText(oldValue);
            }
        };
        pwdFieldVerify.textProperty().addListener(changeListenerTextFieldTwo);
        pwdFieldVerify.setLayoutX(180.0D);
        pwdFieldVerify.setLayoutY(208.0D);
        pwdFieldVerify.setPrefWidth(400.0D);
        pwdFieldVerify.setPrefHeight(30.0D);
        TextField txtFieldVerifyPassword = new TextField() {
            @Override
            public void cut() {
            }

            @Override
            public void copy() {
            }

            @Override
            public void paste() {
            }
        };
        ChangeListener<String> changeListenerTextFieldThree = (ov, oldValue, newValue) -> {
            if(newValue.length() > 90) {
                this.showPrompt(paneVerifyDetails, "Maximum length reached : Account Password length can be of maximum 90 characters.");
                txtFieldVerifyPassword.setText(oldValue);
            }
        };
        txtFieldVerifyPassword.textProperty().addListener(changeListenerTextFieldThree);
        txtFieldVerifyPassword.setLayoutX(180.0D);
        txtFieldVerifyPassword.setLayoutY(208.0D);
        txtFieldVerifyPassword.setPrefWidth(400.0D);
        txtFieldVerifyPassword.setPrefHeight(30.0D);
        txtFieldVerifyPassword.setVisible(false);
        txtFieldVerifyPassword.textProperty().bindBidirectional(pwdFieldVerify.textProperty());
        CheckBox chkBoxShowPassword = new CheckBox();
        chkBoxShowPassword.setLayoutX(200.0D);
        chkBoxShowPassword.setLayoutY(250.0D);
        chkBoxShowPassword.selectedProperty().addListener((ol, oldvalue, newValue) -> {
            txtFieldVerifyPassword.setVisible(newValue);
            pwdFieldVerify.setVisible(!newValue);
        });
        Text txtShowPassword = this.getTextNode("Show Password", 225.0D, 265.0D, 16.0D, "Calibri", strColorTabTitle, false);
        Rectangle rectBack = new Rectangle(140.0D, 290.0D, 100.0D, 40.0D);
        rectBack.setArcWidth(10.0D);
        rectBack.setArcHeight(10.0D);
        rectBack.setFill(Color.web(strColorButton));
        Text txtBack = getTextNode("BACK", 167.0D, 316.0D, 18.0D, "Arial", strColorButtonText, false);
        txtBack.setMouseTransparent(true);
        rectBack.setOnMouseEntered(e -> {
            txtBack.setFill(Color.web(strColorButtonTextHovered));
            txtBack.setScaleX(1.2D);
            txtBack.setScaleY(1.2D);
            rectBack.setStroke(Color.web("#FFC604"));
        });
        rectBack.setOnMouseExited(e -> {
            txtBack.setFill(Color.web(strColorButtonText));
            txtBack.setScaleX(1.0D);
            txtBack.setScaleY(1.0D);
            rectBack.setStroke(null);
        });
        rectBack.setOnMousePressed(e -> {
            this.blnNewAccountDataNextClicked = false;
            this.setContent("Add New Data");
        });
        Rectangle rectVerify = new Rectangle(265.0D, 290.0D, 98.0D, 40.0D);
        rectVerify.setArcWidth(10.0D);
        rectVerify.setArcHeight(10.0D);
        rectVerify.setFill(Color.web(strColorButton));
        Text txtVerify = getTextNode("VERIFY", 280.0D, 316.0D, 18.0D, "Arial", strColorButtonText, false);
        txtVerify.setMouseTransparent(true);
        rectVerify.setOnMouseEntered(e -> {
            txtVerify.setFill(Color.web(strColorButtonTextHovered));
            txtVerify.setScaleX(1.2D);
            txtVerify.setScaleY(1.2D);
            rectVerify.setStroke(Color.web("#FFC604"));
        });
        rectVerify.setOnMouseExited(e -> {
            txtVerify.setFill(Color.web(strColorButtonText));
            txtVerify.setScaleX(1.0D);
            txtVerify.setScaleY(1.0D);
            rectVerify.setStroke(null);
        });
        Rectangle rectClear = new Rectangle(390.0D, 290.0D, 100.0D, 40.0D);
        rectClear.setArcWidth(10.0D);
        rectClear.setArcHeight(10.0D);
        rectClear.setFill(Color.web(strColorButton));
        Text txtClear = getTextNode("CLEAR", 410.0D, 316.0D, 18.0D, "Arial", strColorButtonText, false);
        txtClear.setMouseTransparent(true);
        rectClear.setOnMouseEntered(e -> {
            txtClear.setFill(Color.web(strColorButtonTextHovered));
            txtClear.setScaleX(1.2D);
            txtClear.setScaleY(1.2D);
            rectClear.setStroke(Color.web("#FFC604"));
        });
        rectClear.setOnMouseExited(e -> {
            txtClear.setFill(Color.web(strColorButtonText));
            txtClear.setScaleX(1.0D);
            txtClear.setScaleY(1.0D);
            rectClear.setStroke(null);
        });
        rectClear.setOnMouseClicked(e -> {
            txtFieldVerifyPassword.clear();
            txtFieldVerifyLoginID.clear();
        });
        rectVerify.setOnMousePressed(e -> {
            if (t.getStatus() == Animation.Status.RUNNING) {
                t.pause();
                t.playFrom(Duration.millis(2499));
            }
            String strPrompt = "Account ID does not match : Current ID contains ";
            if (lblVerifyAccInfo.getText().charAt(0) == 'V') {
                if (!txtFieldVerifyLoginID.getText().equals(this.strNewAccId)) {
                    if (txtFieldVerifyLoginID.getText().length() == this.strNewAccId.length()) {
                        strPrompt = "Account ID does not match : Length matches, but incorrect ID entered";
                    } else {
                        strPrompt = strPrompt.concat((txtFieldVerifyLoginID.getText().length() > this.strNewAccId.length()) ? "more" : " less").concat(" characters.");
                    }
                } else if (!txtFieldVerifyPassword.getText().equals(this.strNewAccPwd)) {
                    if (txtFieldVerifyPassword.getText().length() == this.strNewAccPwd.length()) {
                        strPrompt = "Password does not match : Length matches, but incorrect password entered";
                    } else {
                        strPrompt = "Account Password does not match : Password contains ".concat((txtFieldVerifyPassword.getText().length() > this.strNewAccPwd.length()) ? "more" : " less").concat(" characters.");
                    }
                } else {
                    strPrompt = "Account ID and Password successfully verified!";
                    lblVerifyAccInfo.setText("Finalize New Account Info:");
                    this.spc.gotoStage('3');
                    txtFieldVerifyLoginID.setText(this.strNewAccDomain);
                    txtFieldVerifyPassword.setText(this.strNewAccPurpose);
                    txtFieldVerifyPassword.textProperty().unbindBidirectional(pwdFieldVerify.textProperty());
                    txtFieldVerifyPassword.setVisible(true);
                    paneVerifyDetails.getChildren().removeAll(rectClear, txtClear, chkBoxShowPassword, txtShowPassword, pwdFieldVerify);
                    tl.playFromStart();
                    lblVerifyAccDetail.setText("Check 'Domain Name' and 'Purpose'. You can modify it below if required. This need not to be same as previously entered Domain Name and Purpose. This will be considered as final data.");
                    lblVerifyAccLoginID.setText("Domain Name :");
                    lblVerifyAccLoginID.setLayoutX(lblVerifyAccLoginID.getLayoutX() - 23.0D);
                    lblVerifyAccLoginID.setLayoutY(lblVerifyAccLoginID.getLayoutY() + 30.0D);
                    txtFieldVerifyLoginID.setLayoutY(txtFieldVerifyLoginID.getLayoutY() + 30.0D);
                    txtFieldVerifyLoginID.textProperty().removeListener(changeListenerTextFieldOne);
                    txtFieldVerifyLoginID.textProperty().addListener((ov, oldValue, newValue) -> {
                        if(newValue.length() > 90) {
                            this.showPrompt(paneVerifyDetails, "Maximum length reached : Account Domain length can be of maximum 90 characters.");
                            txtFieldVerifyLoginID.setText(oldValue);
                        } else {
                            this.strNewAccDomain = newValue;
                        }
                    });
                    lblVerifyAccPwdID.setText("Purpose :");
                    lblVerifyAccPwdID.setLayoutX(lblVerifyAccPwdID.getLayoutX() + 10.0D);
                    lblVerifyAccPwdID.setLayoutY(lblVerifyAccPwdID.getLayoutY() + 30.0D);
                    txtFieldVerifyPassword.setLayoutY(txtFieldVerifyPassword.getLayoutY() + 30.0D);
                    pwdFieldVerify.textProperty().removeListener(changeListenerTextFieldTwo);
                    txtFieldVerifyPassword.textProperty().removeListener(changeListenerTextFieldThree);
                    txtFieldVerifyPassword.textProperty().addListener((ol, oldValue, newValue) -> {
                        if(newValue.length() > 90) {
                            this.showPrompt(paneVerifyDetails, "Maximum length reached : Account Purpose length can be of maximum 90 characters.");
                            txtFieldVerifyPassword.setText(oldValue);
                        } else {
                            this.strNewAccPurpose = newValue;
                        }
                    });
                    txtBack.setText("DISCARD");
                    txtBack.setLayoutX(txtBack.getLayoutX() + 35.0D);
                    txtBack.setLayoutY(txtBack.getLayoutY() + 10.0D);
                    rectBack.setWidth(rectVerify.getWidth() + 20.0D);
                    rectBack.setLayoutX(rectBack.getLayoutX() + 45.0D);
                    rectBack.setLayoutY(rectBack.getLayoutY() + 10.0D);
                    txtVerify.setText("ADD");
                    txtVerify.setLayoutX(txtVerify.getLayoutX() + 86.0D);
                    txtVerify.setLayoutY(txtVerify.getLayoutY() + 10.0D);
                    rectVerify.setLayoutX(rectVerify.getLayoutX() + 70.0D);
                    rectVerify.setLayoutY(rectVerify.getLayoutY() + 10.0D);
                }
            } else {
                if (txtFieldVerifyLoginID.getText().isEmpty()) {
                    strPrompt = (Math.random() > 0.5D) ? "The field 'Domain Name' should not be empty." : "You are required to enter value for 'Domain name' field.";
                } else if (txtFieldVerifyPassword.getText().isEmpty()) {
                    strPrompt = (Math.random() > 0.5D) ? "The field 'Purpose' should not be empty." : "You are required to enter value for 'Purpose' field.";
                } else if (this.dbh.isAccountDetailAvailable(this.strNewAccId, this.strNewAccPwd, this.strNewAccDomain, this.strNewAccPurpose)) {
                    strPrompt = "Account detail already available. Please change Account Domain or Purpose.";
                } else {
                    /*DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                    LocalDateTime dateCreated = LocalDateTime.now();
                    dtf.format(dateCreated);*/
                    this.dbh.addDetail(this.strNewAccId, this.strNewAccPwd, this.strNewAccDomain, this.strNewAccPurpose);
                    this.strNewAccId = "";
                    this.strNewAccPwd = "";
                    this.strNewAccDomain = "";
                    this.strNewAccPurpose = "";
                    this.blnNewAccountDataNextClicked = false;
                    strPrompt = "New Account Information added successfully!";
                    this.spc.gotoStage('4');
                    lblVerifyAccInfo.setText("Success:");
                    paneVerifyDetails.getChildren().removeAll(rectPrevPrompt, txtPrevPrompt, txtFieldVerifyLoginID, txtFieldVerifyPassword, lblVerifyAccLoginID, lblVerifyAccPwdID, rectBack, txtBack, rectVerify, txtVerify);
                    paneVerifyDetails.setPrefHeight(180.0D);
                    paneVerifyDetails.setLayoutY(paneVerifyDetails.getLayoutY() + 88.0D);
                    this.spc.getStagesPane().setLayoutY(312.0D);
                    t.getKeyFrames().add(new KeyFrame(Duration.millis(5000.0D), fun -> {
                        this.setContent("Add New Data");
                        this.spc.getStagesPane().setLayoutY(400.0D);
                    }));
                    tl.playFromStart();
                    lblVerifyAccDetail.setText("Account information has been registered successfully!");
                    lblVerifyAccDetail.setFont(Font.font("Arial", FontWeight.NORMAL, FontPosture.REGULAR, 22.0D));
                }
            }
            Text txtPrompt = this.getTextNode(strPrompt, 0.0D, -28.0D, 16.0D, "Calibri", "#F36DF5", false);
            txtPrompt.setTextAlignment(TextAlignment.CENTER);
            txtPrompt.setWrappingWidth(636.0D);
            Rectangle r = new Rectangle(0.0D, -50.0D, 636.0D, 35.0D);
            r.setArcWidth(20.0D);
            r.setArcHeight(20.0D);
            r.setFill(Color.web(strColorTabTitle));
            this.rectPrevPrompt = r;
            this.txtPrevPrompt = txtPrompt;
            t.getKeyFrames().addAll((new KeyFrame(Duration.millis(2500.0D), new KeyValue(r.yProperty(), strPrompt.contains("added successfully") ? -200.0D : -130.0D))),
                    new KeyFrame(Duration.millis(2500.0D), new KeyValue(txtPrompt.yProperty(), strPrompt.contains("added successfully") ? -148.0D : -78.0D)),
                    new KeyFrame(Duration.millis(2500.0D), new KeyValue(r.opacityProperty(), 0.2D)),
                    new KeyFrame(Duration.millis(2500.0D), new KeyValue(txtPrompt.opacityProperty(), 0.2D)),
                    new KeyFrame(Duration.millis(2500.0D), eh -> {
                        paneVerifyDetails.getChildren().removeAll(r, txtPrompt);
                    }));
            paneVerifyDetails.getChildren().addAll(r, txtPrompt);
            t.setOnFinished(eh -> {
                t.getKeyFrames().clear();
            });
            t.setDelay(Duration.millis(2500.0D));
            t.playFromStart();
        });
        paneVerifyDetails.getChildren().addAll(lblVerifyAccInfo, lBorder, lblVerifyAccDetail, lblVerifyAccLoginID, lblVerifyAccPwdID, txtFieldVerifyLoginID, pwdFieldVerify,
                rectBack, txtBack, rectVerify, txtVerify, txtFieldVerifyPassword, chkBoxShowPassword, txtShowPassword, rectClear, txtClear, this.spc.getStagesPane());
        this.spc.gotoStage('2');
        tl.playFromStart();
        return paneVerifyDetails;
    }

    private Pane getNewAccountDetailsEntryPane() {
        Pane paneAddNewAccount = new Pane();
        paneAddNewAccount.setId("Pane Add New Account Stage 1");
        paneAddNewAccount.setPrefSize(636.0D, 364.0D);
        Rectangle paneAddNewAccountShape = new Rectangle(526.0D, 370.0D);
        paneAddNewAccountShape.setArcWidth(20.0D);
        paneAddNewAccountShape.setArcHeight(20.0D);
        paneAddNewAccount.setShape(paneAddNewAccountShape);
        paneAddNewAccount.setLayoutX(77.0D);
        paneAddNewAccount.setLayoutY(66.0D);
        paneAddNewAccount.setBackground(new Background(new BackgroundFill(Color.web(strColorPaneBackground), CornerRadii.EMPTY, Insets.EMPTY)));
        Text lblNewAccInfo = this.getTextNode("Add New Account Info:", 40.0D, 50.0D, 26.0D, "Arial", strColorTabTitle, true);
        Text lblAccountLoginID = this.getTextNode("Account ID : ", 67.0D, 110.0D, 18.0D, "Times New Roman", strColorFieldText, false);
        Text lblAccountPasswordID = this.getTextNode("Password / PIN : ", 39.0D, 160.0D, 18.0D, "Times New Roman", strColorFieldText, false);
        Text lblAccountDomainName = this.getTextNode("Domain Name : ", 47.0D, 210.0D, 18.0D, "Times New Roman", strColorFieldText, false);
        Text lblAccountPurpose = this.getTextNode("Purpose : ", 94.0D, 260.0D, 18.0D, "Times New Roman", strColorFieldText, false);
        TextField txtFieldLoginID = this.getTextField(180.0D, 88.0D, 400.0D, 30.0D);
        if (!this.strNewAccId.isEmpty()) txtFieldLoginID.setText(this.strNewAccId);
        txtFieldLoginID.setPromptText("example@mail.com");
        txtFieldLoginID.textProperty().addListener((ov, oldValue, newValue) -> {
            if(newValue.length() < 91) {
                this.strNewAccId = newValue;
            } else {
                this.showPrompt(paneAddNewAccount, "Maximum length reached : Account ID length can be of maximum 90 characters.");
                txtFieldLoginID.setText(oldValue);
            }
        });
        PasswordField pwdField = this.getPasswordField(180.0D, 138.0D, 400.0D, 30.0D);
        if (!this.strNewAccPwd.isEmpty()) pwdField.setText(this.strNewAccPwd);
        pwdField.textProperty().addListener((ov, oldValue, newValue) -> {
            if(newValue.length() < 91) {
                this.strNewAccPwd = newValue;
            }  else {
                this.showPrompt(paneAddNewAccount, "Maximum length reached : Password length can be of maximum 90 characters.");
                pwdField.setText(oldValue);
            }
        });
        TextField txtDomainName = this.getTextField(180.0D, 188.0D, 400.0D, 30.0D);
        if (!this.strNewAccDomain.isEmpty()) txtDomainName.setText(this.strNewAccDomain);
        txtDomainName.setPromptText("Google, facebook or Github");
        txtDomainName.textProperty().addListener((ov, oldValue, newValue) -> {
            if(newValue.length() < 91) {
                this.strNewAccDomain = newValue;
            } else {
                this.showPrompt(paneAddNewAccount, "Maximum length reached : 'Domain' length can be of maximum 90 characters.");
                txtDomainName.setText(oldValue);
            }
        });
        TextField txtPurpose = this.getTextField(180.0D, 238.0D, 400.0D, 30.0D);
        if (!this.strNewAccPurpose.isEmpty()) txtPurpose.setText(this.strNewAccPurpose);
        txtPurpose.setPromptText("Work, Mail, Education or Gaming");
        txtPurpose.textProperty().addListener((ov, oldValue, newValue) -> {
            if(newValue.length() < 91) {
                this.strNewAccPurpose = newValue;
            } else {
                this.showPrompt(paneAddNewAccount, "Maximum length reached : 'Purpose' length can be of maximum 90 characters.");
                txtPurpose.setText(oldValue);
            }
        });
        Rectangle rectNext = new Rectangle(340.0D, 295.0D, 100.0D, 40.0D);
        rectNext.setArcWidth(10.0D);
        rectNext.setArcHeight(10.0D);
        rectNext.setFill(Color.web(strColorButton));
        Text txtNext = getTextNode("NEXT", 367.0D, 321.0D, 18.0D, "Arial", strColorButtonText, false);
        txtNext.setMouseTransparent(true);
        rectNext.setOnMouseEntered(e -> {
            txtNext.setFill(Color.web(strColorButtonTextHovered));
            txtNext.setScaleX(1.2D);
            txtNext.setScaleY(1.2D);
            rectNext.setStroke(Color.web("#FFC604"));
                    /*txtNext.setFont(Font.font("Times New Roman", FontWeight.NORMAL, FontPosture.REGULAR, 20.0D));
                    txtNext.setLayoutX(165.0D);
                    txtNext.setLayoutY(317.0D);*/
        });
        rectNext.setOnMouseExited(e -> {
            txtNext.setFill(Color.web(strColorButtonText));
            txtNext.setScaleX(1.0D);
            txtNext.setScaleY(1.0D);
            rectNext.setStroke(null);
        });
        rectNext.setOnMousePressed(e -> {
            /*if (t.getStatus() == Animation.Status.RUNNING) {
                t.pause();
                t.playFrom(Duration.millis(2499));
            }*/
            int count = 0;
            String strPrompt = "";
            if ((txtFieldLoginID.getText().isEmpty())) {
                strPrompt = "Account ID";
                count++;
            }
            if ((pwdField.getText().isEmpty())) {
                if (strPrompt.isEmpty() || Math.random() > 0.5D) strPrompt = "Password";
                count++;
            }
            if ((txtDomainName.getText().isEmpty())) {
                if (strPrompt.isEmpty() || Math.random() > 0.5D) strPrompt = "Domain Name";
                count++;
            }
            if ((txtPurpose.getText().isEmpty())) {
                if (strPrompt.isEmpty() || Math.random() > 0.5D) strPrompt = "Purpose";
                count++;
            }
            if (count == 0) {
                if (!this.dbh.isAccountDetailAvailable(this.strNewAccId, this.strNewAccPwd, this.strNewAccDomain, this.strNewAccPurpose)) {
                    strPrompt = "You can navigate to any tab without loosing the entered data.";
                    this.blnNewAccountDataNextClicked = true;
                    this.setContent("Add New Data");
                } else {
                    strPrompt = "Entered details already available!";
                }
            } else {
                if (count > 1) {
                    strPrompt = strPrompt.concat("' and " + (--count) + " other" + " field" + ((count > 1) ? "s" : ""));
                    strPrompt = (Math.random() > 0.5D) ? "Please enter values for '".concat(strPrompt) : "You are required to fill values for '".concat(strPrompt);
                } else {
                    strPrompt = (Math.random() > 0.5D) ? "Please enter value for '".concat(strPrompt) + "' field" : "You are required to fill value for '" + strPrompt + "' field";
                }
            }
            this.showPrompt(paneAddNewAccount, strPrompt);
            /*Text txtPrompt = this.getTextNode(strPrompt, 0.0D, -28.0D, 16.0D, "Calibri", "#F36DF5", false);
            txtPrompt.setTextAlignment(TextAlignment.CENTER);
            txtPrompt.setWrappingWidth(525.0D);
            Rectangle r = new Rectangle(0.0D, -50.0D, 525.0D, 35.0D);
            r.setArcWidth(20.0D);
            r.setArcHeight(20.0D);
            r.setFill(Color.web(strColorTabTitle));
            t.getKeyFrames().addAll((new KeyFrame(Duration.millis(2500.0D), new KeyValue(r.yProperty(), -130.0D))),
                    new KeyFrame(Duration.millis(2500.0D), new KeyValue(txtPrompt.yProperty(), -78.0D)),
                    new KeyFrame(Duration.millis(2500.0D), new KeyValue(r.opacityProperty(), 0.2D)),
                    new KeyFrame(Duration.millis(2500.0D), new KeyValue(txtPrompt.opacityProperty(), 0.2D)),
                    new KeyFrame(Duration.millis(2500.0D), eh -> {
                        paneAddNewAccount.getChildren().removeAll(r, txtPrompt);
                    }));
            paneAddNewAccount.getChildren().addAll(r, txtPrompt);
            t.setOnFinished(eh -> {
                t.getKeyFrames().clear();
            });
            t.setDelay(Duration.millis(2500.0D));
            t.playFromStart();*/
        });
        Rectangle rectClear = new Rectangle(200.0D, 295.0D, 100.0D, 40.0D);
        rectClear.setArcWidth(10.0D);
        rectClear.setArcHeight(10.0D);
        rectClear.setFill(Color.web(strColorButton));
        Text txtClear = getTextNode("CLEAR", 221.0D, 321.0D, 18.0D, "Arial", strColorButtonText, false);
        txtClear.setMouseTransparent(true);
        rectClear.setOnMouseEntered(e -> {
            txtClear.setFill(Color.web(strColorButtonTextHovered));
            txtClear.setScaleX(1.2D);
            txtClear.setScaleY(1.2D);
            rectClear.setStroke(Color.web("#FFC604"));
        });
        rectClear.setOnMouseExited(e -> {
            txtClear.setFill(Color.web(strColorButtonText));
            txtClear.setScaleX(1.0D);
            txtClear.setScaleY(1.0D);
            rectClear.setStroke(null);
        });
        rectClear.setOnMouseClicked(e -> {
            txtFieldLoginID.clear();
            pwdField.clear();
            txtDomainName.clear();
            txtPurpose.clear();
        });
        Line lBorder = new Line(40.0D, 64.0D, 590.0D, 64.0D);
        lBorder.setFill(Color.web(strColorTabTitle));
        paneAddNewAccount.getChildren().addAll(lblNewAccInfo, lblAccountLoginID, lblAccountPasswordID, lblAccountDomainName, lblAccountPurpose,
                txtFieldLoginID, pwdField, txtDomainName, txtPurpose, rectNext, txtNext, rectClear, txtClear, lBorder, this.spc.getStagesPane(),
                getInfoImageView(548.0D, 46.0D, "Enter the name or e-mail address of your account that acts as Log-in token."),
                getInfoImageView(548.0D, 146.0D, "Enter the name of Website or Application in which you will use this account."),
                getInfoImageView(548.0D, 196.0D, "Enter how you would use this account."));
        this.spc.gotoStage('1');
        Timeline tl = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(lBorder.scaleXProperty(), 0.0D)),
                new KeyFrame(Duration.millis(444.0D), new KeyValue(lBorder.scaleXProperty(), 1.0D)));
        tl.playFromStart();
        return paneAddNewAccount;
    }

    private ImageView getInfoImageView(final double X, final double Y, final String textHelp) {
        ImageView imgView = new ImageView(this.imgInfo);
        imgView.setX(X);
        imgView.setY(Y);
        imgView.setScaleX(0.25D);
        imgView.setScaleY(0.25D);
        Tooltip tipAccountID = new Tooltip(textHelp);
        tipAccountID.setStyle("-fx-background: rgb(255,201,14);-fx-text-fill: rgba(30,30,30);-fx-background-color: rgba(255,201,14,0.8);-fx-font-size:12px;");
        Tooltip.install(imgView, tipAccountID);
        return imgView;
    }

    private Text getTextNode(final String txt, final double layoutX, final double layoutY, final double fontSize, final String fontFamily, final String strColor, final boolean BOLD) {
        Text txtNode = new Text(txt);
        txtNode.setLayoutX(layoutX);
        txtNode.setLayoutY(layoutY);
        txtNode.setFont(Font.font(fontFamily, BOLD ? FontWeight.BOLD : FontWeight.NORMAL, FontPosture.REGULAR, fontSize));
        txtNode.setFill(Color.web(strColor));
        return txtNode;
    }

    private TextField getTextField(final double DBL_X, final double DBL_Y, final double WIDTH, final double HEIGHT) {
        TextField txtField = new TextField();
        txtField.setLayoutX(DBL_X);
        txtField.setLayoutY(DBL_Y);
        txtField.setPrefWidth(WIDTH);
        txtField.setPrefHeight(HEIGHT);
        return txtField;
    }

    private PasswordField getPasswordField(final double DBL_X, final double DBL_Y, final double WIDTH, final double HEIGHT) {
        PasswordField pwdField = new PasswordField() {
            @Override
            public void paste() {
            }

            @Override
            public void copy() {
            }

            @Override
            public void cut() {
            }
        };
        pwdField.setLayoutX(DBL_X);
        pwdField.setLayoutY(DBL_Y);
        pwdField.setPrefWidth(WIDTH);
        pwdField.setPrefHeight(HEIGHT);
        return pwdField;
    }

    public void eventMouseEntered(MouseEvent event) {
        if (blnUpdateMouseIcon && stage != null) {
            stage.getScene().setCursor(Cursor.MOVE);
        }
    }

    public void eventMouseExited(MouseEvent event) {
        if (this.blnUpdateMouseIcon && stage != null) {
            stage.getScene().setCursor(Cursor.DEFAULT);
        }
    }

    public void eventMouseReleased(MouseEvent event) {
        this.blnUpdateMouseIcon = true;
    }

    public void eventMouseDragged(MouseEvent event) {
        if (event.getScreenX() - this.dbl_screen_loc_x > -700.0D && event.getScreenY() - this.dbl_screen_loc_y > -350.0D &&
                event.getScreenX() - this.dbl_screen_loc_x < DBL_SCREEN_WIDTH - 180.0D && event.getScreenY() - this.dbl_screen_loc_y < DBL_SCREEN_HEIGHT - 160.0D) {
            this.stage.setX(event.getScreenX() - this.dbl_screen_loc_x);
            this.stage.setY(event.getScreenY() - this.dbl_screen_loc_y);
        }
        this.blnUpdateMouseIcon = false;
    }

    public void eventMousePressed(MouseEvent event) {
        this.dbl_screen_loc_x = event.getX();
        this.dbl_screen_loc_y = event.getY();
    }
    public class EditableStringTableCell<T> extends TableCell<T, String> {

        private TextField textField;

        @Override
        public void startEdit() {
            if (editableProperty().get()) {
                if (!isEmpty()) {
                    super.startEdit();
                    createTextField();
                    setText(null);
                    setGraphic(textField);
                    textField.requestFocus();
                    textField.selectAll();
                }
            }
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();
            setText(getItem() != null ? getItem().toString() : null);
            setGraphic(null);
        }

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            blnViewTableUpdate = true;
            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getString());
                        textField.selectAll();
                    }
                    setText(null);
                    setGraphic(textField);
                } else {
                    setText(getString());
                    setAlignment(Pos.CENTER);
                    setGraphic(null);
                    if(hMapIDRecordUpdated.containsKey(((AccountDetail)getTableView().getItems().get(getIndex())).getUniqueID())) {
                        final int index = switch (getTableColumn().getText()) {
                            case "Account Name" -> 0;
                            case "Password" -> 1;
                            case "Domain" -> 2;
                            case "Purpose" -> 3;
                            default -> -1;
                        };
                        //System.out.println(getTableColumn().getText());
                        if(index != -1 && hMapIDRecordUpdated.get(((AccountDetail)getTableView().getItems().get(getIndex())).getUniqueID())[index]) {//instead of index -> getTableView().getColumns().indexOf(getTableColumn())
                            setBackground(new Background(new BackgroundFill(Color.web("#3B2540"), CornerRadii.EMPTY, Insets.EMPTY)));
                            setTextFill(Color.web("#E59C00"));
                        }
                    }
                }
            }
        }

        private void createTextField() {
            textField = new TextField();
            textField.setText(getString());
            textField.setOnAction(evt -> {
                if (textField.getText() != null && !textField.getText().isEmpty()) {
                    StringConverter sc = new StringConverter() {
                        @Override
                        public String toString(Object o) {
                            return o == null ? "" : o.toString();
                        }

                        @Override
                        public String fromString(String s) {
                            return s;
                        }
                    };
                    try {
                        String s = (String) sc.fromString(textField.getText());
                        commitEdit(s);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);

            textField.setOnKeyPressed((ke) -> {
                if (ke.getCode().equals(KeyCode.ESCAPE)) {
                    cancelEdit();
                }
            });
            textField.setId(super.getId());
            textField.textProperty().addListener((ov, oldValue, newValue) -> {
                try {
                    textField.setText(newValue.toString());
                        /*textField.positionCaret(p);
                        int p = textField.getCaretPosition();     */
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            textField.setAlignment(Pos.CENTER);
            this.setAlignment(Pos.CENTER);
        }

        private String getString() {
            return getItem() == null ? "" : getItem();
        }

        private boolean isUniqueDetail(final String strID, final String strPwd, final String strDomain, final String strPurpose) {
            AccountDetail temp = new AccountDetail(strID, strPwd, strDomain, strPurpose, null, null);
            for(AccountDetail d : entries) {
                if(d.containsSameDetail(temp)) return false;
            }
            return true;
        }

        private boolean isUniqueDetailFromOriginalData(final int ID, final String strValue, final String strFieldName) {
            for(AccountDetail d : dbh.getAllDetails()) {
                if(d.getUniqueID() == ID) return switch (strFieldName) {
                  case "Account Name" -> !d.getAccName().equals(strValue);
                  case "Password" -> !d.getAccPwd().equals(strValue);
                  case "Domain" -> !d.getAccDomain().equals(strValue);
                  case "Purpose" -> !d.getAccPurpose().equals(strValue);
                  default -> {
                      System.err.println("ERROR : Unexpected 'field' name given for verification. Given field name : " + strFieldName);
                      yield false;
                  }
                };
            }
            return true;
        }

        @Override
        public void commitEdit(String item) {
            boolean modified = isEditing(), blnChangeColor = true;
            if (modified) {
                boolean blnAccNameModified = false, blnAccPwdModified = false, blnAccDomainModified = false, blnAccPurposeModified = false;
                switch (getTableColumn().getText()) {//getTableView().getColumns().indexOf(getTableColumn())
                    case "Account Name" -> {
                        //System.out.println(((AccountDetail)getTableView().getItems().get(getIndex())).getAccName());
                        //System.out.println(((AccountDetail)getTableView().getItems().get(getIndex())).getUniqueID());
                        if(blnAccNameModified = modified = this.isUniqueDetail(item,((AccountDetail)getTableView().getItems().get(getIndex())).getAccPwd(),
                                ((AccountDetail)getTableView().getItems().get(getIndex())).getAccDomain(),((AccountDetail)getTableView().getItems().get(getIndex())).getAccPurpose())) {
                            ((AccountDetail)getTableView().getItems().get(getIndex())).setAccName(item);
                        }
                        blnChangeColor = this.isUniqueDetailFromOriginalData(((AccountDetail)getTableView().getItems().get(getIndex())).getUniqueID(), item, "Account Name");
                        if(!blnChangeColor) deMarkFromUpdate((((AccountDetail)getTableView().getItems().get(getIndex())).getUniqueID()), "Account Name");
                    }
                    case "Password" -> {
                        if(blnAccPwdModified = modified = this.isUniqueDetail(((AccountDetail)getTableView().getItems().get(getIndex())).getAccName(),item,
                                ((AccountDetail)getTableView().getItems().get(getIndex())).getAccDomain(),((AccountDetail)getTableView().getItems().get(getIndex())).getAccPurpose())) {
                            ((AccountDetail)getTableView().getItems().get(getIndex())).setAccPassword(item);
                        }
                        blnChangeColor = this.isUniqueDetailFromOriginalData(((AccountDetail)getTableView().getItems().get(getIndex())).getUniqueID(), item, "Password");
                        if(!blnChangeColor) deMarkFromUpdate((((AccountDetail)getTableView().getItems().get(getIndex())).getUniqueID()), "Password");
                    }
                    case "Domain" -> {
                        if(blnAccDomainModified = modified = this.isUniqueDetail(((AccountDetail)getTableView().getItems().get(getIndex())).getAccName(),
                                ((AccountDetail)getTableView().getItems().get(getIndex())).getAccPwd(),item,((AccountDetail)getTableView().getItems().get(getIndex())).getAccPurpose())) {
                            ((AccountDetail)getTableView().getItems().get(getIndex())).setAccDomain(item);
                        }
                        blnChangeColor = this.isUniqueDetailFromOriginalData(((AccountDetail)getTableView().getItems().get(getIndex())).getUniqueID(), item, "Domain");
                        if(!blnChangeColor) deMarkFromUpdate((((AccountDetail)getTableView().getItems().get(getIndex())).getUniqueID()), "Domain");
                    }
                    case "Purpose" -> {
                        if(blnAccPurposeModified = modified = this.isUniqueDetail(((AccountDetail)getTableView().getItems().get(getIndex())).getAccName(),
                                ((AccountDetail)getTableView().getItems().get(getIndex())).getAccPwd(),((AccountDetail)getTableView().getItems().get(getIndex())).getAccDomain(),item)) {
                            ((AccountDetail)getTableView().getItems().get(getIndex())).setAccPurpose(item);
                        }
                        blnChangeColor = this.isUniqueDetailFromOriginalData(((AccountDetail)getTableView().getItems().get(getIndex())).getUniqueID(), item, "Purpose");
                        if(!blnChangeColor) deMarkFromUpdate((((AccountDetail)getTableView().getItems().get(getIndex())).getUniqueID()), "Purpose");
                    }
                    case "Added", "Last Modified" -> {}
                    default -> System.err.println("ERROR : Unexpected Table Column Header Passed : " + getTableColumn().getText());
                }
                if(modified) {
                    blnViewTableUpdate = true;
                    if(blnChangeColor) {
                        markAsUpdated(((AccountDetail)getTableView().getItems().get(getIndex())).getUniqueID(), blnAccNameModified, blnAccPwdModified, blnAccDomainModified, blnAccPurposeModified);
                        setBackground(new Background(new BackgroundFill(Color.web("#3B2540"), CornerRadii.EMPTY, Insets.EMPTY)));
                        setTextFill(Color.web("#E59C00"));
                        ((AccountDetail) getTableView().getItems().get(getIndex())).setDateLastModified(LocalDateTime.now());
                    } else {
                        getTableView().refresh();
                    }
                    super.commitEdit(item);
                }
                //System.out.println(getIndex() + "<>" + getTableView().getColumns().indexOf(getTableColumn()));
            }
            /*if(!modified) {
                final TableView<T> table = getTableView();
                if (table != null) {
                    TablePosition<T, String> position = new TablePosition<T, String>(getTableView(),
                            getTableRow().getIndex(), getTableColumn());
                    TableColumn.CellEditEvent<T, String> editEvent = new TableColumn.CellEditEvent<T, String>(table, position,
                            TableColumn.editCommitEvent(), item);
                    Event.fireEvent(getTableColumn(), editEvent);
                }
                System.out.println(item);
                updateItem(item, false);
                if (table != null) {
                    table.edit(-1, null);
                }
            }*/
        }
    }
}
