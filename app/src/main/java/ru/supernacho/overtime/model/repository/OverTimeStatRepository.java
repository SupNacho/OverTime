package ru.supernacho.overtime.model.repository;

import android.support.annotation.NonNull;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import ru.supernacho.overtime.model.Entity.CompanyEntity;
import ru.supernacho.overtime.model.Entity.DateChooserEntry;
import ru.supernacho.overtime.model.Entity.OverTimeEntity;
import ru.supernacho.overtime.model.Entity.User;
import ru.supernacho.overtime.model.Entity.UserCompanyStat;
import ru.supernacho.overtime.utils.NetworkStatus;
import ru.supernacho.overtime.utils.charts.DurationToStringConverter;

public class OverTimeStatRepository {
    private List<UserCompanyStat> stats;
    private Map<User, Long> usersMap;
    private List<OverTimeEntity> overTimesList;
    private String userId = ParseFields.userZero;
    private List<ParseObject> list;
    private Set<DateChooserEntry> set;
    private UserCompanyRepository userCompanyRepository;
    private CompanyRepository companyRepository;


    public OverTimeStatRepository(UserCompanyRepository userCompanyRepository, CompanyRepository companyRepository) {
        this.userCompanyRepository = userCompanyRepository;
        this.companyRepository = companyRepository;
        this.stats = new ArrayList<>();
        this.usersMap = new HashMap<>();
        this.overTimesList = new ArrayList<>();
        this.set = new HashSet<>();
    }

    public Observable<Object[]> getMonthsByUserId(String userId) {
        return Observable.create(emit -> {
            String user;
            if (userId.equals(ParseFields.userZero)) {
                user = ParseUser.getCurrentUser().getObjectId();
            } else {
                user = userId;
            }
            formDatesArray(emit, user, ParseFields.createdBy);
        });

    }

    public Observable<Object[]> getAllEmployeesMonths() {
        return Observable.create(emit -> {
            String companyId = userCompanyRepository.getActiveCompanyId();
            formDatesArray(emit, companyId, ParseFields.forCompany);
        });
    }

