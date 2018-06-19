package ru.supernacho.overtime.model.repository;

public interface ParseFields {
    //OverTime class fields
    String fullName = "fullName";
    String isAdmin = "isAdmin";
    String createdBy = "createdById";
    String startDate = "startDate";
    String stopDate = "stopDate";
    String comment = "comment";
    String createdAt = "createdAt";
    String monthNum = "monthNum";
    String yearNum = "yearNum";
    String timeZoneID = "timeZoneID";
    String forCompany = "forCompany";
    //User class fields
    String userId = "objectId";
    String userName = "username";
    String userEmail = "userEmail";
    String userCompany = "companyId";
    String userZero = "0"; // ID for null userId
    //Company class field
    String companyId = "objectId";
    String companyName = "name";
    String companyAddress = "address";
    String companyPhone = "phone";
    String companyEmail = "email";
    String companyChief = "chief";
    String companyAdminPin = "adminPin";
    String companyEmpPin = "empPin";
    String companyAdmins = "admins";
    //UserCompanies
    String userCompaniesUserId = "userId";
    String userCompaniesCompanies = "companies";
    String userCompaniesActiveCompany = "activeCompany";

}
