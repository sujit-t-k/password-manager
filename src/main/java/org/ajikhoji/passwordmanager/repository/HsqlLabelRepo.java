package org.ajikhoji.passwordmanager.repository;

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
            final String queryInsertNewRecord =
                    """
                        INSERT INTO Labels (label_id, label_name)
                        VALUES (DEFAULT, ?);
                    """;
            final PreparedStatement psInsert = conn.prepareStatement(queryInsertNewRecord, Statement.RETURN_GENERATED_KEYS);
            psInsert.setString(1, newEntity.getLabelName());

            psInsert.executeUpdate();
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
    public void deleteLabel(LabelEntity entityToDelete) {
        try {
            final String queryDeleteLabelRecordById = "DELETE FROM Labels WHERE label_id = ?;";
            final PreparedStatement psDelete = conn.prepareStatement(queryDeleteLabelRecordById);
            psDelete.setLong(1, entityToDelete.getLabelId());

            final int affectedRowsCount = psDelete.executeUpdate();
            if(affectedRowsCount == 0) {
                throw new DatabaseOperationFailureException(String.format("Deletion of label %s failed as it is not available in database", entityToDelete));
            }
        } catch (Exception e) {
            throw new DatabaseOperationFailureException(e.getMessage());
        }
    }

    @Override
    public List<LabelEntity> getAllLabels() {
        final List<LabelEntity> allLabels = new ArrayList<>();
        try {
            final String queryRetrieveAllAccountInfo = "SELECT * FROM Labels;";
            final PreparedStatement psRetrieve = conn.prepareStatement(queryRetrieveAllAccountInfo);

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

}