    private void formDatesArray(ObservableEmitter<Object[]> emit, String fieldContent, String fieldName) {
        ParseQuery<ParseObject> query = new ParseQuery<>(ParseClass.OVER_TIME);
        if (NetworkStatus.getStatus() == NetworkStatus.Status.OFFLINE) {
            query
                    .fromLocalDatastore()
                    .whereEqualTo(fieldName, fieldContent)
            ;
        } else {
            query.whereEqualTo(fieldName, fieldContent);
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
    }

    public Observable<List<UserCompanyStat>> getStats(int month, int year) {
        usersMap.clear();
        stats.clear();
        return Observable.create(emit -> {
            ParseQuery<ParseObject> overTimesQuery = ParseQuery.getQuery(ParseClass.OVER_TIME);
            overTimesQuery
                    .whereEqualTo(ParseFields.monthNum, month)
                    .whereEqualTo(ParseFields.yearNum, year)
                    .whereEqualTo(ParseFields.forCompany, userCompanyRepository.getActiveCompanyId())
                    .findInBackground((objects, e) -> {
                        if (e == null && objects != null) {
                            for (ParseObject overTime : objects) {

                                User userEntity = getUser(overTime);
                                addToMap(overTime, userEntity);
                            }

                            for (Map.Entry<User, Long> entry : usersMap.entrySet()) {
                                stats.add(new UserCompanyStat(entry.getKey(), entry.getValue()));
                            }

                            emit.onNext(stats);
                        }
                    });
        });
    }

    public Observable<String> getFullStatForShare() {
        return Observable.create(emit -> {
            StringBuilder stringBuilder = new StringBuilder();

            for (UserCompanyStat stat : stats) {
                formSummaryStrings(stringBuilder, stat);
                List<ParseObject> overTimes = null;
                try {
                    overTimes = getOverTimes(stat);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (overTimes != null) {
                    formDetailStrings(stringBuilder, overTimes);
                }
            }
            emit.onNext(stringBuilder.toString());
        });
    }

    public Observable<List<OverTimeEntity>> getOverTimesByUserId(int month, int year, String userId, String forCompany) {
        return Observable.create(emit -> {
            Date zeroTime = new Date();
            zeroTime.setTime(0);
            ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseClass.OVER_TIME);
            if (userId != null) {
                this.userId = userId;
            } else {
                this.userId = ParseUser.getCurrentUser().getObjectId();
            }

//            if (NetworkStatus.getStatus() == NetworkStatus.Status.OFFLINE) {
//                query
//                        .fromLocalDatastore()
//                        .whereEqualTo(ParseFields.createdBy, this.userId)
//                        .whereEqualTo(ParseFields.monthNum, month)
//                        .whereEqualTo(ParseFields.yearNum, year)
//                        .whereNotEqualTo(ParseFields.stopDate, zeroTime);
//                if (forCompany != null) {
//                    query.whereEqualTo(ParseFields.forCompany, forCompany);
//                }
//            } else {
                query
                        .whereEqualTo(ParseFields.createdBy, this.userId)
                        .whereEqualTo(ParseFields.monthNum, month)
                        .whereEqualTo(ParseFields.yearNum, year)
                        .whereNotEqualTo(ParseFields.stopDate, zeroTime);
                if (forCompany != null) {
                    query.whereEqualTo(ParseFields.forCompany, forCompany);
                }
//            }

            query.findInBackground((objects, e) -> {
                overTimesList.clear();
                if (objects != null && e == null) {
                    for (ParseObject object : objects) {
                        Date start = object.getDate(ParseFields.startDate);
                        Date stop = object.getDate(ParseFields.stopDate);
                        String timeZoneID = object.getString(ParseFields.timeZoneID);
                        long duration = stop.getTime() - start.getTime();
                        CompanyEntity companyEntity = companyRepository.getCompanyByOverTime(object);

                        overTimesList.add(new OverTimeEntity(start, stop, timeZoneID, duration,
                                object.getString(ParseFields.comment), companyEntity));
                    }
                    emit.onNext(overTimesList);
                }
            });
        });
    }

    private List<ParseObject> getOverTimes(@NotNull UserCompanyStat stat) throws ParseException {
        ParseQuery<ParseObject> employeeOverTimes = ParseQuery.getQuery(ParseClass.OVER_TIME);
        return employeeOverTimes.whereEqualTo(ParseFields.createdBy, stat.getUser().getUserId())
                .whereEqualTo(ParseFields.forCompany,
                        userCompanyRepository.getActiveCompanyId())
                .find();
    }

    private void addToMap(@NotNull ParseObject overTime, User userEntity) {
        long duration = overTime.getDate(ParseFields.stopDate).getTime()
                - overTime.getDate(ParseFields.startDate).getTime();
        if (usersMap.containsKey(userEntity)) {
            usersMap.put(userEntity, usersMap.get(userEntity)
                    + duration);
        } else {
            usersMap.put(userEntity, duration);
        }
    }

    @NonNull
    private User getUser(@NotNull ParseObject overTime) {
        String userId = overTime.getString(ParseFields.createdBy);
        ParseQuery<ParseObject> userQuery = ParseQuery.getQuery(ParseClass.PARSE_USER);
        ParseObject user = null;
        try {
            user = userQuery.whereEqualTo(ParseFields.userId, userId)
                    .getFirst();
        } catch (ParseException e1) {
            e1.printStackTrace();
        }

        return new User(Objects.requireNonNull(user).getObjectId(),
                user.getString(ParseFields.userName),
                user.getString(ParseFields.fullName),
                user.getString(ParseFields.userEmail),
                false);
    }

    private void formSummaryStrings(StringBuilder stringBuilder, UserCompanyStat stat) {
        stringBuilder
                .append("Employee: ").append(stat.getUser().getFullName()).append("\n")
                .append("Total overtime: ").append(DurationToStringConverter.convert(stat.getTimeSummary()))
                .append("\n");
    }

    private void formDetailStrings(StringBuilder stringBuilder, List<ParseObject> overTimes) {
        for (ParseObject overTime : overTimes) {
            Date start = overTime.getDate(ParseFields.startDate);
            Date stop = overTime.getDate(ParseFields.stopDate);
            String timeZoneID = overTime.getString(ParseFields.timeZoneID);
            String otComment = overTime.getString(ParseFields.comment);
            OverTimeEntity overTimeEntity = new OverTimeEntity(start, stop, timeZoneID,
                    stop.getTime() - start.getTime(), otComment, null);
            stringBuilder
                    .append("\n")
                    .append("    Start time: ").append(overTimeEntity.getStartDateTimeLabel())
                    .append("\n").append("    Finish time: ").append(overTimeEntity.getStopDateTimeLabel())
                    .append("\n").append("    Duration: ").append(overTimeEntity.getDurationString())
                    .append("\n").append("    Comment for this overtime: ").append("\n")
                    .append(overTimeEntity.getComment())
                    .append("\n");
        }
        stringBuilder.append("===================================").append("\n\n");
    }
}
