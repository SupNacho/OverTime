package ru.supernacho.overtime.model.repository;

import io.reactivex.Observable;

public class TimerRepository {
    private OverTimeRunRepository overTimeRunRepository;

    public TimerRepository(OverTimeRunRepository overTimeRunRepository) {
        this.overTimeRunRepository = overTimeRunRepository;
    }

    public void startOverTime(String comment, String companyId) {
        overTimeRunRepository.startOverTime(comment,companyId);
    }

    public void addComment(String comment){
        overTimeRunRepository.addComment(comment);
    }

    public void stopOverTime(String comment) {
        overTimeRunRepository.stopOverTime(comment);

    }

    public Observable<Long> restoreTimerState(){
        return overTimeRunRepository.restoreTimerState();
    }
}
