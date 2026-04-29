package org.ajikhoji.passwordmanager.ui_components;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.ajikhoji.passwordmanager.Launcher;
import org.ajikhoji.passwordmanager.config.AppConfig;

import java.util.Objects;
import java.util.function.Consumer;

public class AppFrame {

    private final Stage stage;
    private final Scene sc;
    private final Pane paneBase;
    private final Pane paneUsable;
    private final HBox hbxTitleBarLeft;
    private final double TITLE_BAR_HEIGHT, BORDER_THICKNESS = 2.0D, MIN_FRAME_WIDTH, MIN_FRAME_HEIGHT;
    private ScreenState screenInfoBeforeMaximization;
    private boolean blnFreezeMouseArrowChange = false;
    private final BooleanProperty windowMaximizedProperty = new SimpleBooleanProperty(false);
    private double initialXLoc;
    private double initialWidth;
    private double initialYLoc;
    private double initialHeight;

    public final static double SCREEN_WIDTH_EXCLUDING_TASK_BAR = AppConfig.getVisualScreenWidth();
    public final static double SCREEN_HEIGHT_EXCLUDING_TASK_BAR = AppConfig.getVisualScreenHeight();

    public AppFrame(final Stage st, final double initialWidth, final double initialHeight) {
        this.initialWidth = initialWidth;
        this.initialHeight = initialHeight;

        this.stage = st;
        this.TITLE_BAR_HEIGHT = 44.0D;
        this.MIN_FRAME_WIDTH = 400;
        this.MIN_FRAME_HEIGHT = 320;

        //base
        this.paneBase = new Pane();
        this.paneBase.getStyleClass().add("pane-primary");
        this.paneBase.getStylesheets().add(Objects.requireNonNull(Launcher.class.getResource("style/dark-theme.css")).toExternalForm());
        this.paneBase.setPrefSize(initialWidth, initialHeight);

        //default content pane
        final Pane paneContent = new Pane();
        paneContent.prefWidthProperty().bind(this.paneBase.widthProperty().subtract(2 * BORDER_THICKNESS));
        paneContent.prefHeightProperty().bind(this.paneBase.heightProperty().subtract(2 * BORDER_THICKNESS));
        paneContent.setLayoutX(BORDER_THICKNESS);
        paneContent.setLayoutY(BORDER_THICKNESS);
        this.paneBase.getChildren().add(paneContent);

        this.paneUsable = new Pane();
        this.paneUsable.prefWidthProperty().bind(paneContent.widthProperty());
        this.paneUsable.prefHeightProperty().bind(paneContent.heightProperty().subtract(TITLE_BAR_HEIGHT));
        this.paneUsable.setLayoutY(TITLE_BAR_HEIGHT);
        paneContent.getChildren().add(this.paneUsable);

        //title bar
        this.hbxTitleBarLeft = new HBox();
        this.hbxTitleBarLeft.setMaxHeight(TITLE_BAR_HEIGHT);
        final FlowPane fpCenter = new FlowPane();
        final BorderPane bpTitleBar = new BorderPane();
        bpTitleBar.setPrefHeight(TITLE_BAR_HEIGHT);
        bpTitleBar.setMaxHeight(TITLE_BAR_HEIGHT);
        bpTitleBar.getStyleClass().add("title-pane");
        bpTitleBar.setLeft(this.hbxTitleBarLeft);
        bpTitleBar.setCenter(fpCenter);
        bpTitleBar.setRight(this.getTitleBarControls());
        bpTitleBar.prefWidthProperty().bind(paneContent.widthProperty());
        paneContent.getChildren().add(bpTitleBar);

        this.includeWindowBorders();

        this.sc = new Scene(this.paneBase, initialWidth, initialHeight);
        this.sc.setFill(Color.TRANSPARENT);
        this.sc.getStylesheets().add(Objects.requireNonNull(Launcher.class.getResource("style/app-frame.css")).toExternalForm());
        this.stage.setScene(this.sc);
        this.stage.initStyle(StageStyle.TRANSPARENT);

        this.hbxTitleBarLeft.setCursor(Cursor.MOVE);
        this.windowMaximizedProperty.addListener((ol, ov, nv) -> this.hbxTitleBarLeft.setCursor(nv ? Cursor.DEFAULT : Cursor.MOVE));
        this.hbxTitleBarLeft.setOnMouseEntered(e -> {
            if (!this.windowMaximizedProperty.get()) {
                paneBase.getScene().setCursor(Cursor.MOVE);
            }
        });
        this.hbxTitleBarLeft.setOnMouseExited(e -> {
            paneBase.getScene().setCursor(Cursor.DEFAULT);
        });
        this.hbxTitleBarLeft.setOnMousePressed(e -> {
            if (!this.windowMaximizedProperty.get()) {
                initialXLoc = e.getX();
                initialYLoc = e.getY();
            }
        });
        this.hbxTitleBarLeft.setOnMouseDragged(e -> {
            if (!this.windowMaximizedProperty.get()) {
                stage.setX(e.getScreenX() - initialXLoc);
                stage.setY(e.getScreenY() - initialYLoc);
            }
        });

        fpCenter.setCursor(Cursor.MOVE);
        this.windowMaximizedProperty.addListener((ol, ov, nv) -> fpCenter.setCursor(nv ? Cursor.DEFAULT : Cursor.MOVE));
        fpCenter.setOnMouseEntered(e -> {
            if (!this.windowMaximizedProperty.get()) {
                paneBase.getScene().setCursor(Cursor.MOVE);
            }
        });
        fpCenter.setOnMouseExited(e -> {
            paneBase.getScene().setCursor(Cursor.DEFAULT);
        });
        fpCenter.setOnMousePressed(e -> {
            if (!this.windowMaximizedProperty.get()) {
                initialXLoc = this.hbxTitleBarLeft.getWidth() + e.getX();
                initialYLoc = e.getY();
            }
        });
        fpCenter.setOnMouseDragged(e -> {
            if (!this.windowMaximizedProperty.get()) {
                stage.setX(e.getScreenX() - initialXLoc);
                stage.setY(e.getScreenY() - initialYLoc);
            }
        });
    }

