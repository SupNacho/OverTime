package ru.supernacho.overtime.model.repository;

import io.reactivex.Observable;

public class LogRepository {
    private OverTimeStatRepository overTimeStatRepository;

    public LogRepository(OverTimeStatRepository overTimeStatRepository) {
        this.overTimeStatRepository = overTimeStatRepository;
    }

    public Observable<Object[]> getMonths(String userId) {
        return overTimeStatRepository.getMonthsByUserId(userId);
    }

    public Observable<Object[]> getAllEmployeesMonths() {
        return overTimeStatRepository.getAllEmployeesMonths();
    }
}
