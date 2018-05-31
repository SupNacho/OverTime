package ru.supernacho.overtime.model.repository;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import ru.supernacho.overtime.model.Entity.OverTimeEntity;

public class ChartRepository {
    private List<OverTimeEntity> overTimesList = new ArrayList<>();

    public Observable<List<OverTimeEntity>> getOverTimes(int month, int year){
        return Observable.create( emit -> {
            ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseClass.OVER_TIME);
            query
                    .whereEqualTo(ParseFields.createdBy, ParseUser.getCurrentUser().getObjectId())
                    .whereEqualTo(ParseFields.monthNum, month)
                    .whereEqualTo(ParseFields.yearNum, year)
                    .findInBackground((objects, e) -> {
                overTimesList.clear();
                if (objects != null && e == null){
                    for (ParseObject object : objects) {
                        Date start = object.getDate(ParseFields.startDate);
                        Date stop = object.getDate(ParseFields.stopDate);
                        String timeZoneID = object.getString(ParseFields.timeZoneID);
                        long duration = stop.getTime() - start.getTime();
                        overTimesList.add(new OverTimeEntity(start, stop, timeZoneID, duration,
                                object.getString(ParseFields.comment)));
                    }
                    emit.onNext(overTimesList);
                }
            });
            });
    }
}
