package ru.supernacho.overtime.model.Entity;

import java.util.List;

public class UserCompaniesEntity {
    private String activeCompany;
    private String userId;
    private List<String> companies;

    public UserCompaniesEntity() {
    }

    public UserCompaniesEntity(String userId) {
        this.userId = userId;
    }

    public String getActiveCompany() {
        return activeCompany;
    }

    public String getUserId() {
        return userId;
    }

    public List<String> getCompanies() {
        return companies;
    }

    public void setActiveCompany(String activeCompany) {
        this.activeCompany = activeCompany;
    }
}
