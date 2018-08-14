package ru.supernacho.overtime.model.repository;

import java.util.List;

import io.reactivex.Observable;
import ru.supernacho.overtime.model.Entity.UserCompanyStat;

public interface IAllEmplRepository {
    Observable<List<UserCompanyStat>> getStats(int month, int year);

    Observable<String> getFullStatForShare();
}
