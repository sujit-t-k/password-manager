package com.ajikhoji.pwdmgr;

import com.ajikhoji.db.AccountDetail;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.BufferedInputStream;
import java.util.Objects;

public class Resource {

    public Image[] imgMenuItems;
    public Image imgEyeOpened, imgEyeClosed, imgOpenLink, imgCopy, imgOpened, imgCopied, imgEdit, imgDelete, imgPasswordSet, imgPasswordNotSet;
    public Font fontAppTitle, fontMenuItem, fontTitle, fontNormal, fontNumber;
    private static Resource r;

    private Resource() {
        this.loadImages();
        this.loadFonts();
    }

    public static Resource getInstance() {
        if(r == null) {
            r = new Resource();
        }
        return r;
    }

    private void loadImages() {
        try {
            this.imgMenuItems = new Image[]{new Image(Objects.requireNonNull(AppMenu.class.getResource("images/menu_item/view_table.png")).toExternalForm()),
                    new Image(Objects.requireNonNull(AppMenu.class.getResource("images/menu_item/add_new_data.png")).toExternalForm()),
                    new Image(Objects.requireNonNull(AppMenu.class.getResource("images/menu_item/setting.png")).toExternalForm()),
                    new Image(Objects.requireNonNull(AppMenu.class.getResource("images/menu_item/help.png")).toExternalForm()),
                    new Image(Objects.requireNonNull(AppMenu.class.getResource("images/menu_item/about.png")).toExternalForm())};
            this.imgEyeOpened = new Image(Objects.requireNonNull(AppMenu.class.getResource("images/table_view/eye_open.png")).toExternalForm());
            this.imgEyeClosed = new Image(Objects.requireNonNull(AppMenu.class.getResource("images/table_view/eye_close.png")).toExternalForm());
            this.imgOpenLink = new Image(Objects.requireNonNull(AppMenu.class.getResource("images/table_view/open_link.png")).toExternalForm());
            this.imgCopy = new Image(Objects.requireNonNull(AppMenu.class.getResource("images/table_view/copy.png")).toExternalForm());
            this.imgOpened = new Image(Objects.requireNonNull(AppMenu.class.getResource("images/table_view/copied.png")).toExternalForm());
            this.imgCopied = new Image(Objects.requireNonNull(AppMenu.class.getResource("images/table_view/copied_black.png")).toExternalForm());
            this.imgEdit = new Image(Objects.requireNonNull(AppMenu.class.getResource("images/table_view/edit2.png")).toExternalForm());
            this.imgDelete = new Image(Objects.requireNonNull(AppMenu.class.getResource("images/table_view/del6.png")).toExternalForm());
            this.imgPasswordSet = new Image(Objects.requireNonNull(AppMenu.class.getResource("images/preferences/secured.png")).toExternalForm());
            this.imgPasswordNotSet = new Image(Objects.requireNonNull(AppMenu.class.getResource("images/preferences/not.png")).toExternalForm());
        } catch (final Exception e) {
            System.err.println("Image Loading Failed");
        }
    }

    private void loadFonts() {
        try {
            this.fontAppTitle = Font.loadFont(new BufferedInputStream(Objects.requireNonNull(HelloApplication.class.getResource("fonts/loading screen/PoetsenOne-Regular.ttf")).openStream()), 20.0D);
        } catch (final Exception e) {
            this.fontAppTitle = Font.font("Consolas", 20.0D);
        }
        try {
            this.fontMenuItem = Font.loadFont(new BufferedInputStream(Objects.requireNonNull(HelloApplication.class.getResource("fonts/Boogaloo-Regular.otf")).openStream()), 18.0D);
        } catch (final Exception e) {
            this.fontMenuItem = Font.font("Times New Roman", 18.0D);
        }
        try {
            this.fontNumber = Font.loadFont(new BufferedInputStream(Objects.requireNonNull(HelloApplication.class.getResource("fonts/DeliusSwashCaps-Regular.ttf")).openStream()), 20.0D);
        } catch (final Exception ignored) {
            this.fontNumber = Font.font("Times New Roman", 20.0D);
        }
        try {
            this.fontTitle = Font.loadFont(new BufferedInputStream(Objects.requireNonNull(HelloApplication.class.getResource("fonts/GolosText-VariableFont_wght.ttf")).openStream()) , 20.0D);
        } catch (final Exception ignored) {
            this.fontTitle = Font.font("Times New Roman", 20.0D);
        }
        try {
            this.fontNormal = Font.loadFont(new BufferedInputStream(Objects.requireNonNull(HelloApplication.class.getResource("fonts/normal/UbuntuSans-VariableFont_wdth,wght.ttf")).openStream()), 20.0D);
        } catch (final Exception ignored) {
            this.fontNormal = Font.font("Times New Roman", FontWeight.BOLD, 20.0D);
        }
    }
}
