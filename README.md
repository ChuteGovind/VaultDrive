# VaultDrive

VaultDrive is a full-stack, secure file management application styled after Google Drive. It allows users to register accounts, authenticate securely, and upload, search, preview, update, and delete files with user-specific isolation and storage capacity limits.

## 🔐 Technical Features

- **Secure Authentication** — Session-based login/logout using Spring Security and BCrypt password encoding (standard `JSESSIONID` cookies).
- **Multi-Format Previews** — Live preview window supporting HTML5 audio/video, image galleries, plain text (`.txt`), and PDF viewports.
- **Smart Categorization & Search** — Automatically assigns categories (Images, Videos, Audio, PDFs, Documents, Spreadsheets, Presentations, Archives, Others) based on MIME-types with extension fallback. Supports full-text search by name, category filters, and upload date ranges.
- **Storage Analytics** — Aggregated real-time metrics showing total storage utilized against a maximum quota, with charts/details breakdown.
- **Strict Security Boundaries** — Physical path isolation (`uploads/{userId}/`) and entity validation guarantee users can only list, filter, download, update, or remove their own stored assets.
- **Progressive Metadata Adjustments** — Edit filenames, summaries, and tags on the fly.

## 🛠️ Technology Stack

| Layer | Details |
|---|---|
| Backend | Spring Boot 3.2.5, Spring Security, Spring Data JPA |
| Database | MySQL (`GDdrive` database) |
| Frontend | Vanilla responsive HTML5 layout with modern CSS3 styling (glassmorphic cues, responsive side rails, animations) and asynchronous ES6 JavaScript |
| Build System | Maven + Java 17+ |

## 🏗️ Project Architecture

The database entities are split cleanly, keeping MVC patterns in focus:

```
vaultdrive
├── src/main/java/com/cfs/bms
│   ├── VaultDriveApplication.java  # Main Spring Boot starter class
│   ├── config/
│   │   └── SecurityConfig.java     # Authentication filters & endpoint permissions
│   ├── controller/
│   │   ├── AuthController.java     # Register, Login, Logout, Session check endpoints
│   │   └── FileController.java     # Upload, Stats, Search, Download, Update endpoints
│   ├── dto/                        # REST payloads (LoginRequest, FileResponse, etc.)
│   ├── entity/
│   │   ├── User.java               # Persisted user credentials & profiles
│   │   ├── StoredFile.java         # Persisted file metadata
│   │   ├── Role.java               # Enum roles (ROLE_USER, ROLE_ADMIN)
│   │   └── FileCategory.java       # Enum categories (IMAGE, PDF, etc.)
│   ├── exception/                  # Global controller exception advice mapper
│   ├── repository/
│   │   ├── UserRepository.java
│   │   └── StoredFileRepository.java
│   ├── service/
│   │   ├── AuthService.java        # Registration & current session extraction
│   │   ├── FileService.java        # DB operations, ownership validation, statistics
│   │   └── StorageService.java     # Physical path creation & file I/O operations
│   └── util/
│       └── FileUtils.java          # Filename cleaning, size formatting, MIME parsing
└── src/main/resources
    ├── application.properties      # Database configuration and limits
    └── static/                     # HTML templates, CSS files, JS scripts
```

## ⚙️ Setup & Configuration

### Prerequisites

- Java JDK 17 (or newer)
- Apache Maven 3.9+
- Active MySQL instance running on port 3306

### 1. Database Creation

Ensure MySQL is running, then create the database:

```sql
CREATE DATABASE GDdrive;
```

### 2. Environment Variable Setup

For credential protection, the application retrieves the database password from the `DB_PASSWORD` environment variable.

**Windows (PowerShell)** — current session:

```powershell
$env:DB_PASSWORD="your_mysql_password_here"
```

Permanently at user level:

```powershell
[System.Environment]::SetEnvironmentVariable("DB_PASSWORD", "your_mysql_password_here", "User")
```

**Windows (CMD)**

```cmd
set DB_PASSWORD=your_mysql_password_here
```

**Linux / macOS**

```bash
export DB_PASSWORD="your_mysql_password_here"
```

### 3. Build and Run

Compile:

```bash
mvn clean compile
```

Run the server:

```bash
mvn spring-boot:run
```

Once the console displays `Started VaultDriveApplication`, open your browser to: `http://localhost:8080`

## 📚 API Documentation Summary

### Authentication API (`/api/auth`)

| Method | Endpoint | Description |
|---|---|---|
| POST | `/register` | Registers a new user session profile |
| POST | `/login` | Performs credentials check and issues session identifier (`JSESSIONID`) |
| POST | `/logout` | Invalidates the current user session cookie |
| GET | `/me` | Returns details of the currently authenticated profile |

### File Metadata & Storage API (`/api/files`)

| Method | Endpoint | Description |
|---|---|---|
| POST | `/upload` | Multipart form body with `file` (binary), optional `description`, and `category` |
| GET | `/` | Lists all files owned by the current user |
| GET | `/search` | Resolves name match queries, category filters, and date ranges |
| GET | `/stats` | Aggregates sizes and totals |
| GET | `/{id}/download` | Streams secure binary attachment download |
| GET | `/{id}/preview` | Retrieves raw file stream for inline video/audio/rendering |
| PUT | `/{id}` | Modifies metadata like filename, category, and remarks/description |
| DELETE | `/{id}` | Cleans database records and deletes files from user-specific filesystem paths |

## 🖥️ Screens Showcase

```
+-------------------------------------------------------------+
| [VaultDrive]   [Search input here]              [User Info] |
+-------------------------------------------------------------+
| (Sidebar)   | (Dashboard Grid View)                         |
|  * All      | [Stats Cards]                                 |
|  * Image    | +----------+  +----------+  +----------+      |
|  * Video    | | Photos   |  | Docs     |  | PDFs     |       |
|  * Audio    | +----------+  +----------+  +----------+      |
|  * PDF      |                                               |
|  * Doc      | [Recent Files Grid layout]                    |
|             | [+] Upload File button                        |
+-------------+-----------------------------------------------+
```

## 🔭 Future Scope

- **Shareable Links** — Time-decaying public URLs to share individual attachments.
- **Dynamic Folders** — Nested folders/directories inside user-specific directories.
- **Collaborative Options** — Access lists allowing safe file sharing among distinct users.
