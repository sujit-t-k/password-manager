package com.ajikhoji.passwordmanager;

import java.io.*;
import java.util.ArrayList;

class DataHandler {

    private ArrayList<AccountDetail> details, arrOriginal;
    private static DataHandler dh;
    private int intLastRecordID = 0;

    private DataHandler() {
        this.details = new ArrayList<>();
        this.loadData();
        if(this.arrOriginal == null) {
            this.arrOriginal = new ArrayList<>();
            for(AccountDetail ad : this.details) {
                AccountDetail adnew = new AccountDetail(ad.getAccName(), ad.getAccPwd(), ad.getAccDomain(), ad.getAccPurpose(), ad.getDateCreated(), ad.getDateLastModified());
                adnew.setUniqueID(ad.getUniqueID());
                this.arrOriginal.add(adnew);
            }
        }
    }

    public ArrayList<AccountDetail> getOriginalAccDetails() {
        this.details.clear();
        for(AccountDetail ad : this.arrOriginal) {
            AccountDetail adnew = new AccountDetail(ad.getAccName(), ad.getAccPwd(), ad.getAccDomain(), ad.getAccPurpose(), ad.getDateCreated(), ad.getDateLastModified());
            adnew.setUniqueID(ad.getUniqueID());
            this.details.add(adnew);
        }
        this.saveData();
        return this.details;
    }

    public static DataHandler initialize() {
        if (dh == null) dh = new DataHandler();
        return dh;
    }

    public boolean isAccountDetailAvailable(final AccountDetail ad) {
        for(AccountDetail e : this.details) {
            if(e.containsSameDetail(ad)) return true;
        }
        return false;
    }

    public boolean isAccountDetailAvailable(final String accName,  final String accPwd, final String accDomain, final String accPurpose) {
        for(AccountDetail e : this.details) {
            if(e.getAccName().equals(accName) && e.getAccPwd().equals(accPwd) && e.getAccDomain().equals(accDomain) && e.getAccPurpose().equals(accPurpose)) return true;
        }
        return false;
    }

    public boolean addNewAccountDetail(final AccountDetail ad) {
        if(this.isAccountDetailAvailable(ad)) {
            System.err.println("CANNOT ADD the account detail : " + ad.toString() + "<- REASON : already exists");
            return false;
        }
        ad.setUniqueID(++intLastRecordID);
        this.details.add(ad);
        this.arrOriginal.add(ad);
        return true;
    }
    public ArrayList<AccountDetail> getAllAccountDetails() {
        ArrayList<AccountDetail> req = new ArrayList<>();
        for(AccountDetail ad : this.details) {
            AccountDetail adnew = new AccountDetail(ad.getAccName(), ad.getAccPwd(), ad.getAccDomain(), ad.getAccPurpose(), ad.getDateCreated(), ad.getDateLastModified());
            adnew.setUniqueID(ad.getUniqueID());
            req.add(adnew);
        }
        return req;
    }

    public void updateAccountDetails(final AccountDetail...ad) {
        for(AccountDetail modified : ad) {
            boolean blnIsAccDetailUpdated = false;
            for(AccountDetail existing : this.details) {
                if(existing.getUniqueID() == modified.getUniqueID()) {
                    this.details.set(this.details.indexOf(existing),modified);
                    blnIsAccDetailUpdated = true;
                    break;
                }
            }
            if(!blnIsAccDetailUpdated) System.err.println("AccountDetail with ID " + modified.getUniqueID() + " cannot be updated as it is not present.");
        }
    }

    public void deleteAccountDetail(final int ID) {
        for(AccountDetail ad : this.details) {
            if(ad.getUniqueID() == ID) {
                this.details.remove(ad);
                return;
            }
        }
        System.err.println("The account detail with unique ID " + ID + " cannot be deleted as it does not exist.");
    }

    public void saveData() {
        try {
            FileOutputStream f = new FileOutputStream("donotdelete.rar");
            ObjectOutputStream o = new ObjectOutputStream(f);
            o.writeInt(intLastRecordID);
            o.writeObject(this.details);
            o.close();
            f.close();
        } catch (Exception e) {
            System.err.println("Error while saving data... Either FileNotFound or IO Exception.");
        }
    }

    public void loadData() {
        try {
            File f = new File("donotdelete.rar");
            if (f.exists()) {
                FileInputStream fi = new FileInputStream(f);
                ObjectInputStream oi = new ObjectInputStream(fi);
                this.intLastRecordID = oi.readInt();
                this.details = (ArrayList<AccountDetail>) oi.readObject();
                oi.close();
                fi.close();
            }
        } catch (FileNotFoundException fnfe) {
            System.err.println("Error while retrieving data... The source file Not Found.");
        } catch (IOException ioe) {
            System.err.println("Error while retrieving data... Occurred while attempting to read contents of file.");
            ioe.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("Unexpected ClassNotFoundException");
        }
    }
}
