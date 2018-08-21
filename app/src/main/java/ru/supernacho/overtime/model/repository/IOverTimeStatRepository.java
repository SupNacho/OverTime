package ru.supernacho.overtime.model.repository;

import java.util.List;

import io.reactivex.Observable;
import ru.supernacho.overtime.model.Entity.OverTimeEntity;
import ru.supernacho.overtime.model.Entity.UserCompanyStat;

public interface IOverTimeStatRepository {
    Observable<Object[]> getMonthsByUserId(String userId);

    Observable<Object[]> getAllEmployeesMonths();

    Observable<List<UserCompanyStat>> getStats(int month, int year);

    Observable<String> getFullStatForShare();

    Observable<List<OverTimeEntity>> getOverTimesByUserId(int month, int year, String userId, String forCompany);
}
