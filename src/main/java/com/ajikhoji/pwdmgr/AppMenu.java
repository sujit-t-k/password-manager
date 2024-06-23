package com.ajikhoji.pwdmgr;

import javafx.geometry.Pos;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.io.BufferedInputStream;
import java.util.Objects;
import java.util.function.BiFunction;

public class AppMenu {

    final static VBox paneMenu = new VBox();
    static Pane paneMenuSelected = new Pane();

    private enum MenuItemCondition {
        UNSELECTED,
        UNSELECTED_HOVERED,
        SELECTED,
        SELECTED_HOVERED;
    }

    private static void setColorAdjust(final ColorAdjust ca, final MenuItemCondition MIC) {
        switch (HelloApplication.theme.get()) {
            case DARK -> {
                switch (MIC) {
                    case UNSELECTED -> {
                        ca.setSaturation(0.8D);
                        ca.setHue(0.44D);
                        ca.setBrightness(0.75D);
                        ca.setContrast(-0.8D);
                    }
                    case UNSELECTED_HOVERED -> {
                        ca.setSaturation(0.8D);
                        ca.setHue(0.44D);
                        ca.setBrightness(0.9D);
                        ca.setContrast(-0.7D);
                    }
                    case SELECTED,SELECTED_HOVERED -> {
                        ca.setSaturation(0.0D);
                        ca.setHue(0.0D);
                        ca.setBrightness(0.0D);
                        ca.setContrast(0.0D);
                    }
                }
            }
        }
    }

