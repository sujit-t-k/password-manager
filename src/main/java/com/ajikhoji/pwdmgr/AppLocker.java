package com.ajikhoji.pwdmgr;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.Objects;

public class AppLocker {

    public static void init(final Pane p, final Font fontTitle, final Stage stage, final double DBL_FRAME_WIDTH) {
        final double DBL_WIDTH = p.getWidth(), DBL_HEIGHT = p.getHeight(), DBL_MARGIN = 40.0D;
        p.getChildren().clear();

        final Font f = Font.font(Resource.getInstance().fontNormal.getFamily(), 16.0D);

        final Text tAppTitle = new Text("Ajikhoji's Password Manager");
        tAppTitle.setFont(Font.font(fontTitle.getFamily(), 28.0D));
        tAppTitle.setLayoutX(0.0D);
        tAppTitle.setWrappingWidth(DBL_WIDTH);
        tAppTitle.setTextAlignment(TextAlignment.CENTER);
        tAppTitle.setLayoutY(60.0D);
        tAppTitle.setFill(Color.WHITE);

        final Line l = new Line(DBL_MARGIN, 90.0D, DBL_WIDTH - DBL_MARGIN, 90.0D);
        l.setStroke(Color.web("#fedded"));

        final Text txtPrompt = new Text("Enter app password to continue.");
        txtPrompt.setFont(f);
        txtPrompt.setLayoutX(DBL_MARGIN);
        txtPrompt.setLayoutY(135.0D);
        txtPrompt.setFill(Color.web("#dedeed"));

        final Text txtHint = new Text();

        final TextField tf = new TextField();
        tf.setMinWidth(DBL_WIDTH - 2*DBL_MARGIN);
        tf.setMaxWidth(DBL_WIDTH - 2*DBL_MARGIN);
        tf.setPrefWidth(DBL_WIDTH - 2*DBL_MARGIN);
        tf.setLayoutX(DBL_MARGIN);
        tf.setLayoutY(155.0D);
        tf.setFont(f);

        tf.textProperty().addListener((olt, ovt, nvt) -> {
            if(nvt != null) {
                if (nvt.length() > 60) {
                    txtHint.setVisible(true);
                    txtHint.setText("The field has reached maximum number of 60 characters.");
                    tf.setText(nvt.substring(0, 60));
                }
            }
        });
        tf.setOnKeyPressed(e -> {
            if(!e.isControlDown() && e.getCode() != KeyCode.X && e.getCode() != KeyCode.V) txtHint.setVisible(false);
        });

        txtHint.setFont(f);
        txtHint.setLayoutX(DBL_MARGIN);
        txtHint.setLayoutY(210.0D);
        txtHint.setFill(Color.web("#997D74"));
        txtHint.setWrappingWidth(DBL_WIDTH - 2*DBL_MARGIN);
        txtHint.setVisible(false);

        final Button btnLogin = getButton("LOGIN", "#B5E61D","#2DE854", 310.0D, 260.0D, f);
        btnLogin.setOnAction(e -> {
            if (tf.getText().equals(Settings.getAppPassword())) {
                HelloApplication.stApp.show();
            } else {
                txtHint.setText("Incorrect Password entered.");
                txtHint.setVisible(true);
            }
        });

        final Button btnHint = getButton("HINT", "#FFF200","#FFF672", 215.0D, 260.0D, f);
        btnHint.setOnAction(e -> {
            txtHint.setText("HINT : ".concat(Settings.getAppPasswordHint()));
            txtHint.setVisible(true);
        });

        final Button btnClose = getButton("CLOSE", "#FF3D02","#FF6C4A", 110.0D, 260.0D, f);
        btnClose.setOnAction(e -> System.exit(0));

        p.getChildren().addAll(tAppTitle, l, txtPrompt, tf, txtHint, btnLogin, btnHint, btnClose);
    }

    private static Button getButton(final String str, final String clrNormal, final String clrHover, final double x, final double y, final Font f) {
        final Button btn = new Button(str);
        btn.setLayoutX(x);
        btn.setLayoutY(y);
        btn.setStyle("-fx-background-color : ".concat(clrNormal).concat(";"));
        btn.setOnMouseEntered(e -> {
            btn.setStyle("-fx-background-color : ".concat(clrHover).concat(";"));
            btn.setEffect(new DropShadow(8.0D, Color.web(clrNormal)));
        });
        btn.setOnMouseExited(e -> {
            btn.setStyle("-fx-background-color : ".concat(clrNormal).concat(";"));
            btn.setEffect(null);
        });
        btn.setPadding(new Insets(7.0D, 12.0D, 7.0D, 12.0D));
        btn.setFont(f);
        return btn;
    }

}
