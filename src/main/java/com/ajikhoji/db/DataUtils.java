package com.ajikhoji.db;

import java.util.*;

public class DataUtils {

    private final Map<String, Map<String, Map<String, Set<String>>>> h;
    private final List<AccountDetail> deletedAccounts, originalAccountDetails;
    private static DataUtils du;

    //To check if an account detail already exists in the database (primarily it will be used when a new account detail has to be added).
    public final boolean isAccountDetailAvailable(final String strAccName, final String strAccPassword, final String strDomain, final String strPurpose) {
        return (this.h.containsKey(strAccName) && this.h.get(strAccName).containsKey(strAccPassword) && this.h.get(strAccName).get(strAccPassword).containsKey(strDomain)
                && this.h.get(strAccName).get(strAccPassword).get(strDomain).contains(strPurpose));
    }

    private DataUtils() {
        this.h = new HashMap<>();
        this.deletedAccounts = new ArrayList<>();
        this.originalAccountDetails = new ArrayList<>();
    }

    //3 use-cases : Called to update when new account detail is to be added or deleted account to be restored or modifying an existing account detail to the database, so that duplicate creation of 'this' account detail can be avoided.
    public final void registerAccountInfo(final String strAccName, final String strAccPassword, final String strDomain, final String strPurpose) {
        final var mp = this.h.computeIfAbsent(strAccName, e -> new HashMap<>()).computeIfAbsent(strAccPassword, e -> new HashMap<>());
        final var st = mp.getOrDefault(strDomain, new HashSet<>());
        st.add(strPurpose);
        mp.put(strDomain, st);
    }

    //2 use-cases : Called to update when existing account detail is to be deleted from the database or an account detail is modified, so that new account detail with 'this' same details will be allowed to be created.
    public final void unregisterAccountInfo(final String strAccName, final String strAccPassword, final String strDomain, final String strPurpose) {
        final var mp = this.h.computeIfAbsent(strAccName, e -> new HashMap<>()).computeIfAbsent(strAccPassword, e -> new HashMap<>());
        final var st = mp.getOrDefault(strDomain, new HashSet<>());
        if(st.isEmpty() || st.size() == 1) {
            mp.remove(strDomain);
            if(this.h.get(strAccName).get(strAccPassword).isEmpty()) {
                this.h.get(strAccName).remove(strAccPassword);
                if(this.h.get(strAccName).isEmpty()) {
                    this.h.remove(strAccName);
                }
            }
        } else {
            st.remove(strPurpose);
        }
    }

    public final void addToDeletedAccountDetail(final AccountDetail ad) {
        this.deletedAccounts.add(ad);
    }

    //To store all account details when the app opens (new session starts).
    public final void addToOriginalAccountDetails(final List<AccountDetail> ad) {
        for(final AccountDetail detail : ad) {
            this.originalAccountDetails.add(new AccountDetail(detail));
            this.registerAccountInfo(detail.getStrAccName(),detail.getStrPassword(), detail.getStrDomain(), detail.getStrPurpose());
        }
    }

    public final void addToOriginalAccountDetails(final AccountDetail ad) {
        this.originalAccountDetails.add(new AccountDetail(ad));
        this.registerAccountInfo(ad.getStrAccName(), ad.getStrPassword(), ad.getStrDomain(), ad.getStrPurpose());
    }

    public final List<AccountDetail> getOriginalAccountDetails() {
        final List<AccountDetail> ad = new ArrayList<>();
        for(final AccountDetail a : this.originalAccountDetails) {
            ad.add(new AccountDetail(a));
        }
        return ad;
    }

    //Called when user clicks on 'Recover' button to retrieve all deleted account details of current session.
    public final List<AccountDetail> getDeletedAccountsDetails() {
        final List<AccountDetail> ad = new ArrayList<>();
        for(final AccountDetail a : this.deletedAccounts) {
            ad.add(new AccountDetail(a));
        }
        return ad;
    }

    //1 use-case : Called after all deleted account details are added into the current account detail list.
    public final void eraseDeletedAccountDetails() {
        this.deletedAccounts.clear();
    }

    //2 use-case : Called when all deleted account details are to be restored or 'original account details' to be restored.
    public final void recompileRegistration(final List<AccountDetail> ad) {
        this.h.clear();
        for(final AccountDetail a : ad) {
            this.registerAccountInfo(a.getStrAccName(), a.getStrPassword(), a.getStrDomain(), a.getStrPurpose());
        }
    }

    public static DataUtils getInstance() {
        if(du == null) {
            du = new DataUtils();
        }
        return du;
    }
}
