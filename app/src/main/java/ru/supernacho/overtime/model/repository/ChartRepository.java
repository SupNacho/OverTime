package ru.supernacho.overtime.model.repository;

import java.util.List;

import io.reactivex.Observable;
import ru.supernacho.overtime.model.Entity.OverTimeEntity;
import ru.supernacho.overtime.model.repository.IChartRepository;
import ru.supernacho.overtime.model.repository.IOverTimeStatRepository;

public class ChartRepository implements IChartRepository{
    private IOverTimeStatRepository overTimeStatRepository;

    public ChartRepository(IOverTimeStatRepository overTimeStatRepository) {
        this.overTimeStatRepository = overTimeStatRepository;
    }

    @Override
    public Observable<List<OverTimeEntity>> getOverTimes(int month, int year, String userId, String forCompany){
        return overTimeStatRepository.getOverTimesByUserId(month, year, userId, forCompany);
    }
}
