# Security & Encryption

This document describes the security mechanisms used by **Ajikhoji's Password Manager** to protect sensitive user data.

The application follows a local-first security model. Sensitive data is encrypted and decrypted locally on the user's device, while the master password is never stored in its original form.

The main security mechanisms used by the application are:

* Password-based key derivation using `PBKDF2WithHmacSHA256`
* Cryptographic salting
* AES encryption
* Random initialization vectors
* Base64 encoding of encrypted data
* Local encryption and decryption
* Password verification before sensitive operations

---

## Security Architecture

The master password serves as the basis for both user authentication and encryption-key generation.

The overall security model is:

![Security Architecture](assets/security-architecture.svg)

The master password is processed together with a randomly generated salt using `PBKDF2WithHmacSHA256`. The resulting 256-bit derived key is used to create a `SecretKeySpec`, which is then supplied to the AES encryption service.

The original master password is never stored by the application.

---

## Password-Based Key Derivation

The application uses `PBKDF2WithHmacSHA256` to derive the encryption key from the master password.

The cryptographic parameters are:

| Parameter               | Value                  |
| ----------------------- | ---------------------- |
| Key derivation function | `PBKDF2WithHmacSHA256` |
| Salt                    | Random 16-byte value   |
| Iteration count         | `65,536`               |
| Derived key length      | `256 bits`             |

The key derivation process can be summarized as:

```text
Master Password + Random Salt
              │
              ▼
   PBKDF2WithHmacSHA256
       65,536 Iterations
              │
              ▼
        256-bit Derived Key
              │
              ▼
          SecretKeySpec
```

The `KeyManager` is responsible for generating the required `SecretKeySpec`.

The derived key is not stored as a separate persistent secret. Instead, it can be regenerated when the user provides the correct master password and the stored salt is used during the key derivation process.

---

## Cryptographic Salt

A random 16-byte salt is generated during the initial application setup.

The salt is used as an input to the password-based key derivation process.

The salt does not need to be kept secret. It is stored so that the same key derivation process can be reproduced during subsequent application launches.

The use of a salt ensures that the same master password does not automatically produce the same derived key when processed with a different salt.

---

## Master Password Verification

The master password is never stored in plaintext.

During the initial setup, the required password-related security information is generated using the master password and the generated salt.

During subsequent application launches, the user-provided master password is processed again using the stored salt. The resulting security information is then used to verify whether the entered password is correct.

Conceptually:

```text
Entered Master Password
          │
          ▼
      Stored Salt
          │
          ▼
PBKDF2WithHmacSHA256
          │
          ▼
     Derived Key
          │
          ▼
   Password Verification
          │
        ┌─┴─┐
        │   │
      Valid Invalid
        │   │
        ▼   ▼
    Continue Reject
```

Only after successful verification can the user access the main application.

The same authentication mechanism is also used before sensitive operations such as credential export.

---

# AES Encryption

Sensitive application data is protected using AES encryption.

The application uses the following cipher transformation:

```text
AES/CBC/PKCS5Padding
```

The AES encryption service receives a `SecretKeySpec` through its constructor.

The `KeyManager` and encryption service therefore have separate responsibilities:

* **`KeyManager`** — derives the encryption key.
* **Encryption Service** — uses the key to encrypt and decrypt data.

This separation keeps key generation and cryptographic operations independent.

---

## Encryption Pipeline

A new random 16-byte initialization vector (IV) is generated every time data is encrypted.

The complete encryption pipeline is:

![AES Encryption Pipeline](assets/aes-encryption-pipeline.svg)

Conceptually:

```text
Plaintext
    │
    ▼
Generate Random 16-Byte IV
    │
    ▼
AES/CBC/PKCS5Padding
    │
    ▼
IV + Ciphertext
    │
    ▼
Base64 Encoding
    │
    ▼
Encrypted Database String
```

The IV is stored alongside the ciphertext so that the data can be decrypted later.

The IV does not need to be kept secret.

---

## Initialization Vector

A new random 16-byte IV is generated for every encryption operation.

For example:

```text
Same Plaintext
      │
      ├── Random IV 1 → Ciphertext A
      │
      └── Random IV 2 → Ciphertext B
```

Therefore, encrypting the same plaintext multiple times can produce different ciphertext values.

This is why encrypted values should not be compared directly as strings.

The general stored representation is:

```text
┌──────────────────────┬─────────────────────────┐
│ 16-Byte Random IV    │ Encrypted Data Bytes    │
└──────────────────────┴─────────────────────────┘
                    │
                    ▼
              Base64 Encoding
                    │
                    ▼
             Database String
```

---

## Base64 Encoding

AES encryption produces binary data.

Since encrypted values are stored as strings in the database, the combined IV and ciphertext bytes are converted into a Base64-encoded string before being persisted.

The Base64 encoding is used to provide a textual representation of the encrypted binary data.

It does not provide encryption or additional confidentiality.

The complete process is:

```text
Plaintext
    │
    ▼
AES Encryption
    │
    ▼
IV + Ciphertext
    │
    ▼
Base64 Encoding
    │
    ▼
Database String
```

---

## Decryption

When encrypted data needs to be read:

1. The Base64-encoded value is decoded.
2. The stored 16-byte IV is extracted.
3. The remaining encrypted bytes are treated as the ciphertext.
4. The ciphertext is decrypted using `AES/CBC/PKCS5Padding` and the derived encryption key.

Conceptually:

```text
Base64-Encoded String
          │
          ▼
    Base64 Decoding
          │
          ▼
    Extract Stored IV
          │
          ▼
     AES Decryption
          │
          ▼
       Plaintext
```

