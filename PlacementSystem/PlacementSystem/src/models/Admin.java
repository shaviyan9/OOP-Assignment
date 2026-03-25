package models;

public class Admin {
    private int id;
    private String username;
    private String email;
    private String password;

    public Admin() {}
    public Admin(int id, String username, String email, String password) {
        this.id = id; this.username = username;
        this.email = email; this.password = password;
    }

    public int getId()                      { return id; }
    public void setId(int id)               { this.id = id; }

    public String getUsername()             { return username; }
    public void setUsername(String u)       { this.username = u; }

    public String getEmail()               { return email; }
    public void setEmail(String e)         { this.email = e; }

    public String getPassword()            { return password; }
    public void setPassword(String p)      { this.password = p; }
}
