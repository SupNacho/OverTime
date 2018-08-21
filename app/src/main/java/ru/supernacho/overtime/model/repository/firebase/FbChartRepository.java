package ru.supernacho.overtime.model.repository.firebase;

import java.util.List;

import io.reactivex.Observable;
import ru.supernacho.overtime.model.Entity.OverTimeEntity;
import ru.supernacho.overtime.model.repository.IChartRepository;
import ru.supernacho.overtime.model.repository.IOverTimeStatRepository;

public class FbChartRepository implements IChartRepository{
    private IOverTimeStatRepository overTimeStatRepository;

    public FbChartRepository(IOverTimeStatRepository overTimeStatRepository) {
        this.overTimeStatRepository = overTimeStatRepository;
    }

    @Override
    public Observable<List<OverTimeEntity>> getOverTimes(int month, int year, String userId, String forCompany){
        return overTimeStatRepository.getOverTimesByUserId(month, year, userId, forCompany);
    }
}
