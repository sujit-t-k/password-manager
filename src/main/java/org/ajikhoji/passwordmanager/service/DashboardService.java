package org.ajikhoji.passwordmanager.service;

import org.ajikhoji.passwordmanager.dto.LabelUsage;
import org.ajikhoji.passwordmanager.model.AccountEntity;

import java.util.List;

public interface DashboardService {

    List<AccountEntity> getKMostUsedAccount(int accountCountCap);
    List<AccountEntity> getAccountsAddedThisMonth();
    List<AccountEntity> getKRecentlyModifiedAccounts(int accountCountCap);
    List<AccountEntity> getKRecentlyUsedAccounts(int accountCountCap);
    int getTotalAccountCount();
    List<LabelUsage> getKMostUsedLabels(int labelCountCap);

}
