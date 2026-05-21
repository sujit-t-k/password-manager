package org.ajikhoji.passwordmanager;

import javafx.application.Application;
import javafx.stage.Stage;
import org.ajikhoji.passwordmanager.config.AppConfig;
import org.ajikhoji.passwordmanager.config.DbConfig;
import org.ajikhoji.passwordmanager.security.AesEncryptionService;
import org.ajikhoji.passwordmanager.security.EncryptionService;
import org.ajikhoji.passwordmanager.security.KeyManager;
import org.ajikhoji.passwordmanager.service.SettingService;
import org.ajikhoji.passwordmanager.ui_components.AppFrame;
import org.ajikhoji.passwordmanager.util.HashUtil;
import org.ajikhoji.passwordmanager.util.SaltUtil;
import org.ajikhoji.passwordmanager.view.AppLockScreen;
import org.ajikhoji.passwordmanager.view.AppMainScreen;
import org.ajikhoji.passwordmanager.view.AppSetupScreen;

import javax.crypto.SecretKey;

public class AppStartup extends Application {

    @Override
    public void start(Stage stage) {
        DbConfig.initDb();
        AppConfig.setPrimaryStage(stage);
        AppConfig.setAppFrame(new AppFrame(stage, AppConfig.getScreenWidth() * 0.5D, AppConfig.getScreenHeight() * 0.7D));
        AppConfig.setHostServices(getHostServices());

        final SettingService settingService = DbConfig.getSettingService();

        if(settingService.isSetupDone()) {
            final String hint = settingService.getHint();
            final String hash = settingService.getHash();
            final String saltString = settingService.getSalt();
            final byte[] salt = SaltUtil.getSaltValue(saltString);

            AppLockScreen.init(
                hint,
                (enteredPassword) -> {
                    final String hashed = HashUtil.hashPassword(enteredPassword, salt);
                    if(hashed.equals(hash)) {
                        openAppMainScreen(enteredPassword, salt);
                        return true;
                    }
                    return false;
                }
            );
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
                    openAppMainScreen(plainPassword, salt);
                }
            );
        }

        stage.setOnCloseRequest(e -> {
            DbConfig.closeDb();
        });
        stage.show();
    }

    private void openAppMainScreen(final String plainPassword, final byte[] salt) {
        final SecretKey key = KeyManager.generateKey(plainPassword, salt);
        final EncryptionService encryptionService = new AesEncryptionService(key);
        AppConfig.setEncryptionService(encryptionService);
        AppMainScreen.init();
    }

}
