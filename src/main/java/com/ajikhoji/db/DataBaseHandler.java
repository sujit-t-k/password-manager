package com.ajikhoji.db;

import org.hsqldb.server.Server;
import org.hsqldb.jdbc.JDBCDatabaseMetaData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataBaseHandler {

    private final Server server = new Server();
    private Connection con = null;
    private final String strDatabaseName = "donotopen";
    private final String strUserName = "SA";//"ajikhoji";
    private final String strPasswd = "";//"ajikhoji@NA17AjiKhojiThaan";
    private final String strTableName = "pwdMgrAccInfo", strPreferencesTableName = "tblAppSettings";
    private final String strDatabasePath = System.getenv("LOCALAPPDATA") + "//ajk//pwd//mgr//data//";
    private List<AccountDetail> queriedDetails = new ArrayList<>();
    private int queriedID = -1;

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
                        final ResultSet result = stmt.executeQuery(strSQL);
                        if(strSQL.contains(this.strPreferencesTableName)) {
                            strResult = "not found";
                            while (result.next()) {
                                strResult = result.getString("val");
                            }
                            return strResult;
                        } else if(strSQL.contains("WHERE") && strSQL.contains("AND")) {
                            while (result.next()) {
                                this.queriedID = result.getInt("id");
                            }
                            strResult = "Presence of data checked successfully";
                        } else {
                            this.queriedDetails.clear();
                            while (result.next()) {
                                this.queriedDetails.add(new AccountDetail(result.getInt("id"),
                                        result.getString("accName"),
                                        result.getString("accPassword"),
                                        result.getString("accDomain"),
                                        result.getString("accLink"),
                                        result.getString("accPurpose"),
                                        LocalDateTime.parse(result.getString("dateAdded").substring(0, 19), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                                        result.getString("dateLastModified") == null ? null : LocalDateTime.parse(result.getString("dateLastModified").substring(0, 19), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
                            }
                            strResult = "Read table successfully";
                        }
                    } catch (SQLException ex) {
                        System.err.println("Queried detail not found, or mentioned table not available, or access is denied.");
                        //Logger.getLogger(DataBaseHandler.class.getName()).log(Level.SEVERE, null, ex);
                        return "Error : Unable to read data from table";
                    }
                } else if (strOperation.equals("IsPreferencesTableExist")) {
                    strResult = "not exist";
                    try {
                        stmt = con.createStatement();
                        // System.out.println("select TABLE_NAME from INFORMATION_SCHEMA.SYSTEM_COLUMNS Where TABLE_NAME = '" + strTableName.toUpperCase() + "';");
                        ResultSet result = stmt.executeQuery("select TABLE_NAME from INFORMATION_SCHEMA.SYSTEM_COLUMNS Where TABLE_NAME = '" + strPreferencesTableName.toUpperCase() + "';");
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
                    con.commit();
                    switch (strOperation) {
                        case "INSERT" -> {
                            strResult = "Added record successfully : " + result;
                        }
                        case "UPDATE" -> {
                            strResult = "Updated record successfully : " + result;
                        }
                        case "DELETE" -> {
                            strResult = "Deleted record successfully : " + result;
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
                        default -> strResult = "Operation Failed";
                    }
                }
                stmt.close();
            } else {
                strResult = "Error in starting server";
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseHandler.class.getName()).log(Level.SEVERE, null, ex);
            return "Error in performing data computation.";
        }
        return strResult;
    }

    public final void initialize() {
        this.dbase("CREATE TABLE IF NOT EXISTS " + this.strTableName + " (id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, accName VARCHAR(60) NOT NULL, "
                + "accPassword VARCHAR(60) NOT NULL, accDomain VARCHAR(60) NOT NULL, accLink VARCHAR(300), accPurpose VARCHAR(60) NOT NULL, dateAdded TIMESTAMP NOT NULL, dateLastModified TIMESTAMP);");
        if(this.dbase("IsPreferencesTableExist ").equals("not exist")) {
            this.dbase("CREATE TABLE " + this.strPreferencesTableName + " (id VARCHAR(20) NOT NULL PRIMARY KEY, val VARCHAR(60));");
            this.dbase("INSERT INTO " + this.strPreferencesTableName + " (id, val) VALUES('APP PASSWORD', NULL), ('APP PASSWORD HINT', NULL), ('VT COLUMN ORDER', '7654321'), ('VT SHOW PASSWORD', 'YES'), ('VT COL ORDER DEFAULT','YES');");
        }
    }

    public final String getValue(final String key) {
        return this.dbase("SELECT val FROM " + this.strPreferencesTableName + " WHERE id = '" + key + "';");
    }

    public final void setValue(final String key, final String val) {
        this.dbase("UPDATE " + this.strPreferencesTableName + " SET val = '" + val + "' WHERE id = '" + key + "';");
    }

    public final void resetAppPassword() {
        this.dbase("UPDATE " + this.strPreferencesTableName + " SET val = NULL WHERE id = 'APP PASSWORD' OR id = 'APP PASSWORD HINT';");
    }

    public final String addNewDetail(final String strAccID, final String strAccPwd, final String strDomain, final String strLink, final String strPurpose, final LocalDateTime ldt) {
        return this.dbase("INSERT INTO " + this.strTableName + " VALUES(DEFAULT, '" + strAccID + "','" + strAccPwd +
                "','" + strDomain + "','" + strLink + "','" + strPurpose + "', '" + (ldt.toString().replace("T"," ")) + "', NULL);");
    }

    public final int getUniqueID(final String strAccID, final String strPassword, final String strDomain, final String strPurpose) {
         this.dbase("SELECT id FROM " + this.strTableName + " WHERE accName = '" + strAccID + "' AND accPassword = '" + strPassword + "' AND accDomain = '" + strDomain + "' AND accPurpose = '" + strPurpose + "';");
        return this.queriedID;
    }

    public final void addDeletedDetail(final AccountDetail ad) {
        this.dbase("INSERT INTO " + this.strTableName + " VALUES(" + ad.getIntUniqueID() +", '" + ad.getStrAccName()
                + "','" + ad.getStrPassword() + "','" + ad.getStrDomain() + "','" + ad.getStrLink() + "','"
                + ad.getStrPurpose() + "', '" + (ad.getLdtDateCreated().toString().replace("T"," "))
                + "', "+ (ad.getLdtDateModified() == null ? "NULL" : ("'" + (ad.getLdtDateModified().toString().replace("T"," ")) + "'")) + ");");
    }

    public final void deleteAllDataFromTable() {
        this.dbase("TRUNCATE TABLE " + this.strTableName + ";");
    }

//    public final boolean isAccountDetailAvailable(final String accID, final String accPwd, final String accDomain, final String accPurpose) {
//        final String command = this.dbase("SELECT * FROM " + this.strTableName + " WHERE accName = '" + accID + "' AND accPassword = '" + accPwd
//                + "' AND accDomain = '" + accDomain + "' AND accPurpose = '" + accPurpose + "';");
//        return this.blnAccountDetailAlreadyAvailable;
//    }

//    public final void restoreOriginalData() {
//        final String command = this.dbase("DROP TABLE " + this.strTableName + ";");
//        String a = this.dbase("CREATE TABLE IF NOT EXISTS " + this.strTableName + " (id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, accName VARCHAR(120) NOT NULL, "
//                + "accPassword VARCHAR(120) NOT NULL, accDomain VARCHAR(120) NOT NULL,  accPurpose VARCHAR(120) NOT NULL, dateAdded TIMESTAMP NOT NULL, dateLastModified TIMESTAMP);");
//        for(AccountDetail ad : this.originalDetails) {
//            final String commandInsert = this.dbase("INSERT INTO " + this.strTableName + " VALUES(DEFAULT, '" + ad.getAccName() + "','" + ad.getAccPwd() +
//                    "','" + ad.getAccDomain() + "','" + ad.getAccPurpose() + "', '" + ad.getDateCreated().toString().replace("T"," ") + "', NULL);");
//        }
//    }

    public final String deleteDetail(final int ID) {
        return this.dbase("DELETE FROM " + this.strTableName + " where id = " + ID + ";");
    }

    public final void updateDetail(final AccountDetail d) {
        this.dbase("UPDATE " + this.strTableName + " SET accName = '" + d.getStrAccName() + "', accPassword = '" + d.getStrPassword() + "', accDomain = '" + d.getStrDomain() + "', accLink = '" + d.getStrLink() + "', accPurpose = '" + d.getStrPurpose() + "', dateLastModified = NOW() where id = " + d.getIntUniqueID());
    }

    public final ArrayList<AccountDetail> getAllDetails() {
        this.dbase("SELECT * FROM " + this.strTableName + ";");
        return new ArrayList<>(this.queriedDetails);
    }

    public final void closeServer() {
        this.server.shutdown();
    }

}

