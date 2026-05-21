package org.ajikhoji.passwordmanager.repository;

import org.ajikhoji.passwordmanager.exception.DatabaseOperationFailureException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class HsqlSettingRepo implements SettingRepo {

    private final Connection conn;

    public HsqlSettingRepo(final Connection c) {
        conn = c;
    }

    @Override
    public void updateHash(String hashedString) {
        if(!containsRecord("HASHED_PASSWORD")) {
            try {
                final String strQuery = "INSERT INTO AppSettings (setting_key, setting_value) VALUES ('HASHED_PASSWORD', ?)";
                final PreparedStatement psInsert = conn.prepareStatement(strQuery);
                psInsert.setString(1, hashedString);
                psInsert.executeUpdate();
            } catch (Exception e) {
                throw new DatabaseOperationFailureException(e.getMessage());
            }
        }
        try {
            final String strQuery = "UPDATE AppSettings SET setting_value = ? WHERE setting_key = 'HASHED_PASSWORD'";
            final PreparedStatement psInsert = conn.prepareStatement(strQuery);
            psInsert.setString(1, hashedString);
            psInsert.executeUpdate();
        } catch (Exception e) {
            throw new DatabaseOperationFailureException(e.getMessage());
        }
    }

    @Override
    public String getHashedValue() {
        try {
            final String queryRetrieve = "SELECT setting_value FROM AppSettings WHERE setting_key = 'HASHED_PASSWORD'";
            final PreparedStatement ps = conn.prepareStatement(queryRetrieve);

            final ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return rs.getString("setting_value");
            } else {
                throw new DatabaseOperationFailureException("No record with name HASHED_PASSWORD found in AppSettings relation");
            }
        } catch (Exception e) {
            throw new DatabaseOperationFailureException(e.getMessage());
        }
    }

    @Override
    public void updateSalt(String saltedstring) {
        if(!containsRecord("SALTING")) {
            try {
                final String strQuery = "INSERT INTO AppSettings (setting_key, setting_value) VALUES ('SALTING', ?)";
                final PreparedStatement psInsert = conn.prepareStatement(strQuery);
                psInsert.setString(1, saltedstring);
                psInsert.executeUpdate();
            } catch (Exception e) {
                throw new DatabaseOperationFailureException(e.getMessage());
            }
            return;
        }
        try {
            final String strQuery = "UPDATE AppSettings SET setting_value = ? WHERE setting_key = 'SALTING'";
            final PreparedStatement psInsert = conn.prepareStatement(strQuery);
            psInsert.setString(1, saltedstring);
            psInsert.executeUpdate();
        } catch (Exception e) {
            throw new DatabaseOperationFailureException(e.getMessage());
        }
    }

    @Override
    public String getSalt() {
        try {
            final String queryRetrieve = "SELECT setting_value FROM AppSettings WHERE setting_key = 'SALTING'";
            final PreparedStatement ps = conn.prepareStatement(queryRetrieve);

            final ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return rs.getString("setting_value");
            } else {
                throw new DatabaseOperationFailureException("No record with name SALTING found in AppSettings relation");
            }
        } catch (Exception e) {
            throw new DatabaseOperationFailureException(e.getMessage());
        }
    }

    @Override
    public void updateUserName(String userName) {
        if(!containsRecord("USER_NAME")) {
            try {
                final String strQuery = "INSERT INTO AppSettings (setting_key, setting_value) VALUES ('USER_NAME', ?)";
                final PreparedStatement psInsert = conn.prepareStatement(strQuery);
                psInsert.setString(1, userName);
                psInsert.executeUpdate();
            } catch (Exception e) {
                throw new DatabaseOperationFailureException(e.getMessage());
            }
        }
        try {
            final String strQuery = "UPDATE AppSettings SET setting_value = ? WHERE setting_key = 'USER_NAME'";
            final PreparedStatement psInsert = conn.prepareStatement(strQuery);
            psInsert.setString(1, userName);
            psInsert.executeUpdate();
        } catch (Exception e) {
            throw new DatabaseOperationFailureException(e.getMessage());
        }
    }

    @Override
    public String getUserName() {
        try {
            final String queryRetrieve = "SELECT setting_value FROM AppSettings WHERE setting_key = 'USER_NAME'";
            final PreparedStatement ps = conn.prepareStatement(queryRetrieve);

            final ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return rs.getString("setting_value");
            } else {
                throw new DatabaseOperationFailureException("No record with name USER_NAME found in AppSettings relation");
            }
        } catch (Exception e) {
            throw new DatabaseOperationFailureException(e.getMessage());
        }
    }

    @Override
    public void updateHint(final String hint) {
        if(!containsRecord("HINT")) {
            try {
                final String strQuery = "INSERT INTO AppSettings (setting_key, setting_value) VALUES ('HINT', ?)";
                final PreparedStatement psInsert = conn.prepareStatement(strQuery);
                psInsert.setString(1, hint);
                psInsert.executeUpdate();
            } catch (Exception e) {
                throw new DatabaseOperationFailureException(e.getMessage());
            }
            return;
        }
        try {
            final String strQuery = "UPDATE AppSettings SET setting_value = ? WHERE setting_key = 'HINT'";
            final PreparedStatement psInsert = conn.prepareStatement(strQuery);
            psInsert.setString(1, hint);
            psInsert.executeUpdate();
        } catch (Exception e) {
            throw new DatabaseOperationFailureException(e.getMessage());
        }
    }

    @Override
    public String getHint() {
        try {
            final String queryRetrieve = "SELECT setting_value FROM AppSettings WHERE setting_key = 'HINT'";
            final PreparedStatement ps = conn.prepareStatement(queryRetrieve);

            final ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return rs.getString("setting_value");
            } else {
                throw new DatabaseOperationFailureException("No record with name HINT found in AppSettings relation");
            }
        } catch (Exception e) {
            throw new DatabaseOperationFailureException(e.getMessage());
        }
    }

    private boolean containsRecord(final String name) {
        try {
            final String queryRetrieve = "SELECT setting_value FROM AppSettings WHERE setting_key = ?";
            final PreparedStatement ps = conn.prepareStatement(queryRetrieve);
            ps.setString(1, name);

            final ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            throw new DatabaseOperationFailureException(e.getMessage());
        }
    }

    @Override
    public boolean isSetupDone() {
        return containsRecord("HASHED_PASSWORD") && containsRecord("SALTING")
                && containsRecord("USER_NAME") && containsRecord("HINT");
    }

}
