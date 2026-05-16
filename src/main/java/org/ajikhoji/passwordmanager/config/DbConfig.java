package org.ajikhoji.passwordmanager.config;

import org.ajikhoji.passwordmanager.repository.*;
import org.ajikhoji.passwordmanager.service.*;

public class DbConfig {

    private static DbHandler db;
    private static AccountService accountService;
    private static AccountCustomFieldService accountCustomFieldService;
    private static LabelService labelService;

    public static void initDb() {
        if(db == null) {
            db = DbHandler.getInstance();

            final AccountRepo accRepo = new HsqlAccountRepo(db.getConnection());
            accountService = new AccountServiceImpl(accRepo);

            final AccountCustomFieldRepo accCustomFieldRepo = new HsqlAccountCustomFieldRepo(db.getConnection());
            accountCustomFieldService = new AccountCustomFieldServiceImpl(accCustomFieldRepo);

            final LabelRepo lblRepo = new HsqlLabelRepo(db.getConnection());
            labelService = new LabelServiceImpl(lblRepo);
        }
    }

    public static void closeDb() {
        db.closeConnection();
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


}
