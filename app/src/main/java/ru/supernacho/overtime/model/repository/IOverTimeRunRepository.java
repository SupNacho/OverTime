package ru.supernacho.overtime.model.repository;

import io.reactivex.Observable;

public interface IOverTimeRunRepository {
    void addComment(String comment);

    void startOverTime(String comment, String companyId);

    void stopOverTime(String comment);

    Observable<Long> restoreTimerState();
}
