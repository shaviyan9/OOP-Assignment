# 🎓 Placement & Job Recruitment System

A Java Swing desktop application with MySQL backend for managing student placements and job recruitment.

---

## 📁 Project Structure

```
PlacementSystem/
├── src/
│   ├── Main.java                          ← Entry point
│   ├── db/
│   │   └── DBConnection.java              ← JDBC singleton
│   ├── models/
│   │   ├── Student.java
│   │   ├── Company.java
│   │   ├── Job.java
│   │   ├── Application.java
│   │   └── Admin.java
│   ├── dao/
│   │   ├── StudentDAO.java
│   │   ├── CompanyDAO.java
│   │   ├── JobDAO.java
│   │   ├── ApplicationDAO.java
│   │   └── AdminDAO.java
│   └── ui/
│       ├── UIHelper.java                  ← Shared theme & factory methods
│       ├── LoginFrame.java                ← Login screen (all roles)
│       ├── student/
│       │   ├── StudentDashboard.java      ← Tabbed student portal
│       │   ├── RegisterFrame.java
│       │   ├── ResetPasswordFrame.java
│       │   ├── JobListPanel.java
│       │   ├── ApplicationStatusPanel.java
│       │   ├── UpdateProfilePanel.java
│       │   └── PrivacyPolicyPanel.java
│       ├── company/
│       │   ├── CompanyDashboard.java      ← Tabbed company portal
│       │   ├── CompanyJobListPanel.java
│       │   ├── PostJobPanel.java
│       │   ├── ApplicantsPanel.java
│       │   └── CompanyProfilePanel.java
│       └── admin/
│           ├── AdminDashboard.java        ← Tabbed admin portal
│           ├── ManageCompaniesPanel.java
│           ├── ViewStudentsPanel.java
│           └── ViewApplicationsPanel.java
├── lib/                    ← Place mysql-connector-j-*.jar here
├── out/                    ← Compiled classes (auto-created)
├── database.sql            ← Run this in MySQL first
├── build.sh                ← Linux/macOS build & run
├── build.bat               ← Windows build & run
└── README.md
```

---

## ⚙️ Setup Instructions

### 1. Prerequisites

| Tool | Version |
|------|---------|
| Java JDK | 11 or higher |
| MySQL Server | 8.0 or higher |
| MySQL Connector/J | 8.x (JDBC Driver) |

### 2. Database Setup

Open MySQL Workbench or the MySQL CLI and run:

```sql
source /path/to/PlacementSystem/database.sql
```

This creates the `placement_system` database with all tables and sample data.

### 3. Configure Database Connection

Edit `src/db/DBConnection.java`:

```java
private static final String DB_URL  = "jdbc:mysql://localhost:3306/placement_system?useSSL=false&serverTimezone=UTC";
private static final String USER     = "root";          // ← your MySQL username
private static final String PASSWORD = "yourpassword";  // ← your MySQL password
```

### 4. Add MySQL Connector/J

Download from: https://dev.mysql.com/downloads/connector/j/

Place the `.jar` file inside the `lib/` folder:
```
PlacementSystem/lib/mysql-connector-j-8.x.x.jar
```

### 5. Build & Run

**Linux / macOS:**
```bash
chmod +x build.sh
./build.sh
```

**Windows:**
```
build.bat
```

**Or manually:**
```bash
# Compile
javac -cp "lib/mysql-connector-j-8.x.x.jar" -d out $(find src -name "*.java")

# Run
java -cp "out:lib/mysql-connector-j-8.x.x.jar" Main
# Windows: java -cp "out;lib\mysql-connector-j-8.x.x.jar" Main
```

---

## 🔑 Default Login Credentials

| Role | Username / Email | Password |
|------|-----------------|----------|
| Admin | `admin` | `admin123` |
| Company (TechNova) | `hr@technova.com` | `company123` |
| Company (FinEdge) | `recruit@finedge.com` | `company123` |
| Student | *(Register first)* | *(your choice)* |

---

## ✅ Features Implemented

### 👨‍🎓 Student
| Feature | Status |
|---------|--------|
| Register | ✅ |
| Login / Logout | ✅ |
| View available job & placement posts | ✅ |
| Apply for a job (with cover note) | ✅ |
| View companies applied to + check status | ✅ |
| Update profile (personal + academic details) | ✅ |
| Upload resume (file picker) | ✅ |
| Reset / change password | ✅ |
| View privacy policy | ✅ |
| Remove a job application *(optional)* | ✅ |

### 🏢 Company
| Feature | Status |
|---------|--------|
| Login / Logout | ✅ |
| Post a new job/placement offer | ✅ |
| View and filter own job posts by status | ✅ |
| Edit job details (description, criteria, dates) | ✅ |
| Delete a job post *(optional)* | ✅ |
| List applicants for a job | ✅ |
| Search applicants by name | ✅ |
| Accept / Reject an application | ✅ |
| Update company profile | ✅ |

### 🛡️ Admin
| Feature | Status |
|---------|--------|
| Login / Logout | ✅ |
| Add company | ✅ |
| Update company | ✅ |
| Delete company | ✅ |
| View all companies | ✅ |
| View all job applications | ✅ |
| Update application status *(optional)* | ✅ |
| Manage students | ✅ |
| Filter students by course/branch/section *(optional)* | ✅ |
| Search students by name/username | ✅ |
| Manage student details | ✅ |

---

## 🖼️ UI Architecture

- **LoginFrame** — Single entry point routing to all three portals
- **StudentDashboard** — JTabbedPane with 5 tabs (Dashboard, Browse Jobs, My Applications, Profile, Privacy Policy)
- **CompanyDashboard** — JTabbedPane with 5 tabs (Dashboard, Job Posts, Post Job, Applicants, Profile)
- **AdminDashboard** — JTabbedPane with 4 tabs (Overview, Manage Companies, View Students, Applications)

All frames share the **UIHelper** utility for consistent colours, fonts, and component factories.

---

## 🗄️ Database Schema

```
admins        → id, username, email, password
students      → id, full_name, username, email, password, phone, course, branch, section, cgpa, resume_path, status
companies     → id, company_name, email, password, industry, description, website, hr_contact, status
jobs          → id, company_id, title, description, eligibility, criteria, salary, location, deadline, job_type, status
applications  → id, student_id, job_id, status, cover_note, applied_at, updated_at
```

---

## 📌 Notes

- Passwords are stored in **plain text** in this demo. For production, use BCrypt or SHA-256 hashing.
- The resume upload stores the **local file path** — in production this should be a file server path or cloud URL.
- The system enforces that a student **cannot apply twice** for the same job (unique constraint + DAO check).
