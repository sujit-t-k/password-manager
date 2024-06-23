package com.ajikhoji.pwdmgr;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;

public class AccountDetailOld implements Serializable {

    private transient String accName, accPwd, accDomain, accPurpose, oldAccName, oldPwd, oldDomain;
    private transient LocalDateTime dateCreated, dateLastModified;
    private transient int uniqueID = -1;

    public AccountDetailOld(final String name, final String pwd, final String domain, final String purpose, final LocalDateTime dateCreate, final LocalDateTime dateModified) {
        this.accName = name;
        this.accPwd = pwd;
        this.accDomain = domain;
        this.accPurpose = purpose;
        this.dateCreated = dateCreate;
        this.dateLastModified = dateModified;
    }

    public AccountDetailOld(final int ID, final String name, final String pwd, final String domain, final String purpose, final LocalDateTime dateCreate, final LocalDateTime dateModified) {
        this(name, pwd, domain, purpose, dateCreate, dateModified);
        this.uniqueID = ID;
    }

    public AccountDetailOld(final AccountDetailOld accDetailExisting) {
        this.accName = accDetailExisting.getAccName();
        this.accPwd = accDetailExisting.getAccPwd();
        this.accDomain = accDetailExisting.getAccDomain();
        this.accPurpose = accDetailExisting.getAccPurpose();
        this.dateCreated = accDetailExisting.getDateCreated();
        this.dateLastModified = accDetailExisting.getDateLastModified();
        this.uniqueID = accDetailExisting.getUniqueID();
    }

    public void setAccName(final String name) {
        this.accName = name;
    }

    public void setAccPassword(final String pwd) {
        this.accPwd = pwd;
    }

    public void setUniqueID(final int ID) {
        this.uniqueID = ID;
    }

    public int getUniqueID() {
        return this.uniqueID;
    }

    public void setAccDomain(final String domain) {
        this.accDomain = domain;
    }

    public void setAccPurpose(final String purpose) {
        this.accPurpose = purpose;
    }

    public void setDateLastModified(LocalDateTime dateLastModified) {
        this.dateLastModified = dateLastModified;
    }

    public  String getAccName() {
        return this.accName;
    }
    public  String getAccPwd() {
        return this.accPwd;
    }
    public  String getAccDomain() {
        return this.accDomain;
    }
    public  String getAccPurpose() {
        return this.accPurpose;
    }
    public  LocalDateTime getDateCreated() {
        return this.dateCreated;
    }
    public  LocalDateTime getDateLastModified() {
        return this.dateLastModified;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.writeObject(this.accName);
        stream.writeObject(this.accPwd);
        stream.writeObject(this.accDomain);
        stream.writeObject(this.accPurpose);
        stream.writeObject(this.dateCreated);
        stream.writeObject(this.dateLastModified);
        stream.writeObject(this.uniqueID);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        this.accName = (String) stream.readObject();
        this.accPwd = (String) stream.readObject();
        this.accDomain = (String) stream.readObject();
        this.accPurpose = (String) stream.readObject();
        this.dateCreated = (LocalDateTime) stream.readObject();
        this.dateLastModified = (LocalDateTime) stream.readObject();
        this.uniqueID = (int) stream.readObject();
    }

    public boolean containsSameDetail(final AccountDetailOld given) {
        return given.accName.equals(this.accName) && given.accPwd.equals(this.accPwd) && given.accDomain.equals(this.accDomain) && given.accPurpose.equals(this.accPurpose);
    }

    @Override
    public String toString() {
        return "<Class : AccountDetail>\tID : " + this.accName + "\tPassword : " + this.accPwd + "\tDomain : " + this.accDomain
                + "\tPurpose : " + this.accPurpose + "\tDate Created : " + this.dateCreated + "\tDate Modified : " + this.dateLastModified;
    }

}
