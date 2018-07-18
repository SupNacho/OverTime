package ru.supernacho.overtime.model.repository;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.Observable;
import ru.supernacho.overtime.model.Entity.DateChooserEntry;
import ru.supernacho.overtime.utils.NetworkStatus;

public class LogRepository {
    private List<ParseObject> list;
    private Set<DateChooserEntry> set = new HashSet<>();


    public Observable<Object[]> getMonths(String userId) {
        return Observable.create(emit -> {
            String user;
            if (userId.equals(ParseFields.userZero)) {
                user = ParseUser.getCurrentUser().getObjectId();
            } else {
                user = userId;
            }
            ParseQuery<ParseObject> query = new ParseQuery<>(ParseClass.OVER_TIME);
            if (NetworkStatus.getStatus() == NetworkStatus.Status.OFFLINE) {
                query
                        .fromLocalDatastore()
                        .whereEqualTo(ParseFields.createdBy, user)
                        ;
            } else {
                query.whereEqualTo(ParseFields.createdBy, user);
            }

            query.findInBackground((objects, e) -> {
                if (objects != null && e == null) {
                    set.clear();
                    list = objects;
                    for (ParseObject parseObject : list) {
                        set.add(new DateChooserEntry(parseObject.getInt(ParseFields.monthNum), parseObject.getInt(ParseFields.yearNum)));
                    }
                    emit.onNext(set.toArray());
                }
            });
        });

    }

    public Observable<Object[]> getAllEmployeesMonths() {
        return Observable.create(emit -> {
            ParseQuery<ParseObject> companyIdQuery = ParseQuery.getQuery(ParseClass.USER_COMPANIES);
            ParseObject company = companyIdQuery.whereEqualTo(ParseFields.userCompaniesUserId, ParseUser.getCurrentUser().getObjectId())
                    .getFirst();
            String companyId = company.getString(ParseFields.companyId);
            ParseQuery<ParseObject> query = new ParseQuery<>(ParseClass.OVER_TIME);
            if (NetworkStatus.getStatus() == NetworkStatus.Status.OFFLINE) {
                query
                        .fromLocalDatastore()
                        .whereEqualTo(ParseFields.forCompany, companyId)
                        ;
            } else {
                query.whereEqualTo(ParseFields.forCompany, companyId);
            }

            query.findInBackground((objects, e) -> {
                if (objects != null && e == null) {
                    set.clear();
                    list = objects;
                    for (ParseObject parseObject : list) {
                        set.add(new DateChooserEntry(parseObject.getInt(ParseFields.monthNum), parseObject.getInt(ParseFields.yearNum)));
                    }
                    emit.onNext(set.toArray());
                }
            });
        });

    }
}
