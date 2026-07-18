# Application User Flow

This document describes how the application behaves during common user interactions, from the user's perspective as well as from the application's internal perspective.

The goal is to provide a high-level view of how user actions translate into application operations without going into the implementation details of individual services, repositories, or security mechanisms.

---

## First-Time Application Launch

When the application is launched for the first time, the application does not yet have a configured user account or master password.

### User Perspective

The user:

1. Enters their name and sets a master password.
2. Enters a password hint.
3. Re-enters the master password for confirmation.
4. Clicks the **Done** button.
5. Is redirected to the main application screen.

### Internal Processing

When the application starts:

1. The application checks whether the database exists.
2. If the database does not exist, a new database is created.
3. The required database schemas are initialized.
4. The Settings Repository checks for mandatory application settings, including the stored master password hash.
5. Since the `HASHED_PASSWORD` setting is not found, the application determines that initial setup has not yet been completed.
6. The user is redirected to the setup screen.
7. After the user successfully confirms the master password:

   * The required cryptographic salt is generated.
   * The master password is processed for secure storage.
   * The resulting password hash and required security information are stored.
   * The username and password hint are stored as application settings.
8. The application redirects the user to the main screen.

Although the setup process appears simple from the user's perspective, several security-related operations are performed internally before the application becomes ready for normal use.

The application generates a cryptographic salt and derives the required encryption key from the master password before storing the necessary security information.

For a detailed explanation of password hashing, salting, key derivation, and encryption, see [Security & Encryption](security-and-encryption.md).

![First-Time Application Launch Flow](assets/app-setup-flow.svg)

---

## Subsequent Application Launch

Once the application has been configured, subsequent launches follow a simpler user flow.

### User Perspective

The user:

1. Enters the master password.
2. The password hint is displayed on the login screen to assist the user if required.
3. After successful verification, the user is taken to the main application screen.

### Internal Processing

When the application starts:

1. The application checks for the existing database.
2. The existing database is loaded.
3. The user enters the master password.
4. The entered password is verified against the stored password information.
5. Upon successful verification, the application checks the user's default screen preference.
6. The configured default screen is opened.

For more information about password verification and the security mechanisms involved, see [Security & Encryption](security-and-encryption.md).

![Subsequent Application Launch Flow](assets/app-login-flow.svg)

---

## Adding a Credential

### User Perspective

The user enters the required account information and clicks the **Add** button.

### Internal Processing

1. The entered information is passed for validation.
2. If validation fails, the user is notified through a dialog explaining the reason for failure.
3. If validation succeeds, the data is passed to the appropriate application service.
4. The service applies the required application-level rules before passing the operation to the repository layer.
5. The repository performs the required database operation.
6. The user is notified through a dialog whether the operation was successful or unsuccessful.

---

## Editing a Credential

Editing an existing credential follows a flow similar to adding a new credential.

The entered information is first validated. If validation succeeds, the updated data is passed through the application service and eventually persisted by the repository layer.

The primary difference is that the database operation updates an existing record rather than creating a new one.

---

## Importing Credentials

Credential import is initiated from:

**Settings → Data → Import**

### User Perspective

The user:

1. Clicks the **Import** button.
2. Selects the file containing the credentials to be imported.
3. Waits for the file to be parsed successfully.
4. Is presented with the available options for continuing the import.
5. Selects the desired action.
6. The application processes the imported data according to the selected option.

The available options may include actions such as importing new records, replacing existing records, merging data, manually reviewing conflicts, or cancelling the operation.

For a detailed explanation of the import process and conflict-resolution strategies, see [Import & Export Architecture](import-export-architecture.md).

![Importing Credentials Flow](assets/import-flow.svg)

---

## Exporting Credentials

Credential export is initiated from:

**Settings → Data → Export**

### User Perspective

The user:

1. Clicks the **Export** button.
2. Is prompted to enter the master password for verification.
3. Is warned if the export will contain unencrypted, plain-text data.
4. Selects the destination and file name.
5. Confirms the export operation.
6. After successful password verification, the credentials are exported to the selected location.

### Internal Processing

1. The user initiates the export operation.
2. The application prompts the user for password verification.
3. The user selects the destination and file name.
4. The application verifies the entered password.
5. If verification succeeds, the export operation proceeds.
6. The credential data is exported to the selected destination.
7. If verification fails, the export operation is not performed.

For more information about how the export process is implemented, see [Import & Export Architecture](import-export-architecture.md).

![Exporting Credentials Flow](assets/export-flow.svg)

---

## Relationship with Other Technical Documentation

This document provides a high-level overview of how the application behaves during important user interactions.

The detailed implementation of specific subsystems is documented separately:

* [Application Architecture](application-architecture.md) — How the application is structured.
* [Database Design](database-design.md) — How application data is organized and stored.
* [Security & Encryption](security-and-encryption.md) — How passwords, encryption keys, and sensitive data are protected.
* [Import & Export Architecture](import-export-architecture.md) — How credential import, export, and conflict resolution are handled.

The purpose of this document is to explain **what happens during an application flow**. The linked documents explain **how the underlying systems work in greater detail**.