    public void includeWindowBorders() {
        final EventHandler<MouseEvent> onMouseReleased = e -> this.blnFreezeMouseArrowChange = false;
        final EventHandler<MouseEvent> onMouseExited = e -> {
            if(!this.blnFreezeMouseArrowChange) {
                this.paneBase.getScene().setCursor(Cursor.DEFAULT);
            }
        };
        final EventHandler<MouseEvent> onMousePressed = e -> {
            if (!this.windowMaximizedProperty.get()) {
                initialYLoc = e.getScreenY();
                initialHeight = paneBase.getHeight();
                initialXLoc = e.getScreenX();
                initialWidth = paneBase.getWidth();
                this.blnFreezeMouseArrowChange = true;
            }
        };
        final Consumer<MouseEvent> north = e -> {
            if(!this.windowMaximizedProperty.get()) {
                Platform.runLater(() -> {
                    final double SIZE = Math.max(initialHeight + initialYLoc - e.getScreenY(), MIN_FRAME_HEIGHT);
                    this.paneBase.setPrefHeight(SIZE);
                    this.stage.setHeight(SIZE);
                    this.stage.setY(e.getScreenY());
                });
            }
        };
        final Consumer<MouseEvent> south = e -> {
            if(!this.windowMaximizedProperty.get()) {
                Platform.runLater(() -> {
                    final double SIZE = Math.max(initialHeight - initialYLoc + e.getScreenY(), MIN_FRAME_HEIGHT);
                    this.paneBase.setMinHeight(SIZE);
                    this.paneBase.setMaxHeight(SIZE);
                    this.paneBase.setPrefHeight(SIZE);
                    this.stage.setHeight(SIZE);
                });
            }
        };
        final Consumer<MouseEvent> east = e -> {
            if(!this.windowMaximizedProperty.get()) {
                Platform.runLater(() -> {
                    final double SIZE = Math.max(initialWidth - initialXLoc + e.getScreenX(), MIN_FRAME_WIDTH);
                    this.paneBase.setPrefWidth(SIZE);
                    this.stage.setWidth(SIZE);
                });
            }
        };
        final Consumer<MouseEvent> west = e -> {
            if(!this.windowMaximizedProperty.get()) {
                Platform.runLater(() -> {
                    final double SIZE = Math.max(initialWidth + initialXLoc - e.getScreenX(), MIN_FRAME_WIDTH);
                    this.paneBase.setPrefWidth(SIZE);
                    this.stage.setWidth(SIZE);
                    this.stage.setX(e.getScreenX());
                });
            }
        };
        final Consumer<Cursor> onMouseEntered = c -> {
            if (!(this.windowMaximizedProperty.get() || this.blnFreezeMouseArrowChange)) {
                this.paneBase.getScene().setCursor(c);
            }
        };
        final Consumer<Shape> BorderLineEventListener = shape -> {
            shape.setOnMouseExited(onMouseExited);
            shape.setOnMousePressed(onMousePressed);
            shape.setOnMouseReleased(onMouseReleased);
        };

        final Rectangle left = this.getBorderLine(0, BORDER_THICKNESS);
        left.setWidth(BORDER_THICKNESS);
        left.heightProperty().bind(this.paneBase.heightProperty().subtract(2 * BORDER_THICKNESS));
        left.setOnMouseEntered(e -> onMouseEntered.accept(Cursor.H_RESIZE));
        left.setOnMouseDragged(west::accept);
        BorderLineEventListener.accept(left);

        final Rectangle right = this.getBorderLine(this.paneBase.getPrefWidth() - BORDER_THICKNESS, BORDER_THICKNESS);
        right.setWidth(BORDER_THICKNESS);
        right.layoutXProperty().bind(this.paneBase.widthProperty().subtract(BORDER_THICKNESS));
        right.heightProperty().bind(this.paneBase.heightProperty().subtract(BORDER_THICKNESS * 2.0D));
        right.setOnMouseEntered(e -> onMouseEntered.accept(Cursor.H_RESIZE));
        right.setOnMouseDragged(east::accept);
        BorderLineEventListener.accept(right);

        final Rectangle top = this.getBorderLine(BORDER_THICKNESS, 0);
        top.setHeight(BORDER_THICKNESS);
        top.widthProperty().bind(this.paneBase.widthProperty().subtract(2 * BORDER_THICKNESS));
        top.setOnMouseEntered(e -> onMouseEntered.accept(Cursor.V_RESIZE));
        top.setOnMouseDragged(north::accept);
        BorderLineEventListener.accept(top);

        final Rectangle bottom = this.getBorderLine(BORDER_THICKNESS, this.paneBase.getPrefHeight() - BORDER_THICKNESS);
        bottom.setHeight(BORDER_THICKNESS);
        bottom.widthProperty().bind(this.paneBase.widthProperty().subtract(2 * BORDER_THICKNESS));
        bottom.layoutYProperty().bind(this.paneBase.heightProperty().subtract(BORDER_THICKNESS));
        bottom.setOnMouseEntered(e -> onMouseEntered.accept(Cursor.V_RESIZE));
        bottom.setOnMouseDragged(south::accept);
        BorderLineEventListener.accept(bottom);

        final Arc topLeft = this.getBorderEdgeCurved(BORDER_THICKNESS, BORDER_THICKNESS, 90);
        topLeft.setOnMouseEntered(e -> onMouseEntered.accept(Cursor.NW_RESIZE));
        topLeft.setOnMouseDragged(e -> {
            north.accept(e);
            west.accept(e);
        });
        BorderLineEventListener.accept(topLeft);

        final Arc topRight = this.getBorderEdgeCurved(this.paneBase.getPrefWidth() - BORDER_THICKNESS, BORDER_THICKNESS, 0);
        topRight.layoutXProperty().bind(this.paneBase.widthProperty().subtract(BORDER_THICKNESS));
        topRight.setOnMouseEntered(e -> onMouseEntered.accept(Cursor.NE_RESIZE));
        topRight.setOnMouseDragged(e -> {
            north.accept(e);
            east.accept(e);
        });
        BorderLineEventListener.accept(topRight);

        final Arc bottomLeft = this.getBorderEdgeCurved(BORDER_THICKNESS, this.paneBase.getPrefHeight() - BORDER_THICKNESS,180);
        bottomLeft.layoutYProperty().bind(this.paneBase.heightProperty().subtract(BORDER_THICKNESS));
        bottomLeft.setOnMouseEntered(e -> onMouseEntered.accept(Cursor.SW_RESIZE));
        bottomLeft.setOnMouseDragged(e -> {
            south.accept(e);
            west.accept(e);
        });
        BorderLineEventListener.accept(bottomLeft);

        final Arc bottomRight = this.getBorderEdgeCurved(this.paneBase.getPrefWidth() - BORDER_THICKNESS, this.paneBase.getPrefHeight() - BORDER_THICKNESS,270);
        bottomRight.layoutXProperty().bind(this.paneBase.widthProperty().subtract(BORDER_THICKNESS));
        bottomRight.layoutYProperty().bind(this.paneBase.heightProperty().subtract(BORDER_THICKNESS));
        bottomRight.setOnMouseEntered(e -> onMouseEntered.accept(Cursor.SE_RESIZE));
        bottomRight.setOnMouseDragged(e -> {
            south.accept(e);
            east.accept(e);
        });
        BorderLineEventListener.accept(bottomRight);
        this.paneBase.getChildren().addAll(topLeft, topRight, bottomLeft, bottomRight, left, right, top, bottom);

        //updates cursor
        final Shape[] borders = {left, right, top, bottom, topLeft, topRight, bottomLeft, bottomRight};
        final Cursor[] hoverCursor = {Cursor.H_RESIZE, Cursor.H_RESIZE, Cursor.V_RESIZE, Cursor.V_RESIZE, Cursor.NW_RESIZE, Cursor.NE_RESIZE, Cursor.SW_RESIZE, Cursor.SE_RESIZE};
        final Consumer<Boolean> CursorView = maximized -> {
            if(maximized) {
                for(final Shape border : borders) {
                    border.setCursor(Cursor.DEFAULT);
                }
            } else {
                for (int i = 0; i < borders.length; ++i) {
                    borders[i].setCursor(hoverCursor[i]);
                }
            }
        };
        CursorView.accept(this.windowMaximizedProperty.get());
        this.windowMaximizedProperty.addListener((ol, ov, nv) -> CursorView.accept(nv));
    }

