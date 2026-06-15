package org.ajikhoji.passwordmanager.repository;

import org.ajikhoji.passwordmanager.exception.DatabaseOperationFailureException;
import org.ajikhoji.passwordmanager.model.AccountEntity;
import org.ajikhoji.passwordmanager.util.Utility;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HsqlAccountRepo implements AccountRepo {

    private final Connection conn;

    public HsqlAccountRepo(final Connection c) {
        conn = c;
    }

    @Override
    public void addNewAccountCredential(final AccountEntity newEntity) {
        try {
            final String queryInsertNewRecord =
                    """
                    INSERT INTO Accounts (account_id, account_name, password, platform, label_id, link, created_at)
                    VALUES (DEFAULT, ?, ?, ?, ?, ?, ?);
                """;
            final PreparedStatement psInsert = conn.prepareStatement(queryInsertNewRecord, Statement.RETURN_GENERATED_KEYS);
            psInsert.setString(1, newEntity.getAccName());
            psInsert.setString(2, newEntity.getAccPassword());
            psInsert.setString(3, newEntity.getPlatform());
            psInsert.setLong(4, newEntity.getLabelId());
            psInsert.setString(5, newEntity.getLink());
            psInsert.setTimestamp(6, Utility.getSqlTimeStampForLocalDateTime(newEntity.getCreatedDateTime()));

            psInsert.executeUpdate();
            final ResultSet rs = psInsert.getGeneratedKeys();
            if (rs.next()) {
                final int id = rs.getInt(1);
                newEntity.setAccId(id);
            } else {
                throw new DatabaseOperationFailureException("Unable to retrieve account id of newly inserted record");
            }
        } catch (final Exception e) {
            throw new DatabaseOperationFailureException(e.getMessage());
        }
    }

    @Override
    public void deleteAccountCredential(final AccountEntity entityToDelete) {
        try {
            final String queryDeleteAccountRecordById = "DELETE FROM Accounts WHERE account_id = ?;";
            final PreparedStatement psDelete = conn.prepareStatement(queryDeleteAccountRecordById);
            psDelete.setLong(1, entityToDelete.getAccId());

            final int affectedRowsCount = psDelete.executeUpdate();
            if (affectedRowsCount == 0) {
                throw new DatabaseOperationFailureException(String.format("Deletion of account %s failed as it is not available in database", entityToDelete));
            }
        }  catch (final Exception e) {
            throw new DatabaseOperationFailureException(e.getMessage());
        }
    }

    private AccountEntity parseToAccountCredential(final ResultSet rs) throws Exception {
        final long accId = rs.getLong("account_id");
        final String accName = rs.getString("account_name");
        final String accPassword = rs.getString("password");
        final String platform = rs.getString("platform");
        final long labelId = rs.getLong("label_id");
        final String link = rs.getString("link");
        final int usageCount = rs.getInt("usage_count");
        final LocalDateTime createdAt = rs.getObject("created_at", LocalDateTime.class);
        final LocalDateTime updatedAt = rs.getObject("updated_at", LocalDateTime.class);
        final LocalDateTime lastUsedAt = rs.getObject("last_used_at", LocalDateTime.class);

        return AccountEntity.Builder()
            .withAccountId(accId)
            .withAccountName(accName)
            .withAccountPlatform(platform)
            .withEncryptedAccountPassword(accPassword)
            .withLabelId(labelId)
            .withUsageCount(usageCount)
            .withCreatedDateTime(createdAt)
            .withLastUpdatedDateTime(updatedAt)
            .withLastUsedDateTime(lastUsedAt)
            .withLink(link)
            .build();
    }

    @Override
    public List<AccountEntity> getAllAccountCredential() {
        final List<AccountEntity> allAccounts = new ArrayList<>();
        try {
            final String queryRetrieveAllAccountInfo = "SELECT * FROM Accounts;";
            final PreparedStatement psRetrieve = conn.prepareStatement(queryRetrieveAllAccountInfo);

            final ResultSet rs = psRetrieve.executeQuery();
            while (rs.next()) {
                allAccounts.add(parseToAccountCredential(rs));
            }
        } catch (final Exception e) {
            throw new DatabaseOperationFailureException(e.getMessage());
        }

        return allAccounts;
    }

    @Override
    public void updateAccountCredential(final AccountEntity updatedAccountEntity) {
        try {
            final String queryUpdateAccountInfo = """
                UPDATE Accounts
                SET account_name = ?, password = ?, platform = ?, label_id = ?, link = ?,
                    usage_count = ?, last_used_at = ?, updated_at = ?
                WHERE account_id = ?;
            """;
            final PreparedStatement psUpdate = conn.prepareStatement(queryUpdateAccountInfo);
            psUpdate.setString(1, updatedAccountEntity.getAccName());
            psUpdate.setString(2, updatedAccountEntity.getAccPassword());
            psUpdate.setString(3, updatedAccountEntity.getPlatform());
            psUpdate.setLong(4, updatedAccountEntity.getLabelId());
            psUpdate.setString(5, updatedAccountEntity.getLink());
            psUpdate.setLong(6, updatedAccountEntity.getUsageCount());
            psUpdate.setTimestamp(7, Utility.getSqlTimeStampForLocalDateTime(updatedAccountEntity.getLastUsedDateTime()));
            psUpdate.setTimestamp(8, Utility.getSqlTimeStampForLocalDateTime(updatedAccountEntity.getRecentUpdateDateTime()));
            psUpdate.setLong(9, updatedAccountEntity.getAccId());

            final int affectedRowsCount = psUpdate.executeUpdate();
            if (affectedRowsCount == 0) {
                throw new DatabaseOperationFailureException(String.format("Updation of account with id %d failed as it is not available in database", updatedAccountEntity.getAccId()));
            }
        } catch (Exception e) {
            throw new DatabaseOperationFailureException(e.getMessage());
        }
    }

    @Override
    public List<AccountEntity> getKMostUsedAccounts(int k) {
        final List<AccountEntity> mostUsedAccounts = new ArrayList<>();
        try {
            final String queryRetrieveMostUsedAccounts =
                """
                    SELECT * FROM Accounts
                    ORDER BY usage_count DESC
                    LIMIT ?;
                """;
            final PreparedStatement psRetrieve = conn.prepareStatement(queryRetrieveMostUsedAccounts);
            psRetrieve.setInt(1, k);

            final ResultSet rs = psRetrieve.executeQuery();
            while (rs.next()) {
                mostUsedAccounts.add(parseToAccountCredential(rs));
            }
        } catch (final Exception e) {
            throw new DatabaseOperationFailureException(e.getMessage());
        }

        return mostUsedAccounts;
    }

    @Override
    public List<AccountEntity> getKRecentModifiedAccounts(int k) {
        final List<AccountEntity> recentModifiedAccounts = new ArrayList<>();
        try {
            final String queryRecentModifiedAccounts =
                    """
                        SELECT * FROM Accounts
                        WHERE updated_at IS NOT NULL
                        ORDER BY updated_at DESC
                        LIMIT ?;
                    """;
            final PreparedStatement psRetrieve = conn.prepareStatement(queryRecentModifiedAccounts);
            psRetrieve.setInt(1, k);

            final ResultSet rs = psRetrieve.executeQuery();
            while (rs.next()) {
                recentModifiedAccounts.add(parseToAccountCredential(rs));
            }
        } catch (final Exception e) {
            throw new DatabaseOperationFailureException(e.getMessage());
        }

        return recentModifiedAccounts;
    }

    @Override
    public List<AccountEntity> getKRecentUsedAccounts(int k) {
        final List<AccountEntity> recentUsedAccounts = new ArrayList<>();
        try {
            final String queryRecentUsedAccounts =
                    """
                        SELECT * FROM Accounts
                        WHERE last_used_at IS NOT NULL
                        ORDER BY last_used_at DESC
                        LIMIT ?;
                    """;
            final PreparedStatement psRetrieve = conn.prepareStatement(queryRecentUsedAccounts);
            psRetrieve.setInt(1, k);

            final ResultSet rs = psRetrieve.executeQuery();
            while (rs.next()) {
                recentUsedAccounts.add(parseToAccountCredential(rs));
            }
        } catch (final Exception e) {
            throw new DatabaseOperationFailureException(e.getMessage());
        }

        return recentUsedAccounts;
    }

    @Override
    public List<AccountEntity> getAccountsAddedThisMonth() {
        final List<AccountEntity> accountsAddedThisMonth = new ArrayList<>();
        try {
            final String queryRecentUsedAccounts =
                    """
                        SELECT * FROM Accounts
                        WHERE MONTH(created_at) = ? AND YEAR(created_at) = ?;
                    """;
            final PreparedStatement psRetrieve = conn.prepareStatement(queryRecentUsedAccounts);
            final LocalDate currDate = LocalDate.now();
            psRetrieve.setInt(1, currDate.getMonthValue());
            psRetrieve.setInt(2, currDate.getYear());

            final ResultSet rs = psRetrieve.executeQuery();
            while (rs.next()) {
                accountsAddedThisMonth.add(parseToAccountCredential(rs));
            }
        } catch (final Exception e) {
            throw new DatabaseOperationFailureException(e.getMessage());
        }

        return accountsAddedThisMonth;
    }

}
