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
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.reactivex.Observable;
import ru.supernacho.overtime.model.Entity.OverTimeEntity;
import ru.supernacho.overtime.model.Entity.User;
import ru.supernacho.overtime.model.Entity.UserCompanyStat;
import ru.supernacho.overtime.utils.charts.DurationToStringConverter;

public class AllEmplRepository {
    private List<UserCompanyStat> stats = new ArrayList<>();
    private Map<User, Long> usersMap = new HashMap<>();

    public Observable<List<UserCompanyStat>> getStats(int month, int year){
        return Observable.create(emit -> {
            usersMap.clear();
            stats.clear();
            String currentCompanyId = getCurrentCompany().getString(ParseFields.userCompaniesActiveCompany);
            ParseQuery<ParseObject> overTimesQuery = ParseQuery.getQuery(ParseClass.OVER_TIME);
            overTimesQuery
                    .whereEqualTo(ParseFields.monthNum, month)
                    .whereEqualTo(ParseFields.yearNum, year)
                    .whereEqualTo(ParseFields.forCompany, currentCompanyId)
                    .findInBackground((objects, e) -> {
                        if (e == null && objects != null){
                            for (ParseObject overTime : objects) {

                                User userEntity = getUser(overTime);
                                addToMap(overTime, userEntity);
                            }

                            for (Map.Entry<User, Long> entry : usersMap.entrySet()){
                                stats.add(new UserCompanyStat(entry.getKey(), entry.getValue()));
                            }

                            emit.onNext(stats);
                        }
                    });
        });
    }

    private void addToMap(@NotNull ParseObject overTime, User userEntity) {
        long duration = overTime.getDate(ParseFields.stopDate).getTime()
                - overTime.getDate(ParseFields.startDate).getTime();
        if (usersMap.containsKey(userEntity)){
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

    @NonNull
    private ParseObject getCurrentCompany() throws com.parse.ParseException {
        ParseQuery<ParseObject> companyIdQuery = ParseQuery.getQuery(ParseClass.USER_COMPANIES);
        return companyIdQuery.whereEqualTo(ParseFields.userCompaniesUserId, ParseUser.getCurrentUser().getObjectId())
                .getFirst();
    }

    public Observable<String> getFullStatForShare(){
        return Observable.create( emit -> {
            StringBuilder stringBuilder = new StringBuilder();

            for (UserCompanyStat stat : stats) {
                formSummaryStrings(stringBuilder, stat);
                ParseQuery<ParseObject> employeeOverTimes = ParseQuery.getQuery(ParseClass.OVER_TIME);
                List<ParseObject> overTimes = null;
                try {
                    overTimes = employeeOverTimes.whereEqualTo(ParseFields.createdBy, stat.getUser().getUserId())
                            .whereEqualTo(ParseFields.forCompany,
                                    getCurrentCompany().getString(ParseFields.userCompaniesActiveCompany))
                            .find();
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
