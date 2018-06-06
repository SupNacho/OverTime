package ru.supernacho.overtime.model.Entity;

public class User {
    private final String userName;
    private final String fullName;
    private String email;

    public User(String userName, String fullName) {
        this.userName = userName;
        this.fullName = fullName;
    }

    public User(String userName, String fullName, String email) {
        this.userName = userName;
        this.fullName = fullName;
        this.email = email;
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
}
