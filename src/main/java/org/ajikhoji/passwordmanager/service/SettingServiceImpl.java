package org.ajikhoji.passwordmanager.service;

import org.ajikhoji.passwordmanager.repository.SettingRepo;
import org.ajikhoji.passwordmanager.repository.TableFieldsPreferenceRememberable;
import org.ajikhoji.passwordmanager.security.EncryptionService;

public class SettingServiceImpl implements SettingService {

    private final SettingRepo repo;

    public SettingServiceImpl(final SettingRepo repo) {
        this.repo = repo;
    }

    @Override
    public void setHash(final String hashedString) {
        repo.updateHash(hashedString);
    }

    @Override
    public String getHash() {
        return repo.getHashedValue();
    }

    @Override
    public void setSalt(final String saltedstring) {
        repo.updateSalt(saltedstring);
    }

    @Override
    public String getSalt() {
        return repo.getSalt();
    }

    @Override
    public void setUserName(final String name) {
        repo.updateUserName(name);
    }

    @Override
    public String getUserName() {
        return repo.getUserName();
    }

    @Override
    public void setHint(final String hint) {
        repo.updateHint(hint);
    }

    @Override
    public String getHint() {
        return repo.getHint();
    }

    @Override
    public boolean isSetupDone() {
        return repo.isSetupDone();
    }

    @Override
    public void changePassword(final EncryptionService oldService, final EncryptionService newService, final String hash, final String salt) {
        repo.changePassword(oldService, newService, hash, salt);
    }

    /*
     * Utility method to verify whether the given order is a valid possible field order.
     * If not, the default table field order will be returned.
     */
    private long getCorrectedOrderView(final long order) {
        if(order == TableFieldsPreferenceRememberable.DEFAULT_TABLE_FIELDS_ORDER) {
            return order;
        }
        if(order < 0) {//means that default system order option is selected
            return -getCorrectedOrderView(-order);
        }
        boolean corrupted = order <= 0;
        if(!corrupted) {
            final int totalFieldsCount = TableFieldsPreferenceRememberable.getFieldsCount(order);
            if(totalFieldsCount < 3 || totalFieldsCount > 10) {
                corrupted = true;
            } else {
                long temp = order;
                int count = totalFieldsCount;
                final boolean[] encountered = new boolean[10];
                while(count-- > 0) {
                    final int d = (int) (temp % 10);
                    if(encountered[d]) {
                        corrupted = true;
                        break;
                    }
                    encountered[d] = true;
                    temp /= 10;
                }
            }
        }
        return corrupted ? TableFieldsPreferenceRememberable.DEFAULT_TABLE_FIELDS_ORDER : order;
    }

    @Override
    public void saveTableFieldsOrderPreference(long preferenceOrder) {
        preferenceOrder = getCorrectedOrderView(preferenceOrder);
        repo.saveTableFieldsOrderPreference(preferenceOrder);
    }

    @Override
    public long getTableFieldsOrder() {
        try {
            final long order = repo.getTableFieldsOrder();
            return getCorrectedOrderView(order);
        } catch (final Exception e) {
            return TableFieldsPreferenceRememberable.DEFAULT_TABLE_FIELDS_ORDER;
        }
    }

    @Override
    public void saveOpenLinkAction(final LinkActionOption option) {
        repo.saveOpenLinkAction(option);
    }

    @Override
    public LinkActionOption getOpenLinkActionPreference() {
        try {
            return repo.getOpenLinkActionPreference();
        } catch (final Exception e) {
            return LinkActionOption.OPEN_IN_BROWSER;
        }
    }

    @Override
    public void clearAccountCredentialData() {
        repo.clearAccountCredentialData();
    }

}
