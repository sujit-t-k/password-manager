package com.ajikhoji.pwdmgr;

import com.ajikhoji.db.DataHandler;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class Settings {

    private final static Pane pane = new Pane();
    private static boolean blnViewTableShowPasswordByDefault = true, blnViewTableOrderDefaultPreference = true;
    private static int intTableColumnOrder = 7654321;
    private static String strNewAppPassword = "", strNewAppHint = "", strAppPassword = "", strAppPasswordHint = "";
    private final static StringProperty spAppPasswordChangeStage = new SimpleStringProperty("");

    public static void loadPreferences() {
        strAppPassword = DataHandler.getInstance().getAppPassword();
        strAppPasswordHint = DataHandler.getInstance().getAppPasswordHint();
        intTableColumnOrder = DataHandler.getInstance().getViewTableColumnOrderPreference();
        blnViewTableShowPasswordByDefault = DataHandler.getInstance().getShowPassword();
        blnViewTableOrderDefaultPreference = DataHandler.getInstance().getViewTableColumnDefaultPreference();
    }

    public static void init() {

        final double DBL_GAP = 20.0D, DBL_PANE_INITIAL_WIDTH = ContentPane.paneContent.getPrefWidth() - 2*DBL_GAP, DBL_PANE_INITIAL_HEIGHT = ContentPane.paneContent.getPrefHeight() - 2*DBL_GAP;

        pane.setPrefSize(DBL_PANE_INITIAL_WIDTH, DBL_PANE_INITIAL_HEIGHT);
        pane.setMinSize(DBL_PANE_INITIAL_WIDTH, DBL_PANE_INITIAL_HEIGHT);
        pane.setMaxSize(DBL_PANE_INITIAL_WIDTH, DBL_PANE_INITIAL_HEIGHT);
        pane.getStyleClass().add("info-pane");

        final double DBL_MARGIN = 50.0D;

        final Text txtSettings = getTextNode("Settings", Font.font(Resource.getInstance().fontTitle.getFamily(), 30.0D));
        txtSettings.getStyleClass().add("text-title");
        txtSettings.setLayoutX(DBL_MARGIN);
        txtSettings.setLayoutY(50.0D);

        final Line l = new Line(DBL_MARGIN, 70.0D, DBL_PANE_INITIAL_WIDTH - DBL_MARGIN, 70.0D);
        l.getStyleClass().add("line-title");

        final Line l2 = new Line(DBL_MARGIN, 70.0D, DBL_PANE_INITIAL_WIDTH - DBL_MARGIN, 70.0D);
        l2.getStyleClass().add("line-divider");

        ContentPane.paneContent.widthProperty().addListener((ol, ov, nv) -> {
            pane.setMinWidth(nv.doubleValue() - 2 * DBL_GAP);
            pane.setMaxWidth(nv.doubleValue() - 2 * DBL_GAP);
            pane.setPrefWidth(nv.doubleValue() - 2 * DBL_GAP);
        });
        ContentPane.paneContent.heightProperty().addListener((ol, ov, nv) -> {
            pane.setMinHeight(nv.doubleValue() - 2 * DBL_GAP);
            pane.setMaxHeight(nv.doubleValue() - 2 * DBL_GAP);
            pane.setPrefHeight(nv.doubleValue() - 2 * DBL_GAP);
        });

        final Font fontDefault = Resource.getInstance().fontNormal;

        final Label lblAppLaunch = new Label("At app launch (start of new app session)");
        lblAppLaunch.setFont(Font.font(fontDefault.getFamily(), 18.0D));
        lblAppLaunch.setPadding(new Insets(0.0D, 0.0D, 16.0D, 0.0D));
        lblAppLaunch.getStyleClass().add("label-title");

        final CheckBox cbxShowPassword = new CheckBox("Reveal password in view table");
        cbxShowPassword.setFont(Font.font(fontDefault.getFamily(), 14.0D));
        cbxShowPassword.setPadding(new Insets(0.0D, 0.0D, 0.0D, 10.0D));
        cbxShowPassword.getStyleClass().add("chkbox");;
        cbxShowPassword.setSelected(blnViewTableShowPasswordByDefault);
        cbxShowPassword.selectedProperty().addListener((ol, ov, nv) -> setViewTableShowPasswordByDefault(nv));

        final CheckBox cbxPreserveOrder = new CheckBox("Reset order of view table columns to default");
        cbxPreserveOrder.setFont(Font.font(fontDefault.getFamily(), 14.0D));
        cbxPreserveOrder.setPadding(new Insets(0.0D, 0.0D, 0.0D, 10.0D));
        cbxPreserveOrder.getStyleClass().add("chkbox");
        cbxPreserveOrder.setSelected(blnViewTableOrderDefaultPreference);
        cbxPreserveOrder.selectedProperty().addListener((ol, ov, nv) -> {
            DataHandler.getInstance().setViewTableColumnDefaultPreference(nv);
        });

        final VBox vbViewTableSetting = new VBox(lblAppLaunch, cbxShowPassword, cbxPreserveOrder);
        vbViewTableSetting.setPadding(new Insets(16.0D, 0.0D, 0.0D, 0.0D));
        vbViewTableSetting.setSpacing(10.0D);

        final Label lblAppPassword = new Label("App Password");
        lblAppPassword.setFont(Font.font(fontDefault.getFamily(), 18.0D));
        lblAppPassword.setPadding(new Insets(0.0D, 0.0D, 12.0D, 0.0D));
        lblAppPassword.getStyleClass().add("label-title");

        final VBox vbAppPassword = new VBox(lblAppPassword);
        vbAppPassword.setSpacing(16.0D);

        final VBox vb = new VBox(vbViewTableSetting, l2, vbAppPassword);
        vb.setLayoutX(DBL_MARGIN);
        vb.setLayoutY(80.0D);
        vb.setSpacing(24.0D);

        final Text txtPrompt = getTextNode("", Font.font(fontDefault.getFamily(), 14.0D));
        txtPrompt.getStyleClass().add("info");
        txtPrompt.setTextAlignment(TextAlignment.CENTER);
        txtPrompt.setVisible(false);

        spAppPasswordChangeStage.addListener((ol, ov, nv) -> {
            vbAppPassword.getChildren().retainAll(lblAppPassword);
            txtPrompt.setVisible(false);
            if (nv.startsWith("Set Password and Hint")) {
                final Text txtPassword = getTextNode("          Password          :  " , Font.font(fontDefault.getFamily(), 14.0D));
                txtPassword.getStyleClass().add("text-label");

                final TextField tfPassword = new TextField(strNewAppPassword) {
                    @Override
                    public void paste() { }
                };
                tfPassword.setFont(Font.font(fontDefault.getFamily(), 14.0D));
                tfPassword.setPrefWidth(440.0D);

                final HBox hb1 = new HBox(txtPassword, tfPassword);
                hb1.setAlignment(Pos.CENTER_LEFT);

                final Text txtHelp = getTextNode("          Password Hint :  ", Font.font(fontDefault.getFamily(), 14.0D));
                txtHelp.getStyleClass().add("text-label");

                final TextField tfHelp = new TextField(strNewAppHint);
                tfHelp.setFont(Font.font(fontDefault.getFamily(), 14.0D));

                final HBox hb2 = new HBox(txtHelp, tfHelp);
                hb2.setAlignment(Pos.CENTER_LEFT);

                tfHelp.setOnKeyPressed(e -> {
                    if(!e.isControlDown() && e.getCode() != KeyCode.X && e.getCode() != KeyCode.V) txtPrompt.setVisible(false);
                });
                tfHelp.textProperty().addListener((olt, ovt, nvt) -> {
                    if(nvt != null) {
                        if (nvt.length() > 60) {
                            txtPrompt.setVisible(true);
                            txtPrompt.setText("The field 'Password Hint' has reached maximum number of 60 characters.");
                            tfHelp.setText(nvt.substring(0, 60));
                        }
                        strNewAppHint = tfHelp.getText();
                    }
                });
                tfPassword.setOnKeyPressed(e -> {
                    if(!e.isControlDown() && e.getCode() != KeyCode.X && e.getCode() != KeyCode.V) txtPrompt.setVisible(false);
                });
                tfPassword.textProperty().addListener((olt, ovt, nvt) -> {
                    if(nvt != null) {
                        if (nvt.length() > 30) {
                            txtPrompt.setVisible(true);
                            txtPrompt.setText("The field 'Password' has reached maximum number of 30 characters.");
                            tfPassword.setText(nvt.substring(0, 30));
                        }
                        strNewAppPassword = tfPassword.getText();
                    }
                });

                final Button btnCancel = new Button("CANCEL");
                btnCancel.setPadding(new Insets(0.0D, 0.0D, 0.0D, 50.0D));
                btnCancel.setOnAction(e -> spAppPasswordChangeStage.set(nv.endsWith("(New)") ? "Password not set" : "Password set"));

                final Button btnApply = new Button("NEXT");
                btnCancel.setLayoutX(110.0D);
                btnApply.setLayoutX(230.0D);
                btnApply.setOnAction(e -> {
                    if(strNewAppHint.isBlank()) {
                        txtPrompt.setText("'Password Hint' should not be blank.");
                        txtPrompt.setVisible(true);
                        tfHelp.requestFocus();
                    } else if (strNewAppPassword.isBlank()) {
                        txtPrompt.setText("'Password' should not be blank.");
                        txtPrompt.setVisible(true);
                        tfPassword.requestFocus();
                    } else if (strNewAppPassword.chars().filter(i -> i != ' ').count() < 4) {
                        txtPrompt.setText("'Password' should consist of minimum of four non-empty(non white-space) characters.");
                        txtPrompt.setVisible(true);
                        tfPassword.requestFocus();
                    } else {
                        strNewAppPassword = tfPassword.getText();
                        strNewAppHint = tfHelp.getText();
                        spAppPasswordChangeStage.set("Conform New App Password");
                    }
                });
                final Pane paneButtons = new Pane(btnCancel, btnApply);
                btnApply.heightProperty().addListener((olh, ovh, nvh) -> {
                    paneButtons.setPrefHeight(nvh.doubleValue());
                });
                vb.prefWidthProperty().addListener((olw, ovw, nvw) -> {
                    txtPrompt.setWrappingWidth(nvw.doubleValue());
                    tfHelp.setPrefWidth(Math.min(nvw.doubleValue() - txtHelp.getBoundsInLocal().getWidth(), 810.0D));
                });
                vbAppPassword.getChildren().addAll(hb1, hb2, txtPrompt, paneButtons);
                tfHelp.setPrefWidth(Math.min(vb.getPrefWidth() - txtHelp.getBoundsInLocal().getWidth(), 810.0D));
            } else if(nv.equals("Conform New App Password")) {
                final Text txtConform = getTextNode("Retype the new password for verification of correctness. Upon successful verification, " +
                        "the changes to app password will be applied.", Font.font(fontDefault.getFamily(), 14.0D));
                txtConform.getStyleClass().add("text-label");
                txtConform.setWrappingWidth(vbAppPassword.getPrefWidth());

                final TextField tfConform = new TextField() {
                    @Override
                    public void paste() { }
                };
                tfConform.setFont(Font.font(fontDefault.getFamily(), 14.0D));
                tfConform.setMinWidth(440.0D);
                tfConform.setMaxWidth(440.0D);
                tfConform.setPrefWidth(440.0D);
                tfConform.setOnKeyPressed(e -> {
                    if(!e.isControlDown() && e.getCode() != KeyCode.X && e.getCode() != KeyCode.V) txtPrompt.setVisible(false);
                });
                tfConform.textProperty().addListener((olt, ovt, nvt) -> {
                    if(nvt != null) {
                        if (nvt.length() > 30) {
                            txtPrompt.setVisible(true);
                            txtPrompt.setText("Field has reached maximum number of 30 characters.");
                            tfConform.setText(nvt.substring(0, 30));
                        }
                    }
                });
                final Button btnBack = new Button("BACK");
                final Button btnVerify = new Button("VERIFY & APPLY");
                final Pane paneButtons = new Pane(btnBack, btnVerify);
                btnVerify.heightProperty().addListener((olh, ovh, nvh) -> paneButtons.setPrefHeight(nvh.doubleValue()));
                vbAppPassword.widthProperty().addListener((olw, ovw, nvw) -> txtConform.setWrappingWidth(nvw.doubleValue()));
                btnBack.setLayoutX(110.0D);
                btnVerify.setLayoutX(230.0D);
                btnBack.setOnAction(e -> spAppPasswordChangeStage.set(ov));
                btnVerify.setOnAction(e -> {
                    if(tfConform.getText() == null || !tfConform.getText().equals(strNewAppPassword)) {
                        txtPrompt.setText("Password does not match.");
                        txtPrompt.setVisible(true);
                        tfConform.requestFocus();
                    } else {
                        //TO-DO : Change password in DB
                        DataHandler.getInstance().setAppPassword(strNewAppPassword);
                        DataHandler.getInstance().setAppPasswordHint(strNewAppHint);
                        strAppPassword = strNewAppPassword;
                        strAppPasswordHint = strNewAppHint;
                        strNewAppPassword = strNewAppHint = "";
                        spAppPasswordChangeStage.set("Password set");
                    }
                });
                vbAppPassword.getChildren().addAll(txtConform, tfConform, txtPrompt, paneButtons);
            } else if (nv.equals("Password set")) {
                final Text txtPasswordSet = getTextNode("          App Password has been set.  ", Font.font(fontDefault.getFamily(), 14.0D));
                txtPasswordSet.getStyleClass().add("text-label");

                final ImageView imgSet = new ImageView(Resource.getInstance().imgOpened);
                imgSet.setFitWidth(25.0D);
                imgSet.setFitHeight(25.0D);

                final Button btnChange = new Button("CHANGE");
                final Button btnRemove = new Button("REMOVE");
                final Pane paneButtons = new Pane(btnChange, btnRemove);
                btnRemove.heightProperty().addListener((olh, ovh, nvh) -> paneButtons.setPrefHeight(nvh.doubleValue()));
                btnChange.setOnAction(e -> spAppPasswordChangeStage.set("Authentication : Change"));
                btnRemove.setOnAction(e -> spAppPasswordChangeStage.set("Authentication : Remove"));
                btnChange.setLayoutX(110.0D);
                btnRemove.setLayoutX(230.0D);

                final HBox hb1 = new HBox(txtPasswordSet, imgSet);
                hb1.setAlignment(Pos.CENTER_LEFT);
                vbAppPassword.getChildren().addAll(hb1, paneButtons);
            } else if (nv.startsWith("Authentication")) {
                final Text txt = getTextNode(nv.endsWith("Change") ? "Enter current password to change app password and hint." :
                                            "Enter current password to remove app password.", Font.font(fontDefault.getFamily(), 14.0D));
                txt.getStyleClass().add("text-label");

                final TextField tf = new TextField();
                tf.setFont(Font.font(fontDefault.getFamily(), 14.0D));
                tf.setMinWidth(440.0D);
                tf.setMaxWidth(440.0D);
                tf.setPrefWidth(440.0D);
                tf.setOnKeyPressed(e -> {
                    if(!e.isControlDown() && e.getCode() != KeyCode.X && e.getCode() != KeyCode.V) txtPrompt.setVisible(false);
                });
                tf.textProperty().addListener((olt, ovt, nvt) -> {
                    if(nvt != null) {
                        if (nvt.length() > 30) {
                            txtPrompt.setVisible(true);
                            txtPrompt.setText("Field has reached maximum number of 30 characters.");
                            tf.setText(nvt.substring(0, 30));
                        }
                    }
                });

                final Button btnProceed = new Button("PROCEED");
                btnProceed.setOnAction(e -> {
                    if(tf.getText().equals(strAppPassword)) {
                        if(nv.endsWith("Change")) {
                            spAppPasswordChangeStage.set("Set Password and Hint");
                        } else {
                            //TO-DO : Remove password from DB
                            DataHandler.getInstance().removeAppPassword();
                            strAppPassword = "";
                            strAppPasswordHint = "";
                            spAppPasswordChangeStage.set("Password not set");
                        }
                    } else {
                        txtPrompt.setVisible(true);
                        txtPrompt.setText("Incorrect password entered");
                    }
                });

                final Button btnBack = new Button("BACK");
                final Pane paneButtons = new Pane(btnBack, btnProceed);
                btnProceed.heightProperty().addListener((olh, ovh, nvh) -> paneButtons.setPrefHeight(nvh.doubleValue()));
                btnBack.setOnAction(e -> spAppPasswordChangeStage.set("Password set"));
                btnBack.setLayoutX(110.0D);
                btnProceed.setLayoutX(230.0D);

                vbAppPassword.getChildren().addAll(txt, tf, txtPrompt, paneButtons);
            } else if (nv.equals("Password not set")) {
                final Text txtPasswordNotSet = getTextNode("          App Password has not yet been set.  ", Font.font(fontDefault.getFamily(), 14.0D));
                txtPasswordNotSet.getStyleClass().add("link-not-available");

                final ImageView imgNotSet = new ImageView(Resource.getInstance().imgPasswordNotSet);
                imgNotSet.setFitWidth(25.0D);
                imgNotSet.setFitHeight(25.0D);

                final Button btnSet = new Button("SET PASSWORD");
                btnSet.setOnAction(e -> spAppPasswordChangeStage.set("Set Password and Hint (New)"));
                btnSet.setLayoutX(70.0D);

                final HBox hb1 = new HBox(txtPasswordNotSet, imgNotSet);
                hb1.setAlignment(Pos.CENTER_LEFT);

                vbAppPassword.getChildren().addAll(hb1, new Pane(btnSet));
            }
        });


//        vb.setStyle("-fx-background-color : blue;");

        pane.widthProperty().addListener((ol, ov, nv) -> {
            vb.setMinWidth(nv.doubleValue() - 2*DBL_MARGIN);
            vb.setMaxWidth(nv.doubleValue() - 2*DBL_MARGIN);
            vb.setPrefWidth(nv.doubleValue() - 2*DBL_MARGIN);
            vbAppPassword.setMinWidth(nv.doubleValue() - 2*DBL_MARGIN);
            vbAppPassword.setMaxWidth(nv.doubleValue() - 2*DBL_MARGIN);
            vbAppPassword.setPrefWidth(nv.doubleValue() - 2*DBL_MARGIN);
            l.setEndX(nv.doubleValue() - DBL_MARGIN);
            l2.setEndX(nv.doubleValue() - DBL_MARGIN);
        });
        pane.heightProperty().addListener((ol, ov, nv) -> {
            vb.setMinHeight(nv.doubleValue() - 2*DBL_MARGIN);
            vb.setMaxHeight(nv.doubleValue() - 2*DBL_MARGIN);
            vb.setPrefHeight(nv.doubleValue() - 2*DBL_MARGIN);
        });
        spAppPasswordChangeStage.set((strAppPassword == null || strAppPassword.isBlank()) ? "Password not set" : "Password set");
        pane.getChildren().addAll(txtSettings, l, vb);
    }

    private static Text getTextNode(final String txt, final Font ff) {
        final Text t = new Text(txt);
        t.setFont(ff);
        t.getStyleClass().add("text-label");
        return t;
    }

    public static Pane getPane() {
        return pane;
    }

    public static int getTableColumnOrder() {
        return intTableColumnOrder;
    }

    public static boolean getPasswordViewPreference() {
        return blnViewTableShowPasswordByDefault;
    }

    public static String getAppPassword() {
        return strAppPassword;
    }

    public static String getAppPasswordHint() {
        return strAppPasswordHint;
    }

    public static void setViewTableShowPasswordByDefault(final boolean show) {
        DataHandler.getInstance().setShowPasswordByDefault(show);
        blnViewTableShowPasswordByDefault = show;
    }

    public static void setTableColumnOrder(final int order) {
        DataHandler.getInstance().setTableColumnOrder(order);
        intTableColumnOrder = order;
    }
}
