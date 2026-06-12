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
import org.ajikhoji.passwordmanager.util.Utility;

import java.util.function.Consumer;

public class ToggleableTextField extends HBox {

    private final TextField tf;
    private final PasswordField pf;
    private final StringProperty textProperty;
    private ChangeListener<String> textFieldListener, passwordFieldListener;
    private final BooleanProperty bpMaskPassword;

    public ToggleableTextField(final boolean maskPassword, final String defaultValue, final boolean disablePaste) {
        if(disablePaste) {
            tf = new TextField() {
                @Override
                public void copy() {
                    Utility.showInformationAlert("Copy Disabled", "Copy feature not allowed for this field.");
                }
                @Override
                public void paste() {
                    Utility.showInformationAlert("Paste Disabled", "Paste feature not allowed for this field.");
                }
            };
            pf = new PasswordField() {
                @Override
                public void paste() {
                    Utility.showInformationAlert("Paste Disabled", "Paste feature not allowed for this field.");
                }
            };
        } else {
            tf = new TextField() {
                @Override
                public void copy() {
                    Utility.showInformationAlert("Copy Disabled", "Copy feature not allowed for this field.");
                }
            };
            pf = new PasswordField();
        }

        bpMaskPassword = new SimpleBooleanProperty(maskPassword);
        textProperty = new SimpleStringProperty(defaultValue);
        textProperty.addListener((ol, ov, nv) -> {
            tf.setText(nv);
            pf.setText(nv);
        });

        tf.setMaxWidth(Region.USE_PREF_SIZE);
        pf.setMaxWidth(Region.USE_PREF_SIZE);

        final ImageView toggler = new ImageView();
        toggler.setFitWidth(25.0D);
        toggler.setPreserveRatio(true);
        final Consumer<Boolean> onMaskValueChange = mask -> {
            if(mask) {
                pf.setText(textProperty.get());
                getChildren().set(0, pf);
                toggler.setImage(AppConfig.getAppResources().imgEyeClosed);
            } else {
                tf.setText(textProperty.get());
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
        this(false);
    }

    public ToggleableTextField(final boolean blnDisablePasteFeature) {
        this("", blnDisablePasteFeature);
    }

    public ToggleableTextField(String defaultValue, boolean blnDisablePasteFeature) {
        this(true, defaultValue, blnDisablePasteFeature);
    }

    public String getText() {
        return textProperty.getValue();
    }

    public void setText(final String text) {
        textProperty.set(text);
    }

    public StringProperty getTextProperty() {
        return this.textProperty;
    }

    public void setMaxLength(final int length) {
        if(length < 1) return;
        if(textFieldListener != null) {
            tf.textProperty().removeListener(textFieldListener);
        }
        if(passwordFieldListener != null) {
            pf.textProperty().removeListener(passwordFieldListener);
        }
        textFieldListener = (observable, oldValue, newValue) -> {
            if(newValue == null || newValue.isEmpty()) return;
            if(newValue.length() > length) {
                tf.setText(newValue.substring(0, length));
                return;
            }
            textProperty.set(newValue);
        };
        passwordFieldListener = (observable, oldValue, newValue) -> {
            if(newValue == null || newValue.isEmpty()) return;
            if(newValue.length() > length) {
                pf.setText(newValue.substring(0, length));
                return;
            }
            textProperty.set(newValue);
        };
        tf.setPrefColumnCount(length);
        pf.setPrefColumnCount(length);
        tf.textProperty().addListener(textFieldListener);
        pf.textProperty().addListener(passwordFieldListener);
    }

}
