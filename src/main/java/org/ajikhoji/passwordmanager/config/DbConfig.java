package org.ajikhoji.passwordmanager.config;

import org.ajikhoji.passwordmanager.repository.*;
import org.ajikhoji.passwordmanager.service.*;

import java.sql.Connection;

public class DbConfig {

    private static DbHandler db;
    private static AccountService accountService;
    private static AccountCustomFieldService accountCustomFieldService;
    private static LabelService labelService;
    private static SettingService settingService;
    private static DashboardService dashboardService;

    public static void initDb() {
        if(db == null) {
            db = DbHandler.newInstance();
            final Connection c = db.getConnection();

            final AccountRepo accRepo = new HsqlAccountRepo(c);
            accountService = new AccountServiceImpl(accRepo);

            final AccountCustomFieldRepo accCustomFieldRepo = new HsqlAccountCustomFieldRepo(c);
            accountCustomFieldService = new AccountCustomFieldServiceImpl(accCustomFieldRepo);

            final LabelRepo lblRepo = new HsqlLabelRepo(c);
            labelService = new LabelServiceImpl(lblRepo);

            final SettingRepo settingRepo = new HsqlSettingRepo(c);
            settingService = new SettingServiceImpl(settingRepo);

            dashboardService = new DashboardServiceImpl(accountService, labelService);
        }
    }

    public static void closeDb() {
        if(db != null) {
            db.closeConnection();
            db = null;
        }
    }

    public static AccountService getAccountService() {
        return accountService;
    }

    public static AccountCustomFieldService getAccountCustomFieldService() {
        return accountCustomFieldService;
    }

    public static LabelService getLabelService() {
        return labelService;
    }

    public static SettingService getSettingService() {
        return settingService;
    }

    public static DashboardService getDashboardService() {
        return dashboardService;
    }

}
