package com.ajikhoji.pwdmgr;

import com.ajikhoji.db.DataHandler;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.Bloom;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import javafx.scene.text.*;

public class AddNewData {

    private final static Pane pane = new Pane(), paneProgress = new Pane();
    final static SimpleIntegerProperty currentStageNumber = new SimpleIntegerProperty(0);
    private final static Timeline tlLine = new Timeline();
    private static int intFrameNumber = 0, intLineProceeding = -17;
    static String strAccName = "", strPassword = "", strDomain = "", strPurpose = "", strLink = "", strConform = "";
    private static TextField tfFocusOn;

    static void init() {
        //Code for creating base pane for holding "Add New Data" input fields.
        final double GAP = Math.min(ContentPane.paneContent.getWidth()*0.1D, ContentPane.paneContent.getHeight()*0.8D*0.3D);
        final double DBL_PANE_INITIAL_WIDTH = ContentPane.paneContent.getPrefWidth() - GAP, DBL_PANE_INITIAL_HEIGHT = ContentPane.paneContent.getPrefHeight()*0.8D - GAP;
        pane.setMinSize(ContentPane.paneContent.getWidth() - GAP, ContentPane.paneContent.getHeight()*0.8D - GAP);
        pane.setMaxSize(ContentPane.paneContent.getWidth()- GAP, ContentPane.paneContent.getHeight()*0.8D - GAP);
        pane.setPrefSize(ContentPane.paneContent.getWidth()- GAP, ContentPane.paneContent.getHeight()*0.8D - GAP);
        pane.getStyleClass().add("info-pane");

        final double DBL_TITLE_FONT_SIZE = 26.0D;
        final Font fontTitle = Font.font(Resource.getInstance().fontTitle.getFamily(), FontWeight.BOLD, DBL_TITLE_FONT_SIZE);
        final String[] strStagesTitle = {"Add New Account Info", "Verify New Account Info", "Finalize New Account Info", "Success"};

        final Text txtStageTitle = new Text(strStagesTitle[0]);
        txtStageTitle.setLayoutX(DBL_PANE_INITIAL_WIDTH*0.07D);
        txtStageTitle.setLayoutY(DBL_PANE_INITIAL_HEIGHT*0.07D + txtStageTitle.getBoundsInLocal().getHeight());
        txtStageTitle.setFont(fontTitle);
        txtStageTitle.getStyleClass().add("text-title");

        final Line l = new Line(txtStageTitle.getLayoutX(), txtStageTitle.getLayoutY() + DBL_PANE_INITIAL_HEIGHT*0.03D, DBL_PANE_INITIAL_WIDTH - txtStageTitle.getLayoutX(), txtStageTitle.getLayoutY() + DBL_PANE_INITIAL_HEIGHT*0.03D);
        l.getStyleClass().add("line-title");

        final Timeline tLineExpand = new Timeline();
        tLineExpand.getKeyFrames().addAll(new KeyFrame(Duration.ZERO, new KeyValue(l.scaleXProperty(), 0.0D)),
                new KeyFrame(Duration.millis(250.0D), new KeyValue(l.scaleXProperty(), 1.0D)));

        pane.getChildren().addAll(txtStageTitle, l);
        tLineExpand.playFromStart();

        pane.widthProperty().addListener((ol, ov, nv) -> {
//            txtStageTitle.setLayoutX(nv.doubleValue()*0.07D);
//            l.setStartX(txtStageTitle.getLayoutX());
            l.setEndX(nv.doubleValue() - txtStageTitle.getLayoutX());
        });
        pane.heightProperty().addListener((ol, ov, nv) -> {
            txtStageTitle.setLayoutY(nv.doubleValue()*0.04D + txtStageTitle.getBoundsInLocal().getHeight());
            l.setStartY(txtStageTitle.getLayoutY() + nv.doubleValue()*0.03D);
            l.setEndY(l.getStartY());
        });

        ContentPane.paneContent.widthProperty().addListener((ol, ov, nv) -> {
            final double NEW_GAP = Math.min(nv.doubleValue()*0.1D, ContentPane.paneContent.getHeight()*0.8D*0.3D);
            pane.setMinSize(nv.doubleValue() - NEW_GAP, ContentPane.paneContent.getHeight()*0.8D - NEW_GAP);
            pane.setMaxSize(nv.doubleValue() - NEW_GAP, ContentPane.paneContent.getHeight()*0.8D - NEW_GAP);
            pane.setPrefSize(nv.doubleValue() - NEW_GAP, ContentPane.paneContent.getHeight()*0.8D - NEW_GAP);
        });
        ContentPane.paneContent.heightProperty().addListener((ol, ov, nv) -> {
            final double NEW_GAP = Math.min(ContentPane.paneContent.getWidth()*0.1D ,nv.doubleValue()*0.8D*0.3D);
            pane.setMinSize(ContentPane.paneContent.getWidth() - NEW_GAP, nv.doubleValue()*0.8D - NEW_GAP);
            pane.setMaxSize(ContentPane.paneContent.getWidth() - NEW_GAP, nv.doubleValue()*0.8D - NEW_GAP);
            pane.setPrefSize(ContentPane.paneContent.getWidth() - NEW_GAP, nv.doubleValue()*0.8D - NEW_GAP);
        });

        final String[] strLeftButton = {"CLEAR", "BACK", "DISCARD"}, strRightButton = {"NEXT", "VERIFY", "ADD"};
        Button b1 = new Button(strLeftButton[0]);
        b1.setLayoutX(pane.getPrefWidth()*0.1D);
        b1.setLayoutY(pane.getPrefHeight()*0.85D);
        b1.setOnAction(e -> {
            currentStageNumber.set(Math.max(1, currentStageNumber.get() - 1));
        });
        Button b2 = new Button(strRightButton[0]);
        b2.setLayoutX(pane.getPrefWidth()*0.9D - b2.getWidth());
        b2.setLayoutY(b1.getLayoutY());
        b1.heightProperty().addListener((ol, ov, nv) -> {
            b1.setLayoutY(pane.getPrefHeight()*0.95D - nv.doubleValue());
        });
        b2.heightProperty().addListener((ol, ov, nv) -> {
            b2.setLayoutY(pane.getPrefHeight()*0.95D - nv.doubleValue());
        });
        b2.widthProperty().addListener((ol, ov, nv) -> {
            b2.setLayoutX(pane.getPrefWidth()*0.9D - nv.doubleValue());
        });
        pane.widthProperty().addListener((ol, ov, nv) -> {
            b1.setLayoutX(nv.doubleValue()*0.1D);
            b2.setLayoutX(nv.doubleValue()*0.9D - b2.getBoundsInLocal().getWidth());
        });
        pane.heightProperty().addListener((ol, ov, nv) -> {
            b1.setLayoutY(nv.doubleValue()*0.95D - b1.getHeight());
            b2.setLayoutY(nv.doubleValue()*0.95D - b2.getHeight());
        });
        pane.getChildren().addAll(b2, b1);

        //Code to handle four stages progressbar.
        final double DBL_INITIAL_HEIGHT_PANE_PROGRESS = ContentPane.paneContent.getPrefHeight()*0.2D;
        paneProgress.setStyle("-fx-background-color : transparent;");
        paneProgress.setMinSize(ContentPane.paneContent.getPrefWidth(),ContentPane.paneContent.getPrefHeight()*0.2D);
        paneProgress.setMaxSize(ContentPane.paneContent.getPrefWidth(),ContentPane.paneContent.getPrefHeight()*0.2D);
        paneProgress.setPrefSize(ContentPane.paneContent.getPrefWidth(),ContentPane.paneContent.getPrefHeight()*0.2D);
        ContentPane.paneContent.widthProperty().addListener((ol, ov, nv) -> {
            paneProgress.setMinWidth(nv.doubleValue());
            paneProgress.setMaxWidth(nv.doubleValue());
            paneProgress.setPrefWidth(nv.doubleValue());
        });
        ContentPane.paneContent.heightProperty().addListener((ol, ov, nv) -> {
            paneProgress.setMinHeight(nv.doubleValue()*0.2D);
            paneProgress.setMaxHeight(nv.doubleValue()*0.2D);
            paneProgress.setPrefHeight(nv.doubleValue()*0.2D);
        });

        final Circle[] c = new Circle[4];
        final Text[] t = new Text[4];
        final double DBL_GAP_PERCENTAGE = 0.15D, DBL_NUMBER_FONT_SIZE = 30.0D, DBL_STEPS_FONT_SIZE = 18.0D, DBL_CIRCLE_RADIUS_PERCENTAGE = 0.3D, DBL_Y_AXIS_ADJUSTMENT = 0.05D;

        final Font fontStepsNumber = Font.font(Resource.getInstance().fontNumber.getFamily(), DBL_NUMBER_FONT_SIZE);
        final Font fontStepsText = Font.font(Resource.getInstance().fontTitle.getFamily(), DBL_STEPS_FONT_SIZE);

        final String[] strProcessSteps = {"Enter", "Verify", "Confirm", "Finish"};
        final Text[] txtStep = new Text[4];

        final double DBL_CIRCLE_STROKE_WIDTH = 1.5D;
        final Line[] lUnfinishedTrack = new Line[3];
        final Line[] lFinishedTrack = new Line[3];
        for (int i = 0; i < 3; ++i) {
            lUnfinishedTrack[i] = new Line();
            lUnfinishedTrack[i].getStyleClass().add("line-track-empty");
            lFinishedTrack[i] = new Line();
            lFinishedTrack[i].getStyleClass().add("line-track-finished");
            lFinishedTrack[i].setVisible(false);
            paneProgress.getChildren().addAll(lUnfinishedTrack[i], lFinishedTrack[i]);
        }

        for(int i = 0; i < 4; ++i) {
            t[i] = new Text((i+1)+"");
            t[i].setFont(Font.font(fontStepsNumber.getFamily(), DBL_NUMBER_FONT_SIZE));
            t[i].setWrappingWidth(paneProgress.getPrefHeight()*DBL_CIRCLE_RADIUS_PERCENTAGE*2.0D);
            t[i].setTextAlignment(TextAlignment.CENTER);
            t[i].setLayoutX(paneProgress.getPrefWidth()*DBL_GAP_PERCENTAGE + i*paneProgress.getPrefWidth()*((1.0D-2*DBL_GAP_PERCENTAGE)/3.0D) - paneProgress.getPrefHeight()*0.3D);
            t[i].setLayoutY(paneProgress.getPrefHeight()*(DBL_CIRCLE_RADIUS_PERCENTAGE + DBL_Y_AXIS_ADJUSTMENT) + t[i].getBoundsInLocal().getHeight()/3.0D);
            t[i].getStyleClass().add("text-step-number");

            c[i] = new Circle(paneProgress.getPrefHeight()*DBL_CIRCLE_RADIUS_PERCENTAGE);
            c[i].setLayoutX(paneProgress.getPrefWidth()*DBL_GAP_PERCENTAGE + i*paneProgress.getPrefWidth()*((1.0D-2*DBL_GAP_PERCENTAGE)/3.0D));
            c[i].setLayoutY(paneProgress.getPrefHeight()*(DBL_CIRCLE_RADIUS_PERCENTAGE + DBL_Y_AXIS_ADJUSTMENT));
            c[i].getStyleClass().add("circle-stage-untouched");

            txtStep[i] = new Text(strProcessSteps[i]);
            txtStep[i].getStyleClass().add("text-instruction-steps");
            txtStep[i].setFont(Font.font(fontStepsText.getFamily(), DBL_STEPS_FONT_SIZE));
            if(txtStep[i].getBoundsInLocal().getWidth() > t[i].getWrappingWidth()) {
                txtStep[i].setWrappingWidth(txtStep[i].getBoundsInLocal().getWidth());
                txtStep[i].setLayoutX(t[i].getLayoutX() - (txtStep[i].getBoundsInLocal().getWidth()-t[i].getWrappingWidth())*0.5D);
            } else {
                txtStep[i].setWrappingWidth(t[i].getWrappingWidth());
                txtStep[i].setLayoutX(t[i].getLayoutX());
            }
            txtStep[i].setTextAlignment(TextAlignment.CENTER);
            txtStep[i].setLayoutY(c[i].getLayoutY() + c[i].getRadius() + txtStep[i].getBoundsInLocal().getHeight() + paneProgress.getPrefHeight()*0.05D);
            paneProgress.getChildren().addAll(c[i], t[i], txtStep[i]);
        }

        paneProgress.widthProperty().addListener((ol, ov, nv) -> {
            for(int i = 0; i < 4; ++i) {
                t[i].setLayoutX(nv.doubleValue()*DBL_GAP_PERCENTAGE + i*nv.doubleValue()*((1.0D-2*DBL_GAP_PERCENTAGE)/3.0D) - paneProgress.getPrefHeight()*0.3D);
                if(txtStep[i].getBoundsInLocal().getWidth() > t[i].getWrappingWidth()) {
                    txtStep[i].setWrappingWidth(txtStep[i].getBoundsInLocal().getWidth());
                    txtStep[i].setLayoutX(t[i].getLayoutX() - (txtStep[i].getBoundsInLocal().getWidth()-t[i].getWrappingWidth())*0.5D);
                } else {
                    txtStep[i].setWrappingWidth(t[i].getWrappingWidth());
                    txtStep[i].setLayoutX(t[i].getLayoutX());
                }
                if(i > 0) {
                    lUnfinishedTrack[i-1].setStartX(c[i-1].getLayoutX() + c[i-1].getRadius() + DBL_CIRCLE_STROKE_WIDTH);
                    lUnfinishedTrack[i-1].setEndX(c[i].getLayoutX() - c[i].getRadius() - DBL_CIRCLE_STROKE_WIDTH);
                    if(lFinishedTrack[i-1].isVisible() && i-1 != intLineProceeding) {
                        lFinishedTrack[i-1].setStartX(c[i-1].getLayoutX() + c[i-1].getRadius() + DBL_CIRCLE_STROKE_WIDTH);
                        lFinishedTrack[i-1].setEndX(c[i].getLayoutX() - c[i].getRadius() - DBL_CIRCLE_STROKE_WIDTH);
                    }
                }
                c[i].setLayoutX(nv.doubleValue()*DBL_GAP_PERCENTAGE + i*nv.doubleValue()*((1.0D-2*DBL_GAP_PERCENTAGE)/3.0D));
            }
        });

        paneProgress.heightProperty().addListener((ol, ov, nv) -> {
            for (int i = 0; i < 4; ++i) {
                c[i].setRadius(nv.doubleValue()*0.3D);
                c[i].setLayoutY(nv.doubleValue()*0.3D + DBL_Y_AXIS_ADJUSTMENT);
                t[i].setWrappingWidth(c[i].getRadius()*2.0D);
                t[i].setFont(Font.font(fontStepsNumber.getFamily(),DBL_NUMBER_FONT_SIZE*(nv.doubleValue()/DBL_INITIAL_HEIGHT_PANE_PROGRESS)));
                txtStep[i].setFont(Font.font(fontStepsText.getFamily(), DBL_STEPS_FONT_SIZE*(nv.doubleValue()/DBL_INITIAL_HEIGHT_PANE_PROGRESS)));

                t[i].setLayoutX(paneProgress.getPrefWidth()*DBL_GAP_PERCENTAGE + i*paneProgress.getPrefWidth()*((1.0D-2*DBL_GAP_PERCENTAGE)/3.0D) - nv.doubleValue()*0.3D);
                t[i].setLayoutY(nv.doubleValue()*(DBL_CIRCLE_RADIUS_PERCENTAGE) + t[i].getBoundsInLocal().getHeight()/3.0D);

                final Text temp = new Text(txtStep[i].getText());
                temp.setFont(txtStep[i].getFont());
                if(temp.getBoundsInLocal().getWidth() > c[i].getRadius()*2.0) {
                    txtStep[i].setWrappingWidth(temp.getBoundsInLocal().getWidth());
                    txtStep[i].setLayoutX(t[i].getLayoutX() - (txtStep[i].getWrappingWidth()-t[i].getWrappingWidth())*0.5D);
                } else {
                    txtStep[i].setWrappingWidth(t[i].getWrappingWidth());
                    txtStep[i].setLayoutX(t[i].getLayoutX());
                }
                txtStep[i].setLayoutY(c[i].getLayoutY() + c[i].getRadius() + txtStep[i].getBoundsInLocal().getHeight() + nv.doubleValue()*0.05D);
                if(i > 0) {
                    lUnfinishedTrack[i-1].setStartX(c[i-1].getLayoutX() + c[i-1].getRadius() + DBL_CIRCLE_STROKE_WIDTH);
                    lUnfinishedTrack[i-1].setEndX(c[i].getLayoutX() - c[i].getRadius() - DBL_CIRCLE_STROKE_WIDTH);
                    lUnfinishedTrack[i-1].setStartY(c[i-1].getLayoutY());
                    lUnfinishedTrack[i-1].setEndY(c[i].getLayoutY());
                    lFinishedTrack[i-1].setStartY(c[i-1].getLayoutY());
                    lFinishedTrack[i-1].setEndY(c[i].getLayoutY());
                    if(lFinishedTrack[i-1].isVisible() && intLineProceeding != i - 1) {
                        lFinishedTrack[i-1].setStartX(c[i-1].getLayoutX() + c[i-1].getRadius() + DBL_CIRCLE_STROKE_WIDTH);
                        lFinishedTrack[i-1].setEndX(c[i].getLayoutX() - c[i].getRadius() - DBL_CIRCLE_STROKE_WIDTH);
                    }
                }
            }
        });
        final Bloom b = new Bloom(0.4D);
        tlLine.setCycleCount(20);
        currentStageNumber.addListener((ol,ov, nv) -> {
            if(ov.intValue() == 4) {
                if (nv.intValue() == 1) {
                    strAccName = strPassword = strConform = strDomain = strLink = strPurpose = "";
                    for(int i = 0; i < 3; ++i) {
                        c[i + 1].getStyleClass().clear();
                        c[i + 1].getStyleClass().add("circle-stage-untouched");
                        lFinishedTrack[i].setVisible(false);
                    }
                    c[0].getStyleClass().clear();
                    c[0].getStyleClass().add("circle-stage-active");
                }
            }
            if(ov.intValue() != nv.intValue()) {
                txtStageTitle.setText(strStagesTitle[nv.intValue() - 1]);
                tLineExpand.playFromStart();
                if(nv.intValue() == 4) {
                    b1.setVisible(false);
                    b2.setVisible(false);
                } else {
                    b1.setVisible(true);
                    b2.setVisible(true);
                    b1.setText(strLeftButton[nv.intValue() - 1]);
                    b2.setText(strRightButton[nv.intValue() - 1]);
                }
            }
            if(ov.intValue() + 1 == nv.intValue()) {
                tlLine.stop();
                if(intLineProceeding != -17) {
                    lFinishedTrack[intLineProceeding].setEndX(lUnfinishedTrack[intLineProceeding].getEndX());
                }
                intFrameNumber = 0;
                tlLine.getKeyFrames().clear();
                if(ov.intValue() > 0) {
                    intLineProceeding = ov.intValue() - 1;
                    c[ov.intValue() - 1].getStyleClass().clear();
                    c[ov.intValue() - 1].getStyleClass().add("circle-stage-passed");
                    lFinishedTrack[ov.intValue() - 1].setVisible(true);
                    lFinishedTrack[ov.intValue() - 1].setStartX(lUnfinishedTrack[ov.intValue() - 1].getStartX());
                    lFinishedTrack[ov.intValue() - 1].setEndX(lFinishedTrack[ov.intValue() - 1].getStartX());
                    lFinishedTrack[ov.intValue() - 1].setStartY(lUnfinishedTrack[ov.intValue() - 1].getStartY());
                    lFinishedTrack[ov.intValue() - 1].setEndY(lUnfinishedTrack[ov.intValue() - 1].getEndY());
                    tlLine.getKeyFrames().add(new KeyFrame(Duration.millis(25.0D), e -> {
                        ++intFrameNumber;
                        final double DBL_STRETCH_UNTO = lUnfinishedTrack[ov.intValue() - 1].getEndX() - lUnfinishedTrack[ov.intValue() - 1].getStartX();
                        lFinishedTrack[ov.intValue() - 1].setStartX(lUnfinishedTrack[ov.intValue() - 1].getStartX());
                        lFinishedTrack[ov.intValue() - 1].setEndX(lFinishedTrack[ov.intValue() - 1].getStartX() + DBL_STRETCH_UNTO*(((double)intFrameNumber)/20.0D));
                        lFinishedTrack[ov.intValue() - 1].setStartY(lUnfinishedTrack[ov.intValue() - 1].getStartY());
                        lFinishedTrack[ov.intValue() - 1].setEndY(lUnfinishedTrack[ov.intValue() - 1].getEndY());
                    }));
                    tlLine.setOnFinished(e -> {
                        if(lFinishedTrack[ov.intValue() - 1].isVisible() && lFinishedTrack[ov.intValue() - 1].getEndX() == lUnfinishedTrack[ov.intValue() - 1].getEndX()) {
                            t[nv.intValue() - 1].setEffect(b);
                            c[nv.intValue() - 1].getStyleClass().clear();
                            c[nv.intValue() - 1].getStyleClass().add("circle-stage-active");
                            intLineProceeding = -17;
                        }
                    });
                    tlLine.playFromStart();
                    t[ov.intValue() - 1].setEffect(null);
                } else {
                    t[nv.intValue() - 1].setEffect(b);
                    c[nv.intValue() - 1].getStyleClass().clear();
                    c[nv.intValue() - 1].getStyleClass().add("circle-stage-active");
                }
            } else if (ov.intValue() - 1 == nv.intValue()) {
                c[ov.intValue() - 1].getStyleClass().clear();
                c[ov.intValue() - 1].getStyleClass().add("circle-stage-untouched");
                t[ov.intValue() - 1].setEffect(null);
                tlLine.stop();
                if(intLineProceeding != -17) {
                    lFinishedTrack[intLineProceeding].setVisible(false);
                }
                tlLine.getKeyFrames().clear();
                intFrameNumber = 0;
                intLineProceeding = nv.intValue() - 1;
                lFinishedTrack[nv.intValue() - 1].setStartX(lUnfinishedTrack[nv.intValue() - 1].getStartX());
                lFinishedTrack[nv.intValue() - 1].setEndX(lFinishedTrack[nv.intValue() - 1].getEndX());
                lFinishedTrack[nv.intValue() - 1].setStartY(lUnfinishedTrack[nv.intValue() - 1].getStartY());
                lFinishedTrack[nv.intValue() - 1].setEndY(lUnfinishedTrack[nv.intValue() - 1].getEndY());
                tlLine.getKeyFrames().add(new KeyFrame(Duration.millis(25.0D), e -> {
                    ++intFrameNumber;
                    final double DBL_STRETCH_UNTO = lUnfinishedTrack[nv.intValue() - 1].getEndX() - lUnfinishedTrack[nv.intValue() - 1].getStartX();
                    lFinishedTrack[nv.intValue() - 1].setStartX(lUnfinishedTrack[nv.intValue() - 1].getStartX());
                    lFinishedTrack[nv.intValue() - 1].setEndX(lUnfinishedTrack[nv.intValue() - 1].getEndX() - DBL_STRETCH_UNTO*(((double)intFrameNumber)/20.0D));
                    lFinishedTrack[nv.intValue() - 1].setStartY(lUnfinishedTrack[nv.intValue() - 1].getStartY());
                    lFinishedTrack[nv.intValue() - 1].setEndY(lUnfinishedTrack[nv.intValue() - 1].getEndY());
                }));
                tlLine.setOnFinished(e -> {
                    intLineProceeding = -17;
                    lFinishedTrack[nv.intValue() - 1].setVisible(false);
                    t[nv.intValue() - 1].setEffect(b);
                    c[nv.intValue() - 1].getStyleClass().clear();
                    c[nv.intValue() - 1].getStyleClass().add("circle-stage-active");
                });
                tlLine.playFromStart();
            }
        });

        final double DBL_FONT_SIZE = 18.0D;
        final Font fontDefault = Font.font(Resource.getInstance().fontNormal.getFamily(), DBL_FONT_SIZE);

        final Text txtPrompt = new Text();
        txtPrompt.setFont(Font.font(fontTitle.getFamily(), 18.0D));
        txtPrompt.setTextAlignment(TextAlignment.LEFT);
        txtPrompt.getStyleClass().add("info");
        txtPrompt.setVisible(false);
        pane.getChildren().add(txtPrompt);

        final VBox bx = new VBox();
        bx.setAlignment(Pos.CENTER);
        bx.setLayoutX(DBL_PANE_INITIAL_WIDTH*0.075D);
        bx.setLayoutY(l.getStartY() + DBL_PANE_INITIAL_HEIGHT*0.01D);
        bx.setSpacing(DBL_PANE_INITIAL_HEIGHT*0.05D);
        bx.setPrefSize(pane.getPrefWidth() - 2*bx.getLayoutX(), b1.getLayoutY() - l.getStartY() - 8.0D);
        pane.widthProperty().addListener((olw, ovw, nvw) -> {
            bx.setLayoutX(txtStageTitle.getLayoutX());
            final double DBL_STAGE_FIELD_WIDTH = nvw.doubleValue() - 2*bx.getLayoutX();
            bx.setMaxWidth(DBL_STAGE_FIELD_WIDTH);
            bx.setMaxWidth(DBL_STAGE_FIELD_WIDTH);
            bx.setPrefWidth(DBL_STAGE_FIELD_WIDTH);
        });
        pane.heightProperty().addListener((olh, ovh, nvh) -> bx.setLayoutY(l.getStartY() + nvh.doubleValue()*0.01D));
        b1.layoutYProperty().addListener((oll, ovl, nvl) -> {
            final double DBL_HEIGHT = nvl.doubleValue() - l.getStartY() - 8.0D;
            bx.setMaxHeight(DBL_HEIGHT);
            bx.setMaxHeight(DBL_HEIGHT);
            bx.setPrefHeight(DBL_HEIGHT);
        });

        currentStageNumber.addListener((ol, ov, nv) -> {
            if(ov.intValue() != nv.intValue()) {
                bx.getChildren().clear();
                txtPrompt.setVisible(false);
                switch (nv.intValue()) {
                    case 1 -> {
                        //Field - 1
                        final Text txtAccID = new Text("Account ID");
                        txtAccID.setFont(Font.font(fontDefault.getFamily(), 16.0D));
                        txtAccID.getStyleClass().add("text-label");
                        final TextField tfAccId = new TextField(strAccName);
                        tfAccId.setFont(Font.font(fontDefault.getFamily(), 16.0D));
                        tfAccId.textProperty().addListener((olt, ovt, nvt) -> {
                            if(nvt != null) {
                                if (nvt.length() > 60) {
                                    txtPrompt.setVisible(true);
                                    txtPrompt.setText("The field 'Account ID' has reached maximum number of 60 characters.");
                                    tfAccId.setText(nvt.substring(0, 60));
                                }
                                strAccName = tfAccId.getText();
                            }
                        });
                        tfAccId.setOnKeyPressed(e -> {
                            if(!e.isControlDown() && e.getCode() != KeyCode.X && e.getCode() != KeyCode.V) txtPrompt.setVisible(false);
                        });
                        final VBox v1 = new VBox(txtAccID, tfAccId);
                        v1.setSpacing(3.0D);
                        //Field - 2
                        final Text txtPassword = new Text("Password");
                        txtPassword.setFont(Font.font(fontDefault.getFamily(), 16.0D));
                        txtPassword.getStyleClass().add("text-label");
                        final PasswordField pwdField = new PasswordField();
                        pwdField.setText(strPassword);
                        pwdField.setFont(Font.font(fontDefault.getFamily(), 16.0D));
                        pwdField.textProperty().addListener((olt, ovt, nvt) -> {
                            if(nvt != null) {
                                if (nvt.length() > 60) {
                                    txtPrompt.setVisible(true);
                                    txtPrompt.setText("The field 'Password' has reached maximum number of 60 characters.");
                                    pwdField.textProperty().setValue(nvt.substring(0, 60));
                                }
                                strPassword = pwdField.getText();
                            }
                        });
                        pwdField.setOnKeyPressed(e -> {
                            if(!e.isControlDown() && e.getCode() != KeyCode.X && e.getCode() != KeyCode.V) txtPrompt.setVisible(false);
                        });
                        final PasswordField pfVerify = new PasswordField() {
                            @Override
                            public void paste() { }
                        };
                        pfVerify.setText(strConform);
                        final TextField tfPwd = new TextField(strPassword);
                        tfPwd.setFont(Font.font(fontDefault.getFamily(), 16.0D));
                        tfPwd.textProperty().addListener((olt, ovt, nvt) -> {
                            if(nvt != null) {
                                if (nvt.length() > 60) {
                                    txtPrompt.setVisible(true);
                                    txtPrompt.setText("The field 'Password' has reached maximum number of 60 characters.");
                                    tfPwd.textProperty().setValue(nvt.substring(0, 60));
                                }
                                strPassword = tfPwd.getText();
                                if(strConform.isEmpty() && strPassword.isEmpty()) {
                                    pfVerify.getStyleClass().removeAll("pwd-correct", "pwd-incorrect");
                                } else if (strPassword.equals(strConform)) {
                                    pfVerify.getStyleClass().remove("pwd-incorrect");
                                    pfVerify.getStyleClass().add("pwd-correct");
                                } else {
                                    pfVerify.getStyleClass().remove("pwd-correct");
                                    pfVerify.getStyleClass().add("pwd-incorrect");
                                }
                            }
                        });
                        tfPwd.setOnKeyPressed(e -> {
                            if(!e.isControlDown() && e.getCode() != KeyCode.X && e.getCode() != KeyCode.V) txtPrompt.setVisible(false);
                        });
                        final CheckBox cbx = new CheckBox("Show Password");
                        cbx.setFont(Font.font(fontDefault.getFamily(), 16.0D));
                        cbx.setPadding(new Insets(5.0D, 0.D, 0.0D, 25.0D));
                        cbx.getStyleClass().add("chkbox");
                        final VBox v2 = new VBox(txtPassword, pwdField, cbx);
                        pwdField.textProperty().bindBidirectional(tfPwd.textProperty());
                        cbx.selectedProperty().addListener((olc, ovc, nvc) -> {
                            if (nvc) {
                                v2.getChildren().removeAll(pwdField, cbx);
                                v2.getChildren().addAll(tfPwd, cbx);
                            } else {
                                v2.getChildren().removeAll(tfPwd, cbx);
                                v2.getChildren().addAll(pwdField, cbx);
                            }
                        });
                        v2.setSpacing(3.0D);
                        //Field - 3
                        final Text txtVerifyPassword = new Text("Re-enter password for verification of correctness");
                        txtVerifyPassword.setFont(Font.font(fontDefault.getFamily(), 16.0D));
                        txtVerifyPassword.getStyleClass().add("text-label");
                        txtVerifyPassword.setFont(Font.font(fontDefault.getFamily(), 16.0D));
                        pfVerify.setFont(Font.font(fontDefault.getFamily(), 16.0D));
                        pfVerify.textProperty().addListener((olt, ovt, nvt) -> {
                            if(nvt != null) {
                                if (nvt.length() > 60) {
                                    pfVerify.textProperty().setValue(nvt.substring(0, 60));
                                }
                                strConform = pfVerify.getText();
                                if(strConform.isEmpty() && strPassword.isEmpty()) {
                                    pfVerify.getStyleClass().removeAll("pwd-correct", "pwd-incorrect");
                                } else if (strConform.equals(strPassword)) {
                                    pfVerify.getStyleClass().remove("pwd-incorrect");
                                    pfVerify.getStyleClass().add("pwd-correct");
                                } else {
                                    pfVerify.getStyleClass().remove("pwd-correct");
                                    pfVerify.getStyleClass().add("pwd-incorrect");
                                }
                            }
                        });
                        pfVerify.setOnKeyPressed(e -> {
                            if(!e.isControlDown() && e.getCode() != KeyCode.X && e.getCode() != KeyCode.V) txtPrompt.setVisible(false);
                        });
                        final VBox v3 = new VBox(txtVerifyPassword, pfVerify);
                        v3.setSpacing(3.0D);
                        v3.heightProperty().addListener((olh, ovh, nvh) -> bx.setSpacing((bx.getPrefHeight() - v1.getHeight() - v2.getHeight() - nvh.doubleValue()) / 4.0D));
                        bx.heightProperty().addListener((olh, ovh, nvh) -> bx.setSpacing((nvh.doubleValue() - v1.getHeight() - v2.getHeight() - v3.getHeight()) / 4.0D));
                        b1.setOnAction(e -> {
                            tfAccId.setText("");
                            tfPwd.setText("");
                            pwdField.setText("");
                            pfVerify.setText("");
                            tfAccId.requestFocus();
                        });
                        b2.setOnAction(e -> {
                            if(strAccName.isEmpty()) {
                                txtPrompt.setText("Account ID cannot be empty.");
                                txtPrompt.setVisible(true);
                            } else if(strPassword.isEmpty()) {
                                txtPrompt.setText("Password cannot be empty.");
                                txtPrompt.setVisible(true);
                            } else if (!strPassword.equals(strConform)) {
                                txtPrompt.setText("Verification failed : Password does not match.");
                                txtPrompt.setVisible(true);
                            } else {
                                currentStageNumber.set(2);
                            }
                        });
                        bx.getChildren().addAll(v1, v2, v3);
                        if(!strPassword.isEmpty()) {
                            if (strPassword.equals(strConform)) {
                                pfVerify.getStyleClass().remove("pwd-incorrect");
                                pfVerify.getStyleClass().add("pwd-correct");
                            } else {
                                pfVerify.getStyleClass().remove("pwd-correct");
                                pfVerify.getStyleClass().add("pwd-incorrect");
                            }
                        }
                        tfAccId.requestFocus();
                        tfFocusOn = tfAccId;
                    }
                    case 2 -> {
                        //Field - 1
                        final Text txtDomain = new Text("Domain Name");
                        txtDomain.setFont(Font.font(fontDefault.getFamily(), 16.0D));
                        txtDomain.getStyleClass().add("text-label");
                        final TextField tfDomain = new TextField(strDomain);
                        tfDomain.setFont(Font.font(fontDefault.getFamily(), 16.0D));
                        tfDomain.textProperty().addListener((olt, ovt, nvt) -> {
                            if(nvt != null) {
                                if (nvt.length() > 60) {
                                    txtPrompt.setVisible(true);
                                    txtPrompt.setText("The field 'Domain Name' has reached maximum number of 60 characters.");
                                    tfDomain.textProperty().setValue(nvt.substring(0, 60));
                                }
                                strDomain = tfDomain.getText();
                            }
                        });
                        tfDomain.setOnKeyPressed(e -> {
                            if(!e.isControlDown() && e.getCode() != KeyCode.X && e.getCode() != KeyCode.V) txtPrompt.setVisible(false);
                        });
                        final VBox v4 = new VBox(txtDomain, tfDomain);
                        v4.setSpacing(3.0D);
                        //Field - 2
                        final Text txtLink = new Text("Domain Link (Optional)");
                        txtLink.setFont(Font.font(fontDefault.getFamily(), 16.0D));
                        txtLink.getStyleClass().add("text-label");
                        final TextField tfLink = new TextField(strLink);
                        tfLink.setFont(Font.font(fontDefault.getFamily(), 16.0D));
                        tfLink.textProperty().addListener((olt, ovt, nvt) -> {
                            if(nvt != null) {
                                if (nvt.length() > 300) {
                                    txtPrompt.setVisible(true);
                                    txtPrompt.setText("The field 'Domain Link' has reached maximum number of 300 characters.");
                                    tfLink.textProperty().setValue(nvt.substring(0, 300));
                                }
                                strLink = tfLink.getText();
                            }
                        });
                        tfLink.setOnKeyPressed(e -> {
                            if(!e.isControlDown() && e.getCode() != KeyCode.X && e.getCode() != KeyCode.V) txtPrompt.setVisible(false);
                        });
                        final VBox v5 = new VBox(txtLink, tfLink);
                        v5.setSpacing(3.0D);
                        //Field - 3
                        final Text txtPurpose = new Text("Purpose");
                        txtPurpose.setFont(Font.font(fontDefault.getFamily(), 16.0D));
                        txtPurpose.getStyleClass().add("text-label");
                        txtPurpose.setFont(Font.font(fontDefault.getFamily(), 16.0D));
                        final TextField tfPurpose = new TextField(strPurpose);
                        tfPurpose.setFont(Font.font(fontDefault.getFamily(), 16.0D));
                        tfPurpose.textProperty().addListener((olt, ovt, nvt) -> {
                            if(nvt != null) {
                                if (nvt.length() > 60) {
                                    txtPrompt.setVisible(true);
                                    txtPrompt.setText("The field 'Purpose' has reached maximum number of 60 characters.");
                                    tfPurpose.textProperty().setValue(nvt.substring(0, 60));
                                }
                                strPurpose = tfPurpose.getText();
                            }
                        });
                        tfPurpose.setOnKeyPressed(e -> {
                            if(!e.isControlDown() && e.getCode() != KeyCode.X && e.getCode() != KeyCode.V) txtPrompt.setVisible(false);
                        });
                        final VBox v6 = new VBox(txtPurpose, tfPurpose);
                        v6.setSpacing(3.0D);
                        v6.heightProperty().addListener((olh, ovh, nvh) -> bx.setSpacing((bx.getPrefHeight() - v4.getHeight() - v5.getHeight() - nvh.doubleValue()) / 4.0D));
                        bx.heightProperty().addListener((olh, ovh, nvh) -> bx.setSpacing((nvh.doubleValue() - v4.getHeight() - v5.getHeight() - v6.getHeight()) / 4.0D));
                        b1.setOnAction(e -> currentStageNumber.set(1));
                        b2.setOnAction(e -> {
                            if(strDomain.isEmpty()) {
                                txtPrompt.setText("Domain Name cannot be empty.");
                                txtPrompt.setVisible(true);
                            } else if (strPurpose.isEmpty()) {
                                txtPrompt.setText("Purpose cannot be empty.");
                                txtPrompt.setVisible(true);
                            } else if(DataHandler.getInstance().checkIfAccDetailAvailable(strAccName, strPassword, strDomain, strPurpose)) {
                                txtPrompt.setText("Account detail with entered info already available.");
                                txtPrompt.setVisible(true);
                                //TO-DO : cdhecj if all 4 are avaialble alresayd.
                            } else {
                                currentStageNumber.set(3);
                            }
                        });
                        bx.getChildren().addAll(v4, v5, v6);
                        tfDomain.requestFocus();
                    }
                    case 3 -> {
                        final Text txtAccID = new Text("Account ID : ");
                        txtAccID.setFont(Font.font(fontDefault.getFamily(), 16.0D));
                        txtAccID.getStyleClass().add("text-label");
                        txtAccID.setTextAlignment(TextAlignment.RIGHT);
                        final Text txtEnteredID = new Text(strAccName);
                        txtEnteredID.setFont(Font.font(fontDefault.getFamily(), 16.0D));
                        txtEnteredID.getStyleClass().add("text-label");
                        final Text txtPassword = new Text("Password : ");
                        txtPassword.setTextAlignment(TextAlignment.RIGHT);
                        txtPassword.setFont(Font.font(fontDefault.getFamily(), 16.0D));
                        txtPassword.getStyleClass().add("text-label");
                        final Text txtEnteredPwd = new Text(strPassword);
                        txtEnteredPwd.setFont(Font.font(fontDefault.getFamily(), 16.0D));
                        txtEnteredPwd.getStyleClass().add("text-label");
                        final Text txtDomain = new Text("Domain Name : ");
                        txtDomain.setFont(Font.font(fontDefault.getFamily(), 16.0D));
                        txtDomain.setTextAlignment(TextAlignment.RIGHT);
                        txtDomain.getStyleClass().add("text-label");
                        final Text txtEnteredDomain = new Text(strDomain);
                        txtEnteredDomain.setFont(Font.font(fontDefault.getFamily(), 16.0D));
                        txtEnteredDomain.getStyleClass().add("text-label");
                        final Text txtLink = new Text("Domain Link : ");
                        txtLink.setFont(Font.font(fontDefault.getFamily(), 16.0D));
                        txtLink.setTextAlignment(TextAlignment.RIGHT);
                        txtLink.getStyleClass().add("text-label");
                        final Text txtEnteredLink = new Text(strLink.isEmpty() ? "NOT AVAILABLE" : strLink);
                        txtEnteredLink.setFont(Font.font(fontDefault.getFamily(), 16.0D));
                        txtEnteredLink.getStyleClass().add(strLink.isEmpty() ? "link-not-available" : "link");
                        final Text txtPurpose = new Text("Purpose : ");
                        txtPurpose.setTextAlignment(TextAlignment.RIGHT);
                        txtPurpose.setFont(Font.font(fontDefault.getFamily(), 16.0D));
                        txtPurpose.getStyleClass().add("text-label");
                        final Text txtEnteredPurpose = new Text(strPurpose);
                        txtEnteredPurpose.setFont(Font.font(fontDefault.getFamily(), 16.0D));
                        txtEnteredPurpose.getStyleClass().add("text-label");

                        txtEnteredLink.setOnMouseClicked(e -> HelloApplication.hs.showDocument(strLink));

                        final double WW = txtDomain.getBoundsInLocal().getWidth()*1.08D;
                        txtAccID.setWrappingWidth(WW);
                        txtPassword.setWrappingWidth(WW);
                        txtLink.setWrappingWidth(WW);
                        txtPurpose.setWrappingWidth(WW);
                        txtDomain.setWrappingWidth(WW);
                        txtEnteredLink.setWrappingWidth(bx.getPrefWidth() - WW);

                        final VBox container = new VBox(new HBox(txtAccID, txtEnteredID), new HBox(txtPassword ,txtEnteredPwd),
                                new HBox(txtDomain, txtEnteredDomain), new HBox(txtPurpose ,txtEnteredPurpose), new HBox(txtLink ,txtEnteredLink));
                        container.setAlignment(Pos.CENTER);
                        container.setSpacing(20.0D);
                        container.setPadding(new Insets(20.0D, 0.0D, 0.0D, 0.0D));
                        final ScrollPane sp = new ScrollPane(container);
                        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
//                        container.setStyle("-fx-background-color: purple;");
                        bx.prefWidthProperty().addListener((olw, ovw, nvw) -> {
                            sp.setMinWidth(nvw.doubleValue());
                            sp.setMaxWidth(nvw.doubleValue());
                            sp.setPrefWidth(nvw.doubleValue());
                            container.setMinWidth(nvw.doubleValue());
                            container.setMaxWidth(nvw.doubleValue());
                            container.setPrefWidth(nvw.doubleValue());
                            txtEnteredID.setWrappingWidth(nvw.doubleValue() - WW*1.03D);
                            txtEnteredPwd.setWrappingWidth(nvw.doubleValue() - WW*1.03D);
                            txtEnteredDomain.setWrappingWidth(nvw.doubleValue() - WW*1.03D);
                            txtEnteredLink.setWrappingWidth(nvw.doubleValue() - WW*1.03D);
                            txtEnteredPurpose.setWrappingWidth(nvw.doubleValue() - WW*1.03D);
                        });
                        bx.prefHeightProperty().addListener((olw, ovw, nvw) -> {
                            sp.setMinHeight(nvw.doubleValue());
                            sp.setMaxHeight(nvw.doubleValue());
                            sp.setPrefHeight(nvw.doubleValue());
//                            container.setMinHeight(nvw.doubleValue());
//                            container.setMaxHeight(nvw.doubleValue());
                            container.setPrefHeight(nvw.doubleValue() - 20.0D);
                        });
                        bx.getChildren().addAll(sp);

                        b1.setOnAction(e -> currentStageNumber.set(2));
                        b2.setOnAction(e -> currentStageNumber.set(4));
                    }
                    case 4 -> {
                        final Text txtStatus = new Text("Processing Data   ");
                        txtStatus.setFont(Font.font(fontDefault.getFamily(), 36.0D));
                        txtStatus.getStyleClass().add("text-label");
                        final Timeline tlProcessing = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(txtStatus.textProperty(), "Processing Data.  ")),
                                new KeyFrame(Duration.millis(300.0D), new KeyValue(txtStatus.textProperty(), "Processing Data.. ")),
                                new KeyFrame(Duration.millis(600.0D), new KeyValue(txtStatus.textProperty(), "Processing Data...")),
                                new KeyFrame(Duration.millis(900.0D), new KeyValue(txtStatus.textProperty(), "Processing Data...")));
                        bx.getChildren().add(txtStatus);
                        tlProcessing.setCycleCount(Timeline.INDEFINITE);
                        tlProcessing.playFromStart();
                        final String result = DataHandler.getInstance().addNewAccInfo(strAccName, strPassword, strDomain, strLink, strPurpose);
                        tlProcessing.stop();
                        tlProcessing.setCycleCount(1);
                        tlProcessing.getKeyFrames().clear();
                        if(result.startsWith("Added")) {
                            // goto stage - 1
                            txtStatus.setText("Data added successfully!");
                            tlProcessing.getKeyFrames().add(new KeyFrame(Duration.millis(3300.0D), e -> {
                                currentStageNumber.set(1);
                            }));
                        } else {
                            //display unable to add data, error occured
                            txtStatus.setText("Error : Unable to add data.\nPlease try again after some time.");
                            tlProcessing.getKeyFrames().add(new KeyFrame(Duration.millis(3300.0D), e -> {
                                currentStageNumber.set(3);
                            }));
                        }
                        tlProcessing.playFrom(Duration.ZERO);
                        //To-DO : if data added successfully, then show "success message" and go to stage -1 (reset everything), otherwise show failed
                    }
                }
            }
        });
