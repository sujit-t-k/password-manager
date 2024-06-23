package com.ajikhoji.db;

import com.ajikhoji.pwdmgr.ViewData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DataHandler {

    public final ObservableList<AccountDetail> currentAccDetails;
    private final DataBaseHandler dbh;
    private final DataUtils du;
    private static DataHandler dh;

    private DataHandler(final DataBaseHandler dbh) {
        this.dbh = dbh;
        this.du = DataUtils.getInstance();
        //Loading Data from DB
        this.currentAccDetails =  FXCollections.observableArrayList(this.dbh.getAllDetails());
        for(final AccountDetail ad : this.currentAccDetails) {
            this.du.addToOriginalAccountDetails(ad);
            this.du.registerAccountInfo(ad.getStrAccName(), ad.getStrPassword(), ad.getStrDomain(), ad.getStrPurpose());
            System.out.println(ad);
        }
    }

    public static void init(final DataBaseHandler dbh) {
        dh = new DataHandler(dbh);
    }

    public static DataHandler getInstance() {
        return dh;
    }

    public final boolean checkIfAccDetailAvailable(final String strAccName, final String strPassword, final String strDomain, final String strPurpose) {
        return this.du.isAccountDetailAvailable(strAccName, strPassword, strDomain, strPurpose);
    }

    public final String addNewAccInfo(final String strAccName, final String strPassword, final String strDomain, final String strLink, final String strPurpose) {
        final LocalDateTime ldt = LocalDateTime.now();
        final String result = this.dbh.addNewDetail(strAccName, strPassword, strDomain, strLink, strPurpose, ldt);
        if(result.startsWith("Added")) {
            final int intAllocatedUniqueID = this.dbh.getUniqueID(strAccName, strPassword, strDomain, strPurpose);
            this.currentAccDetails.add(new AccountDetail(intAllocatedUniqueID, strAccName, strPassword, strDomain, strLink, strPurpose, ldt));
            this.du.registerAccountInfo(strAccName, strPassword, strDomain, strPurpose);
            ViewData.updateTable();
            ViewData.enableRollBack();
        }
        return result;
    }

    public final String editAccInfo(final AccountDetail oldAccInfo, final String strAccId, final String strPassword, final String strDomain, final String strLink, final String strPurpose) {
        if((oldAccInfo.getStrAccName().equals(strAccId) && oldAccInfo.getStrPassword().equals(strPassword) && oldAccInfo.getStrDomain().equals(strDomain)
                && oldAccInfo.getStrPurpose().equals(strPurpose)) || !this.du.isAccountDetailAvailable(strAccId, strPassword, strDomain, strPurpose)) {
            this.du.unregisterAccountInfo(oldAccInfo.getStrAccName(), oldAccInfo.getStrPassword(), oldAccInfo.getStrDomain(), oldAccInfo.getStrPurpose());
            final AccountDetail oldAccIdCopy = new AccountDetail(oldAccInfo);
            final LocalDateTime ldt = LocalDateTime.now();
            oldAccInfo.updateAccDetail(strAccId, strPassword, strDomain, strLink, strPurpose, ldt);
            try {
                this.dbh.updateDetail(oldAccInfo);
                this.du.registerAccountInfo(strAccId, strPassword, strDomain, strPurpose);
                ViewData.updateTable();
                ViewData.enableRollBack();
                return "Account Detail modified successfully!";
            } catch (Exception e) {
                this.du.registerAccountInfo(oldAccInfo.getStrAccName(), oldAccInfo.getStrPassword(), oldAccInfo.getStrDomain(), oldAccInfo.getStrPurpose());
                oldAccInfo.set(oldAccIdCopy);
                return "Error : Unable to apply modification. Try again after sometime.";
            }
        }
        return "Already Available";
    }

    public final String deleteAccountInfo(final AccountDetail ad) {
        final String result = this.dbh.deleteDetail(ad.getIntUniqueID());
        if(result.startsWith("Deleted")) {
            this.currentAccDetails.remove(ad);
            this.du.addToDeletedAccountDetail(ad);
            this.du.unregisterAccountInfo(ad.getStrAccName(), ad.getStrPassword(), ad.getStrDomain(), ad.getStrPurpose());
            ViewData.updateTable();
            ViewData.enableRollBack();
        }
        return result;
    }

    public final int restoreDeletedInfo() {
        final List<AccountDetail> deletedDetails = this.du.getDeletedAccountsDetails();
        this.currentAccDetails.addAll(deletedDetails);
        this.du.eraseDeletedAccountDetails();
        for(final AccountDetail ad : deletedDetails) {
            this.dbh.addDeletedDetail(ad);
            this.du.registerAccountInfo(ad.getStrAccName(), ad.getStrPassword(), ad.getStrDomain(), ad.getStrPurpose());
        }
        ViewData.updateTable();
        return deletedDetails.size();
    }

    public final void rollBackToOriginalState() {
        this.currentAccDetails.clear();
        this.du.eraseDeletedAccountDetails();
        final List<AccountDetail> ad = this.du.getOriginalAccountDetails();
        this.dbh.deleteAllDataFromTable();
        for(final AccountDetail d : ad) {
            this.dbh.addDeletedDetail(d);
            this.currentAccDetails.add(d);
        }
        ViewData.updateTable();
        ViewData.disableRollBack();
        this.du.recompileRegistration(ad);
    }

    public final String getAppPassword() {
        return this.dbh.getValue("APP PASSWORD");
    }

    public final String getAppPasswordHint() {
        return this.dbh.getValue("APP PASSWORD HINT");
    }

    public final int getViewTableColumnOrderPreference() {
        final String result = this.dbh.getValue("VT COLUMN ORDER");
        try {
            return Integer.parseInt(result);
        } catch (final Exception e) {
            System.err.println("ERROR : Unable to retrieve column order of view table.");
            return 7654321;
        }
    }

    public final boolean getViewTableColumnDefaultPreference() {
        return this.dbh.getValue("VT COL ORDER DEFAULT").equals("YES");
    }

    public final boolean getShowPassword() {
        final String result = this.dbh.getValue("VT SHOW PASSWORD");
        if(result == null) {
            System.err.println("ERROR : Unable to retrieve 'show password by default' property.");
            return false;
        } else if (result.equals("YES")) {
            return true;
        } else if(result.equals("NO")) {
            return false;
        } else {
            System.err.println("ERROR : Unexpected value for 'show password by default' property encountered : " + result);
        }
        return false;
    }

    public final void setAppPassword(final String str) {
        this.dbh.setValue("APP PASSWORD", str);
    }

    public final void setAppPasswordHint(final String str) {
        this.dbh.setValue("APP PASSWORD HINT", str);
    }

    public final void setTableColumnOrder(final int i) {
        this.dbh.setValue("VT COLUMN ORDER", String.valueOf(i));
    }

    public final void setShowPasswordByDefault(final boolean b) {
        this.dbh.setValue("VT SHOW PASSWORD", b ? "YES" : "NO");
    }

    public final void setViewTableColumnDefaultPreference(final boolean b) {
        this.dbh.setValue("VT COL ORDER DEFAULT", b ? "YES" : "NO");
    }

    public final void removeAppPassword() {
        this.dbh.resetAppPassword();
    }

}
