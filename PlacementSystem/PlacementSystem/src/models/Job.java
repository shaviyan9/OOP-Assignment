package models;

import java.sql.Date;
import java.sql.Timestamp;

public class Job {
    private int id;
    private int companyId;
    private String companyName;   // joined field for display
    private String title;
    private String description;
    private String eligibility;
    private String criteria;
    private String salary;
    private String location;
    private Date deadline;
    private String jobType;       // "placement" or "job"
    private String status;        // "available", "secured", "closed"
    private Timestamp createdAt;

    public Job() {}

    public Job(int id, int companyId, String title, String description,
               String eligibility, String criteria, String salary,
               String location, Date deadline, String jobType, String status) {
        this.id = id;
        this.companyId = companyId;
        this.title = title;
        this.description = description;
        this.eligibility = eligibility;
        this.criteria = criteria;
        this.salary = salary;
        this.location = location;
        this.deadline = deadline;
        this.jobType = jobType;
        this.status = status;
    }

    public int getId()                          { return id; }
    public void setId(int id)                   { this.id = id; }

    public int getCompanyId()                   { return companyId; }
    public void setCompanyId(int companyId)     { this.companyId = companyId; }

    public String getCompanyName()              { return companyName; }
    public void setCompanyName(String n)        { this.companyName = n; }

    public String getTitle()                    { return title; }
    public void setTitle(String title)          { this.title = title; }

    public String getDescription()              { return description; }
    public void setDescription(String d)        { this.description = d; }

    public String getEligibility()              { return eligibility; }
    public void setEligibility(String e)        { this.eligibility = e; }

    public String getCriteria()                 { return criteria; }
    public void setCriteria(String c)           { this.criteria = c; }

    public String getSalary()                   { return salary; }
    public void setSalary(String salary)        { this.salary = salary; }

    public String getLocation()                 { return location; }
    public void setLocation(String location)    { this.location = location; }

    public Date getDeadline()                   { return deadline; }
    public void setDeadline(Date deadline)      { this.deadline = deadline; }

    public String getJobType()                  { return jobType; }
    public void setJobType(String jobType)      { this.jobType = jobType; }

    public String getStatus()                   { return status; }
    public void setStatus(String status)        { this.status = status; }

    public Timestamp getCreatedAt()             { return createdAt; }
    public void setCreatedAt(Timestamp t)       { this.createdAt = t; }

    @Override
    public String toString() { return title + " [" + companyName + "]"; }
}
