package com.ajikhoji.passwordmanager;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import static javafx.application.Application.launch;

public class PasswordManagerApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(PasswordManagerApp.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 650);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle("Password Manager");
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add("theme1.css");
        stage.setScene(scene);
        stage.show();
        /*DataBaseHandler db = new DataBaseHandler();
        db.initialize();
        //for(AccountDetail d : db.getAllDetails()) System.out.println(d);
        System.out.println(db.isAccountDetailAvailable("2we","4re1","2yt","h1t"));
        //db.deleteDetail(1);
        //db.addDetail("2we","re1","2yt","h1t");
        //db.updateDetail(0, "21" , "09", "334", "87");
        /*db.deleteDetail(329);
        System.out.println(db.getAllDetails().size());
        db.restoreOriginalData();
        System.out.println(db.getAllDetails().size());
        db.closeServer();*/
    }

    public static void main(String[] args) {
        new UniqueAppRunner().launchApp(args);
    }
}
class UniqueAppRunner {
    public final void launchApp(final String[] args) {
        if(this.isAppInstanceRunningAlready()) {
            Platform.exit();
        } else {
            launch(PasswordManagerApp.class, args);
        }
    }

    private boolean isAppInstanceRunningAlready() {//Applicable only for windows OS.
        int instanceCount = 0;
        ProcessBuilder pb = new ProcessBuilder();
        pb.command("cmd.exe", "/c", "jps -lm");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(pb.start().getInputStream()));
            String processName = null;
            while ((processName = reader.readLine()) != null) {
                if (processName.endsWith("com.ajikhoji.passwordmanager.PasswordManagerApp")) {
                    if (instanceCount++ > 0) {//if it is zero, then it means no instance of this app is running. Otherwise, an instance is running.
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Exception occurred on launch of application. Unable to check for instance already running.");
            e.printStackTrace();
        }
        return false;
    }
}
