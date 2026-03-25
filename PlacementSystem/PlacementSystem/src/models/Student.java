package models;

import java.sql.Timestamp;

public class Student {
    private int id;
    private String fullName;
    private String username;
    private String email;
    private String password;
    private String phone;
    private String course;
    private String branch;
    private String section;
    private double cgpa;
    private String resumePath;
    private String status;
    private Timestamp createdAt;

    public Student() {}

    public Student(int id, String fullName, String username, String email,
                   String password, String phone, String course, String branch,
                   String section, double cgpa, String resumePath, String status) {
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.course = course;
        this.branch = branch;
        this.section = section;
        this.cgpa = cgpa;
        this.resumePath = resumePath;
        this.status = status;
    }

    // ---- Getters & Setters ----
    public int getId()                          { return id; }
    public void setId(int id)                   { this.id = id; }

    public String getFullName()                 { return fullName; }
    public void setFullName(String fullName)    { this.fullName = fullName; }

    public String getUsername()                 { return username; }
    public void setUsername(String username)    { this.username = username; }

    public String getEmail()                    { return email; }
    public void setEmail(String email)          { this.email = email; }

    public String getPassword()                 { return password; }
    public void setPassword(String password)    { this.password = password; }

    public String getPhone()                    { return phone; }
    public void setPhone(String phone)          { this.phone = phone; }

    public String getCourse()                   { return course; }
    public void setCourse(String course)        { this.course = course; }

    public String getBranch()                   { return branch; }
    public void setBranch(String branch)        { this.branch = branch; }

    public String getSection()                  { return section; }
    public void setSection(String section)      { this.section = section; }

    public double getCgpa()                     { return cgpa; }
    public void setCgpa(double cgpa)            { this.cgpa = cgpa; }

    public String getResumePath()               { return resumePath; }
    public void setResumePath(String resumePath){ this.resumePath = resumePath; }

    public String getStatus()                   { return status; }
    public void setStatus(String status)        { this.status = status; }

    public Timestamp getCreatedAt()             { return createdAt; }
    public void setCreatedAt(Timestamp t)       { this.createdAt = t; }

    @Override
    public String toString() {
        return fullName + " (" + username + ")";
    }
}
