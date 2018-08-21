package ru.supernacho.overtime.model.repository;

import java.util.List;

import io.reactivex.Observable;
import ru.supernacho.overtime.model.Entity.OverTimeEntity;

public interface IChartRepository {
    Observable<List<OverTimeEntity>> getOverTimes(int month, int year, String userId, String forCompany);
}
