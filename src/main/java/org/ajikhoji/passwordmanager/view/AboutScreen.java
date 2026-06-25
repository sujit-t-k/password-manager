package org.ajikhoji.passwordmanager.view;

import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.ajikhoji.passwordmanager.config.AppConfig;

public class AboutScreen extends Pane {

    private final VBox vbxParent;

    public AboutScreen() {
        final ScrollPane sp = new ScrollPane();
        sp.getStyleClass().add("info-scroll");
        sp.setFitToWidth(true);

        vbxParent = new VBox(14.0D);
        vbxParent.setStyle("-fx-padding: 20px;");

        sp.setContent(vbxParent);
        getChildren().add(sp);
        sp.prefWidthProperty().bind(widthProperty());
        sp.prefHeightProperty().bind(heightProperty());

        addHeaderSection();
        addWhyThisAppSection();
        addAppFeaturesSection();
        addPrivacySection();
        addOpenSourceSection();
        addContactSection();
    }

    private void addHeaderSection() {
        final VBox vbxHeader = new VBox();

        vbxHeader.getChildren().addAll(
            getTitleLabel(AppConfig.getAppName()),
            getContentLabel("Version 2.0.1"),
            getContentLabel("An open-source, lightweight offline password manager"),
            getContentLabel("Securely store, organize, and manage your credentials locally with strong encryption. " +
                    "Your data never leaves your device."),
            getContentLabel("Released: June 2026")
        );

        addChild(vbxHeader);
    }

    private void addWhyThisAppSection() {
        final VBox vbx = new VBox();

        vbx.getChildren().addAll(
            getTitleLabel("Origin of this app"),
            getContentLabel("Originally developed to solve a personal problem: " +
                "managing hundreds of credentials stored in spreadsheets. " +
                "It has since evolved into a fully encrypted offline password manager focused on simplicity, privacy, and security.")
        );

        addChild(vbx);
    }

    private void addAppFeaturesSection() {
        final VBox vbx = new VBox();

        vbx.getChildren().addAll(
            getTitleLabel("App Features"),
            addBulletedPoints(
                new String[] {
                    "AES encrypted database",
                    "Store notes and additional account details",
                    "Fast Filtering & Sorting",
                    "Customizable view",
                    "Export account credentials",
                    "Offline storage with AES encryption"
                }
            )
        );

        addChild(vbx);
    }

    private void addPrivacySection() {
        final VBox vbx = new VBox();

        vbx.getChildren().addAll(
            getTitleLabel("Privacy"),
            getContentLabel("This application is designed to keep your data under your control."),
            getContentLabel("Unlike cloud-based password managers, your encrypted vault remains on your own device. No account creation, internet connection, or remote server is required."),
            addBulletedPoints(
                new String[] {
                    "No cloud storage",
                    "No advertisements",
                    "No tracking",
                    "No telemetry",
                    "Encryption & decryption occurs locally"
                }
            )
        );

        addChild(vbx);
    }


    private void addOpenSourceSection() {
        final VBox vbx = new VBox();

        final Hyperlink link = new Hyperlink("Open Github");
        link.setUnderline(true);
        link.setOnAction(e -> AppConfig.openDocument("https://github.com/sujit-t-k/password-manager"));

        vbx.getChildren().addAll(
            getTitleLabel("Open source"),
            getContentLabel("This application is open source."),
            getContentLabel("Inspect the source code, report issues, or contribute improvements."),
            link
        );

        addChild(vbx);
    }

    private void addContactSection() {
        final VBox vbx = new VBox();

        final Hyperlink email = new Hyperlink("sujittkannan@gmail.com");
        email.setUnderline(true);
        email.setOnAction(e -> AppConfig.openDocument("mailto:sujittkannan@gmail.com"));
        final HBox hbxMail = new HBox(6.0D, getContentLabel("Email"), email);
        hbxMail.setAlignment(Pos.CENTER_LEFT);

        vbx.getChildren().addAll(
            getTitleLabel("Contact"),
            getContentLabel("Have a suggestion? Found a bug?"),
            hbxMail
        );

        addChild(vbx);
    }

    private VBox addBulletedPoints(final String[] points) {
        final VBox vbxContent = new VBox();

        for(final String point : points) {
            final Label lblContent = getContentLabel(point);
            final Circle c = new Circle(2.0D, Color.rgb(204,198,207));
            lblContent.setGraphic(c);
            vbxContent.getChildren().add(lblContent);
        }

        return vbxContent;
    }

    private void addChild(final Pane content) {
        final String borderColor = vbxParent.getChildren().size() % 2 == 0 ? "6E3FA9" : "CCA91F";
        final String bg = vbxParent.getChildren().isEmpty() ? "0F0612" : "0C090D";
        content.setStyle(String.format("-fx-border-width: 0 0 0 3px; -fx-border-color: #%s; -fx-padding: 6px 8px 6px 8px; -fx-background-color: #%s;", borderColor, bg));
        vbxParent.getChildren().add(content);
    }

    public Label getTitleLabel(final String text) {
        final Label lbl = new Label(text);
        lbl.setWrapText(true);
        lbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        return lbl;
    }

    public Label getContentLabel(final String text) {
        final Label lbl = new Label(text);
        lbl.setWrapText(true);
        lbl.setStyle("-fx-font-size: 12px;");
        return lbl;
    }

}
