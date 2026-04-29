package org.ajikhoji.passwordmanager.validator;

import org.ajikhoji.passwordmanager.exception.DataInconsistentException;
import org.ajikhoji.passwordmanager.exception.ValidationException;
import org.ajikhoji.passwordmanager.model.AccountEntity;
import org.ajikhoji.passwordmanager.util.Utility;

public class AccountInfoValidator {

    private AccountInfoValidator() { }

    public static void validateAccountName(final String accountName) {
        if(accountName == null || accountName.isBlank()) {
            throw new ValidationException("Account name cannot be blank");
        }
        if(accountName.chars().filter(c -> c != ' ').reduce(0, Integer::sum) < 5) {
            throw new ValidationException("Account name should consist minimum of 5 non-whitespace characters");
        }
    }

    public static void validateAccountName(final AccountEntity ae) {
        validateAccountName(ae.getAccName());
    }

    public static void validateUnencryptedPassword(final String password) {
        if(password == null || password.isBlank()) {
            throw new ValidationException("Password cannot be blank");
        }
        if(password.chars().filter(c -> c != ' ').reduce(0, Integer::sum) < 5) {
            throw new ValidationException("Password should consist minimum of 5 non-whitespace characters");
        }
    }

    public static void validateEncryptedPassword(final String password) {
        if(password == null || password.isBlank()) {
            throw new ValidationException("Password encryption value not assigned properly");
        }
    }

    public static void validatePlatform(final String platform) {
        if(platform == null || platform.isBlank()) {
            throw new ValidationException("Platform name cannot be blank");
        }
        if(platform.chars().filter(c -> c != ' ').reduce(0, Integer::sum) < 5) {
            throw new ValidationException("Platform should consist minimum of 5 non-whitespace characters");
        }
    }

    public static void validatePlatform(final AccountEntity ae) {
        validateAccountName(ae.getPlatform());
    }

    /* This method does not check for id, name, password and platform of the account to be valid or not.
     * Rather it checks whether other associated information are properly integrated with each other.
     * One example that fails this test: An account where lastUsedDate is NULL but usageCount > 0
     * Another example: Created data time falls after recent updated date time.
     * This indicates that either the data parsed is either inconsistently saved beforehand,
     * or accidentally partial data has been copied (maybe forgotten to include all fields in builder call).
     */
    public static void isAccountEntityDataConsistent(final AccountEntity ae) {
        if(ae.getCreatedDateTime() == null) {
            throw new ValidationException("Created date is undefined");
        }
        if(ae.getLastUsedDateTime() != null && ae.getUsageCount() == 0) {
            throw new DataInconsistentException(String.format("Usage count is never recorded but last used date is %s", Utility.getFormatedDateTimeString(ae.getLastUsedDateTime())), ae.toString());
        }
        if(ae.getLastUsedDateTime() == null && ae.getUsageCount() > 0) {
            throw new DataInconsistentException(String.format("Usage count is %d but last used date is never recorded", ae.getUsageCount()), ae.toString());
        }
        if(ae.getRecentUpdateDateTime() != null && (ae.getRecentUpdateDateTime().isBefore(ae.getCreatedDateTime()) || ae.getRecentUpdateDateTime().isEqual(ae.getCreatedDateTime()))) {
            throw new DataInconsistentException("Created date cannot be after or same as of recent updated date", ae.toString());
        }
    }

}
