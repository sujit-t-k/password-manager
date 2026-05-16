package org.ajikhoji.passwordmanager.config;

import javafx.scene.image.Image;
import org.ajikhoji.passwordmanager.AppStartup;

import java.util.Objects;

public class AppResources {

    public Image imgEyeOpened, imgEyeClosed, imgCopy, imgCopied, imgEdit, imgDelete;

    private static AppResources ar;
    private AppResources() {
        this.imgEyeOpened = new Image(Objects.requireNonNull(AppStartup.class.getResource("image/eye_open.png")).toExternalForm());
        this.imgEyeClosed = new Image(Objects.requireNonNull(AppStartup.class.getResource("image/eye_close.png")).toExternalForm());
        this.imgCopy = new Image(Objects.requireNonNull(AppStartup.class.getResource("image/copy.png")).toExternalForm());
        this.imgCopied = new Image(Objects.requireNonNull(AppStartup.class.getResource("image/copied_black.png")).toExternalForm());
        this.imgEdit = new Image(Objects.requireNonNull(AppStartup.class.getResource("image/edit.png")).toExternalForm());
        this.imgDelete = new Image(Objects.requireNonNull(AppStartup.class.getResource("image/del.png")).toExternalForm());
    }

    public static AppResources getInstance() {
        if(ar == null) {
            ar = new AppResources();
        }
        return ar;
    }

}
