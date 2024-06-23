package com.ajikhoji.pwdmgr;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class AppFrame {

    private static boolean blnFreezeMouseArrowChange = false, blnWindowMaximized = false;
    static double dblTitleBarHeight = 0.0D;
    final static double STROKE_WIDTH = 4.0D, MIN_FRAME_WIDTH = Screen.getPrimary().getBounds().getWidth()*0.55D, MIN_FRAME_HEIGHT = MIN_FRAME_WIDTH/1.5D;
    final static Pane paneBase = new Pane();

    static Pane getFrame(final Stage stage, final double width, final double height, final double titleBarHeight, final double titleBarFontSize, final String appTitle) {
        dblTitleBarHeight = titleBarHeight;

        final AtomicReference<Double> x = new AtomicReference<>();
        final AtomicReference<Double> y = new AtomicReference<>();
        final AtomicReference<Double> k = new AtomicReference<>();

        final Supplier<Pane> TitleButton = () -> {
            final Pane p = new Pane();
            p.setMinSize((titleBarHeight - STROKE_WIDTH)*1.15D, titleBarHeight - STROKE_WIDTH);
            p.setMaxSize((titleBarHeight - STROKE_WIDTH)*1.15D, titleBarHeight - STROKE_WIDTH);
            p.setPrefSize((titleBarHeight - STROKE_WIDTH)*1.15D, titleBarHeight - STROKE_WIDTH);
            p.getStyleClass().add("title-bar-button");
            return p;
        };

        paneBase.setPrefSize(width, height);
        paneBase.setMinSize(width, height);
        paneBase.setMaxSize(width, height);
        paneBase.setStyle("-fx-background-color: transparent;");

        final FlowPane paneTitle = new FlowPane();
        paneTitle.setAlignment(Pos.CENTER_LEFT);
        paneTitle.setPrefSize(width, titleBarHeight);
        paneTitle.setMinSize(width , titleBarHeight);
        paneTitle.setMaxSize(width, titleBarHeight);
        paneTitle.setLayoutX(0.0D);
        paneTitle.setLayoutY(0.0D);
        paneTitle.setId("title-pane");
        paneTitle.setStyle("-fx-padding: 16px;");

        final Text txtTitle = new Text(appTitle);
        txtTitle.setFont(Font.font(Resource.getInstance().fontAppTitle.getFamily(), titleBarFontSize));
        txtTitle.setId("app-title");

        final Pane paneClose = TitleButton.get();
        paneClose.setOnMouseClicked(e -> {
            System.exit(0);
        });
        paneClose.setId("pane-close");
        final double DIST = Math.min(paneClose.getPrefWidth(), paneClose.getPrefHeight());
        final Line LtoR = new Line(paneClose.getPrefWidth()*0.5D - DIST*0.12D,paneClose.getPrefHeight()*0.38D,paneClose.getPrefWidth()*0.5D + DIST*0.12D,paneClose.getPrefHeight()*0.62D);
        final Line RtoL = new Line(paneClose.getPrefWidth()*0.5D - DIST*0.12D,paneClose.getPrefHeight()*0.62D,paneClose.getPrefWidth()*0.5D + DIST*0.12D,paneClose.getPrefHeight()*0.38D);
        LtoR.getStyleClass().add("title-button-line");
        RtoL.getStyleClass().add("title-button-line");
        LtoR.setMouseTransparent(true);
        RtoL.setMouseTransparent(true);
        paneClose.getChildren().addAll(LtoR, RtoL);

        final Pane paneMaxRestore = TitleButton.get();

        final Rectangle rect = new Rectangle(paneMaxRestore.getPrefWidth()*0.5D - DIST*0.12D,paneMaxRestore.getPrefHeight()*0.5D - DIST*0.12D, DIST*0.24D, DIST*0.24D);
        rect.getStyleClass().add("title-button-line");
        rect.setFill(Color.TRANSPARENT);
        rect.setMouseTransparent(true);
        final Line lRectLeft = new Line(paneMaxRestore.getPrefWidth()*0.5D - DIST*0.06D, paneMaxRestore.getPrefHeight()*0.5D + DIST*0.12D, paneMaxRestore.getPrefWidth()*0.5D - DIST*0.06D, paneMaxRestore.getPrefHeight()*0.5D + DIST*0.18D);
        lRectLeft.getStyleClass().add("title-button-line");
        lRectLeft.setMouseTransparent(true);
        lRectLeft.setVisible(false);
        final Line lRectBottom = new Line(paneMaxRestore.getPrefWidth()*0.5D - DIST*0.06D, paneMaxRestore.getPrefHeight()*0.5D + DIST*0.18D, paneMaxRestore.getPrefWidth()*0.5D + DIST*0.18D, paneMaxRestore.getPrefHeight()*0.5D + DIST*0.18D);
        lRectBottom.getStyleClass().add("title-button-line");
        lRectBottom.setMouseTransparent(true);
        lRectBottom.setVisible(false);
        final Line lRectRight = new Line(paneMaxRestore.getPrefWidth()*0.5D + DIST*0.18D, paneMaxRestore.getPrefHeight()*0.5D + DIST*0.18D, paneMaxRestore.getPrefWidth()*0.5D + DIST*0.18D, paneMaxRestore.getPrefHeight()*0.5D - DIST*0.06D);
        lRectRight.getStyleClass().add("title-button-line");
        lRectRight.setMouseTransparent(true);
        lRectRight.setVisible(false);
        final Line lRectTop = new Line(paneMaxRestore.getPrefWidth()*0.5D + DIST*0.18D, paneMaxRestore.getPrefHeight()*0.5D - DIST*0.06D, paneMaxRestore.getPrefWidth()*0.5D + DIST*0.12D, paneMaxRestore.getPrefHeight()*0.5D - DIST*0.06D);
        lRectTop.getStyleClass().add("title-button-line");
        lRectTop.setMouseTransparent(true);
        lRectTop.setVisible(false);
        paneMaxRestore.getChildren().addAll(rect, lRectLeft, lRectBottom, lRectRight, lRectTop);
        final Pane paneMinimize = TitleButton.get();
        paneMinimize.setOnMouseClicked(e -> {
//            paneMinimize.setVisible(false);
            stage.setIconified(true);
        });
        stage.iconifiedProperty().addListener((ol, ov, nv) -> {
            paneMinimize.setVisible(!nv);
        });

        paneMaxRestore.setOnMouseClicked(e -> {
            stage.setMaximized(blnWindowMaximized = !blnWindowMaximized);
            lRectLeft.setVisible(blnWindowMaximized);
            lRectRight.setVisible(blnWindowMaximized);
            lRectTop.setVisible(blnWindowMaximized);
            lRectBottom.setVisible(blnWindowMaximized);
        });

        final GridPane drawer = new GridPane(0.0D, 0.0D);
        drawer.add(paneMinimize, 0 , 0);
        drawer.add(paneMaxRestore, 1 , 0);
        drawer.add(paneClose, 2 , 0);
        drawer.setMinSize(paneMinimize.getPrefWidth()*3.0D, paneMinimize.getPrefHeight());
        drawer.setMaxSize(paneMinimize.getPrefWidth()*3.0D, paneMinimize.getPrefHeight());
        drawer.setPrefSize(paneMinimize.getPrefWidth()*3.0D, paneMinimize.getPrefHeight());
        drawer.setLayoutX(width - 3*paneMinimize.getPrefWidth() - STROKE_WIDTH);
        drawer.setLayoutY(STROKE_WIDTH);

        final Line lMinimize = new Line(paneMinimize.getPrefWidth()*0.4D, paneMinimize.getPrefHeight()*0.5D, paneMinimize.getPrefWidth()*0.6D, paneMinimize.getPrefHeight()*0.5D);
        lMinimize.getStyleClass().add("title-button-line");
        lMinimize.setMouseTransparent(true);
        paneMinimize.getChildren().add(lMinimize);

        paneTitle.setOnMouseEntered(e -> {
            if (!blnWindowMaximized) {
                paneBase.getScene().setCursor(Cursor.MOVE);
            }
        });
        paneTitle.setOnMouseExited(e -> {
            paneBase.getScene().setCursor(Cursor.DEFAULT);
        });
        paneTitle.setOnMousePressed(e -> {
            if (!blnWindowMaximized) {
                x.set(e.getX());
                y.set(e.getY());
            }
        });
        paneTitle.setOnMouseDragged(e -> {
            if (!blnWindowMaximized) {
                stage.setX(e.getScreenX() - x.get());
                stage.setY(e.getScreenY() - y.get());
            }
        });

        final Line left = new Line(STROKE_WIDTH/2.0D, STROKE_WIDTH/2.0D, STROKE_WIDTH/2.0D, height-STROKE_WIDTH/2.0D);
        left.getStyleClass().add("window-frame-line-inactive");
        left.setOnMouseEntered(e -> {
            if (!(blnWindowMaximized || blnFreezeMouseArrowChange)) {
                paneBase.getScene().setCursor(Cursor.H_RESIZE);
            }
        });
        left.setOnMouseExited(e -> {
            if(!blnFreezeMouseArrowChange) {
                paneBase.getScene().setCursor(Cursor.DEFAULT);
            }
        });
        left.setOnMousePressed(e -> {
            if (!blnWindowMaximized) {
                x.set(e.getScreenX());
                y.set(paneBase.getWidth());
                k.set(e.getScreenX());
                blnFreezeMouseArrowChange = true;
            }
        });
        left.setOnMouseDragged(e -> {
            if(!blnWindowMaximized) {
                Platform.runLater(() -> {
                    final double SIZE = Math.max(y.get() + x.get() - e.getScreenX(), MIN_FRAME_WIDTH);
                    if(SIZE == MIN_FRAME_WIDTH) {
                        paneBase.getScene().setCursor(Cursor.DEFAULT);
                    } else {
                        paneBase.getScene().setCursor(Cursor.H_RESIZE);
                        paneBase.setMinWidth(SIZE);
                        paneBase.setMaxWidth(SIZE);
                        paneBase.setPrefWidth(SIZE);
                        stage.setWidth(SIZE);
                        stage.setX(k.get() + e.getScreenX() - x.get());
                    }
                });
            }
        });
        left.setOnMouseReleased(e -> {
            blnFreezeMouseArrowChange = false;
        });

        final Line right = new Line(width - STROKE_WIDTH/2.0D, STROKE_WIDTH/2.0D, width - STROKE_WIDTH/2.0D, height-STROKE_WIDTH/2.0D);
        right.getStyleClass().add("window-frame-line-inactive");
        right.setOnMouseEntered(e -> {
            if (!(blnWindowMaximized || blnFreezeMouseArrowChange)) {
                paneBase.getScene().setCursor(Cursor.H_RESIZE);
            }
        });
        right.setOnMouseExited(e -> {
            if(!blnFreezeMouseArrowChange) {
                paneBase.getScene().setCursor(Cursor.DEFAULT);
            }
        });
        right.setOnMousePressed(e -> {
            if (!blnWindowMaximized) {
                x.set(e.getScreenX());
                y.set(paneBase.getWidth());
                blnFreezeMouseArrowChange = true;
            }
        });
        right.setOnMouseDragged(e -> {
            if(!blnWindowMaximized) {
                Platform.runLater(() -> {
                    final double SIZE = Math.max(y.get() - x.get() + e.getScreenX(), MIN_FRAME_WIDTH);
                    paneBase.getScene().setCursor((SIZE == MIN_FRAME_WIDTH) ? Cursor.DEFAULT : Cursor.H_RESIZE);
                    paneBase.setMinWidth(SIZE);
                    paneBase.setMaxWidth(SIZE);
                    paneBase.setPrefWidth(SIZE);
                    stage.setWidth(SIZE);
                });
            }
        });
        right.setOnMouseReleased(e -> {
            blnFreezeMouseArrowChange = false;
        });

        final Line top = new Line(0.0D, STROKE_WIDTH/2.0D, width, STROKE_WIDTH/2.0D);
        top.getStyleClass().add("window-frame-line-inactive");
        top.setOnMouseEntered(e -> {
            if (!(blnWindowMaximized || blnFreezeMouseArrowChange)) {
                paneBase.getScene().setCursor(Cursor.V_RESIZE);
            }
        });
        top.setOnMouseExited(e -> {
            if(!blnFreezeMouseArrowChange) {
                paneBase.getScene().setCursor(Cursor.DEFAULT);
            }
        });
        top.setOnMousePressed(e -> {
            if (!blnWindowMaximized) {
                x.set(e.getScreenY());
                y.set(paneBase.getHeight());
                k.set(e.getScreenY());
                blnFreezeMouseArrowChange = true;
            }
        });
        top.setOnMouseDragged(e -> {
            if(!blnWindowMaximized) {
                Platform.runLater(() -> {
                    final double SIZE = Math.max(y.get() + x.get() - e.getScreenY(), MIN_FRAME_HEIGHT);
                    if(SIZE == MIN_FRAME_HEIGHT) {
                        paneBase.getScene().setCursor(Cursor.DEFAULT);
                    } else {
                        paneBase.getScene().setCursor(Cursor.V_RESIZE);
                        paneBase.setMinHeight(SIZE);
                        paneBase.setMaxHeight(SIZE);
                        paneBase.setPrefHeight(SIZE);
                        stage.setHeight(SIZE);
                        stage.setY(k.get() + e.getScreenY() - x.get());
                    }
                });
            }
        });
        top.setOnMouseReleased(e -> {
            blnFreezeMouseArrowChange = false;
        });

        final Line bottom = new Line(0.0D, height - STROKE_WIDTH/2.0D, width, height - STROKE_WIDTH/2.0D);
        bottom.getStyleClass().add("window-frame-line-inactive");
        bottom.setOnMouseEntered(e -> {
            if (!(blnWindowMaximized || blnFreezeMouseArrowChange)) {
                paneBase.getScene().setCursor(Cursor.V_RESIZE);
            }
        });
        bottom.setOnMouseExited(e -> {
            if(!blnFreezeMouseArrowChange) {
                paneBase.getScene().setCursor(Cursor.DEFAULT);
            }
        });
        bottom.setOnMousePressed(e -> {
            if (!blnWindowMaximized) {
                x.set(e.getScreenY());
                y.set(paneBase.getHeight());
                blnFreezeMouseArrowChange = true;
            }
        });
        bottom.setOnMouseDragged(e -> {
            if(!blnWindowMaximized) {
                Platform.runLater(() -> {
                    final double SIZE = Math.max(y.get() - x.get() + e.getScreenY(), MIN_FRAME_HEIGHT);
                        paneBase.getScene().setCursor(SIZE == MIN_FRAME_HEIGHT ? Cursor.DEFAULT : Cursor.V_RESIZE);
                        paneBase.setMinHeight(SIZE);
                        paneBase.setMaxHeight(SIZE);
                        paneBase.setPrefHeight(SIZE);
                        stage.setHeight(SIZE);
                });
            }
        });
        bottom.setOnMouseReleased(e -> {
            blnFreezeMouseArrowChange = false;
        });

        paneBase.widthProperty().addListener((ol, ov, nv) -> {
            Platform.runLater(() -> {
                paneTitle.setMinWidth(nv.doubleValue() - STROKE_WIDTH);
                paneTitle.setMaxWidth(nv.doubleValue() - STROKE_WIDTH);
                paneTitle.setPrefWidth(nv.doubleValue() - STROKE_WIDTH);
                drawer.setLayoutX(nv.doubleValue() - 3*paneMinimize.getPrefWidth() - STROKE_WIDTH*1.5D);
                top.setEndX(nv.doubleValue() - STROKE_WIDTH/2.0D);
                bottom.setEndX(nv.doubleValue() - STROKE_WIDTH/2.0D);
                right.setStartX(nv.doubleValue() - STROKE_WIDTH/2.0D);
                right.setEndX(nv.doubleValue() - STROKE_WIDTH/2.0D);
            });
        });

        paneBase.heightProperty().addListener((ol, ov, nv) -> {
            Platform.runLater(() -> {
                left.setEndY(nv.doubleValue() - STROKE_WIDTH/2.0D);
                right.setEndY(nv.doubleValue() - STROKE_WIDTH/2.0D);
                bottom.setStartY(nv.doubleValue() - STROKE_WIDTH/2.0D);
                bottom.setEndY(nv.doubleValue() - STROKE_WIDTH/2.0D);
            });
        });

        paneTitle.getChildren().add(txtTitle);
        paneBase.getChildren().addAll(AppMenu.paneMenu, ContentPane.paneContent, paneTitle, left, right, top, bottom, drawer);

//        final StackPane sp = new StackPane(new Pane(paneBase), p);
//        sp.widthProperty().addListener((ol, ov, nv) -> {
//            p.setMinWidth(nv.doubleValue());
//            p.setMaxWidth(nv.doubleValue());
//            p.setPrefWidth(nv.doubleValue());
//            paneBase.setMinWidth(nv.doubleValue());
//            paneBase.setMaxWidth(nv.doubleValue());
//            paneBase.setPrefWidth(nv.doubleValue());
//        });
//        sp.heightProperty().addListener((ol, ov, nv) -> {
//            p.setMinHeight(nv.doubleValue());
//            p.setMaxHeight(nv.doubleValue());
//            p.setPrefHeight(nv.doubleValue());
//            paneBase.setMinHeight(nv.doubleValue());
//            paneBase.setMaxHeight(nv.doubleValue());
//            paneBase.setPrefHeight(nv.doubleValue());
//        });
//        sp.setPrefSize(width, height);
//        sp.setMinSize(width, height);
//        sp.setMaxSize(width, height);

        return paneBase;
    }
}
