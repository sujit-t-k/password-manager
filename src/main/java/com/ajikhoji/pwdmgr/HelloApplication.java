package com.ajikhoji.pwdmgr;

import com.ajikhoji.db.DataBaseHandler;
import com.ajikhoji.db.DataHandler;
import com.ajikhoji.db.DataUtils;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.BufferedInputStream;
import java.util.Objects;
import java.util.function.Function;

public class HelloApplication extends Application {

    static SimpleObjectProperty<AppTheme> theme = new SimpleObjectProperty<>();
    static HostServices hs;
    static Stage stApp;

    @Override
    public void start(Stage stage) {
        final Pane paneLoading = new Pane();
        paneLoading.setPrefSize(500.0D, 330.0D);
        paneLoading.setStyle("-fx-background-color: #020103;");

        Font fontTitle;
        try {
            fontTitle = Font.loadFont(new BufferedInputStream(Objects.requireNonNull(HelloApplication.class.getResource("fonts/loading screen/Cinzel-VariableFont_wght.ttf")).openStream()), 40.0D);
        }catch (Exception e) {
            fontTitle = Font.font("Times New Roman", 40.0D);
        }

        final Font finalFontTitle = fontTitle;

        final Function<Object[], Text> TitleText = (final Object[] params) -> {
            final Text txt = new Text((String) params[0]);
            txt.setFont(finalFontTitle);
            txt.setLayoutX((double) params[1]);
            txt.setLayoutY((double) params[2]);
            txt.setFill(Color.web((String) params[3]));
            txt.setMouseTransparent(true);
            return txt;
        };

        final Text txtAji = TitleText.apply(new Object[]{"Aji", 40.0D, 90.0D, "#FFC90E"});

        final Line l = new Line(40.0D, 290.D, 460.0D, 290.0D);
        l.setStroke(Color.web("#FFC90E"));
        l.setStrokeWidth(3.0D);
        l.setStrokeLineCap(StrokeLineCap.ROUND);

        final Line lBlack = new Line(0.0D, 290.25D, 40.0D, 290.25D);
        lBlack.setStroke(Color.BLACK);
        lBlack.setStrokeWidth(2.0D);
        lBlack.setStrokeLineCap(StrokeLineCap.ROUND);

        final Timeline tProgressBarIndeterminate = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(lBlack.layoutXProperty(), 0.0D)),
                                                                new KeyFrame(Duration.millis(1220.0D), new KeyValue(lBlack.layoutXProperty(), 460.0D)));
        tProgressBarIndeterminate.setCycleCount(Timeline.INDEFINITE);
        tProgressBarIndeterminate.setAutoReverse(false);

        final Text txtLoading = new Text("LOADING");
        txtLoading.setFont(Font.font("Consolas", 16.0D));
        txtLoading.setFill(Color.web("#FFC90E"));
        txtLoading.setLayoutX(40.0D);
        txtLoading.setLayoutY(290.0D - txtLoading.getBoundsInLocal().getHeight() + 5.0D);
        txtLoading.setMouseTransparent(true);

        final Timeline tLine = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(txtLoading.textProperty(), "LOADING")),
                new KeyFrame(Duration.millis(300.0D), new KeyValue(txtLoading.textProperty(), "LOADING.")),
                new KeyFrame(Duration.millis(600.0D), new KeyValue(txtLoading.textProperty(), "LOADING..")),
                new KeyFrame(Duration.millis(900.0D), new KeyValue(txtLoading.textProperty(), "LOADING...")),
                new KeyFrame(Duration.millis(1200.0D), new KeyValue(txtLoading.textProperty(), "LOADING...")));
        tLine.setCycleCount(Timeline.INDEFINITE);
        tLine.setAutoReverse(false);

        paneLoading.getChildren().addAll(txtAji, l, lBlack, txtLoading,
                TitleText.apply(new Object[]{"Khoji's", 40.0D + txtAji.getBoundsInLocal().getWidth() + 5.0D, 90.0D, "#EFCCEF"}),
                TitleText.apply(new Object[]{"Password\nManager", 40.0D, 90.0D + txtAji.getBoundsInLocal().getWidth() + 5.0D, "#EFCCEF"}));

        tLine.playFromStart();
        tProgressBarIndeterminate.playFromStart();

        stage.setScene(new Scene(paneLoading));
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setX((Screen.getPrimary().getBounds().getWidth() - 500.0D)/2.0D);
        stage.setY((Screen.getPrimary().getBounds().getHeight() - 400.0D)/2.0D);
        stage.show();

        Font finalFontTitle1 = fontTitle;
        new Thread(() -> {
            //Data loading
            final DataBaseHandler dbh = new DataBaseHandler();
            dbh.initialize();
            DataHandler.init(dbh);
            Settings.loadPreferences();
            System.out.println(Settings.getTableColumnOrder() + "<>" + Settings.getAppPassword() + "<>" + Settings.getPasswordViewPreference());
            //UI Loading
            Platform.runLater(() -> {
                hs = getHostServices();
                theme.set(AppTheme.DARK);
                final double DBL_FRAME_WIDTH = Screen.getPrimary().getBounds().getWidth()*0.65D;

                final Stage s = new Stage(StageStyle.TRANSPARENT);
                final Pane p = AppFrame.getFrame(s, DBL_FRAME_WIDTH, DBL_FRAME_WIDTH/1.5D, 50.0D, 20.0D, "Ajikhoji's Password Manager");
                s.setScene(new Scene(p, Color.TRANSPARENT));
                p.getScene().getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("theme/dark/app-frame.css")).toString());
                stApp = s;
                s.setOnShown(e -> {
                    tProgressBarIndeterminate.stop();
                    tLine.stop();
                    stage.hide();
                    AppMenu.buildAppMenu(p);
                    ContentPane.init();
                    ContentPane.setContent("View");
                });
                if(Settings.getAppPassword() == null) {
                    s.show();
                } else {
                    tProgressBarIndeterminate.stop();
                    tLine.stop();
                    AppLocker.init(paneLoading, finalFontTitle1, stage, DBL_FRAME_WIDTH);
                }
            });
        }).start();
    }

    public static void main(String[] args) {
        launch();
    }
}