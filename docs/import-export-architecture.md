# Import & Export Architecture

This document describes how credential data is imported into and exported from **Ajikhoji's Password Manager**.

The import and export system currently supports the **CSV** format.

While both operations involve transferring credential data, they follow different workflows:

* **Import** involves parsing external data, analyzing it against existing records, detecting conflicts, and applying a user-selected conflict-resolution strategy.
* **Export** involves retrieving stored account data, transforming it into a portable format, and writing it to a user-selected file.

---

## Import Architecture

The import process is designed to safely introduce external credential data into the application while providing the user with control over how conflicting records are handled.

The overall process can be summarized as:

```text
Select Import File
        │
        ▼
Parse CSV Data
        │
        ▼
Analyze Imported Records
        │
        ▼
Detect Conflicts
        │
        ▼
Select Conflict Resolution Strategy
        │
        ▼
Resolve Conflicts
        │
        ▼
Import Records
```

---

## Import Workflow

### 1. File Selection

The user initiates the import process from:

**Settings → Data → Import**

The user selects the CSV file containing the credentials to be imported.

---

### 2. File Parsing

The selected CSV file is parsed by the import service.

The imported rows are converted into the application's internal data structures.

The imported data is not immediately written to the database.

Instead, it is first analyzed to determine how it relates to the existing records.

---

### 3. Import Analysis

The imported records are analyzed against the existing account data.

The analysis identifies:

* Records that can be imported directly.
* Records that conflict with existing accounts.

This allows the application to determine which records require a conflict-resolution decision before the import can proceed.

---

## Conflict Resolution

When one or more conflicts are detected, the user is presented with four available strategies.

### 1. Import Only the Latest Records

**Recommended**

This strategy imports only the records that are considered newer according to the available record information.

Existing records are preserved when they are considered more recent.

This is the recommended option when the user wants to synchronize data while minimizing unnecessary overwrites.

---

### 2. Import All Conflicting Records

This strategy imports all conflicting records from the selected file.

Where a conflict exists, the imported record overrides the corresponding existing record.

This option is useful when the imported file is considered the authoritative source.

---

### 3. Ignore All Conflicting Records

This strategy skips all records that conflict with existing records.

Only non-conflicting records are imported.

Existing records remain unchanged.

---

### 4. Review Conflicts Individually

This strategy gives the user control over each individual conflict.

For every conflicting record, the application displays:

* The existing account information.
* The imported account information.

The two versions are presented side by side, allowing the user to compare them and select which version should be preserved.

The user reviews each conflict individually until all conflicts have been resolved.

Once all conflicts have been reviewed, the user can proceed with the import.

---

## Conflict Resolution Strategy Selection

The conflict-resolution strategies are represented by dedicated strategy implementations.

After the user selects an option, the `ConflictResolutionStrategyFactory` returns the appropriate strategy instance.

Conceptually:

```text
User Selection
      │
      ▼
ConflictResolutionStrategyFactory
      │
      ├── Import Latest Records Strategy
      │
      ├── Import All Conflicting Records Strategy
      │
      ├── Ignore Conflicting Records Strategy
      │
      └── Review Conflicts Individually Strategy
```

This allows the import process to select the required behavior without embedding all conflict-resolution logic into a single import operation.

The import workflow can therefore delegate conflict handling to the selected strategy.

---

## Conflict Resolution Flow

The overall conflict-resolution process can be represented as:

![Conflict Resolution Flow](assets/conflict-resolution-flow.svg)

---

## Individual Conflict Review

When the user chooses to review conflicts individually, the application presents the existing and imported versions of a conflicting record side by side.

Conceptually:

![Conflict Review Sample](assets/individual-conflict-review-sample-idea.svg)

The user chooses which version should be preserved for each conflicting record.

The process continues until every conflict has been individually resolved.

---

## Import Completion

After the selected conflict-resolution strategy has been applied:

1. The records selected for import are identified.
2. The appropriate database operations are performed.
3. New records are inserted.
4. Existing records selected for replacement are updated.
5. Records selected to be ignored are skipped.
6. Once all records have been processed, the import operation is completed.

The exact database operation depends on the conflict-resolution strategy selected by the user.

---

# Export Architecture

The export process converts stored credential information into a portable CSV file.

The overall workflow is:

```text
Select Export Location
        │
        ▼
Enter File Name
        │
        ▼
Enter Master Password
        │
        ▼
Verify Password
        │
        ▼
Retrieve Account Data
        │
        ▼
Build Export DTOs
        │
        ▼
Convert Records to CSV Rows
        │
        ▼
Write Records to File
        │
        ▼
Save Exported File
```

---

## Export Workflow

### 1. Select Export Destination

The user is prompted to select:

* The location where the exported file should be saved.
* The name of the file.

The export process does not begin until the required destination information is provided.

---

### 2. Password Verification

The user is prompted to enter the master password.

The password is verified before the export operation proceeds.

This provides an additional security check before sensitive credential data is written to an external file.

---

### 3. Retrieve Account Data

After successful password verification, the required account data is retrieved.

The export process retrieves:

* Account information.
* Associated custom account fields.

The retrieved information is then organized into dedicated Data Transfer Objects (DTOs).

These DTOs provide a structured representation of the data that is independent of the database entities used internally by the application.

---

## DTO-Based Export Processing

The export DTOs are responsible for representing the data required for export.

Each DTO provides functionality to convert its data into the appropriate CSV row structure.

Conceptually:

```text
Database Records
       │
       ▼
Retrieve Account
       │
       ▼
Retrieve Associated Custom Fields
       │
       ▼
Create Export DTO
       │
       ▼
Convert DTO to CSV Row
       │
       ▼
Write Row to File
```

This separates database retrieval from CSV formatting and allows the export process to work with a dedicated representation of the data being exported.

---

## Record-by-Record Writing

The export process writes the data to the CSV file record by record.

For each account:

1. The account information is retrieved.
2. Its associated custom fields are retrieved.
3. The data is represented using the appropriate export DTO.
4. The DTO converts the information into the required CSV row structure.
5. The resulting row is written to the output file.

This process continues until all eligible account records have been written.

Once all records have been processed, the file is saved at the location selected by the user.

---

## Import and Export Design

Although import and export are related operations, they have different responsibilities.

### Import

The import process must:

* Interpret external data.
* Compare imported records with existing records.
* Detect conflicts.
* Allow the user to decide how conflicts should be handled.
* Apply the selected resolution strategy.
* Persist the resulting data.

### Export

The export process must:

* Retrieve existing application data.
* Combine accounts with their associated custom fields.
* Convert application data into a portable format.
* Write the resulting data to a user-selected file.

This separation allows each operation to focus on its specific responsibility while sharing the common CSV data format.