    private HBox getTitleBarControls() {
        class TitleButton {
            Pane get() {
                final Pane p = new Pane();
                p.setMinSize(TITLE_BAR_HEIGHT * 1.15D, TITLE_BAR_HEIGHT - 1.5D);
                p.setMaxSize(TITLE_BAR_HEIGHT * 1.15D, TITLE_BAR_HEIGHT - 1.5D);
                p.setPrefSize(TITLE_BAR_HEIGHT * 1.15D, TITLE_BAR_HEIGHT - 1.5D);
                p.getStyleClass().add("title-bar-button");
                return p;
            }
            Pane getCloseButton() {
                final Pane paneClose = this.get();
                paneClose.setOnMouseClicked(e -> System.exit(0));
                paneClose.setId("pane-close");
                final double DIST = Math.min(paneClose.getPrefWidth(), paneClose.getPrefHeight());
                final Line LtoR = new Line(paneClose.getPrefWidth()*0.5D - DIST*0.12D,paneClose.getPrefHeight()*0.38D,paneClose.getPrefWidth()*0.5D + DIST*0.12D,paneClose.getPrefHeight()*0.62D);
                final Line RtoL = new Line(paneClose.getPrefWidth()*0.5D - DIST*0.12D,paneClose.getPrefHeight()*0.62D,paneClose.getPrefWidth()*0.5D + DIST*0.12D,paneClose.getPrefHeight()*0.38D);
                LtoR.getStyleClass().add("title-button-line");
                RtoL.getStyleClass().add("title-button-line");
                LtoR.setMouseTransparent(true);
                RtoL.setMouseTransparent(true);
                paneClose.getChildren().addAll(LtoR, RtoL);
                return paneClose;
            }
            Pane getWindowResizeButton() {
                final Pane paneMaxRestore = this.get();
                final double DIST = Math.min(paneMaxRestore.getPrefWidth(), paneMaxRestore.getPrefHeight());

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
                paneMaxRestore.setOnMouseClicked(e -> {
                    final boolean newValue = !windowMaximizedProperty.get();
                    if(!newValue) {
                        screenInfoBeforeMaximization.restore(stage);
                    } else {
                        screenInfoBeforeMaximization = new ScreenState(stage);
                        stage.setX(0.0D);
                        stage.setWidth(SCREEN_WIDTH_EXCLUDING_TASK_BAR);
                        stage.setY(0.0D);
                        stage.setHeight(SCREEN_HEIGHT_EXCLUDING_TASK_BAR);
                    }
                    windowMaximizedProperty.set(newValue);
                    lRectLeft.setVisible(newValue);
                    lRectRight.setVisible(newValue);
                    lRectTop.setVisible(newValue);
                    lRectBottom.setVisible(newValue);
                });
                return paneMaxRestore;
            }
            Pane getAppDockButton() {
                final Pane paneMinimize = this.get();
                paneMinimize.setOnMouseClicked(e -> stage.setIconified(true));
                stage.iconifiedProperty().addListener((ol, ov, nv) -> paneMinimize.setVisible(!nv));
                final Line lMinimize = new Line(paneMinimize.getPrefWidth()*0.4D, paneMinimize.getPrefHeight()*0.5D, paneMinimize.getPrefWidth()*0.6D, paneMinimize.getPrefHeight()*0.5D);
                lMinimize.getStyleClass().add("title-button-line");
                lMinimize.setMouseTransparent(true);
                paneMinimize.getChildren().add(lMinimize);
                return paneMinimize;
            }
        }
        final TitleButton tb = new TitleButton();
        final HBox hbx = new HBox(tb.getAppDockButton(), tb.getWindowResizeButton(), tb.getCloseButton());
        hbx.setPrefHeight(TITLE_BAR_HEIGHT - 0.5D);
        hbx.setMaxHeight(TITLE_BAR_HEIGHT - 0.5D);
        hbx.getStyleClass().add("title-pane");
        return hbx;
    }

