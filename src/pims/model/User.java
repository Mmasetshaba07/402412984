package pims.model;

public class User {
    private int userId;
    private String username, password, role, fullName;

    public User(int userId, String username, String password, String role, String fullName) {
        this.userId = userId; this.username = username;
        this.password = password; this.role = role; this.fullName = fullName;
    }
    public int getUserId()      { return userId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole()     { return role; }
    public String getFullName() { return fullName; }
    public boolean isAdmin()    { return "Admin".equalsIgnoreCase(role); }
}
// User model class
