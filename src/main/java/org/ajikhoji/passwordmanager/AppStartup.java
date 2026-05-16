package org.ajikhoji.passwordmanager;

import javafx.application.Application;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.ajikhoji.passwordmanager.config.AppConfig;
import org.ajikhoji.passwordmanager.config.DbConfig;
import org.ajikhoji.passwordmanager.config.SideBarItem;
import org.ajikhoji.passwordmanager.ui_components.AppFrame;
import org.ajikhoji.passwordmanager.view.AddNewAccountScreen;

import java.util.function.Consumer;

public class AppStartup extends Application {

    @Override
    public void start(Stage stage) {
        DbConfig.initDb();
        AppConfig.setPrimaryStage(stage);
        AppConfig.setAppFrame(new AppFrame(stage, AppConfig.getScreenWidth() * 0.5D, AppConfig.getScreenHeight() * 0.7D));
        addAppTitleAndSideBar(
            AppConfig.getAppFrame(),
            AppConfig.getAppName(),
            selectedMenuItem -> {
                switch (selectedMenuItem) {
                    case ADD_NEW -> {
                        AppConfig.setCurrentDisplayPage(new AddNewAccountScreen(e -> {}));
                    }
                }
        });
        stage.setOnCloseRequest(e -> {
            DbConfig.closeDb();
        });
        stage.show();
    }

    private void addAppTitleAndSideBar(final AppFrame af, final String appTitle, final Consumer<SideBarItem> onMenuItemSelected) {
        final HBox hbxTitleBar = af.getTitleBar();
        hbxTitleBar.setAlignment(Pos.CENTER_LEFT);
        final Label lblAppTitle = new Label(appTitle);
        lblAppTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 0 0 0 14px;");
        hbxTitleBar.getChildren().add(lblAppTitle);

        class SideMenuItem extends HBox {
            public static SideMenuItem selectedItem = null;
            public final SideBarItem item;

            public SideMenuItem(SideBarItem item) {
                this.item = item;
                Label lbl = new Label(item.getSideBarItemName());
                getChildren().add(lbl);
                getStyleClass().add("side-menu-option");
            }
            public void activate() {
                pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), true);
            }
            public void deactivate() {
                pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), false);
            }
            public boolean setAsActive() {
                if(this == selectedItem) {
                    return true;
                }
                if(selectedItem != null) {
                    selectedItem.deactivate();
                }
                selectedItem = this;
                selectedItem.activate();
                return false;
            }
        }

        final Pane paneContent = af.getPane();

        final VBox vbxMenu = new VBox();
        vbxMenu.prefHeightProperty().bind(paneContent.heightProperty());
        vbxMenu.setStyle("-fx-border-width: 0 0.8px 0 0; -fx-border-color: #424242;");
        vbxMenu.setMinWidth(160);

        final Pane panePageView = new Pane();
        panePageView.prefHeightProperty().bind(paneContent.heightProperty());
        panePageView.prefWidthProperty().bind(paneContent.widthProperty().subtract(vbxMenu.widthProperty()));
        panePageView.layoutXProperty().bind(vbxMenu.widthProperty());

        final Consumer<SideMenuItem> onMenuItemSelection = selectedMenuItem -> {
            if(selectedMenuItem.setAsActive()) {
                return;
            }
            AppConfig.setCurrentDisplayPage(null);
            onMenuItemSelected.accept(selectedMenuItem.item);
        };

        for(final SideBarItem item : SideBarItem.getAllSideBarItems()) {
            final SideMenuItem menuItem = new SideMenuItem(item);
            menuItem.setOnMouseClicked(e -> {
                onMenuItemSelection.accept(menuItem);
            });
            vbxMenu.getChildren().add(menuItem);
        }

        AppConfig.getCurrentDisplayPageProperty().addListener((ol, panePrevious, pageContent) -> {
            if(panePrevious != null) {
                panePrevious.prefWidthProperty().unbind();
                panePrevious.prefHeightProperty().unbind();
            }
            panePageView.getChildren().clear();
            if(pageContent != null) {
                pageContent.prefWidthProperty().bind(panePageView.widthProperty());
                pageContent.prefHeightProperty().bind(panePageView.heightProperty());
                panePageView.getChildren().add(pageContent);
            }
        });

        //to select default menu item on app launch
        for(final Node n : vbxMenu.getChildren()) {
            final SideMenuItem smi = (SideMenuItem) n;
            if(smi.item.equals(AppConfig.getDefaultSideMenuItem())) {
                onMenuItemSelection.accept(smi);
                break;
            }
        }

        paneContent.getChildren().addAll(vbxMenu, panePageView);
    }

}
