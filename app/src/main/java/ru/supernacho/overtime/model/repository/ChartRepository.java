package ru.supernacho.overtime.model.repository;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import ru.supernacho.overtime.model.Entity.CompanyEntity;
import ru.supernacho.overtime.model.Entity.OverTimeEntity;
import ru.supernacho.overtime.utils.NetworkStatus;

public class ChartRepository {
    private List<OverTimeEntity> overTimesList = new ArrayList<>();
    private String userId = ParseFields.userZero;

    public Observable<List<OverTimeEntity>> getOverTimes(int month, int year, String userId){
        return Observable.create( emit -> {
            ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseClass.OVER_TIME);
            if (userId != null) {
                this.userId = userId;
            } else {
                this.userId = ParseUser.getCurrentUser().getObjectId();
            }

            if (NetworkStatus.getStatus() == NetworkStatus.Status.OFFLINE){
                query
                        .fromLocalDatastore()
                        .whereEqualTo(ParseFields.createdBy, this.userId)
                        .whereEqualTo(ParseFields.monthNum, month)
                        .whereEqualTo(ParseFields.yearNum, year);
            } else {
                query
                        .whereEqualTo(ParseFields.createdBy, this.userId)
                        .whereEqualTo(ParseFields.monthNum, month)
                        .whereEqualTo(ParseFields.yearNum, year);
            }

            query.findInBackground((objects, e) -> {
                overTimesList.clear();
                if (objects != null && e == null){
                    for (ParseObject object : objects) {
                        Date start = object.getDate(ParseFields.startDate);
                        Date stop = object.getDate(ParseFields.stopDate);
                        String timeZoneID = object.getString(ParseFields.timeZoneID);
                        long duration = stop.getTime() - start.getTime();

                        ParseQuery<ParseObject> companyQuery = ParseQuery.getQuery(ParseClass.COMPANY);
                        ParseObject company = null;
                        CompanyEntity companyEntity;
                        try {
                            company = companyQuery.whereEqualTo(ParseFields.companyId, object.get(ParseFields.forCompany))
                                    .getFirst();
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                        if (company != null) {
                            companyEntity = new CompanyEntity(company.getObjectId(),
                                    company.getString(ParseFields.companyName),
                                    company.getString(ParseFields.companyAddress),
                                    company.getString(ParseFields.companyPhone),
                                    company.getString(ParseFields.companyEmail),
                                    company.getString(ParseFields.companyChief),
                                    company.getString(ParseFields.companyEmpPin));
                        } else {
                            companyEntity = new CompanyEntity("","No connection", false);
                        }

                        overTimesList.add(new OverTimeEntity(start, stop, timeZoneID, duration,
                                object.getString(ParseFields.comment), companyEntity));
                    }
                    emit.onNext(overTimesList);
                }
            });
            });
    }
}
