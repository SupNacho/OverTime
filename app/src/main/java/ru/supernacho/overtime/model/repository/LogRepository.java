package ru.supernacho.overtime.model.repository;

import io.reactivex.Observable;

public class LogRepository implements ILogRepository {
    private IOverTimeStatRepository overTimeStatRepository;

    public LogRepository(IOverTimeStatRepository overTimeStatRepository) {
        this.overTimeStatRepository = overTimeStatRepository;
    }

    @Override
    public Observable<Object[]> getMonths(String userId) {
        return overTimeStatRepository.getMonthsByUserId(userId);
    }

    @Override
    public Observable<Object[]> getAllEmployeesMonths() {
        return overTimeStatRepository.getAllEmployeesMonths();
    }
}
