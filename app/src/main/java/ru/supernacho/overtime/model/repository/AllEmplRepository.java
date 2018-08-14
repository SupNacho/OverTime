package ru.supernacho.overtime.model.repository;

import java.util.List;

import io.reactivex.Observable;
import ru.supernacho.overtime.model.Entity.UserCompanyStat;

public class AllEmplRepository implements IAllEmplRepository{
    private IOverTimeStatRepository overTimeStatRepository;

    public AllEmplRepository(IOverTimeStatRepository overTimeStatRepository) {
        this.overTimeStatRepository = overTimeStatRepository;
    }

    @Override
    public Observable<List<UserCompanyStat>> getStats(int month, int year){
        return overTimeStatRepository.getStats(month, year);
    }

    @Override
    public Observable<String> getFullStatForShare(){
        return overTimeStatRepository.getFullStatForShare();
    }
}
