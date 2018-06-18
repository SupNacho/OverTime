package ru.supernacho.overtime.model.repository;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import ru.supernacho.overtime.model.Entity.User;

public class EmployeeRepository {
    private List<User> employees;

    public EmployeeRepository() {
        employees = new ArrayList<>();
    }

    public List<User> getEmployeesList() {
        return employees;
    }

    public Observable<Boolean> getEmployees() {
        return Observable.create(emit -> {
            employees.clear();
            String[] activeCompanyId = new String[1];
            ParseQuery<ParseObject> userQuery = ParseQuery.getQuery(ParseClass.USER_COMPANIES);
            userQuery.whereEqualTo(ParseFields.userCompaniesUserId, ParseUser.getCurrentUser().getObjectId())
                    .findInBackground((users, e1) -> {
                        if (users != null && users.size() > 0) {
                            activeCompanyId[0] = users.get(0).getString(ParseFields.userCompaniesActiveCompany);

                            ParseQuery<ParseObject> filterForCompany = ParseQuery.getQuery(ParseClass.USER_COMPANIES);
                            filterForCompany.whereEqualTo(ParseFields.userCompaniesCompanies,
                                    activeCompanyId[0]);

                            ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseClass.PARSE_USER);
                            query
                                    .whereMatchesKeyInQuery(ParseFields.userId, ParseFields.userCompaniesUserId,
                                            filterForCompany)
                                    .findInBackground((objects, e) -> {
                                        if (objects != null && e == null) {
                                            for (ParseObject object : objects) {
                                                object.getString(ParseFields.userId);
                                                employees.add(new User(object.getObjectId(),
                                                        object.getString(ParseFields.userName),
                                                        object.getString(ParseFields.fullName),
                                                        object.getString(ParseFields.userEmail)));
                                            }
                                            emit.onNext(true);
                                        } else {
                                            emit.onNext(false);
                                        }
                                    });
                        }
                    });
        });
    }
}
