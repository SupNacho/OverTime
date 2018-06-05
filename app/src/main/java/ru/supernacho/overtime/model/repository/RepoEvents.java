package ru.supernacho.overtime.model.repository;

public enum RepoEvents {
    LOGIN_SUCCESS (0), LOGIN_FAILED_NO_CONNECTION (1), LOGIN_FAILED_WRONG_PASS (101), LOGIN_NEEDED (2),
    REGISTRATION_SUCCESS (7), REGISTRATION_FAILED (200), REGISTRATION_FAILED_USERNAME (202), REGISTRATION_FAILED_EMAIL (203);

    private final int code;

    RepoEvents(int code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
