package ru.supernacho.overtime.model.Entity;

public class User {
    private final String userName;
    private String email;

    public User(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
