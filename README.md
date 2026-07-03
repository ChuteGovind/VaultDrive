# BMS Drive

BMS Drive is a full-stack, secure file management application styled after Google Drive. It allows users to register accounts, authenticate securely, and upload, search, preview, update, and delete files with user-specific isolation and storage capacity limits.

---

## Technical Features

* **Secure Authentication**: Session-based login/logout using Spring Security and BCrypt password encoding (using standard `JSESSIONID` cookies).
* **Multi-Format Previews**: Live preview window supporting HTML5 audio/video, image galleries, plain text (.txt files), and PDF viewports.
* **Smart Categorization & Search**: Automatically assigns categorizations (Images, Videos, Audio, PDFs, Documents, Spreadsheets, Presentations, Archives, and Others) based on MIME-types and extension fallback. Supports full-text search by name, category filters, and uploader date ranges.
* **Storage Analytics**: Aggregated real-time metrics showing total storage utilized (against a maximum quota) and charts/details breakdown.
* **Strict Security Boundaries**: Uses physical path isolation (`uploads/{userId}/`) and entity validation to guarantee users can only list, filter, download, update, or remove their own stored assets.
* **Progressive Metadata Adjustments**: Edit and adjust filename headers, summaries, and tags on the fly.

---

## Technology Stack

* **Backend**: Spring Boot 3.2.5, Spring Security, Spring Data JPA
* **Database**: MySQL (GDdrive database)
* **Frontend**: Vanilla responsive HTML5 layout featuring modern interactive CSS3 styling (glassmorphic cues, responsive side rails, animations) and asynchronous ES6 javascript integrations.
* **Build System**: Maven + Java 17+

---

## File and Project Architecture

The database entities are split cleanly, keeping MVC patterns in focus:

```
c:\Anitigravity\bms
├── src/main/java/com/cfs/bms
│   ├── BmsApplication.java         # Main Spring Boot starter class
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
│   ├── exception/                  # Global controllers exception advice mapper
│   ├── repository/
│   │   ├── UserRepository.java
│   │   └── StoredFileRepository.java
│   ├── service/
│   │   ├── AuthService.java        # Registration & Current session extraction
│   │   ├── FileService.java        # DB operations, ownership validation, statistics
│   │   └── StorageService.java     # Physical path creation & file I/O operations
│   └── util/
│       └── FileUtils.java          # Filename cleaning, sizes formats, MIME parses
└── src/main/resources
    ├── application.properties      # Database configurations and limits
    └── static/                     # HTML Templates, CSS files and JS scripts
```

---

## Setup & Configuration

### Prerequisites
* Java JDK 17 (or newer)
* Apache Maven 3.9+
* Active MySQL instance running on port `3306`

### 1. Database Creation
Ensure that you have MySQL running and create the `GDdrive` database:
```sql
CREATE DATABASE GDdrive;
```

### 2. Environment Variable Setup
For security credentials protection, the application retrieves the database password from the `DB_PASSWORD` environment variable.

#### On Windows (PowerShell)
To set this in your current Shell session:
```powershell
$env:DB_PASSWORD="your_mysql_password_here"
```
To set it permanently at user level:
```powershell
[System.Environment]::SetEnvironmentVariable("DB_PASSWORD", "your_mysql_password_here", "User")
```

#### On Windows (CMD)
```cmd
set DB_PASSWORD=your_mysql_password_here
```

#### On Linux / macOS
```bash
export DB_PASSWORD="your_mysql_password_here"
```

---

## Build and Run Instructions

1. **Compilation**: Clean target outputs and compile source classes:
   ```bash
   mvn clean compile
   ```

2. **Run Server**: Launch the Spring Boot Web Server:
   ```bash
   mvn spring-boot:run
   ```
   
3. **Application URL**: Once the console displays `Started BmsApplication`, open your web browser and navigate to:
   [http://localhost:8080](http://localhost:8080)

---

## API Documentation Summary

### Authentication API (`/api/auth`)
* `POST /register`: Registers a new user session profile.
* `POST /login`: Performs credentials check and issues application session identifier (`JSESSIONID`).
* `POST /logout`: Invalidates the current user session cookie.
* `GET /me`: Returns details of the currently authenticated profile.

### File Metadata & Storage API (`/api/files`)
* `POST /upload`: Expects a multipart form body containing `file` (binary payload), optional `description`, and `category`.
* `GET /`: Lists all files owned by the current user.
* `GET /search`: Resolves name match queries, categories query parameters, and date filters.
* `GET /stats`: Aggregates sizes and lists totals.
* `GET /{id}/download`: Streams secure binary attachment download.
* `GET /{id}/preview`: Retrieves raw file stream for inline video, audio, rendering.
* `PUT /{id}`: Modifies metadata like filename, category, and remarks description.
* `DELETE /{id}`: Cleans database records and deletes files from user-specific filesystem paths.

---

## Screens Showcase
*(A screen placeholder diagram showing the layout structure)*
```
+-------------------------------------------------------------+
| [BMS Drive]   [Search input here]               [User Info] |
+-------------------------------------------------------------+
| (Sidebar)   | (Dashboard Grid View)                         |
|  * All      | [Stats Cards]                                 |
|  * Image    | +----------+  +----------+  +----------+      |
|  * Video    | | Photos   |  | Docs     |  | PDFs     |      |
|  * Audio    | +----------+  +----------+  +----------+      |
|  * PDF      |                                               |
|  * Doc      | [Recent Files Grid layout]                    |
|             | [+] Upload File button                        |
+-------------+-----------------------------------------------+
```

---

## Future Scope

1. **Sharable Links**: Implementing time-decaying public URLs to share individual attachments.
2. **Dynamic Folders**: Creating nested folders/directories inside user-specific directories.
3. **Collaborative Options**: Access lists allowing safe file sharing among distinct users.