    public static void buildAppMenu(final Pane paneFrame) {
        final double DBL_MENU_PORTION = 0.17D, DBL_SPACING = 5.0D, DBL_BORDER_WIDTH = 3.0D;

        paneMenuSelected.setId("nothing");

        paneMenu.setSpacing(DBL_SPACING);
        paneMenu.setMinSize((paneFrame.getWidth() - AppFrame.STROKE_WIDTH)*0.66D*DBL_MENU_PORTION, paneFrame.getHeight() - AppFrame.STROKE_WIDTH - AppFrame.dblTitleBarHeight);
        paneMenu.setMaxSize((paneFrame.getWidth() - AppFrame.STROKE_WIDTH)*0.66D*DBL_MENU_PORTION, paneFrame.getHeight() - AppFrame.STROKE_WIDTH - AppFrame.dblTitleBarHeight);
        paneMenu.setPrefSize((paneFrame.getWidth() - AppFrame.STROKE_WIDTH)*0.66D*DBL_MENU_PORTION, paneFrame.getHeight() - AppFrame.STROKE_WIDTH - AppFrame.dblTitleBarHeight);
        paneMenu.setLayoutX(AppFrame.STROKE_WIDTH);
        paneMenu.setLayoutY(AppFrame.dblTitleBarHeight);
        paneMenu.setId("pane-menu");
        paneMenu.getStylesheets().add(Objects.requireNonNull(AppMenu.class.getResource("theme/dark/menu-item.css")).toExternalForm());

        ContentPane.paneContent.setLayoutX(paneMenu.getLayoutX() + paneMenu.getPrefWidth());
        ContentPane.paneContent.setLayoutY(AppFrame.dblTitleBarHeight);
        ContentPane.paneContent.setPrefSize(paneFrame.getWidth() - 2*AppFrame.STROKE_WIDTH - paneMenu.getPrefWidth(), paneMenu.getPrefHeight());
        ContentPane.paneContent.setMinSize(paneFrame.getWidth() - 2*AppFrame.STROKE_WIDTH - paneMenu.getPrefWidth(), paneMenu.getPrefHeight());
        ContentPane.paneContent.setMaxSize(paneFrame.getWidth() - 2*AppFrame.STROKE_WIDTH - paneMenu.getPrefWidth(), paneMenu.getPrefHeight());

        paneFrame.widthProperty().addListener((ol, ov, nv) -> {
            paneMenu.setMinWidth(Math.min((nv.doubleValue() - 2*AppFrame.STROKE_WIDTH)*0.66D*DBL_MENU_PORTION,paneMenu.getHeight()*0.2D - 4.0D/5.0D));
            paneMenu.setMaxWidth(paneMenu.getMinWidth());
            paneMenu.setPrefWidth(paneMenu.getMinWidth());
            ContentPane.paneContent.setMinWidth(nv.doubleValue() - 2*AppFrame.STROKE_WIDTH - paneMenu.getPrefWidth());
            ContentPane.paneContent.setMaxWidth(ContentPane.paneContent.getMinWidth());
            ContentPane.paneContent.setPrefWidth(ContentPane.paneContent.getMinWidth());
            ContentPane.paneContent.setLayoutX(paneMenu.getLayoutX() + paneMenu.getPrefWidth());
        });

        paneFrame.heightProperty().addListener((ol, ov, nv) -> {
            paneMenu.setMinHeight(nv.doubleValue() - AppFrame.STROKE_WIDTH - AppFrame.dblTitleBarHeight);
            paneMenu.setMaxHeight(paneMenu.getMinHeight());
            paneMenu.setPrefHeight(paneMenu.getMinHeight());
            paneMenu.setMinWidth(Math.min(paneMenu.getPrefHeight()*0.2D - 4.0D/5.0D, (paneFrame.getWidth() - 2*AppFrame.STROKE_WIDTH)*0.66D*DBL_MENU_PORTION));
            paneMenu.setMaxWidth(paneMenu.getMinWidth());
            paneMenu.setPrefWidth(paneMenu.getMinWidth());
            ContentPane.paneContent.setMinWidth(paneFrame.getWidth() - 2*AppFrame.STROKE_WIDTH - paneMenu.getPrefWidth());
            ContentPane.paneContent.setMaxWidth(ContentPane.paneContent.getMinWidth());
            ContentPane.paneContent.setPrefWidth(ContentPane.paneContent.getMinWidth());
            ContentPane.paneContent.setLayoutX(paneMenu.getLayoutX() + paneMenu.getPrefWidth());
            ContentPane.paneContent.setMinHeight(paneMenu.getPrefHeight());
            ContentPane.paneContent.setMaxHeight(paneMenu.getPrefHeight());
            ContentPane.paneContent.setPrefHeight(paneMenu.getPrefHeight());
        });

        final Font fontMenuItem = Resource.getInstance().fontMenuItem;

        final BiFunction<String, Image, Pane> MenuItemBuilder = (txt, img) ->  {
            final ImageView imgView = new ImageView(img);
            final double DBL_COMPONENT_SIZE = Math.min(paneMenu.getPrefWidth()*0.66D - DBL_BORDER_WIDTH, paneMenu.getPrefHeight()*0.2D);
            imgView.setFitWidth(DBL_COMPONENT_SIZE*0.66D);
            imgView.setFitHeight(DBL_COMPONENT_SIZE*0.66D);
            imgView.setMouseTransparent(true);
            imgView.getStyleClass().add("img-unselected");

            final ColorAdjust ca = new ColorAdjust();
            setColorAdjust(ca, MenuItemCondition.UNSELECTED);
            imgView.setEffect(ca);

            final Text txtMenuItem = new Text(txt);
            txtMenuItem.setFont(Font.font(fontMenuItem.getFamily(), 18.0D));
            txtMenuItem.setTextAlignment(TextAlignment.CENTER);
            txtMenuItem.setMouseTransparent(true);
            txtMenuItem.getStyleClass().add("text-unselected");

            final VBox pMenu = new VBox();
            pMenu.setAlignment(Pos.CENTER);
            pMenu.setMinSize(DBL_COMPONENT_SIZE, DBL_COMPONENT_SIZE);
            pMenu.setMaxSize(DBL_COMPONENT_SIZE, DBL_COMPONENT_SIZE);
            pMenu.setPrefSize(DBL_COMPONENT_SIZE, DBL_COMPONENT_SIZE);
            pMenu.setId(txt);
            pMenu.getStyleClass().add("mnu-item-panel-unselected");
            pMenu.getChildren().addAll(imgView, txtMenuItem);
            pMenu.setOnMouseEntered(e -> {
                pMenu.getStyleClass().clear();
                if(paneMenuSelected.getId().equals(txt)) {
                    pMenu.getStyleClass().add("mnu-item-panel-selected-hover");
                    setColorAdjust(ca, MenuItemCondition.SELECTED_HOVERED);
                } else {
                    pMenu.getStyleClass().add("mnu-item-panel-unselected-hover");
                    setColorAdjust(ca, MenuItemCondition.UNSELECTED_HOVERED);
                }
            });
            pMenu.setOnMouseExited(e -> {
                setColorAdjust(ca, paneMenuSelected.getId().equals(txt) ? MenuItemCondition.SELECTED : MenuItemCondition.UNSELECTED);
                pMenu.getStyleClass().clear();
                pMenu.getStyleClass().add(paneMenuSelected.getId().equals(txt) ? "mnu-item-panel-selected" : "mnu-item-panel-unselected");
            });
            pMenu.setOnMouseClicked(e -> {
                if(!paneMenuSelected.getChildren().isEmpty()) {
                    paneMenuSelected.getStyleClass().clear();
                    paneMenuSelected.getStyleClass().add("mnu-item-panel-unselected");
                    paneMenuSelected.getChildren().get(1).getStyleClass().clear();
                    paneMenuSelected.getChildren().get(1).getStyleClass().add("text-unselected");
                    setColorAdjust((ColorAdjust) paneMenuSelected.getChildren().getFirst().getEffect(), MenuItemCondition.UNSELECTED);
                }
                paneMenuSelected = pMenu;
                ContentPane.setContent(paneMenuSelected.getId());
                paneMenuSelected.getStyleClass().clear();
                paneMenuSelected.getStyleClass().add("mnu-item-panel-selected");
                txtMenuItem.getStyleClass().clear();
                txtMenuItem.getStyleClass().add("text-selected");
                setColorAdjust(ca, MenuItemCondition.SELECTED);
            });
            
            paneMenu.widthProperty().addListener((ol, ov, nv) -> {
                    final double DBL_NEW_SIZE = Math.min(nv.doubleValue() - DBL_BORDER_WIDTH, paneMenu.getPrefHeight()*0.2D);
                    pMenu.setMinSize(DBL_NEW_SIZE, DBL_NEW_SIZE);
                    pMenu.setMaxSize(DBL_NEW_SIZE, DBL_NEW_SIZE);
                    pMenu.setPrefSize(DBL_NEW_SIZE, DBL_NEW_SIZE);
                    imgView.setFitWidth(DBL_NEW_SIZE*0.44D);
                    imgView.setFitHeight(DBL_NEW_SIZE*0.44D);
                    txtMenuItem.setFont(Font.font(fontMenuItem.getFamily(), (pMenu.getPrefHeight() - (DBL_NEW_SIZE*0.66D) - 14.0D)));
            });
            paneMenu.heightProperty().addListener((ol, ov, nv) -> {
                final double DBL_NEW_SIZE = Math.min(nv.doubleValue()*0.2D, paneMenu.getPrefWidth() - DBL_BORDER_WIDTH);
                pMenu.setMinSize(DBL_NEW_SIZE, DBL_NEW_SIZE);
                pMenu.setMaxSize(DBL_NEW_SIZE, DBL_NEW_SIZE);
                pMenu.setPrefSize(DBL_NEW_SIZE, DBL_NEW_SIZE);
                imgView.setFitWidth(DBL_NEW_SIZE*0.44D);//0.66*0.66
                imgView.setFitHeight(DBL_NEW_SIZE*0.44D);
                //txtMenuItem.setFont(Font.font(fontMenuItem.getFamily(), (pMenu.getPrefHeight() - imgView.getFitHeight() - 14.0D)));
                txtMenuItem.setFont(Font.font(fontMenuItem.getFamily(), (pMenu.getPrefHeight() - (DBL_NEW_SIZE*0.66D) - 14.0D)));
            });
            return pMenu;
        };
        try {
            final String[] strMenuItems = {"View", "Add", "Settings", "Help", "About"};
            final Image[] imgMenuItems = Resource.getInstance().imgMenuItems;
            for(int i = 0; i < 5; ++i) paneMenu.getChildren().add(MenuItemBuilder.apply(strMenuItems[i], imgMenuItems[i]));
        } catch (final Exception e) {
            System.err.println("Image Loading Failed!");
        }
    }
}
