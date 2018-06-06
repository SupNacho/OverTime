package ru.supernacho.overtime.model.repository;

public interface ParseFields {
    //OverTime class fields
    String fullName = "fullName";
    String createdBy = "createdById";
    String startDate = "startDate";
    String stopDate = "stopDate";
    String comment = "comment";
    String createdAt = "createdAt";
    String monthNum = "monthNum";
    String yearNum = "yearNum";
    String timeZoneID = "timeZoneID";
    //User class fields
    String userId = "objectId";
    String email = "email";
}
