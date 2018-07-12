package ru.supernacho.overtime.model.repository;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.http.POST;
import ru.supernacho.overtime.model.Entity.CompanyEntity;
import ru.supernacho.overtime.model.Entity.User;

public class EmployeeRepository {
    private List<User> employees;
    private CompanyInfoRepository companyInfoRepository;
    private CompanyEntity currentCompany;

    public EmployeeRepository(CompanyInfoRepository companyInfoRepository) {
        this.companyInfoRepository = companyInfoRepository;
        employees = new ArrayList<>();
    }

    public List<User> getEmployeesList() {
        return employees;
    }

    public Observable<Boolean> getEmployees() {
        return Observable.create(emit -> {
            String[] activeCompanyId = new String[1];
            ParseQuery<ParseObject> userQuery = ParseQuery.getQuery(ParseClass.USER_COMPANIES);
            userQuery.whereEqualTo(ParseFields.userCompaniesUserId, ParseUser.getCurrentUser().getObjectId())
                    .findInBackground((users, e1) -> {
                        if (users != null && users.size() > 0) {
                            activeCompanyId[0] = users.get(0).getString(ParseFields.userCompaniesActiveCompany);

                            ParseQuery<ParseObject> filterForCompany = ParseQuery.getQuery(ParseClass.USER_COMPANIES);
                            filterForCompany.whereEqualTo(ParseFields.userCompaniesCompanies,
                                    activeCompanyId[0]);

                            ParseQuery<ParseObject> companyQuery = ParseQuery.getQuery(ParseClass.COMPANY);

                            ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseClass.PARSE_USER);
                            query
                                    .whereMatchesKeyInQuery(ParseFields.userId, ParseFields.userCompaniesUserId,
                                            filterForCompany)
                                    .findInBackground((objects, e) -> {
                                        if (objects != null && e == null) {
                                            employees.clear();
                                            for (ParseObject object : objects) {
                                                ParseObject companyAdmin = null;
                                                try {
                                                    companyAdmin = companyQuery.whereEqualTo(ParseFields.companyId, activeCompanyId[0])
                                                            .whereEqualTo(ParseFields.companyAdmins, object.getObjectId())
                                                            .getFirst();
                                                } catch (ParseException e2) {
                                                    e2.printStackTrace();
                                                }
                                                employees.add(new User(object.getObjectId(),
                                                        object.getString(ParseFields.userName),
                                                        object.getString(ParseFields.fullName),
                                                        object.getString(ParseFields.userEmail),
                                                        companyAdmin != null));
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

    public Observable<CompanyEntity> getCompany() {
        return Observable.create(emit ->
                companyInfoRepository.getCompanyInfo()
                        .subscribeOn(Schedulers.io())
                        .subscribe(new DisposableObserver<CompanyEntity>() {
                            @Override
                            public void onNext(CompanyEntity companyEntity) {
                                emit.onNext(companyEntity);
                                currentCompany = companyEntity;
                            }

                            @Override
                            public void onError(Throwable e) {
                                throw new RuntimeException(e);
                            }

                            @Override
                            public void onComplete() {
                                dispose();
                            }
                        }));
    }

    public Observable<Boolean> setAdminStatus(User user) {
        return Observable.create(emit -> {
            ParseQuery<ParseObject> companyQuery = ParseQuery.getQuery(ParseClass.COMPANY);
            ParseObject company = companyQuery.whereEqualTo(ParseFields.companyId, currentCompany.getObjectId()).getFirst();
            if (user.isAdmin()) {
                company.addUnique(ParseFields.companyAdmins, user.getUserId());
                company.saveEventually(e -> {
                    if (e == null) {
                        emit.onNext(true);
                    } else {
                        emit.onNext(false);
                    }
                });
                emit.onNext(true);

            } else {

                JSONArray admins = company.getJSONArray(ParseFields.companyAdmins);
                for (int i = 0; i < admins.length(); i++) {
                    if (admins.get(i).equals(user.getUserId())) admins.remove(i);
                }

                company.put(ParseFields.companyAdmins, admins);
                company.saveEventually(e -> {
                    if (e == null) {
                        emit.onNext(true);
                    } else {
                        emit.onNext(false);
                    }
                });
            }
        });
    }

    public Observable<Boolean> fireEmployee(User employee){
        return Observable.create( emit -> {
            ParseQuery<ParseObject> userCompanyQuery = ParseQuery.getQuery(ParseClass.USER_COMPANIES);
            ParseObject userCompany = userCompanyQuery
                    .whereEqualTo(ParseFields.userCompaniesUserId, employee.getUserId()).getFirst();

            JSONArray companies = userCompany.getJSONArray(ParseFields.userCompaniesCompanies);

            ParseQuery<ParseObject> adminCompanyQuery = ParseQuery.getQuery(ParseClass.USER_COMPANIES);
            ParseObject adminCompanies = adminCompanyQuery
                    .whereEqualTo(ParseFields.userCompaniesUserId, ParseUser.getCurrentUser()
                            .getObjectId()).getFirst();

            String companyId = adminCompanies.getString(ParseFields.userCompaniesActiveCompany);

            for (int i = 0; i < companies.length(); i++) {
                if (companies.get(i).equals(companyId)) companies.remove(i);
            }

            userCompany.put(ParseFields.userCompaniesCompanies, companies);
            userCompany.saveEventually(e -> {
                if (e == null) {
                    emit.onNext(true);
                    employees.remove(employee);
                } else {
                    emit.onNext(false);
                }
            });
        });
    }
}
