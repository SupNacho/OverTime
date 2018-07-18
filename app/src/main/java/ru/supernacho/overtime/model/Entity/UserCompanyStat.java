package ru.supernacho.overtime.model.Entity;

public class UserCompanyStat {
    private final User user;
    private final long timeSummary;

    public UserCompanyStat(User user, long timeSummary) {
        this.user = user;
        this.timeSummary = timeSummary;
    }

    public User getUser() {
        return user;
    }

    public long getTimeSummary() {
        return timeSummary;
    }
}
