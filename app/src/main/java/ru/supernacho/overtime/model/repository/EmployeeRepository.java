package ru.supernacho.overtime.model.repository;

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

    public List<User> getEmployeesList(){
        return employees;
    }

    public Observable<Boolean> getEmployees(){
        return Observable.create( emit -> {
            employees.clear();
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query
                    .findInBackground((objects, e) -> {
                if (objects != null && e == null) {
                    for (ParseUser object : objects) {
                        employees.add(new User(object.getUsername(), object.getString(ParseFields.fullName),
                                object.getString(ParseFields.email)));
                    }
//                    emit.onNext(employees);
                    emit.onNext(true);
                } else {
                    emit.onNext(false);
                }
            });
        });
    }
}
