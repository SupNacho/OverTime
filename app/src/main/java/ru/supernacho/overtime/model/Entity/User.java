package ru.supernacho.overtime.model.Entity;

public class User {
    private final String userName;
    private final String fullName;
    private final String userId;
    private String email;
    private boolean isAdmin;

    public User(String userName, String fullName) {
        this.userId = null;
        this.userName = userName;
        this.fullName = fullName;
    }

    public User(String userId, String userName, String fullName, String email) {
        this.userId = userId;
        this.userName = userName;
        this.fullName = fullName;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
