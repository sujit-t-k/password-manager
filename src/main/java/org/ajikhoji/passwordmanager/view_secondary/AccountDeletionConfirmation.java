package org.ajikhoji.passwordmanager.view_secondary;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.ajikhoji.passwordmanager.Launcher;
import org.ajikhoji.passwordmanager.config.AppConfig;

import java.util.Objects;

public class AccountDeletionConfirmation {

    private AccountDeletionConfirmation() { }

    public static void show(final String accName, final String platform, final Runnable onDeletionConfirmation) {
        final Stage st = new Stage();

        final VBox vbxBase = new VBox(16.0D);
        vbxBase.setStyle("-fx-padding: 12px;");

        final GridPane gp = new GridPane(10.0D, 16.0D);
        final ColumnConstraints ccField = new ColumnConstraints();
        ccField.setHalignment(HPos.RIGHT);
        gp.getColumnConstraints().add(ccField);
        final ColumnConstraints ccValue = new ColumnConstraints();
        ccValue.setHalignment(HPos.LEFT);
        ccValue.setHgrow(Priority.SOMETIMES);
        gp.getColumnConstraints().add(ccValue);

        final Label lblPrompt = new Label("Are you sure want to delete the account credential with the following information?\nAll associated additional information will also be deleted.");
        lblPrompt.setWrapText(true);
        vbxBase.getChildren().add(lblPrompt);

        final Label lblFieldAccId = new Label("Account ID/Name:");
        final Label lblFieldAccIdValue = new Label(accName);
        gp.addRow(0, lblFieldAccId, lblFieldAccIdValue);

        final Label lblFieldPlatform = new Label("Platform:");
        final Label lblFieldPlatformValue = new Label(platform);
        gp.addRow(1, lblFieldPlatform, lblFieldPlatformValue);
        vbxBase.getChildren().add(gp);

        final Image imgWarning = AppConfig.getAppResources().imgWarning;
        final Label lblWarning = new Label("This action cannot be undone.");
        lblWarning.setStyle("-fx-text-fill: #FFB80F; -fx-font-size: 16px;");
        lblWarning.setAlignment(Pos.CENTER);
        lblWarning.setTextAlignment(TextAlignment.CENTER);
        lblWarning.prefWidthProperty().bind(gp.widthProperty());
        final ImageView ivWarning = new ImageView(imgWarning);
        ivWarning.setFitHeight(25.0D);
        ivWarning.setPreserveRatio(true);
        lblWarning.setGraphic(ivWarning);
        vbxBase.getChildren().add(lblWarning);

        final Button btnDelete = new Button("Delete");
        btnDelete.setOnAction(e -> {
            onDeletionConfirmation.run();
            st.close();
        });
        btnDelete.getStyleClass().add("btn-important-decision-warning");
        btnDelete.setStyle("-fx-font-size: 16px;");
        final HBox hbxControls = new HBox(btnDelete);
        hbxControls.setAlignment(Pos.CENTER);
        vbxBase.getChildren().add(hbxControls);

        st.setResizable(false);
        st.setTitle("Delete account credential?");
        st.initOwner(AppConfig.getPrimaryStage());
        st.initModality(Modality.APPLICATION_MODAL);

        vbxBase.getStyleClass().add("pane-primary");
        vbxBase.getStylesheets().add(Objects.requireNonNull(Launcher.class.getResource("style/dark-theme.css")).toExternalForm());

        final Scene sc = new Scene(vbxBase);
        st.setScene(sc);
        st.show();
    }

}
