package ru.supernacho.overtime.model.repository;

import io.reactivex.Observable;

public class TimerRepository implements ITimerRepository {
    private IOverTimeRunRepository overTimeRunRepository;

    public TimerRepository(IOverTimeRunRepository overTimeRunRepository) {
        this.overTimeRunRepository = overTimeRunRepository;
    }

    @Override
    public void startOverTime(String comment, String companyId) {
        overTimeRunRepository.startOverTime(comment, companyId);
    }

    @Override
    public void addComment(String comment) {
        overTimeRunRepository.addComment(comment);
    }

    @Override
    public void stopOverTime(String comment) {
        overTimeRunRepository.stopOverTime(comment);

    }

    @Override
    public Observable<Long> restoreTimerState() {
        return overTimeRunRepository.restoreTimerState();
    }
}
