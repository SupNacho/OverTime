package ru.supernacho.overtime.model.repository;

import io.reactivex.Observable;

public interface ILogRepository {
    Observable<Object[]> getMonths(String userId);
    Observable<Object[]> getAllEmployeesMonths();
}
