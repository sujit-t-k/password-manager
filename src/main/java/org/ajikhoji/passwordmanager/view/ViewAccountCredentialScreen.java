package org.ajikhoji.passwordmanager.view;

import javafx.scene.layout.BorderPane;
import org.ajikhoji.passwordmanager.ui_components.AccountCredentialViewer;

public class ViewAccountCredentialScreen extends BorderPane {

    public ViewAccountCredentialScreen() {
        final AccountCredentialViewer viewer = new AccountCredentialViewer();
        setCenter(viewer);
        setStyle("-fx-padding: 40px;");
    }

}
