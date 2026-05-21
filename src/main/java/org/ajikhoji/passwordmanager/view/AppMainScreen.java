package org.ajikhoji.passwordmanager.view;

import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.ajikhoji.passwordmanager.config.AppConfig;
import org.ajikhoji.passwordmanager.config.SideBarItem;
import org.ajikhoji.passwordmanager.ui_components.AppFrame;
import org.ajikhoji.passwordmanager.viewmodel.AddNewAccountViewModel;

import java.util.function.Consumer;

//view that incorporates left navigation bar and content on its right
public class AppMainScreen {

    public static void init() {
        //setting up app title bar
        final HBox hbxTitleBar = AppConfig.getAppFrame().getTitleBar();
        hbxTitleBar.getChildren().clear();
        hbxTitleBar.setAlignment(Pos.CENTER_LEFT);
        final Label lblAppTitle = new Label(AppConfig.getAppName());
        lblAppTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 0 0 0 14px;");
        hbxTitleBar.getChildren().add(lblAppTitle);
        AppConfig.getPrimaryStage().setTitle(lblAppTitle.getText());

        addAppSideBar(
            AppConfig.getAppFrame(),
        selectedMenuItem -> {
            switch (selectedMenuItem) {
                case ADD_NEW -> {
                    AppConfig.setCurrentDisplayPage(new AddNewAccountScreen(AddNewAccountViewModel.getInstance(), e -> {}));
                }
                case VIEW_ALL -> {
                    AppConfig.setCurrentDisplayPage(new ViewAccountCredentialScreen());
                }
            }
        });
    }

    private static void addAppSideBar(final AppFrame af, final Consumer<SideBarItem> onMenuItemSelected) {
        af.getPane().getChildren().clear();
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
                if(panePrevious instanceof AddNewAccountScreen screen) {
                    screen.saveInfo();
                }
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
