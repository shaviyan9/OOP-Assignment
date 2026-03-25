package models;

import java.sql.Timestamp;

public class Company {
    private int id;
    private String companyName;
    private String email;
    private String password;
    private String industry;
    private String description;
    private String website;
    private String hrContact;
    private String status;
    private Timestamp createdAt;

    public Company() {}

    public Company(int id, String companyName, String email, String password,
                   String industry, String description, String website,
                   String hrContact, String status) {
        this.id = id;
        this.companyName = companyName;
        this.email = email;
        this.password = password;
        this.industry = industry;
        this.description = description;
        this.website = website;
        this.hrContact = hrContact;
        this.status = status;
    }

    public int getId()                              { return id; }
    public void setId(int id)                       { this.id = id; }

    public String getCompanyName()                  { return companyName; }
    public void setCompanyName(String companyName)  { this.companyName = companyName; }

    public String getEmail()                        { return email; }
    public void setEmail(String email)              { this.email = email; }

    public String getPassword()                     { return password; }
    public void setPassword(String password)        { this.password = password; }

    public String getIndustry()                     { return industry; }
    public void setIndustry(String industry)        { this.industry = industry; }

    public String getDescription()                  { return description; }
    public void setDescription(String description)  { this.description = description; }

    public String getWebsite()                      { return website; }
    public void setWebsite(String website)          { this.website = website; }

    public String getHrContact()                    { return hrContact; }
    public void setHrContact(String hrContact)      { this.hrContact = hrContact; }

    public String getStatus()                       { return status; }
    public void setStatus(String status)            { this.status = status; }

    public Timestamp getCreatedAt()                 { return createdAt; }
    public void setCreatedAt(Timestamp t)           { this.createdAt = t; }

    @Override
    public String toString() { return companyName; }
}
