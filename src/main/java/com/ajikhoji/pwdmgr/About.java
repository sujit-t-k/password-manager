package com.ajikhoji.pwdmgr;

import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class About {

    private final static Pane pane = new Pane();

    static void init() {
        final double DBL_GAP = 15.0D, DBL_PANE_INITIAL_WIDTH = 450.0D, DBL_PANE_INITIAL_HEIGHT = 500.0D;
        pane.setMinSize(DBL_PANE_INITIAL_WIDTH, DBL_PANE_INITIAL_HEIGHT);
        pane.setMaxSize(DBL_PANE_INITIAL_WIDTH, DBL_PANE_INITIAL_HEIGHT);
        pane.setPrefSize(DBL_PANE_INITIAL_WIDTH, DBL_PANE_INITIAL_HEIGHT);
        pane.getStyleClass().add("info-pane");

        final Font f = Font.font(Resource.getInstance().fontNormal.getFamily(), 18.0D);

        final Text txtAbout = getTextNode("About", "#E0E0E0", Font.font(Resource.getInstance().fontTitle.getFamily(), 30.0D));
        txtAbout.setStyle("-fx-padding-bottom : 10px;");

        final Line l = new Line(0.0D, 0.0D, 410.0D, 0.0D);
        l.setStroke(Color.web("#857D71"));

        final Text txtApp = getTextNode("App", "#856F84", Font.font(Resource.getInstance().fontTitle.getFamily(), 26.0D));
        txtApp.setStyle("-fx-padding-top : 10px; -fx-padding-bottom : 6px; -fx-padding-left : 10px;");

        final Text tApp1 = getTextNode("Ajikhoji's Password Manager", "#eeccdd", Font.font(Resource.getInstance().fontTitle.getFamily(), 22.0D));
        final Text tApp2 = getTextNode("v 1.0.0.5 (alpha version)", "#CFC7B5", f);
        final Text tApp3 = getTextNode("Ajikhoji Software Solutions Private Limited", "#CFC7B5", f);

        final VBox bx1 = new VBox(tApp1, tApp2, tApp3);
        bx1.setPadding(new Insets(0.0D, 0.0D, 10.0D, 20.0D));
        bx1.setSpacing(6.0D);

        final Text txtAuthor = getTextNode("Developer", "#856F84", Font.font(Resource.getInstance().fontTitle.getFamily(), 26.0D));
        txtAuthor.setStyle("-fx-padding-top : 10px; -fx-padding-bottom : 6px; -fx-padding-left : 10px;");

        final Text txtAuthorName = getTextNode("Sujit T K", "#DF64E0", Font.font(Resource.getInstance().fontTitle.getFamily(), 42.0D));
        final Text txtRole = getTextNode("Founder and President", "#CFC7B5", f);
        final Text company = getTextNode("Ajikhoji Software Solutions Private Limited", "#CFC7B5", f);

        final VBox bx2 = new VBox(txtAuthorName, txtRole, company);
        bx2.setPadding(new Insets(0.0D, 0.0D, 10.0D, 20.0D));
        bx2.setSpacing(6.0D);

        final Text txtContact = getTextNode("Contact", "#856F84", Font.font(Resource.getInstance().fontTitle.getFamily(), 26.0D));
        txtContact.setStyle("-fx-padding-top : 10px; -fx-padding-bottom : 6px; -fx-padding-left : 10px;");

        final Text txtMail = getTextNode("Mail : sujittkannan@gmail.com", "#CFC7B5", f);
        final Text txtPhone = getTextNode("Phone : +91 99436 53448", "#CFC7B5", f);

        final VBox bx3 = new VBox(txtMail, txtPhone);
        bx3.setPadding(new Insets(0.0D, 0.0D, 10.0D, 20.0D));
        bx3.setSpacing(6.0D);

        final VBox bx = new VBox(txtAbout, l,txtApp, bx1, txtAuthor, bx2, txtContact, bx3);
        bx.setSpacing(8.0D);
        bx.setLayoutX(20.0D);
        bx.setLayoutY(20.0D);
        pane.getChildren().add(bx);
    }

    private static Text getTextNode(final String content, final String strColor, final Font f) {
        final Text txt = new Text(content);
        txt.setFont(f);
        txt.setFill(Color.web(strColor));
        return txt;
    }

    static Pane getPane() {
        return pane;
    }
}
