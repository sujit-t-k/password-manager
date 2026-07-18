# Application Architecture

This document provides an overview of the overall architecture of **Ajikhoji's Password Manager** and explains how the major components interact with one another.

The application follows a layered architecture where each layer has a clearly defined responsibility. User interface components are separated from business logic, business logic is isolated from database operations, and security-related functionality is encapsulated within dedicated components. This separation improves maintainability, readability, and makes individual components easier to extend and test.

The following diagram illustrates the high-level architecture of the application.

> **Application Architecture Diagram**
>
> ![Application Architecture](assets/application-architecture-diagram.svg)

---

## Architecture Overview

The application is composed of the following major layers:

* User Interface Layer
* Configuration Layer
* Service Layer
* Repository Layer
* Database Layer
* Security Layer
* Supporting Components

Each layer is responsible for a specific part of the application and communicates only with the components necessary to perform its task.

---

## User Interface Layer

The User Interface (UI) layer is responsible for presenting information to the user and collecting user input.

It consists of application screens, dialogs, reusable UI components, and view models that maintain UI-specific state.

### Responsibilities

* Display application screens
* Handle user interactions
* Present dialogs and notifications
* Maintain temporary UI state
* Invoke application services to perform operations

The UI layer does not directly communicate with the database. Instead, it delegates all business operations to the Service Layer.

---

## Configuration Layer

The Configuration Layer provides application-wide resources and shared configuration objects.

Instead of acting as a business layer, it serves as a centralized location for resources and globally required instances that are accessed throughout the application.

Some of its responsibilities include:

* Managing application resources such as images
* Maintaining application-wide configuration
* Providing access to shared service instances
* Maintaining database configuration
* Storing global application state where appropriate

This layer allows commonly required objects to be accessed consistently without duplicating initialization logic across the application.

---

## Service Layer

The Service Layer contains the core business logic of the application.

It acts as the bridge between the user interface and the repository layer by validating requests, enforcing application rules, coordinating multiple repositories when necessary, and exposing operations required by the UI.

Typical responsibilities include:

* Validating user input
* Applying business rules
* Coordinating multiple repositories
* Preparing data for presentation
* Managing dashboard statistics
* Importing and exporting credentials

The Service Layer does not directly manipulate database schemas or execute SQL statements. Those responsibilities belong exclusively to the Repository Layer.

---

## Repository Layer

The Repository Layer is responsible for all interactions with the database.

Each repository encapsulates SQL queries and database operations for a particular domain of the application. This keeps persistence logic isolated from the business logic implemented by the Service Layer.

Responsibilities include:

* Executing SQL queries
* Managing prepared statements
* Reading and writing database records
* Mapping database records to application models

Repositories are intentionally lightweight and contain no application-specific business rules.

---

## Database Layer

The application uses an embedded HyperSQL database for persistent storage.

The database layer is responsible for:

* Establishing database connections
* Creating the database when required
* Initializing schemas
* Managing persistent storage

Using an embedded database enables the application to function entirely offline while keeping all user data stored locally on the user's device.

---

## Security Layer

Security-sensitive functionality is isolated within dedicated components.

This layer is responsible for protecting user credentials through encryption and secure key management.

Its responsibilities include:

* Encrypting sensitive information before storage
* Decrypting information when required
* Generating encryption keys from the master password
* Comparing encrypted values securely where applicable

By separating encryption logic from the rest of the application, security-related operations remain centralized and easier to maintain.

---

## Supporting Components

Several supporting packages assist the primary layers of the application.

### Models

Models represent individual records stored within the database and closely mirror the database structure.

### Data Transfer Objects (DTOs)

DTOs are used to transfer structured information between different parts of the application without directly exposing database models.

### Utilities

Utility classes provide reusable helper functionality such as formatting, random value generation, and other commonly used operations.

### Validators

Validators perform reusable validation logic to ensure application data satisfies expected constraints before business operations are executed.

### Exceptions

Custom exception classes provide meaningful error reporting and simplify debugging by categorizing different types of application failures.

---

## Layer Communication

The following illustrates the typical flow of an operation within the application.

```text
User
   │
   ▼
User Interface
   │
   ▼
Service Layer
   │
   ▼
Repository Layer
   │
   ▼
HyperSQL Database
```

Whenever sensitive information is involved, the Service Layer collaborates with the Security Layer before data is persisted or retrieved.

This design ensures that responsibilities remain clearly separated while keeping the overall architecture modular, maintainable, and easy to extend.
