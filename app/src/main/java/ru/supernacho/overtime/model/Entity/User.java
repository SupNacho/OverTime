package ru.supernacho.overtime.model.Entity;

public class User {
    // TODO: 18.08.2018  ru.supernacho.overtime W/Firestore: (0.6.6-dev) [zzg]: No setter/field for userEmail found on class ru.supernacho.overtime.model.Entity.User
    private String userName;
    private String fullName;
    private String objectId;
    private String userEmail;
    private boolean isAdmin;

    public User() {
    }

    public User(String userId) {
        this.objectId = userId;
    }

    public User(String userName, String fullName) {
        this.userName = userName;
        this.fullName = fullName;
    }

    public User(String userId, String userName, String fullName, String email, boolean isAdmin) {
        this.objectId = userId;
        this.userName = userName;
        this.fullName = fullName;
        this.userEmail = email;
        this.isAdmin = isAdmin;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getObjectId() {
        return objectId;
    }

    public String getUserName() {
        return userName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
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
        return (getObjectId() != null ? getObjectId().equals(user.getObjectId()) : user.getObjectId() == null)
                && (getEmail() != null ? getEmail().equals(user.getEmail()) : user.getEmail() == null);
    }

    @Override
    public int hashCode() {
        int result = getUserName().hashCode();
        result = 31 * result + getFullName().hashCode();
        result = 31 * result + (getObjectId() != null ? getObjectId().hashCode() : 0);
        result = 31 * result + (getEmail() != null ? getEmail().hashCode() : 0);
        return result;
    }
}
