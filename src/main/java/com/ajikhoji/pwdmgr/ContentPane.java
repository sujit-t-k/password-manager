package com.ajikhoji.pwdmgr;

import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.Objects;

public class ContentPane {

    final static BorderPane paneContent = new BorderPane();

    static void init() {
        paneContent.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("theme/dark/content-pane.css")).toString());
        paneContent.setId("pane-content");
        AddNewData.init();
        ViewData.init();
        Settings.init();
        About.init();
    }

    static void setContent(final String str) {
        paneContent.getChildren().clear();
        final Pane paneCurrent = switch (str) {
            case "View" -> {
                paneContent.setBottom(ViewData.getStatusBar());
                ViewData.updateTable();
                yield ViewData.getPane();
            }
            case "Add" -> {
                paneContent.setBottom(AddNewData.getProgressPane());
                AddNewData.currentStageNumber.set(1);
                yield AddNewData.getPane();
            }
            case "Settings" -> Settings.getPane();
            case "About" -> About.getPane();
            case "Help" -> {
                final Text t = new Text("Will be available from stable version. You are currently using pre-release alpha version of this application.");
                t.setWrappingWidth(500.0D);
                t.setTextAlignment(TextAlignment.CENTER);
                t.setFont(Resource.getInstance().fontTitle);
                t.setFill(Color.web("#edccfe"));
                paneContent.widthProperty().addListener((ol, ov, nv) -> t.setWrappingWidth(nv.doubleValue()*0.8D));
                final BorderPane bp = new BorderPane();
                bp.setCenter(t);
                yield bp;
            }
            default -> new Pane();
        };
        final Rectangle rect = new Rectangle(paneCurrent.getPrefWidth(), paneCurrent.getPrefHeight());
        rect.setArcWidth(20.0D);
        rect.setArcHeight(20.0D);
        paneCurrent.setShape(rect);
        paneContent.setCenter(paneCurrent);
        if(str.equals("Add")) {
            AddNewData.focus();
        }
    }

}
