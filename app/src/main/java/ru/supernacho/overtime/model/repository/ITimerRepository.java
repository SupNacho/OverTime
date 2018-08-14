package ru.supernacho.overtime.model.repository;

import io.reactivex.Observable;

public interface ITimerRepository {
    void startOverTime(String comment, String companyId);

    void addComment(String comment);

    void stopOverTime(String comment);

    Observable<Long> restoreTimerState();
}
