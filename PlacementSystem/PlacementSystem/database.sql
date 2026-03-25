-- ============================================================
-- PLACEMENT AND JOB RECRUITMENT SYSTEM - MySQL Schema
-- ============================================================

CREATE DATABASE IF NOT EXISTS placement_system;
USE placement_system;

-- -------------------------------------------------------
-- ADMINS
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS admins (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(100) NOT NULL UNIQUE,
    email       VARCHAR(150) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Default admin  (password: admin123)
INSERT INTO admins (username, email, password) VALUES
('admin', 'admin@placement.com', 'admin123');

-- -------------------------------------------------------
-- STUDENTS
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS students (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    full_name    VARCHAR(150) NOT NULL,
    username     VARCHAR(100) NOT NULL UNIQUE,
    email        VARCHAR(150) NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,
    phone        VARCHAR(20),
    course       VARCHAR(100),
    branch       VARCHAR(100),
    section      VARCHAR(10),
    cgpa         DECIMAL(4,2),
    resume_path  VARCHAR(300),
    status       ENUM('active','placed','inactive') DEFAULT 'active',
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- -------------------------------------------------------
-- COMPANIES
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS companies (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    company_name  VARCHAR(200) NOT NULL,
    email         VARCHAR(150) NOT NULL UNIQUE,
    password      VARCHAR(255) NOT NULL,
    industry      VARCHAR(100),
    description   TEXT,
    website       VARCHAR(200),
    hr_contact    VARCHAR(150),
    status        ENUM('active','inactive') DEFAULT 'active',
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- -------------------------------------------------------
-- JOBS
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS jobs (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    company_id    INT NOT NULL,
    title         VARCHAR(200) NOT NULL,
    description   TEXT,
    eligibility   TEXT,
    criteria      TEXT,
    salary        VARCHAR(100),
    location      VARCHAR(150),
    deadline      DATE,
    job_type      ENUM('Placement','Job') DEFAULT 'Job',
    status        ENUM('available','secured','closed') DEFAULT 'available',
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE
);

-- -------------------------------------------------------
-- APPLICATIONS
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS applications (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    student_id  INT NOT NULL,
    job_id      INT NOT NULL,
    status      ENUM('pending','accepted','rejected') DEFAULT 'pending',
    cover_note  TEXT,
    applied_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_application (student_id, job_id),
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (job_id)     REFERENCES jobs(id)     ON DELETE CASCADE
);

-- -------------------------------------------------------
-- Sample company + jobs (for testing)
-- -------------------------------------------------------
INSERT INTO companies (company_name, email, password, industry, description, website, hr_contact) VALUES
('TechNova Solutions', 'hr@technova.com', 'company123', 'Information Technology',
 'Leading software company specialising in enterprise solutions.', 'https://technova.com', 'Jane Smith'),
('FinEdge Corp', 'recruit@finedge.com', 'company123', 'Finance',
 'Global financial services firm.', 'https://finedge.com', 'John Doe');

INSERT INTO jobs (company_id, title, description, eligibility, criteria, salary, location, deadline, job_type, status) VALUES
(1, 'Junior Software Developer',
 'Develop and maintain Java-based enterprise applications.',
 'BSc/BEng Computer Science or related field',
 'CGPA >= 3.0, Knowledge of Java, SQL',
 'Rs 25,000 / month', 'Port Louis', '2026-06-30', 'Job', 'available'),

(1, 'Internship - Web Developer',
 '3-month internship developing React + Spring Boot apps.',
 'Penultimate year students',
 'CGPA >= 2.8, HTML/CSS/JS basics',
 'Rs 10,000 / month', 'Ebene', '2026-05-15', 'Placement', 'available'),

(2, 'Financial Analyst Trainee',
 'Entry-level analyst position supporting the equity research team.',
 'BSc Finance / Accounting',
 'CGPA >= 3.2, Excel proficient',
 'Rs 22,000 / month', 'Port Louis', '2026-07-01', 'Job', 'available');
