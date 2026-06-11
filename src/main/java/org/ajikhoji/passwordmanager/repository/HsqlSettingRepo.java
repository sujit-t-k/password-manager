package org.ajikhoji.passwordmanager.repository;

import org.ajikhoji.passwordmanager.config.DbConfig;
import org.ajikhoji.passwordmanager.exception.DatabaseOperationFailureException;
import org.ajikhoji.passwordmanager.model.AccountCustomFieldEntity;
import org.ajikhoji.passwordmanager.model.AccountEntity;
import org.ajikhoji.passwordmanager.security.EncryptionService;
import org.ajikhoji.passwordmanager.util.Utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

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
            return;
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
            return;
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

    @Override
    public void changePassword(final EncryptionService oldService, final EncryptionService newService, final String hash, final String salt) {
        try {
            final boolean isAutoCommitEnabled = conn.getAutoCommit();
            try {
                conn.setAutoCommit(false);

                int batchSize = 500;
                int count = 0;

                //retrieve and update password of all account credential
                final List<AccountEntity> allAccounts = DbConfig.getAccountService().getAllAccountCredential();
                final String queryUpdateAccountPassword = """
                    UPDATE Accounts
                    SET password = ?
                    WHERE account_id = ?;
                """;
                final PreparedStatement psAccountPassword = conn.prepareStatement(queryUpdateAccountPassword);
                for (final AccountEntity e : allAccounts) {
                    final String plainPassword = oldService.decrypt(e.getAccPassword());
                    final String newEncryptedPassword = newService.encrypt(plainPassword);

                    psAccountPassword.setString(1, newEncryptedPassword);
                    psAccountPassword.setLong(2, e.getAccId());
                    psAccountPassword.addBatch();

                    if (++count % batchSize == 0) {
                        psAccountPassword.executeBatch();
                    }
                }
                psAccountPassword.executeBatch();

                //retrieve and update all account custom field values
                final List<AccountCustomFieldEntity> allCustomFieldEntities = new ArrayList<>();
                final String queryRetrieveAllAccountInfo = "SELECT * FROM AccountCustomFields;";
                final PreparedStatement psRetrieve = conn.prepareStatement(queryRetrieveAllAccountInfo);

                final ResultSet rs = psRetrieve.executeQuery();
                while (rs.next()) {
                    final long accId = rs.getLong("account_id");
                    final String fieldName = rs.getString("field_key");
                    final String fieldValue = rs.getString("field_value");
                    allCustomFieldEntities.add(new AccountCustomFieldEntity(accId, fieldName, fieldValue));
                }

                final String queryUpdateAccountCustomFields = """
                    UPDATE AccountCustomFields
                    SET field_value = ?
                    WHERE account_id = ? AND field_key = ?
                """;
                final PreparedStatement psUpdateAccountCustomFields = conn.prepareStatement(queryUpdateAccountCustomFields);

                count = 0;
                for (AccountCustomFieldEntity e : allCustomFieldEntities) {
                    final String plainFieldValue = oldService.decrypt(e.getFieldValue());
                    final String newEncryptedFieldValue = newService.encrypt(plainFieldValue);

                    psUpdateAccountCustomFields.setString(1, newEncryptedFieldValue);
                    psUpdateAccountCustomFields.setLong(2, e.getAccId());
                    psUpdateAccountCustomFields.setString(3, e.getFieldName());
                    psUpdateAccountCustomFields.addBatch();

                    if (++count % batchSize == 0) {
                        psUpdateAccountCustomFields.executeBatch();
                    }
                }
                psUpdateAccountCustomFields.executeBatch();

                //update salt and hash
                updateHash(hash);
                updateSalt(salt);

                conn.commit();
            } catch (final Exception e) {
                try {
                    conn.rollback();
                } catch (final Exception ex) {
                    throw new DatabaseOperationFailureException("Failed to change master password");
                }
                throw new DatabaseOperationFailureException("Failed to change master password");
            } finally {
                conn.setAutoCommit(isAutoCommitEnabled);
            }
        } catch (final Exception e) {
            throw new DatabaseOperationFailureException("Failed to change master password");
        }
    }

    @Override
    public void saveTableFieldsOrderPreference(long preferenceOrder) {
        if(!containsRecord("TABLE_VIEW_FIELD_ORDER_PREFERENCE")) {
            try {
                final String strQuery = "INSERT INTO AppSettings (setting_key, setting_value) VALUES ('TABLE_VIEW_FIELD_ORDER_PREFERENCE', ?)";
                final PreparedStatement psInsert = conn.prepareStatement(strQuery);
                psInsert.setString(1, String.valueOf(preferenceOrder));
                psInsert.executeUpdate();
            } catch (Exception e) {
                throw new DatabaseOperationFailureException(e.getMessage());
            }
            return;
        }
        try {
            final String strQuery = "UPDATE AppSettings SET setting_value = ? WHERE setting_key = 'TABLE_VIEW_FIELD_ORDER_PREFERENCE'";
            final PreparedStatement psInsert = conn.prepareStatement(strQuery);
            psInsert.setString(1, String.valueOf(preferenceOrder));
            psInsert.executeUpdate();
        } catch (Exception e) {
            throw new DatabaseOperationFailureException(e.getMessage());
        }
    }

    @Override
    public long getTableFieldsOrder() {
        try {
            final String queryRetrieve = "SELECT setting_value FROM AppSettings WHERE setting_key = 'TABLE_VIEW_FIELD_ORDER_PREFERENCE'";
            final PreparedStatement ps = conn.prepareStatement(queryRetrieve);

            final ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                final String preferenceValue = rs.getString("setting_value");
                return Long.parseLong(preferenceValue);
            } else {
                throw new DatabaseOperationFailureException("No record with name TABLE_VIEW_FIELD_ORDER_PREFERENCE found in AppSettings relation");
            }
        } catch (Exception e) {
            throw new DatabaseOperationFailureException(e.getMessage());
        }
    }

    @Override
    public void saveOpenLinkAction(final LinkActionOption option) {
        if(!containsRecord("OPEN_LINK_ACTION")) {
            try {
                final String strQuery = "INSERT INTO AppSettings (setting_key, setting_value) VALUES ('OPEN_LINK_ACTION', ?)";
                final PreparedStatement psInsert = conn.prepareStatement(strQuery);
                psInsert.setString(1, option.name());
                psInsert.executeUpdate();
            } catch (Exception e) {
                throw new DatabaseOperationFailureException(e.getMessage());
            }
            return;
        }
        try {
            final String strQuery = "UPDATE AppSettings SET setting_value = ? WHERE setting_key = 'OPEN_LINK_ACTION'";
            final PreparedStatement psInsert = conn.prepareStatement(strQuery);
            psInsert.setString(1, option.name());
            psInsert.executeUpdate();
        } catch (Exception e) {
            throw new DatabaseOperationFailureException(e.getMessage());
        }
    }

    @Override
    public LinkActionOption getOpenLinkActionPreference() {
        try {
            final String queryRetrieve = "SELECT setting_value FROM AppSettings WHERE setting_key = 'OPEN_LINK_ACTION'";
            final PreparedStatement ps = conn.prepareStatement(queryRetrieve);

            final ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return OpenLinkButtonActionCustomizable.LinkActionOption.valueOf(rs.getString("setting_value"));
            } else {
                throw new DatabaseOperationFailureException("No record with name OPEN_LINK_ACTION found in AppSettings relation");
            }
        } catch (Exception e) {
            throw new DatabaseOperationFailureException(e.getMessage());
        }
    }

    @Override
    public void clearAccountCredentialData() {
        try {
            final String strTruncateAccountInfo = "DELETE FROM Accounts;";
            final PreparedStatement ps = conn.prepareStatement(strTruncateAccountInfo);
            ps.execute();
        } catch (final Exception e) {
            throw new DatabaseOperationFailureException(e.getMessage());
        }
    }

}
