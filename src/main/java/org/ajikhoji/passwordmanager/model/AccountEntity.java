package org.ajikhoji.passwordmanager.model;

import org.ajikhoji.passwordmanager.util.Utility;
import org.ajikhoji.passwordmanager.validator.AccountInfoValidator;

import java.time.LocalDateTime;

public class AccountEntity {

    public static final long UNDEFINED_ACCOUNT_ID = -17;

    private long accId;
    private String accName;
    private String accPassword;
    private String platform;
    private long labelId;
    private String link;
    private int usageCount;
    private LocalDateTime lastUsedDateTime;
    private LocalDateTime createdDateTime;
    private LocalDateTime recentUpdateDateTime;

    private AccountEntity() { }

    public long getAccId() {
        return accId;
    }

    public void setAccId(long accId) {
        this.accId = accId;
    }

    public String getAccName() {
        return accName;
    }

    public void setAccName(String accName) {
        this.accName = accName;
    }

    public String getAccPassword() {
        return accPassword;
    }

    public void setAccPassword(String accPassword) {
        this.accPassword = accPassword;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public long getLabelId() {
        return labelId;
    }

    public void setLabelId(long labelId) {
        this.labelId = labelId;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(int usageCount) {
        this.usageCount = usageCount;
    }

    public LocalDateTime getLastUsedDateTime() {
        return lastUsedDateTime;
    }

    public void setLastUsedDateTime(LocalDateTime lastUsedDateTime) {
        this.lastUsedDateTime = lastUsedDateTime;
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public LocalDateTime getRecentUpdateDateTime() {
        return recentUpdateDateTime;
    }

    public void setRecentUpdateDateTime(LocalDateTime recentUpdateDateTime) {
        this.recentUpdateDateTime = recentUpdateDateTime;
    }

    @Override
    public boolean equals(final Object other) {
        if(other instanceof AccountEntity ae) {
            return
                    this.accId == ae.accId &&
                    this.accName.equals(ae.accName) &&
                    this.accPassword.equals(ae.accPassword) &&
                    this.platform.equals(ae.platform) &&
                    Utility.isSameValuedObject(this.link, ae.link) &&
                    this.usageCount == ae.usageCount &&
                    this.labelId == ae.labelId &&
                    this.createdDateTime.isEqual(ae.createdDateTime) &&
                    Utility.isSameDateTimeValue(this.createdDateTime, ae.createdDateTime) &&
                    Utility.isSameDateTimeValue(this.lastUsedDateTime, ae.lastUsedDateTime);
        }
        return false;
    }

    public static AccountEntityBuilder Builder() {
        return new AccountEntityBuilder();
    }

    public static final class AccountEntityBuilder {
        private long accId;
        private String accName;
        private String accPassword;
        private String platform;
        private long labelId;
        private String link;
        private int usageCount;
        private LocalDateTime lastUsedDateTime;
        private LocalDateTime createdDateTime;
        private LocalDateTime recentUpdateDateTime;

        private AccountEntityBuilder() {
            accId = AccountEntity.UNDEFINED_ACCOUNT_ID;
            usageCount = 0;
        }

        public AccountEntityBuilder withAccountId(final long id) {
            accId = id;
            return this;
        }

        public AccountEntityBuilder withAccountName(final String accName) {
            this.accName = accName;
            return this;
        }

        public AccountEntityBuilder withEncryptedAccountPassword(final String encryptedPassword) {
            this.accPassword = encryptedPassword;
            return this;
        }

        public AccountEntityBuilder withAccountPlatform(final String platform) {
            this.platform = platform;
            return this;
        }

        public AccountEntityBuilder withLabelId(final long lblId) {
            this.labelId = lblId;
            return this;
        }

        public AccountEntityBuilder withLink(final String externalLink) {
            this.link = externalLink;
            return this;
        }

        public AccountEntityBuilder withUsageCount(final int usageCount) {
            this.usageCount = usageCount;
            return this;
        }

        public AccountEntityBuilder withCreatedDateTime(final LocalDateTime ldtCreated) {
            this.createdDateTime = ldtCreated;
            return this;
        }

        public AccountEntityBuilder withLastUsedDateTime(final LocalDateTime ldtLastUsed) {
            this.lastUsedDateTime = ldtLastUsed;
            return this;
        }

        public AccountEntityBuilder withLastUpdatedDateTime(final LocalDateTime ldtLastUpdated) {
            this.recentUpdateDateTime = ldtLastUpdated;
            return this;
        }

        public AccountEntity build() {
            final AccountEntity ae = new AccountEntity();
            ae.setAccId(this.accId);
            ae.setAccName(this.accName);
            ae.setAccPassword(this.accPassword);
            ae.setPlatform(this.platform);
            ae.setLabelId(this.labelId);
            ae.setLink(this.link);
            ae.setUsageCount(this.usageCount);
            ae.setCreatedDateTime(this.createdDateTime);
            ae.setLastUsedDateTime(this.lastUsedDateTime);
            ae.setRecentUpdateDateTime(this.recentUpdateDateTime);

            AccountInfoValidator.validateAccountName(ae);
            AccountInfoValidator.validatePlatform(ae);
            AccountInfoValidator.validateEncryptedPassword(ae.getAccPassword());
            AccountInfoValidator.isAccountEntityDataConsistent(ae);

            return ae;
        }

    }

}
