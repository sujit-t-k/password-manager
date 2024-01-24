package com.ajikhoji.passwordmanager;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hsqldb.Server;
import org.hsqldb.jdbc.JDBCDatabaseMetaData;

public class DataBaseHandler {

    private final Server server = new Server();
    private Connection con = null;
    private final String strDatabaseName = "donotopen";
    private final String strUserName = "SA";//"ajikhoji";
    private final String strPasswd = "";//"ajikhoji@NA17AjiKhojiThaan";
    private final String strTableName = "pwdMgrAccInfo";
    private final String strDatabasePath = this.getCurrentDirectory() + "//data//";
    private boolean blnAccountDetailAlreadyAvailable = false;

    private final ArrayList<AccountDetail> originalDetails = new ArrayList<>(), currQueryDetails = new ArrayList<>();
    private boolean startServer() {
        if (server.isNotRunning()) {
            try {
                server.setSilent(true);
                server.setLogWriter(null);
                server.setDatabaseName(0, strDatabaseName);
                server.setDatabasePath(0, strDatabasePath);
                server.setPort(9001);
                server.start();
            } catch (Exception e) {
                e.printStackTrace(System.out);
                return false;
            }
        }
        return true;
    }

    private String getCurrentDirectory() {
        String str = "";
        try {
            str = new File(".").getCanonicalPath();
        } catch (IOException ex) {
            Logger.getLogger(DataBaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return str;
    }

    private boolean connectDatabase() {
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
            con = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/" + strDatabaseName + ";if exists = true", strUserName, strPasswd); //Creating the connection with HSQLDB
            if (con != null) {
                //System.out.println("Connection created successfully");
            } else {
                //System.out.println("Problem with creating connection");
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace(System.out);
            return false;
        }
        return true;
    }

    private String dbase(String strSQL) {
        String strResult = "";
        try {
            if (startServer()) {
                try {
                    if (con.isClosed()) {
                        if (!connectDatabase()) {
                            return "Error in establishing connection with database";
                        }
                    }
                } catch (java.lang.NullPointerException e) {
                    if (!connectDatabase()) {
                        return "Error in establishing connection with database";
                    }
                }
                String strOperation = strSQL.substring(0, strSQL.indexOf(" "));
                //System.out.println(strSQL);
                java.sql.Statement stmt = con.createStatement();
                if (strOperation.equals("SELECT")) {
                    try {
                        ResultSet result = stmt.executeQuery(strSQL);
                        if(strSQL.contains("WHERE") && strSQL.contains("AND")) {
                            this.blnAccountDetailAlreadyAvailable = result.next();
                            strResult = "Presence of data checked successfully";
                        } else {
                            this.currQueryDetails.clear();
                            while (result.next()) {
                                this.currQueryDetails.add(new AccountDetail(result.getInt("id"),
                                        result.getString("accName"),
                                        result.getString("accPassword"),
                                        result.getString("accDomain"),
                                        result.getString("accPurpose"),
                                        LocalDateTime.parse(result.getString("dateAdded").substring(0, 19), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                                        result.getString("dateLastModified") == null ? null : LocalDateTime.parse(result.getString("dateLastModified").substring(0, 19), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
                                /*System.out.println(result.getInt("id") + " | "
                                        + result.getString("accName") + " | "
                                        + result.getString("accPassword") + " | "
                                        + result.getString("accDomain") + " | "
                                        + result.getString("accPurpose") + " | "
                                        + result.getString("dateAdded") + " | "
                                        + result.getString("dateLastModified"));*/
                            }
                            strResult = "Read table successfully";
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(DataBaseHandler.class.getName()).log(Level.SEVERE, null,
                                ex);
                    }
                } else if (strOperation.equals("IsTableExist")) {
                    strResult = "not exist";
                    try {
                        stmt = con.createStatement();
                        // System.out.println("select TABLE_NAME from INFORMATION_SCHEMA.SYSTEM_COLUMNS Where TABLE_NAME = '" + strTableName.toUpperCase() + "';");
                        ResultSet result = stmt.executeQuery("select TABLE_NAME from INFORMATION_SCHEMA.SYSTEM_COLUMNS Where TABLE_NAME = '" + strTableName.toUpperCase() + "';");
                        // result = stmt.executeQuery("Select TABLE_NAME From INFORMATION_SCHEMA.SYSTEM_COLUMNS;");
                        if (result.next()) {
                            stmt.close();
                            strResult = "exist";
                        }
                        stmt.close();
                    } catch (SQLException e) {
                        strResult = "error";
                    }
                } else if (strOperation.equals("ListAllTables")) {
                    try {
                        JDBCDatabaseMetaData m = (JDBCDatabaseMetaData) con.getMetaData();
                        String table[] = {"TABLE"};
                        ResultSet rs = m.getTables(null, null, null, table);
                        while (rs.next()) {
                            strResult += rs.getString(3) + " ";
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(DataBaseHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    final int result = stmt.executeUpdate(strSQL);
                    switch (strOperation) {
                        case "INSERT" -> {
                            con.commit();
                            strResult = "Add record successfully : " + result;
                        }
                        case "UPDATE" -> {
                            strResult = "Updated record successfully : " + result;
                        }
                        case "DELETE" -> {
                            strResult = "Delete record successfully : " + result;
                        }
                        case "CREATE" -> {
                            strResult = "Table created successfully : " + result;
                        }
                        case "DROP" -> {
                            strResult = "Table dropped successfully : " + result;
                        }
                        case "TRUNCATE" -> {
                            strResult = "Table all record deleted successfully : " + result;
                        }
                        default ->
                                throw new AssertionError();
                    }
                }
                stmt.close();
            } else {
                strResult = "Error in starting server";
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return strResult;
    }

    public void createTables() {
        //System.out.println("called");
        String a = this.dbase("CREATE TABLE IF NOT EXISTS " + this.strTableName + " (id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, accName VARCHAR(120) NOT NULL, "
                + "accPassword VARCHAR(120) NOT NULL, accDomain VARCHAR(120) NOT NULL,  accPurpose VARCHAR(120) NOT NULL, dateAdded TIMESTAMP NOT NULL, dateLastModified TIMESTAMP);");
        //System.out.println(a);
    }

    public final void initialize() {
        String a = this.dbase("CREATE TABLE IF NOT EXISTS " + this.strTableName + " (id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, accName VARCHAR(120) NOT NULL, "
                + "accPassword VARCHAR(120) NOT NULL, accDomain VARCHAR(120) NOT NULL,  accPurpose VARCHAR(120) NOT NULL, dateAdded TIMESTAMP NOT NULL, dateLastModified TIMESTAMP);");
        this.getAllDetails();
        for(AccountDetail d : this.currQueryDetails) this.originalDetails.add(new AccountDetail(d.getUniqueID(), d.getAccName(), d.getAccPwd(), d.getAccDomain(), d.getAccPurpose(), d.getDateCreated(), d.getDateLastModified()));
    }

    public final void addDetail(final String strAccID, final String strAccPwd, final String strDomain, final String strPurpose) {
        final String command = this.dbase("INSERT INTO " + this.strTableName + " VALUES(DEFAULT, '" + strAccID + "','" + strAccPwd +
                "','" + strDomain + "','" + strPurpose + "', '" + LocalDateTime.now().toString().replace("T"," ") + "', NULL);");
        this.originalDetails.add(new AccountDetail(-1, strAccID, strAccPwd, strDomain, strPurpose, LocalDateTime.now(), null));
    }

    public final boolean isAccountDetailAvailable(final String accID, final String accPwd, final String accDomain, final String accPurpose) {
        final String command = this.dbase("SELECT * FROM " + this.strTableName + " WHERE accName = '" + accID + "' AND accPassword = '" + accPwd
                + "' AND accDomain = '" + accDomain + "' AND accPurpose = '" + accPurpose + "';");
        return this.blnAccountDetailAlreadyAvailable;
    }

    public final int updateAccountDetails(final int[] ID, final String[] accName, final String[] accPwd, final String[] accDomain, final String[] accPurpose, final String[] dateLastModified) {
        int intTotalChangesMade = 0;
        for(int i = 0; i < ID.length; i++) {
            boolean insertComma = false;
            final StringBuilder sbCommand = new StringBuilder("UPDATE " + this.strTableName + " SET");
            if(accName[i] != null) {
                sbCommand.append(" accName = '".concat(accName[i]+"'"));
                intTotalChangesMade++;
                insertComma = true;
            }
            if(accPwd[i] != null) {
                if(insertComma) sbCommand.append(", ");
                sbCommand.append(" accPassword = '".concat(accPwd[i]+"'"));
                intTotalChangesMade++;
                insertComma = true;
            }
            if(accDomain[i] != null) {
                if(insertComma) sbCommand.append(", ");
                sbCommand.append(" accDomain = '".concat(accDomain[i]+"'"));
                intTotalChangesMade++;
                insertComma = true;
            }
            if(accPurpose[i] != null) {
                if(insertComma) sbCommand.append(", ");
                intTotalChangesMade++;
                sbCommand.append(" accPurpose = '".concat(accPurpose[i]+"'"));
            }
            sbCommand.append(", dateLastModified = '" + dateLastModified[i] + "' WHERE id = ".concat(ID[i]+";"));
            final String result = this.dbase(sbCommand.toString());
        }
        return intTotalChangesMade;
    }

    public final void updateAccountDetails(final AccountDetail[] ad) {
        for(AccountDetail d :ad) {
            final String command = this.dbase("UPDATE " + this.strTableName + " SET accName = '" + d.getAccName() + "', accPassword = '" + d.getAccPwd() + "', accDomain = '"
                    + d.getAccDomain() + "', accPurpose = '" + d.getAccPurpose() + "', dateLastModified = NOW() where id = " + d.getUniqueID() + " AND ( accName != '" + d.getAccName()
                    + "' OR accPassword != '" + d.getAccPwd() + "' OR accDomain != '" + d.getAccDomain() + "' OR accPurpose != '" + d.getAccPurpose() + "');");
        }
    }

    public final void eraseAllDetails() {
        this.originalDetails.clear();
        this.restoreOriginalData();
    }

    public final void restoreOriginalData() {
        final String command = this.dbase("DROP TABLE " + this.strTableName + ";");
        String a = this.dbase("CREATE TABLE IF NOT EXISTS " + this.strTableName + " (id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, accName VARCHAR(120) NOT NULL, "
                + "accPassword VARCHAR(120) NOT NULL, accDomain VARCHAR(120) NOT NULL,  accPurpose VARCHAR(120) NOT NULL, dateAdded TIMESTAMP NOT NULL, dateLastModified TIMESTAMP);");
        for(AccountDetail ad : this.originalDetails) {
            final String commandInsert = this.dbase("INSERT INTO " + this.strTableName + " VALUES(DEFAULT, '" + ad.getAccName() + "','" + ad.getAccPwd() +
                    "','" + ad.getAccDomain() + "','" + ad.getAccPurpose() + "', '" + ad.getDateCreated().toString().replace("T"," ") + "', NULL);");
        }
    }

    public final void deleteDetail(final int ID) {
        final String command = this.dbase("DELETE FROM " + this.strTableName + " where id = " + ID + ";");
        //System.out.println(command);
    }

    public final void updateDetail(final int id, final String strAccID, final String strAccPwd, final String strDomain, final String strPurpose) {
        final String cmd = this.dbase("UPDATE " + this.strTableName + " SET accName = '" + strAccID + "', accPassword = '" + strAccPwd + "', accDomain = '" + strDomain + "', accPurpose = '" + strPurpose + "', dateLastModified = NOW() where id = " + id);
        //System.out.println(cmd);
    }

    public final ArrayList<AccountDetail> getAllDetails() {
        final String cmd = this.dbase("SELECT * FROM " + this.strTableName + ";");
        ArrayList<AccountDetail> required = new ArrayList<>();
        //System.out.println(">>>>>>>>>>>>>>>>> " + this.currQueryDetails.size());
        for(AccountDetail ad : this.currQueryDetails) required.add(new AccountDetail(ad.getUniqueID(), ad.getAccName(), ad.getAccPwd(), ad.getAccDomain(), ad.getAccPurpose(), ad.getDateCreated(), ad.getDateLastModified()));
        return required;
    }

    public final void closeServer() {
        this.server.shutdown();
    }

}
