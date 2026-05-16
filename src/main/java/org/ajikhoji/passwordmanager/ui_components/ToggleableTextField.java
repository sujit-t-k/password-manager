package org.ajikhoji.passwordmanager.ui_components;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import org.ajikhoji.passwordmanager.config.AppConfig;

import java.util.function.Consumer;

public class ToggleableTextField extends HBox {

    private final StringProperty spText;
    private ChangeListener<String> listener;

    public ToggleableTextField(final boolean maskPassword, final String defaultValue) {
        spText = new SimpleStringProperty(defaultValue);

        final TextField tf = new TextField();
        final PasswordField pf = new PasswordField();
        final BooleanProperty bpMaskPassword = new SimpleBooleanProperty(maskPassword);

        tf.textProperty().bindBidirectional(spText);
        pf.textProperty().bindBidirectional(spText);

        tf.setPrefColumnCount(60);
        tf.setMaxWidth(Region.USE_PREF_SIZE);
        pf.setPrefColumnCount(60);
        pf.setMaxWidth(Region.USE_PREF_SIZE);

        final ImageView toggler = new ImageView();
        toggler.setFitWidth(25.0D);
        toggler.setPreserveRatio(true);
        final Consumer<Boolean> onMaskValueChange = mask -> {
            if(mask) {
                getChildren().set(0, pf);
                toggler.setImage(AppConfig.getAppResources().imgEyeClosed);
            } else {
                getChildren().set(0, tf);
                toggler.setImage(AppConfig.getAppResources().imgEyeOpened);
            }
        };
        final Button btnToggler = new Button();
        btnToggler.setGraphic(toggler);
        btnToggler.setOnAction(e -> bpMaskPassword.set(!bpMaskPassword.get()));

        bpMaskPassword.addListener((ol, ov, nv) -> onMaskValueChange.accept(nv));
        getChildren().add(tf);
        getChildren().add(btnToggler);
        setMaxLength(30);
        setSpacing(10.0D);
        onMaskValueChange.accept(maskPassword);
        setMaxWidth(Double.MAX_VALUE);
        setAlignment(Pos.CENTER_LEFT);
    }

    public ToggleableTextField() {
        this("");
    }

    public ToggleableTextField(String defaultValue) {
        this(true, defaultValue);
    }

    public String getText() {
        return spText.get();
    }

    public void setText(final String text) {
        spText.set(text);
    }

    public StringProperty getTextProperty() {
        return this.spText;
    }

    public void setMaxLength(final int length) {
        if(length < 1) return;
        if(listener != null) {
            spText.removeListener(listener);
        }
        listener = (observable, oldValue, newValue) -> {
            if(newValue == null || newValue.isEmpty()) return;
            if(newValue.length() > length) {
                spText.set(newValue.substring(0, length));
            }
        };
        spText.addListener(listener);
    }

}
