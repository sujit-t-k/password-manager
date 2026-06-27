package org.ajikhoji.passwordmanager.view_secondary;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.ajikhoji.passwordmanager.Launcher;
import org.ajikhoji.passwordmanager.config.AppConfig;

import java.util.Objects;

public class AppResetConfirmation {

    public static void show(final Runnable onResetConfirmation) {
        final Stage st = new Stage();

        final VBox vbxBase = new VBox(16.0D);
        vbxBase.setStyle("-fx-padding: 12px;");

        vbxBase.getChildren().addAll(
            getLabel("Your password cannot be recovered."),
            getLabel("This application stores data locally and does not use online recovery services."),
            getLabel("To use the application again, you must reset it.")
        );

        final Image imgWarning = AppConfig.getAppResources().imgWarning;
        final Label lblWarning = new Label("All stored credentials will be permanently deleted.");
        lblWarning.setStyle("-fx-text-fill: #FFB80F; -fx-font-size: 16px;");
        lblWarning.setAlignment(Pos.CENTER);
        lblWarning.setTextAlignment(TextAlignment.CENTER);
        lblWarning.prefWidthProperty().bind(vbxBase.widthProperty().subtract(20.0D));
        final ImageView ivWarning = new ImageView(imgWarning);
        ivWarning.setFitHeight(25.0D);
        ivWarning.setPreserveRatio(true);
        lblWarning.setGraphic(ivWarning);
        vbxBase.getChildren().add(lblWarning);

        final VBox vbxConfirmation = new VBox(6.0D);
        vbxConfirmation.getChildren().add(getLabel("Type RESET to continue:"));
        final TextField tfConfirmation = new TextField();
        tfConfirmation.setPrefColumnCount(20);
        tfConfirmation.setPrefWidth(Region.USE_PREF_SIZE);
        vbxConfirmation.getChildren().add(tfConfirmation);
        vbxBase.getChildren().add(vbxConfirmation);

        final Button btnCancel = new Button("Cancel");
        btnCancel.setStyle("-fx-font-size: 16px;");

        final Button btnReset = new Button("Proceed");
        btnReset.getStyleClass().add("btn-important-decision-warning");
        btnReset.setStyle("-fx-font-size: 16px;");
        btnReset.setDisable(true);

        final HBox hbxControls = new HBox(16.0D, btnCancel, btnReset);
        hbxControls.setAlignment(Pos.CENTER);
        vbxBase.getChildren().add(hbxControls);

        st.setResizable(false);
        st.setTitle("Reset application?");
        st.initOwner(AppConfig.getPrimaryStage());
        st.initModality(Modality.APPLICATION_MODAL);

        vbxBase.getStyleClass().add("pane-primary");
        vbxBase.getStylesheets().add(Objects.requireNonNull(Launcher.class.getResource("style/dark-theme.css")).toExternalForm());

        final Scene sc = new Scene(vbxBase);
        st.setScene(sc);
        st.show();

        btnCancel.setOnAction(e -> st.close());
        final ChangeListener<String> textListener = (ol, ov, nv) -> btnReset.setDisable(nv == null || !nv.equals("RESET"));
        tfConfirmation.textProperty().addListener(textListener);
        btnReset.setOnAction(e -> {
            final String confirmationText = tfConfirmation.getText();
            if(confirmationText == null || !confirmationText.equals("RESET")) {
                return;
            }
            tfConfirmation.textProperty().removeListener(textListener);
            vbxBase.setDisable(true);
            onResetConfirmation.run();
            st.close();
        });
    }

    private static Label getLabel(final String text) {
        final Label lbl = new Label(text);
        lbl.setWrapText(true);
        lbl.setStyle("-fx-font-size: 12px;");
        return lbl;
    }

}
