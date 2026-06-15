package org.ajikhoji.passwordmanager.service;

import org.ajikhoji.passwordmanager.dto.LabelUsage;
import org.ajikhoji.passwordmanager.model.AccountEntity;

import java.util.List;

public class DashboardServiceImpl implements DashboardService {

    private final AccountService accountService;
    private final LabelService labelService;

    public DashboardServiceImpl(final AccountService accountService, final LabelService labelService) {
        this.accountService = accountService;
        this.labelService = labelService;
    }

    @Override
    public List<AccountEntity> getKMostUsedAccount(int accountCountCap) {
        return accountService.getKMostUsedAccounts(accountCountCap);
    }

    @Override
    public List<AccountEntity> getAccountsAddedThisMonth() {
        return accountService.getAccountsAddedThisMonth();
    }

    @Override
    public List<AccountEntity> getKRecentlyModifiedAccounts(final int accountCountCap) {
        return accountService.getKRecentModifiedAccounts(accountCountCap);
    }

    @Override
    public List<AccountEntity> getKRecentlyUsedAccounts(final int accountCountCap) {
        return accountService.getKRecentUsedAccounts(accountCountCap);
    }

    @Override
    public int getTotalAccountCount() {
        return accountService.getAllAccountCredential().size();
    }

    @Override
    public List<LabelUsage> getKMostUsedLabels(final int labelCountCap) {
        final List<LabelUsage> labelStats = labelService.getLabelUsageStatistics();
        if(labelStats.size() > labelCountCap) {
            return labelStats.subList(0, labelCountCap);
        }
        return labelStats;
    }

}
