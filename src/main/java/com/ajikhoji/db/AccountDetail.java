package com.ajikhoji.db;

import java.time.LocalDateTime;

public class AccountDetail {

    private String /*strPreviousAccName, strPreviousPassword, strPreviousDomain, strPreviousLink,
            strPreviousPurpose,*/ strAccName, strPassword, strDomain, strLink, strPurpose;
    private int intUniqueID;
    private LocalDateTime ldtDateCreated, ldtDateModified;

    public AccountDetail(final String strAccName, final String strPassword, final String strDomain, final String strLink, final String strPurpose) {
        this.strAccName = strAccName;
        this.strPassword = strPassword;
        this.strDomain = strDomain;
        this.strLink = strLink;
        this.strPurpose = strPurpose;
    }

    public AccountDetail(final String strAccName, final String strPassword, final String strDomain, final String strLink, final String strPurpose, final LocalDateTime ldtDateCreated) {
        this(strAccName, strPassword, strDomain, strLink, strPurpose);
        this.ldtDateCreated = ldtDateCreated;
    }

    public AccountDetail(final int intUniqueID, final String strAccName,final String strPassword, final String strDomain, final String strLink, final String strPurpose, final LocalDateTime ldtDateCreated) {
        this(strAccName, strPassword, strDomain, strLink, strPurpose, ldtDateCreated);
        this.intUniqueID = intUniqueID;
    }

    public AccountDetail(final String strAccName, final String strPassword, final String strDomain, final String strLink, final String strPurpose, final LocalDateTime ldtDateCreated, final LocalDateTime ldtDateModified) {
        this(strAccName, strPassword, strDomain, strLink, strPurpose, ldtDateCreated);
        this.ldtDateModified = ldtDateModified;
    }

    public AccountDetail(final int intUniqueID, final String strAccName, final String strPassword, final String strDomain, final String strLink, final String strPurpose, final LocalDateTime ldtDateCreated, final LocalDateTime ldtDateModified) {
        this(strAccName, strPassword, strDomain, strLink, strPurpose, ldtDateCreated);
        this.ldtDateModified = ldtDateModified;
        this.intUniqueID = intUniqueID;
    }

    public AccountDetail(final AccountDetail ad) {
        this.set(ad);
    }

    public final void updateAccDetail(final String strAccID, final String strPassword, final String strDomain, final String strLink, final String strPurpose, final LocalDateTime ldtDateModified) {
        this.strAccName = strAccID;
        this.strPassword = strPassword;
        this.strDomain = strDomain;
        this.strLink = strLink;
        this.strPurpose = strPurpose;
        this.ldtDateModified = ldtDateModified;
    }

    public int getIntUniqueID() {
        return this.intUniqueID;
    }

    public String getStrAccName() {
        return this.strAccName;
    }

    public String getStrPassword() {
        return this.strPassword;
    }

    public String getStrDomain() {
        return this.strDomain;
    }

    public String getStrLink() {
        return this.strLink;
    }

    public String getStrPurpose() {
        return this.strPurpose;
    }

    public LocalDateTime getLdtDateCreated() {
        return this.ldtDateCreated;
    }

    public LocalDateTime getLdtDateModified() {
        return this.ldtDateModified;
    }

    public final void set(final AccountDetail ad) {
        this.intUniqueID = ad.getIntUniqueID();
        this.strAccName = ad.strAccName;
        this.strPassword = ad.strPassword;
        this.strDomain = ad.strDomain;
        this.strLink = ad.strLink;
        this.strPurpose = ad.strPurpose;
        this.ldtDateCreated = ad.ldtDateCreated;
        this.ldtDateModified = ad.ldtDateModified;
    }

    @Override
    public String toString() {
        return "UID : " + this.intUniqueID + " | Acc ID : " + this.strAccName + " | Password : " + this.strPassword + " | Domain : " + this.strDomain + " | Link : "
            + this.strLink + " | Purpose : " + this.strPurpose + " | Created : " + this.ldtDateCreated + " | Modified : " + this.ldtDateModified;
    }

  /*  public boolean isModified() {
        return !(this.strPreviousAccName == null && this.strPreviousPassword == null && this.strPreviousDomain == null
                && this.strPreviousLink == null && this.strPreviousPurpose == null);
    }

    public void modifyAccName(final String str) {
        if(this.strPreviousAccName == null) {
            this.strPreviousAccName = this.strAccName;
        }
        this.strAccName = str;
    }

    public void modifyPassword(final String str) {
        if(this.strPreviousPassword == null) {
            this.strPreviousPassword = this.strPassword;
        }
        this.strPassword = str;
    }

    public void modifyDomain(final String str) {
        if(this.strPreviousDomain == null) {
            this.strPreviousDomain = this.strDomain;
        }
        this.strDomain = str;
    }

    public void modifyLink(final String str) {
        if(this.strPreviousLink == null) {
            this.strPreviousLink = this.strLink;
        }
        this.strLink = str;
    }

    public void modifyPurpose(final String str) {
        if(this.strPreviousPurpose == null) {
            this.strPreviousPurpose = this.strPurpose;
        }
        this.strPurpose = str;
    }
*/
}
