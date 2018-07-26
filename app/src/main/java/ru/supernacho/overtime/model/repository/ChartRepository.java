package ru.supernacho.overtime.model.repository;

import java.util.List;

import io.reactivex.Observable;
import ru.supernacho.overtime.model.Entity.OverTimeEntity;

public class ChartRepository {
    private OverTimeStatRepository overTimeStatRepository;

    public ChartRepository(OverTimeStatRepository overTimeStatRepository) {
        this.overTimeStatRepository = overTimeStatRepository;
    }

    public Observable<List<OverTimeEntity>> getOverTimes(int month, int year, String userId){
        return overTimeStatRepository.getOverTimesByUserId(month, year, userId);
    }
}
