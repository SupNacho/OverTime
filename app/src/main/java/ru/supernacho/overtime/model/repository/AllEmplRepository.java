package ru.supernacho.overtime.model.repository;

import android.support.annotation.NonNull;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.reactivex.Observable;
import ru.supernacho.overtime.model.Entity.User;
import ru.supernacho.overtime.model.Entity.UserCompanyStat;

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

        return new User(Objects.requireNonNull(user).getString(ParseFields.userId),
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

    public List<UserCompanyStat> getStatsList() {
        return stats;
    }
}
