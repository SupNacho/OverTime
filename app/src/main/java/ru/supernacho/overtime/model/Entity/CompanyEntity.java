package ru.supernacho.overtime.model.Entity;

public class CompanyEntity {
    private final String objectId;
    private String name;
    private String address;
    private String phone;
    private String email;
    private String chief;
    private String pin;
    private boolean isActive;

    public CompanyEntity(String objectId, String name, boolean isActive) {
        this.objectId = objectId;
        this.name = name;
        this.address = "";
        this.phone = "";
        this.email = "";
        this.chief = "";
        this.pin = "";
        this.isActive = isActive;
    }
    public CompanyEntity(String objectId, String name, String address, String phone, String email, String chief, String pin) {
        this.objectId = objectId;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.chief = chief;
        this.pin = pin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getChief() {
        return chief;
    }

    public void setChief(String chief) {
        this.chief = chief;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getPin() {
        return pin;
    }

    public String getObjectId() {
        return objectId;
    }
}