//                        bx.setStyle("-fx-background-color : purple;");
        pane.getChildren().add(bx);

        b1.layoutXProperty().addListener((ol, ov, nv) -> {
            txtPrompt.setLayoutX(nv.doubleValue() + b1.getBoundsInLocal().getWidth() + 20.0D);
            txtPrompt.setWrappingWidth(b2.getLayoutX() - nv.doubleValue() - b2.getWidth() - 40.0D);
        });
        b1.layoutYProperty().addListener((ol, ov, nv) -> {
            txtPrompt.setLayoutY(nv.doubleValue() + b1.getHeight()*0.4D);
        });
        b1.widthProperty().addListener((ol, ov, nv) -> {
            txtPrompt.setLayoutX(nv.doubleValue() + b1.getLayoutX() + 20.0D);
        });
        b2.layoutXProperty().addListener((ol, ov, nv) -> {
            txtPrompt.setWrappingWidth(nv.doubleValue() - b1.getLayoutX() - b2.getWidth() - 40.0D);
        });
        //Stage one fields
        /*final String[] strStageOneFields = {"Account ID : ", "Password : ", "Domain : ", "Purpose : "};
        final Text[] txtStageOne = new Text[strStageOneFields.length];
        final TextField[] txtFieldStageOne = new TextField[3];
        final PasswordField pf = new PasswordField();
        final VBox stageOneField = new VBox();
        final Text txtPrompt = new Text();
        txtPrompt.setFont(Font.font(fontTitle.getFamily(), 18.0D));
        txtPrompt.setTextAlignment(TextAlignment.CENTER);
        txtPrompt.getStyleClass().add("info");
        pane.getChildren().add(txtPrompt);
        txtPrompt.setVisible(false);
        stageOneField.setAlignment(Pos.CENTER);
        for(int i = 0; i < 4; ++i) {
//            final HBox hb = new HBox();
            txtStageOne[i] = new Text(strStageOneFields[i]);
            txtStageOne[i].setFont(Font.font(fontDefault.getFamily(), DBL_FONT_SIZE));
            txtStageOne[i].setWrappingWidth(DBL_PANE_INITIAL_WIDTH*0.14D);
            txtStageOne[i].setTextAlignment(TextAlignment.RIGHT);
            txtStageOne[i].getStyleClass().add("text-label");
//            hb.getChildren().add(txtStageOne[i]);
            final int finalI = i;
            if(i != 1) {
                final int index = (i == 0) ? 0 : i - 1;
                txtFieldStageOne[index] = new TextField();
                txtFieldStageOne[index].addEventHandler(KeyEvent.KEY_TYPED, e -> {
                    if(!e.isControlDown() && e.getCode() != KeyCode.X && e.getCode() != KeyCode.V) txtPrompt.setVisible(false);
                });
                txtFieldStageOne[index].textProperty().addListener((ol, ov, nv) -> {
                    if(nv.length() > 60) {
                        txtPrompt.setVisible(true);
                        txtPrompt.setText("The field '" + strStageOneFields[finalI].split(":")[0].trim() + "' has reached maximum number of characters.");
                        txtFieldStageOne[index].textProperty().setValue(nv.substring(0, 60));
                    }
                });
//                hb.getChildren().add(txtFieldStageOne[index]);
            } else {
                pf.setFont(Font.font(fontDefault.getFamily(), DBL_FONT_SIZE));
                pf.addEventHandler(KeyEvent.KEY_TYPED, e -> {
                    if(!e.isControlDown() && e.getCode() != KeyCode.X && e.getCode() != KeyCode.V) txtPrompt.setVisible(false);
                });
                pf.textProperty().addListener((ol, ov, nv) -> {
                    if(nv.length() > 60) {
                        txtPrompt.setVisible(true);
                        txtPrompt.setText("The field '" + strStageOneFields[finalI].split(":")[0].trim() + "' has reached maximum number of characters.");
                        pf.textProperty().setValue(nv.substring(0, 60));
                    }
                });
//                hb.getChildren().add(pf);
            }
//            hb.setAlignment(Pos.CENTER);
//            stageOneField.getChildren().add(hb);
        }
        pane.getChildren().add(stageOneField);
//        stageOneField.setStyle("-fx-background-color: blue;");
        stageOneField.setLayoutX(DBL_PANE_INITIAL_WIDTH*0.075D);
        stageOneField.setLayoutY(l.getStartY() + DBL_PANE_INITIAL_HEIGHT*0.01D);
        stageOneField.setSpacing(DBL_PANE_INITIAL_HEIGHT*0.05D);
        b1.layoutXProperty().addListener((ol, ov, nv) -> {
            txtPrompt.setLayoutX(nv.doubleValue() + b1.getBoundsInLocal().getWidth() + 20.0D);
            txtPrompt.setWrappingWidth(b2.getLayoutX() - nv.doubleValue() - b2.getWidth() - 40.0D);
        });
        b1.layoutYProperty().addListener((ol, ov, nv) -> {
            txtPrompt.setLayoutY(nv.doubleValue() + b1.getHeight()*0.4D);
        });
        b1.widthProperty().addListener((ol, ov, nv) -> {
            txtPrompt.setLayoutX(nv.doubleValue() + b1.getLayoutX() + 20.0D);
        });
        b2.layoutXProperty().addListener((ol, ov, nv) -> {
            txtPrompt.setWrappingWidth(nv.doubleValue() - b1.getLayoutX() - b2.getWidth() - 40.0D);
        });
        pane.widthProperty().addListener((ol, ov, nv) -> {
            stageOneField.setLayoutX(txtStageTitle.getLayoutX());
            final double DBL_STAGE_FIELD_WIDTH = nv.doubleValue() - 2*stageOneField.getLayoutX();
            stageOneField.setMaxWidth(DBL_STAGE_FIELD_WIDTH);
            stageOneField.setMaxWidth(DBL_STAGE_FIELD_WIDTH);
            stageOneField.setPrefWidth(DBL_STAGE_FIELD_WIDTH);
            for (int i = 0; i < 4; ++i) {
                txtStageOne[i].setFont(Font.font(fontDefault.getFamily(), DBL_FONT_SIZE*Math.min(nv.doubleValue()/DBL_PANE_INITIAL_WIDTH, pane.getPrefHeight()/DBL_PANE_INITIAL_HEIGHT)));
            }
            for(int i = 0 ; i < 4; ++i) {
                txtStageOne[i].setWrappingWidth(txtStageOne[0].getWrappingWidth());
                if(i != 1) {
                    txtFieldStageOne[Math.max(0, i - 1)].setFont(Font.font(fontDefault.getFamily(), DBL_FONT_SIZE*Math.min(nv.doubleValue()/DBL_PANE_INITIAL_WIDTH, pane.getPrefHeight()/DBL_PANE_INITIAL_HEIGHT)));
                    txtFieldStageOne[Math.max(0, i - 1)].setMinWidth(DBL_STAGE_FIELD_WIDTH - txtStageOne[0].getWrappingWidth());
                    txtFieldStageOne[Math.max(0, i - 1)].setMaxWidth(DBL_STAGE_FIELD_WIDTH - txtStageOne[0].getWrappingWidth());
                    txtFieldStageOne[Math.max(0, i - 1)].setPrefWidth(DBL_STAGE_FIELD_WIDTH - txtStageOne[0].getWrappingWidth());
                } else {
                    pf.setFont(Font.font(fontDefault.getFamily(), DBL_FONT_SIZE*Math.min(nv.doubleValue()/DBL_PANE_INITIAL_WIDTH, pane.getPrefHeight()/DBL_PANE_INITIAL_HEIGHT)));
                    pf.setMinWidth(DBL_STAGE_FIELD_WIDTH - txtStageOne[0].getWrappingWidth());
                    pf.setMaxWidth(DBL_STAGE_FIELD_WIDTH - txtStageOne[0].getWrappingWidth());
                    pf.setPrefWidth(DBL_STAGE_FIELD_WIDTH - txtStageOne[0].getWrappingWidth());
                }
            }
        });
        pf.heightProperty().addListener((ol, ov, nv) -> {
            for(int i = 0 ; i < 3; ++i) {
                txtFieldStageOne[i].setMinHeight(nv.doubleValue());
                txtFieldStageOne[i].setMaxHeight(nv.doubleValue());
                txtFieldStageOne[i].setPrefHeight(nv.doubleValue());
            }
            stageOneField.setSpacing((stageOneField.getPrefHeight()- 4*nv.doubleValue())/5.0D);
        });
        pane.heightProperty().addListener((ol, ov, nv) -> {
            stageOneField.setMaxHeight(nv.doubleValue()*0.66D);
            stageOneField.setMaxHeight(nv.doubleValue()*0.66D);
            stageOneField.setPrefHeight(nv.doubleValue()*0.66D);
            stageOneField.setLayoutY(l.getStartY() + nv.doubleValue()*0.01D);
            for (int i = 0; i < strStageOneFields.length; ++i) {
                txtStageOne[i].setFont(Font.font(fontDefault.getFamily(), DBL_FONT_SIZE*Math.min(nv.doubleValue()/DBL_PANE_INITIAL_HEIGHT, pane.getPrefWidth()/DBL_PANE_INITIAL_WIDTH)));
            }
            for(int i = 0 ; i < 4; ++i) {
                txtStageOne[i].setWrappingWidth(txtStageOne[0].getWrappingWidth());
                if(i != 1) {
                    txtFieldStageOne[Math.max(0, i - 1)].setMinWidth(stageOneField.getPrefWidth() - txtStageOne[0].getWrappingWidth());
                    txtFieldStageOne[Math.max(0, i - 1)].setMaxWidth(stageOneField.getPrefWidth() - txtStageOne[0].getWrappingWidth());
                    txtFieldStageOne[Math.max(0, i - 1)].setPrefWidth(stageOneField.getPrefWidth() - txtStageOne[0].getWrappingWidth());
                } else {
                    pf.setMinWidth(stageOneField.getPrefWidth() - txtStageOne[0].getWrappingWidth());
                    pf.setMaxWidth(stageOneField.getPrefWidth() - txtStageOne[0].getWrappingWidth());
                    pf.setPrefWidth(stageOneField.getPrefWidth() - txtStageOne[0].getWrappingWidth());
                }
            }
            stageOneField.setSpacing((nv.doubleValue()*0.66D - 4*pf.getBoundsInLocal().getHeight())/5.0D);
        });
        currentStageNumber.addListener((ol, ov, nv) -> {
            stageOneField.getChildren().clear();
            switch (nv.intValue()) {
                case 1 -> {
                    for (int i = 0; i < 4; i++) {
                        final HBox hb = new HBox(txtStageOne[i], (i != 1) ? txtFieldStageOne[Math.max(0, i - 1)] : pf);
                        hb.setAlignment(Pos.CENTER);
                        stageOneField.getChildren().add(hb);
                    }
                    txtFieldStageOne[0].requestFocus();
                }
                case 2 -> {
                    final Text txtInfo = new Text("Kindly re-enter your account's ID and Password to be added for verification of correctness of given information.");
                    txtInfo.getStyleClass().add("text-label");
                    txtInfo.setFont(Font.font(fontDefault.getFamily(), 20.0D*Math.min(pane.getPrefWidth()/DBL_PANE_INITIAL_WIDTH, pane.getPrefHeight()/DBL_PANE_INITIAL_HEIGHT)));
                    txtInfo.setWrappingWidth(stageOneField.getPrefWidth()*0.98D);
                    stageOneField.widthProperty().addListener((olp, ovp, nvp) -> {
                        txtInfo.setWrappingWidth(nvp.doubleValue()*0.98D);
                    });
                    pane.widthProperty().addListener((olp, ovp, nvp) -> {
                        txtInfo.setFont(Font.font(fontDefault.getFamily(), 20.0D*Math.min(nvp.doubleValue()/DBL_PANE_INITIAL_WIDTH, pane.getPrefHeight()/DBL_PANE_INITIAL_HEIGHT)));
                    });
                    pane.heightProperty().addListener((olp, ovp, nvp) -> {
                        txtInfo.setFont(Font.font(fontDefault.getFamily(), 20.0D*Math.min(nvp.doubleValue()/DBL_PANE_INITIAL_HEIGHT, pane.getPrefWidth()/DBL_PANE_INITIAL_WIDTH)));
                    });
                    final HBox hb1 = new HBox(txtStageOne[0], txtFieldStageOne[0]), hb2 = new HBox(txtStageOne[1], pf);
                    hb1.setAlignment(Pos.CENTER);
                    hb2.setAlignment(Pos.CENTER);
                    final TextField tfPwd = new TextField();
                    tfPwd.setFont(txtFieldStageOne[0].getFont());
                    tfPwd.setMinSize(txtFieldStageOne[0].getPrefWidth(), txtFieldStageOne[0].getPrefHeight());
                    tfPwd.setMaxSize(txtFieldStageOne[0].getPrefWidth(), txtFieldStageOne[0].getPrefHeight());
                    tfPwd.setPrefSize(txtFieldStageOne[0].getPrefWidth(), txtFieldStageOne[0].getPrefHeight());
                    txtFieldStageOne[0].fontProperty().addListener((olf, ovf, nvf) -> {
                        tfPwd.setFont(nvf);
                    });
                    txtFieldStageOne[0].widthProperty().addListener((olw, ovw, nvw) -> {
                        tfPwd.setMinWidth(nvw.doubleValue());
                        tfPwd.setMaxWidth(nvw.doubleValue());
                        tfPwd.setPrefWidth(nvw.doubleValue());
                    });
                    txtFieldStageOne[0].heightProperty().addListener((olw, ovw, nvw) -> {
                        tfPwd.setMinHeight(nvw.doubleValue());
                        tfPwd.setMaxHeight(nvw.doubleValue());
                        tfPwd.setPrefHeight(nvw.doubleValue());
                    });
                    final CheckBox cbox = new CheckBox("Show Password");
                    cbox.setFont(Font.font(fontDefault.getFamily(), 16.0D));
                    cbox.getStyleClass().add("chkbox");
                    pf.textProperty().bindBidirectional(tfPwd.textProperty());
                    cbox.selectedProperty().addListener((olc, ovc, nvc) -> {
                        if(nvc) {
                            hb2.getChildren().remove(pf);
                            hb2.getChildren().add(tfPwd);
                        } else {
                            hb2.getChildren().remove(tfPwd);
                            hb2.getChildren().add(pf);
                        }
                    });
                    stageOneField.getChildren().addAll(txtInfo, hb1, hb2, cbox);
                }
                case 3 -> {
                    final Text txtInfo = new Text("Check 'Domain Name' and 'Purpose'. You can modify it below if required." +
                        "This need not to be same as previously entered Domain Name and Purpose. This will be considered as final data.");
                    txtInfo.getStyleClass().add("text-label");
                    txtInfo.setFont(Font.font(fontDefault.getFamily(), 20.0D*Math.min(pane.getPrefWidth()/DBL_PANE_INITIAL_WIDTH, pane.getPrefHeight()/DBL_PANE_INITIAL_HEIGHT)));
                    txtInfo.setWrappingWidth(stageOneField.getPrefWidth()*0.98D);
                    stageOneField.widthProperty().addListener((olp, ovp, nvp) -> {
                        txtInfo.setWrappingWidth(nvp.doubleValue()*0.98D);
                    });
                    pane.widthProperty().addListener((olp, ovp, nvp) -> {
                        txtInfo.setFont(Font.font(fontDefault.getFamily(), 20.0D*Math.min(nvp.doubleValue()/DBL_PANE_INITIAL_WIDTH, pane.getPrefHeight()/DBL_PANE_INITIAL_HEIGHT)));
                    });
                    pane.heightProperty().addListener((olp, ovp, nvp) -> {
                        txtInfo.setFont(Font.font(fontDefault.getFamily(), 20.0D*Math.min(nvp.doubleValue()/DBL_PANE_INITIAL_HEIGHT, pane.getPrefWidth()/DBL_PANE_INITIAL_WIDTH)));
                    });
                    final HBox hb1 = new HBox(txtStageOne[2], txtFieldStageOne[1]), hb2 = new HBox(txtStageOne[3], txtFieldStageOne[2]);
                    hb1.setAlignment(Pos.CENTER);
                    hb2.setAlignment(Pos.CENTER);
                    stageOneField.getChildren().addAll(txtInfo, hb1, hb2);
                }
                case 4 -> {
                    final Text txtInfo = new Text("Account Information has been registered successfully.");
                    txtInfo.getStyleClass().add("text-label");
                    txtInfo.setFont(Font.font(fontDefault.getFamily(), 40.0D*Math.min(pane.getPrefWidth()/DBL_PANE_INITIAL_WIDTH, pane.getPrefHeight()/DBL_PANE_INITIAL_HEIGHT)));
                    txtInfo.setWrappingWidth(stageOneField.getPrefWidth()*0.98D);
                    txtInfo.setTextAlignment(TextAlignment.CENTER);
                    stageOneField.widthProperty().addListener((olp, ovp, nvp) -> {
                        txtInfo.setWrappingWidth(nvp.doubleValue()*0.98D);
                    });
                    pane.widthProperty().addListener((olp, ovp, nvp) -> {
                        txtInfo.setFont(Font.font(fontDefault.getFamily(), 40.0D*Math.min(nvp.doubleValue()/DBL_PANE_INITIAL_WIDTH, pane.getPrefHeight()/DBL_PANE_INITIAL_HEIGHT)));
                    });
                    pane.heightProperty().addListener((olp, ovp, nvp) -> {
                        txtInfo.setFont(Font.font(fontDefault.getFamily(), 40.0D*Math.min(nvp.doubleValue()/DBL_PANE_INITIAL_HEIGHT, pane.getPrefWidth()/DBL_PANE_INITIAL_WIDTH)));
                    });
                    stageOneField.getChildren().add(txtInfo);
                }
            }
        });
        b2.setOnAction(e -> {
            if(currentStageNumber.get() == 1) {
                String strMessage = "";
                int emptyFields = 0;
                for(int i = 3; i > -1; --i) {
                    if(i == 1) {
                        if(pf.getText() == null || pf.getText().isEmpty()) {
                            ++emptyFields;
                            strMessage = (strMessage.isEmpty() || Math.random() > 0.5D) ? strStageOneFields[i].split(":")[0].trim() : strMessage;
                        }
                    } else if(txtFieldStageOne[Math.max(i - 1, 0)].getText() == null || txtFieldStageOne[Math.max(i - 1, 0)].getText().isEmpty()) {
                        ++emptyFields;
                        strMessage = (strMessage.isEmpty() || Math.random() > 0.5D) ? strStageOneFields[i].split(":")[0].trim() : strMessage;
                    }
                }
                if(emptyFields == 0) {
                    strAccName = txtFieldStageOne[0].getText();
                    strPassword = pf.getText();
                    strDomain = txtFieldStageOne[1].getText();
                    strPurpose = txtFieldStageOne[2].getText();
                    for(int i = 0; i < 3; ++i) {
                        txtFieldStageOne[i].setText("");
                    }
                    pf.setText("");
                } else {
                    txtFieldStageOne[0].requestFocus();
                    strMessage += (emptyFields == 1) ? " field is empty" : (emptyFields == 2) ? " and 1 other field are empty" : " and " + (emptyFields - 1) + " other fields are empty";
                    txtPrompt.setText(strMessage);
                    txtPrompt.setVisible(true);
                    return;
                }
            }
            currentStageNumber.set(Math.min(4, currentStageNumber.get() + 1));
        });*/
    }

    static Pane getPane() {
        return pane;
    }

    public static void focus() {
        if(tfFocusOn != null) {
            tfFocusOn.requestFocus();
        }
    }

    static Pane getProgressPane() {
        return paneProgress;
    }

}