Decryption occurs locally within the application.

---

# Data Stored in Encrypted Form

The same encryption pipeline is used for different types of sensitive application data.

## Account Passwords

Account passwords are encrypted before being persisted in the `Accounts` table.

They are decrypted only when the application requires the original value, such as when the user chooses to view or use the credential.

---

## Custom Field Values

Values stored in user-defined account custom fields are also encrypted before being persisted.

This allows users to store additional sensitive information without leaving the contents of those fields in plaintext in the database.

The custom field key is used to identify the field, while the field value is protected as sensitive data.

---

## Shared Encryption Pipeline

Account passwords and custom field values follow the same encryption pipeline:

```text
Sensitive Plaintext Value
          │
          ▼
   Random 16-Byte IV
          │
          ▼
AES/CBC/PKCS5Padding
          │
          ▼
    IV + Ciphertext
          │
          ▼
     Base64 Encoding
          │
          ▼
    Database String
```

The encryption mechanism is centralized within the encryption service rather than being independently implemented for each type of sensitive data.

---

# Comparing Encrypted Values

Encrypted values should not be compared by directly comparing their stored ciphertext strings.

Because a new random IV is generated for every encryption operation, encrypting the same plaintext multiple times can produce different ciphertext values.

For example:

```text
Same Plaintext
      │
      ├── Random IV 1 → Ciphertext A
      │
      └── Random IV 2 → Ciphertext B
```

Therefore:

```text
Ciphertext A ≠ Ciphertext B
```

does not necessarily mean:

```text
Plaintext A ≠ Plaintext B
```

When the application needs to determine whether two encrypted values represent the same plaintext, the values are decrypted using the appropriate key before comparison.

Conceptually:

```text
Encrypted Value A ──► Decrypt ──┐
                                ├──► Compare Plaintext Values
Encrypted Value B ──► Decrypt ──┘
```

This behavior is centralized within the encryption service.

---

# Security During Credential Export

Exporting credential data is a sensitive operation because the resulting file may exist outside the application's protected database.

Before exporting data:

1. The user is prompted to enter the master password.
2. The password is verified.
3. The user is informed when the exported data will be stored in plaintext.
4. The export proceeds only after successful verification.

This provides an additional authentication step before sensitive data leaves the application's protected storage.

For more information about the complete export process, see [Import & Export Architecture](import-export-architecture.md).

---

# Local-First Security Model

The application is designed around a local-first security model.

Sensitive operations are performed locally on the user's device, including:

* Password verification
* Key generation
* Encryption
* Decryption
* Database operations

The application does not require a remote server to perform these operations.

The encrypted vault remains stored locally on the user's device.

This design avoids requiring credential data to be transmitted to or stored on an external service.

---

# Security Responsibilities

Security-related responsibilities are separated across dedicated components.

| Component                     | Responsibility                                                                |
| ----------------------------- | ----------------------------------------------------------------------------- |
| `KeyManager`                  | Derives the encryption key from the master password and stored salt           |
| `EncryptionService`           | Provides the encryption and decryption abstraction                            |
| AES Encryption Implementation | Encrypts, decrypts, and compares encrypted values                             |
| `SettingService`              | Manages application security settings and stored password-related information |
| `AccountService`              | Manages account credential operations                                         |
| `AccountCustomFieldService`   | Manages custom account field operations                                       |
| Database Layer                | Persists encrypted data locally                                               |

This separation allows security-related operations to remain centralized rather than being implemented independently throughout the application.

---

# Cryptographic Parameters

For reference, the application's current cryptographic configuration is:

| Purpose                 | Configuration                       |
| ----------------------- | ----------------------------------- |
| Key derivation function | `PBKDF2WithHmacSHA256`              |
| Salt                    | Random 16-byte value                |
| PBKDF2 iteration count  | `65,536`                            |
| Derived key length      | `256 bits`                          |
| Encryption algorithm    | `AES`                               |
| Cipher transformation   | `AES/CBC/PKCS5Padding`              |
| Initialization vector   | Random 16-byte value per encryption |
| Database representation | Base64-encoded string               |

---

# Summary

Ajikhoji's Password Manager follows a layered security model:

```text
Master Password
       │
       ▼
Random 16-Byte Salt
       │
       ▼
PBKDF2WithHmacSHA256
(65,536 Iterations)
       │
       ▼
256-Bit Derived Key
       │
       ▼
SecretKeySpec
       │
       ▼
AES/CBC/PKCS5Padding
       │
       ▼
Random 16-Byte IV + Ciphertext
       │
       ▼
Base64-Encoded String
       │
       ▼
Encrypted Database Value
```

The application is designed so that:

* The original master password is not stored.
* A random 16-byte salt is used during password-based key derivation.
* PBKDF2 with HMAC-SHA256 is used with 65,536 iterations.
* A 256-bit derived key is used to create the encryption key.
* Sensitive credential data is encrypted before being persisted.
* A new random 16-byte IV is generated for every encryption operation.
* AES/CBC/PKCS5Padding is used for encryption and decryption.
* The IV and ciphertext are Base64-encoded before being stored as a database string.
* Account passwords and custom field values follow the same encryption pipeline.
* Encryption and decryption occur locally.
* Password verification is required before accessing the vault and performing sensitive operations.
* The encryption key is derived from the master password rather than stored as a separate persistent secret.

Together, password-based key derivation, cryptographic salting, AES encryption, random initialization vectors, and local processing form the core of the application's security model.
