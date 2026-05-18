package org.ajikhoji.passwordmanager;

import javafx.application.Application;
import javafx.stage.Stage;
import org.ajikhoji.passwordmanager.config.AppConfig;
import org.ajikhoji.passwordmanager.config.DbConfig;
import org.ajikhoji.passwordmanager.service.SettingService;
import org.ajikhoji.passwordmanager.ui_components.AppFrame;
import org.ajikhoji.passwordmanager.util.HashUtil;
import org.ajikhoji.passwordmanager.util.SaltUtil;
import org.ajikhoji.passwordmanager.view.AppMainScreen;
import org.ajikhoji.passwordmanager.view.AppSetupScreen;

public class AppStartup extends Application {

    @Override
    public void start(Stage stage) {
        DbConfig.initDb();
        AppConfig.setPrimaryStage(stage);
        AppConfig.setAppFrame(new AppFrame(stage, AppConfig.getScreenWidth() * 0.5D, AppConfig.getScreenHeight() * 0.7D));

        final SettingService settingService = DbConfig.getSettingService();

        if(settingService.isSetupDone()) {
            openAppMainScreen();
        } else {
            AppSetupScreen.init(
                info -> {
                    final String userName = info[0];
                    final String hint = info[1];
                    final String plainPassword = info[2];

                    final byte[] salt = SaltUtil.generateSalt();
                    final String hashedPassword = HashUtil.hashPassword(plainPassword, salt);
                    settingService.setSalt(SaltUtil.getAsString(salt));
                    settingService.setHash(hashedPassword);
                    settingService.setUserName(userName);
                    settingService.setHint(hint);
                }
            );
        }

        stage.setOnCloseRequest(e -> {
            DbConfig.closeDb();
        });
        stage.show();
    }

    private void openAppMainScreen() {
        AppMainScreen.init();
    }

}
