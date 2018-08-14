package ru.supernacho.overtime.model.repository.firebase;

import java.util.List;

import io.reactivex.Observable;
import ru.supernacho.overtime.model.Entity.UserCompanyStat;
import ru.supernacho.overtime.model.repository.IAllEmplRepository;
import ru.supernacho.overtime.model.repository.IOverTimeStatRepository;

public class FbAllEmplRepository implements IAllEmplRepository{
    private IOverTimeStatRepository overTimeStatRepository;

    public FbAllEmplRepository(IOverTimeStatRepository overTimeStatRepository) {
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
