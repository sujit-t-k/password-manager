# Technical Documentation

This section provides an overview of the internal architecture and technical design of **Ajikhoji's Password Manager**.

The documentation is divided into independent sections so that each aspect of the application can be explored separately. You can begin with any section based on your area of interest.

---

## Application Architecture

Learn how the application is structured internally, including the different architectural layers, their responsibilities, and how they interact with one another.

📄 [Read Application Architecture](application-architecture.md)

---

## Application User Flow

Explore how the application behaves during important user journeys, including:

* Initial application setup
* Subsequent application launches
* Adding and editing credentials
* Importing credentials
* Exporting credentials

This document focuses on the relationship between user actions and the internal application flow.

📄 [Read Application User Flow](app-user-flow.md)

---

## Database Design

Understand the structure of the local HyperSQL database, including:

* Database tables
* Relationships between entities
* Foreign key constraints
* Data ownership
* Deletion behavior
* Application-level data rules

📄 [Read Database Design](database-design.md)

---

## Security & Encryption

Learn how sensitive information is protected within the application.

This document covers:

* Master password handling
* PBKDF2-based key derivation
* Cryptographic salting
* AES encryption
* Initialization vectors
* Base64 encoding
* Encrypted data storage
* Local encryption and decryption

📄 [Read Security & Encryption](security-and-encryption.md)

---

## Import & Export Architecture

Understand how credential data is exported and imported, including:

* CSV data export
* CSV data import
* Conflict detection
* Conflict resolution strategies
* Individual conflict review
* Importing the final resolved dataset

📄 [Read Import & Export Architecture](import-export-architecture.md)

---

## Documentation Structure

The documentation is intentionally divided into independent sections.

There is no required reading order. Readers may choose a document based on the aspect of the application they wish to understand.

For example:

* To understand the overall structure of the application, start with **Application Architecture**.
* To understand what happens when the application is launched, start with **Application User Flow**.
* To understand how data is persisted, start with **Database Design**.
* To understand how sensitive data is protected, start with **Security & Encryption**.
* To understand how data is transferred into and out of the application, start with **Import & Export Architecture**.

Each document focuses on a specific area while linking to related documentation where additional context may be useful.
