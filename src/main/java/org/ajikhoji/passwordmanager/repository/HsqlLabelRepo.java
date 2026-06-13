package org.ajikhoji.passwordmanager.repository;

import org.ajikhoji.passwordmanager.dto.LabelUsage;
import org.ajikhoji.passwordmanager.exception.DatabaseOperationFailureException;
import org.ajikhoji.passwordmanager.model.LabelEntity;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class HsqlLabelRepo implements LabelRepo {

    private final Connection conn;

    public HsqlLabelRepo(final Connection conn) {
        this.conn = conn;
    }

    @Override
    public void addNewLabel(LabelEntity newEntity) {
        try {
            final String queryInsertNewLabel =
                    """
                        INSERT INTO Labels (label_id, label_name)
                        VALUES (DEFAULT, ?);
                    """;
            final PreparedStatement psInsert = conn.prepareStatement(queryInsertNewLabel, Statement.RETURN_GENERATED_KEYS);
            psInsert.setString(1, newEntity.getLabelName());

            psInsert.execute();
            final ResultSet rs = psInsert.getGeneratedKeys();
            if(rs.next()) {
                final int id = rs.getInt(1);
                newEntity.setLabelId(id);
            } else {
                throw new DatabaseOperationFailureException("Unable to retrieve label id of newly inserted record");
            }
        } catch (Exception e) {
            throw new DatabaseOperationFailureException(e.getMessage());
        }
    }

    @Override
    public void updateLabel(LabelEntity labelEntity) {
        try {
            final String queryUpdateLabel =
                    """
                        UPDATE Labels
                        SET label_name = ?
                        WHERE label_id = ?;
                    """;
            final PreparedStatement psUpdate = conn.prepareStatement(queryUpdateLabel);
            psUpdate.setString(1, labelEntity.getLabelName());
            psUpdate.setLong(2, labelEntity.getLabelId());
            psUpdate.executeUpdate();
        } catch (Exception e) {
            throw new DatabaseOperationFailureException(e.getMessage());
        }
    }

    @Override
    public void deleteLabel(final LabelEntity entityToDelete, final LabelEntity replacementLabel) {
        try {
            final boolean isAutoCommitEnabled = conn.getAutoCommit();
            try {
                if(replacementLabel != null && !replacementLabel.equals(LabelEntity.NULL_LABEL) && !replacementLabel.getLabelName().equals(LabelEntity.DEFAULT_LABEL_NAME)) {
                    final String queryReplaceLabelIdInAccounts =
                        """
                            UPDATE Accounts
                            SET label_id = ?
                            WHERE label_id = ?;
                        """;
                    final PreparedStatement psUpdate = conn.prepareStatement(queryReplaceLabelIdInAccounts);
                    psUpdate.setLong(1, replacementLabel.getLabelId());
                    psUpdate.setLong(2, entityToDelete.getLabelId());
                    psUpdate.executeUpdate();
                }

                final String queryDeleteLabelRecordById = "DELETE FROM Labels WHERE label_id = ?;";
                final PreparedStatement psDelete = conn.prepareStatement(queryDeleteLabelRecordById);
                psDelete.setLong(1, entityToDelete.getLabelId());

                final int rowsAffected = psDelete.executeUpdate();
                if (rowsAffected == 0) {
                    throw new DatabaseOperationFailureException(String.format("Deletion of label %s failed as it is not available in database", entityToDelete));
                }
            } catch (final Exception e) {
                conn.rollback();
                throw new DatabaseOperationFailureException(e.getMessage());
            } finally {
                conn.setAutoCommit(isAutoCommitEnabled);
            }
        } catch (final Exception e) {
            throw new DatabaseOperationFailureException(e.getMessage());
        }
    }

    @Override
    public List<LabelEntity> getAllLabels() {
        final List<LabelEntity> allLabels = new ArrayList<>();
        try {
            final String queryRetrieveAllLabels = "SELECT * FROM Labels;";
            final PreparedStatement psRetrieve = conn.prepareStatement(queryRetrieveAllLabels);

            final ResultSet rs = psRetrieve.executeQuery();
            while(rs.next()) {
                final long labelId = rs.getLong("label_id");
                final String labelName = rs.getString("label_name");
                allLabels.add(new LabelEntity(labelId, labelName));
            }
        } catch (Exception e) {
            throw new DatabaseOperationFailureException(e.getMessage());
        }

        return allLabels;
    }

    @Override
    public List<LabelEntity> getUsedLabels() {
        final List<LabelEntity> usedLabels = new ArrayList<>();
        try {
            final String queryRetrieveUsedLabels = """            
                SELECT DISTINCT l.label_id, l.label_name
                FROM Labels l
                JOIN Accounts a
                ON a.label_id = l.label_id
                ORDER BY l.label_name
            """;
            final PreparedStatement psRetrieve = conn.prepareStatement(queryRetrieveUsedLabels);

            final ResultSet rs = psRetrieve.executeQuery();
            while(rs.next()) {
                final long labelId = rs.getLong("label_id");
                final String labelName = rs.getString("label_name");
                usedLabels.add(new LabelEntity(labelId, labelName));
            }
        } catch (Exception e) {
            throw new DatabaseOperationFailureException(e.getMessage());
        }

        return usedLabels;
    }

    @Override
    public List<LabelUsage> getLabelUsageStatistics() {
        final List<LabelUsage> allLabelStats = new ArrayList<>();
        try {
            final String queryRetrieveAllAccountInfo = """            
                SELECT
                    l.label_id,
                    l.label_name,
                    COUNT(a.account_id) AS usage_count
                FROM Labels l
                LEFT JOIN Accounts a
                    ON a.label_id = l.label_id
                GROUP BY
                    l.label_id,
                    l.label_name
                ORDER BY
                    usage_count DESC;
            """;
            final PreparedStatement psRetrieve = conn.prepareStatement(queryRetrieveAllAccountInfo);

            final ResultSet rs = psRetrieve.executeQuery();
            while(rs.next()) {
                final long labelId = rs.getLong("label_id");
                final String labelName = rs.getString("label_name");
                final int usageCount = rs.getInt("usage_count");
                allLabelStats.add(new LabelUsage(labelId, labelName, usageCount));
            }
        } catch (Exception e) {
            throw new DatabaseOperationFailureException(e.getMessage());
        }

        return allLabelStats;
    }

}
