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

    public User(String userId, String userName, String fullName, String email, boolean isAdmin) {
        this.userId = userId;
        this.userName = userName;
        this.fullName = fullName;
        this.email = email;
        this.isAdmin = isAdmin;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        if (!getUserName().equals(user.getUserName())) return false;
        if (!getFullName().equals(user.getFullName())) return false;
        if (getUserId() != null ? !getUserId().equals(user.getUserId()) : user.getUserId() != null)
            return false;
        return getEmail() != null ? getEmail().equals(user.getEmail()) : user.getEmail() == null;
    }

    @Override
    public int hashCode() {
        int result = getUserName().hashCode();
        result = 31 * result + getFullName().hashCode();
        result = 31 * result + (getUserId() != null ? getUserId().hashCode() : 0);
        result = 31 * result + (getEmail() != null ? getEmail().hashCode() : 0);
        return result;
    }
}
