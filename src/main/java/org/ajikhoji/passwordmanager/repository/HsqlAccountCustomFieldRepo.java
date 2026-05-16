package org.ajikhoji.passwordmanager.repository;

import org.ajikhoji.passwordmanager.exception.DatabaseOperationFailureException;
import org.ajikhoji.passwordmanager.model.AccountCustomFieldEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class HsqlAccountCustomFieldRepo implements AccountCustomFieldRepo {

    private final Connection conn;

    public HsqlAccountCustomFieldRepo(final Connection c) {
        conn = c;
    }

    @Override
    public void addNewAccountCustomField(AccountCustomFieldEntity newEntity) {
        try {
            final String queryInsertNewRecord =
                    """
                    INSERT INTO AccountCustomFields (account_id, field_key, field_value)
                    VALUES (?, ?, ?);
                """;
            final PreparedStatement psInsert = conn.prepareStatement(queryInsertNewRecord, Statement.RETURN_GENERATED_KEYS);
            psInsert.setLong(1, newEntity.getAccId());
            psInsert.setString(2, newEntity.getFieldName());
            psInsert.setString(3, newEntity.getFieldValue());
            psInsert.executeUpdate();
        } catch (final Exception e) {
            throw new DatabaseOperationFailureException(e.getMessage());
        }
    }

    @Override
    public void batchInsertAccountCustomFields(long accId, List<AccountCustomFieldEntity> entities) {
        final String query =
                """
                INSERT INTO AccountCustomFields (account_id, field_key, field_value)
                VALUES (?, ?, ?)
                """;

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            conn.setAutoCommit(false);
            for (AccountCustomFieldEntity e : entities) {
                ps.setLong(1, accId);
                ps.setString(2, e.getFieldName());
                ps.setString(3, e.getFieldValue());
                ps.addBatch();
            }
            ps.executeBatch();
            conn.commit();
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (Exception rollbackEx) {
                rollbackEx.printStackTrace();
            }
            throw new DatabaseOperationFailureException(e.getMessage());
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (Exception ignored) {}
        }
    }

    @Override
    public void deleteAccountCustomField(AccountCustomFieldEntity entityToDelete) {
        try {
            final String queryDeleteAccountRecordById = "DELETE FROM AccountCustomFields WHERE account_id = ? AND field_key = ?;";
            final PreparedStatement psDelete = conn.prepareStatement(queryDeleteAccountRecordById);
            psDelete.setLong(1, entityToDelete.getAccId());
            psDelete.setString(2, entityToDelete.getFieldName());

            final int affectedRowsCount = psDelete.executeUpdate();
            if (affectedRowsCount == 0) {
                throw new DatabaseOperationFailureException(String.format("Deletion of custom account field %s failed as it is not available in database", entityToDelete));
            }
        }  catch (final Exception e) {
            throw new DatabaseOperationFailureException(e.getMessage());
        }
    }

    @Override
    public void batchDeleteAccountCustomFields(long accId, List<AccountCustomFieldEntity> entities) {
        final String query = """
        DELETE FROM AccountCustomFields
        WHERE account_id = ? AND field_key = ?
    """;

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            conn.setAutoCommit(false);
            int batchSize = 500;
            int count = 0;

            for (AccountCustomFieldEntity e : entities) {
                ps.setLong(1, accId);
                ps.setString(2, e.getFieldName());
                ps.addBatch();
                if (++count % batchSize == 0) {
                    ps.executeBatch();
                }
            }
            ps.executeBatch();
            conn.commit();
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (Exception ignored) {}
            throw new DatabaseOperationFailureException(e.getMessage());
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (Exception ignored) {}
        }
    }

    @Override
    public void updateAccountCustomField(AccountCustomFieldEntity updatedEntity) {
        try {
            final String queryUpdateAccountInfo = """
                UPDATE AccountCustomFields
                SET field_value = ?
                WHERE account_id = ? AND field_key = ?;
            """;
            final PreparedStatement psUpdate = conn.prepareStatement(queryUpdateAccountInfo);
            psUpdate.setString(1, updatedEntity.getFieldValue());
            psUpdate.setLong(2, updatedEntity.getAccId());
            psUpdate.setString(3, updatedEntity.getFieldName());

            final int affectedRowsCount = psUpdate.executeUpdate();
            if (affectedRowsCount == 0) {
                throw new DatabaseOperationFailureException(String.format("Updation of account custom field with id %d failed as it is not available in database", updatedEntity.getAccId()));
            }
        } catch (Exception e) {
            throw new DatabaseOperationFailureException(e.getMessage());
        }
    }

    @Override
    public void batchUpdateAccountCustomFields(long accId, List<AccountCustomFieldEntity> entities) {
        final String query = """
        UPDATE AccountCustomFields
        SET field_value = ?
        WHERE account_id = ? AND field_key = ?
    """;

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            conn.setAutoCommit(false);
            int batchSize = 500;
            int count = 0;

            for (AccountCustomFieldEntity e : entities) {
                ps.setString(1, e.getFieldValue());
                ps.setLong(2, accId);
                ps.setString(3, e.getFieldName());
                ps.addBatch();

                if (++count % batchSize == 0) {
                    ps.executeBatch();
                }
            }

            ps.executeBatch();
            conn.commit();
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (Exception ignored) {}
            throw new DatabaseOperationFailureException(e.getMessage());
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (Exception ignored) {}
        }
    }

    @Override
    public List<AccountCustomFieldEntity> getAccountCustomFieldsForAccountId(long accId) {
        final List<AccountCustomFieldEntity> requiredCustomFields = new ArrayList<>();
        try {
            final String queryRetrieveAllAccountInfo = "SELECT * FROM AccountCustomFields WHERE account_id = ?;";
            final PreparedStatement psRetrieve = conn.prepareStatement(queryRetrieveAllAccountInfo);
            psRetrieve.setLong(1, accId);

            final ResultSet rs = psRetrieve.executeQuery();
            while(rs.next()) {
                final String fieldName = rs.getString("field_key");
                final String fieldValue = rs.getString("field_value");
                requiredCustomFields.add(new AccountCustomFieldEntity(accId, fieldName, fieldValue));
            }
        } catch (Exception e) {
            throw new DatabaseOperationFailureException(e.getMessage());
        }

        return requiredCustomFields;
    }

}
