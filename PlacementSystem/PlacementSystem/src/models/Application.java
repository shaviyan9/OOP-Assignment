package models;

import java.sql.Timestamp;

public class Application {
    private int id;
    private int studentId;
    private int jobId;
    private String studentName;   // joined
    private String jobTitle;      // joined
    private String companyName;   // joined
    private String status;        // pending / accepted / rejected
    private String coverNote;
    private Timestamp appliedAt;
    private Timestamp updatedAt;

    public Application() {}

    public int getId()                          { return id; }
    public void setId(int id)                   { this.id = id; }

    public int getStudentId()                   { return studentId; }
    public void setStudentId(int studentId)     { this.studentId = studentId; }

    public int getJobId()                       { return jobId; }
    public void setJobId(int jobId)             { this.jobId = jobId; }

    public String getStudentName()              { return studentName; }
    public void setStudentName(String n)        { this.studentName = n; }

    public String getJobTitle()                 { return jobTitle; }
    public void setJobTitle(String t)           { this.jobTitle = t; }

    public String getCompanyName()              { return companyName; }
    public void setCompanyName(String n)        { this.companyName = n; }

    public String getStatus()                   { return status; }
    public void setStatus(String status)        { this.status = status; }

    public String getCoverNote()                { return coverNote; }
    public void setCoverNote(String coverNote)  { this.coverNote = coverNote; }

    public Timestamp getAppliedAt()             { return appliedAt; }
    public void setAppliedAt(Timestamp t)       { this.appliedAt = t; }

    public Timestamp getUpdatedAt()             { return updatedAt; }
    public void setUpdatedAt(Timestamp t)       { this.updatedAt = t; }
}