    private Rectangle getBorderLine(final double startX, final double startY) {
        final Rectangle r = new Rectangle();
        r.setLayoutX(startX);
        r.setLayoutY(startY);
        r.getStyleClass().add("window-frame-border-line");
        return r;
    }

    private Arc getBorderEdgeCurved(final double centerX, final double centerY, final double angleBegin) {
        final Arc a = new Arc();
        a.setRadiusX(BORDER_THICKNESS);
        a.setRadiusY(BORDER_THICKNESS);
        a.setStartAngle(angleBegin);
        a.setLength(90);
        a.setType(ArcType.ROUND);
        a.getStyleClass().add("window-frame-border-line");
        a.setLayoutX(centerX);
        a.setLayoutY(centerY);
        return a;
    }

    public Pane getPane() {
        return this.paneUsable;
    }

    public HBox getTitleBar() {
        return this.hbxTitleBarLeft;
    }

    public Scene getScene() {
        return this.sc;
    }

    record ScreenState(double x, double y, double width, double height) {
        ScreenState(Stage s) {
            this(s.getX(), s.getY(), s.getWidth(), s.getHeight());
        }
        void restore(Stage s) {
            s.setX(x);
            s.setY(y);
            s.setWidth(width);
            s.setHeight(height);
        }
    }

}
