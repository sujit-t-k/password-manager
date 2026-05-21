package org.ajikhoji.passwordmanager.viewmodel;

import org.ajikhoji.passwordmanager.config.DbConfig;
import org.ajikhoji.passwordmanager.model.AccountCustomFieldEntity;
import org.ajikhoji.passwordmanager.model.LabelEntity;

import java.util.ArrayList;
import java.util.List;

public class AddNewAccountViewModel {

    public String accName;
    public String accPassword;
    public String confirmPassword;
    public String link;
    public String platform;
    public LabelEntity label;
    public List<AccountCustomFieldEntity> customFields;

    public AddNewAccountViewModel() {
        reset();
    }

    public void reset() {
        accName = "";
        accPassword = "";
        confirmPassword = "";
        link = "";
        platform = "";
        label = DbConfig.getLabelService().getLabelEntityByName(LabelEntity.DEFAULT_LABEL_NAME);
        customFields = new ArrayList<>();
    }

    private static AddNewAccountViewModel instance;
    public static AddNewAccountViewModel getInstance() {
        if(instance == null) {
            instance = new AddNewAccountViewModel();
        }
        return instance;
    }

    public static AddNewAccountViewModel getNewInstance() {
        instance = null;
        return getInstance();
    }

}
