package ru.supernacho.overtime.model.repository;

import java.util.List;

import io.reactivex.Observable;
import ru.supernacho.overtime.model.Entity.UserCompanyStat;

public class AllEmplRepository {
    private OverTimeStatRepository overTimeStatRepository;

    public AllEmplRepository(OverTimeStatRepository overTimeStatRepository) {
        this.overTimeStatRepository = overTimeStatRepository;
    }

    public Observable<List<UserCompanyStat>> getStats(int month, int year){
        return overTimeStatRepository.getStats(month, year);
    }

    public Observable<String> getFullStatForShare(){
        return overTimeStatRepository.getFullStatForShare();
    }
}
