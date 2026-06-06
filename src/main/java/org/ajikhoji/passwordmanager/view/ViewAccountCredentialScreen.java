package org.ajikhoji.passwordmanager.view;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import org.ajikhoji.passwordmanager.ui_components.AccountCredentialViewer;
import org.ajikhoji.passwordmanager.ui_components.AccountRecordFilter;

public class ViewAccountCredentialScreen extends BorderPane {

    public ViewAccountCredentialScreen() {
        setStyle("-fx-padding: 40px;");
        final AccountCredentialViewer viewer = new AccountCredentialViewer();
        setCenter(viewer);

        final AccountRecordFilter filter = new AccountRecordFilter(viewer);
        filter.setStyle("-fx-padding: 0 0 12px 0;");
        setTop(filter);
    }

}
