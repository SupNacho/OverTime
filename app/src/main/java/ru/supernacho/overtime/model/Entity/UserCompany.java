package ru.supernacho.overtime.model.Entity;

public class UserCompany {
    private String companyId;
    private boolean isAdmin;

    public UserCompany(String companyId, boolean isAdmin) {
        this.companyId = companyId;
        this.isAdmin = isAdmin;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
