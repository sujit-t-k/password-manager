# Database Design

This document describes the database structure used by **Ajikhoji's Password Manager**, including its tables, relationships, constraints, and data ownership rules.

The application uses an embedded HyperSQL database to store application data locally on the user's device. The schema is organized into separate tables for account credentials, user-defined account information, labels, and application settings.

---

## Database Overview

The database consists of the following tables:

* `Accounts`
* `AccountCustomFields`
* `Labels`
* `AppSettings`

The relationship between these tables is illustrated below.

![Database Schema](assets/database-schema.svg)

---

## Entity Relationship Overview

The `Accounts` table acts as the central entity for credential-related data.

An account may:

* Belong to a label.
* Have zero or many custom fields.

The `Labels` table provides categorization for accounts, while `AppSettings` stores application-wide configuration independently of the credential data.

---

## Accounts

The `Accounts` table stores the primary information associated with each credential.

| Column         | Type            | Description                                     |
| -------------- | --------------- | ----------------------------------------------- |
| `account_id`   | `Long`          | Unique identifier for the account               |
| `account_name` | `String`        | Name of the account or service                  |
| `password`     | `String`        | Encrypted account password                      |
| `platform`     | `String`        | Platform or service associated with the account |
| `label_id`     | `Long`          | Reference to an associated label                |
| `link`         | `String`        | Optional link associated with the account       |
| `last_used_at` | `LocalDateTime` | Timestamp of the most recent usage              |
| `usage_count`  | `Int`           | Number of times the account has been used       |
| `created_at`   | `LocalDateTime` | Timestamp when the account was created          |
| `updated_at`   | `LocalDateTime` | Timestamp when the account was last modified    |

The account password is always stored in encrypted form.

### Constraints

* `account_id` is the primary key and is automatically generated.
* `account_name` is mandatory.
* `platform` is mandatory.
* `password` is mandatory.
* The account name and platform combination must be unique.
* `label_id` references the `Labels` table.

The combination of account name and platform prevents duplicate accounts from being created for the same platform.

### Relationship with Labels

Every account is associated with a label.

When a label is deleted, the associated accounts are not deleted. Instead, the application prompts the user to select another existing label, and all accounts associated with the label being deleted are reassigned to the selected label before the deletion is completed.

---

## Account Custom Fields

The `AccountCustomFields` table stores additional user-defined information associated with an account.

The table allows users to store information that does not belong to the fixed structure of the `Accounts` table.

| Column        | Type     | Description                         |
| ------------- | -------- | ----------------------------------- |
| `account_id`  | `Long`   | Reference to the associated account |
| `field_key`   | `String` | Name of the custom field            |
| `field_value` | `String` | Encrypted value of the custom field |

The `field_value` is always stored in encrypted form.

### Primary Key

The primary key is composed of:

```text
(account_id, field_key)
```

This ensures that a single account cannot contain multiple custom fields with the same name.

For example, the following combination is valid:

```text
account_id: 10
field_key:  "Security Question"
field_value: "..."
```

However, the same account cannot have another custom field with the same `field_key`.

### Relationship with Accounts

Each custom field belongs to an account.

When an account is deleted, all custom fields associated with that account are automatically deleted through the `ON DELETE CASCADE` relationship.

This prevents orphaned custom field records from remaining in the database.

---

## Labels

The `Labels` table stores labels used to categorize accounts.

| Column       | Type     | Description                     |
| ------------ | -------- | ------------------------------- |
| `label_id`   | `Long`   | Unique identifier for the label |
| `label_name` | `String` | Name of the label               |

The `label_id` column is the primary key.

Labels allow accounts to be organized into categories without storing the category name repeatedly within every account record.

An individual label may be associated with multiple accounts.

A default label named `Unlabelled` is always available and cannot be deleted.

The application allows a maximum of 16 labels at any point in time, including the default label.

### Label Deletion

Label deletion is handled by the application.

Before deleting a label associated with one or more accounts:

1. The user is prompted to select another existing label.
2. All accounts associated with the label being deleted are reassigned to the selected label.
3. The selected label is then deleted.

This ensures that deleting a label does not delete or orphan any account records.

---

## App Settings

The `AppSettings` table stores application-wide configuration values.

| Column          | Type     | Description                     |
| --------------- | -------- | ------------------------------- |
| `setting_key`   | `String` | Unique identifier for a setting |
| `setting_value` | `String` | Stored value of the setting     |

The `setting_key` column acts as the primary key.

This key-value structure allows application settings to be added without requiring a new database column for every new setting.

Examples of values stored as application settings include:

* User information
* Password-related security information
* Password hints
* User preferences
* Default screen preferences
* View configuration
* Application behavior preferences

The actual interpretation of each setting is handled by the application layer.

---

## Relationships and Data Ownership

The database uses foreign key relationships to maintain data integrity.

### Account → AccountCustomFields

```text
One Account
     │
     └───► Zero or Many AccountCustomFields
```

Each custom field belongs to a single account.

Deleting an account automatically deletes its associated custom fields through `ON DELETE CASCADE`.

### Labels → Accounts

```text
One Label
     │
     └───► Many Accounts
```

A label may be associated with multiple accounts.

Deleting a label does not delete the associated accounts. Instead, the application first reassigns those accounts to another label selected by the user.

---

## Design Characteristics

The database structure separates different types of information according to their purpose.

### Fixed Account Information

Frequently used credential information is stored directly in the `Accounts` table.

### User-Defined Account Information

Flexible, user-defined data is stored in `AccountCustomFields`.

This allows users to add additional information without requiring changes to the database schema.

### Account Organization

Labels are stored separately from the core account data.

This allows organizational features to evolve independently from the credential structure.

### Application Configuration

Application-wide settings are stored independently in `AppSettings`.

This prevents configuration data from being mixed with credential records.

---

## Data Deletion Behavior

The database uses foreign key relationships and application-level rules to maintain data integrity.

### Account Deletion

When an account is deleted:

* The account's credential data is removed.
* All associated custom fields are automatically deleted through `ON DELETE CASCADE`.

### Label Deletion

When a label is deleted:

* The associated accounts are not deleted.
* The application prompts the user to select another existing label.
* The affected accounts are reassigned to the selected label.
* The selected label is then deleted.

This ensures that deleting an organizational label does not accidentally delete credential data or leave accounts without a valid label.

---

## Application-Level Rules

Some rules are enforced by the application rather than directly by the database schema.

These include:

* A maximum of 16 labels can exist at any point in time, including the default `Unlabelled` label.
* The default `Unlabelled` label cannot be deleted.
* A label with associated accounts cannot be deleted until those accounts are reassigned to another existing label.
* The account name and platform combination must be unique according to the application's duplicate-detection rules.

These rules are enforced by the application layer before the corresponding database operation is performed.

---

## Summary

The database is centered around the `Accounts` table, with related tables extending its functionality.

The design separates:

* Core credential information
* Flexible custom account information
* Account categorization
* Application-wide settings

This structure allows the application to support flexible user-defined data while maintaining clear relationships and data integrity across the database.
