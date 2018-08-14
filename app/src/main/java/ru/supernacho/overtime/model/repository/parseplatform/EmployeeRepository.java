package ru.supernacho.overtime.model.repository.parseplatform;

import com.google.firebase.firestore.DocumentReference;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import ru.supernacho.overtime.model.Entity.CompanyEntity;
import ru.supernacho.overtime.model.Entity.User;
import ru.supernacho.overtime.model.Entity.UserCompaniesEntity;
import ru.supernacho.overtime.model.repository.ICompanyRepository;
import ru.supernacho.overtime.model.repository.IUserCompanyRepository;
import ru.supernacho.overtime.model.repository.ParseClass;
import ru.supernacho.overtime.model.repository.ParseFields;

public class EmployeeRepository {
    private List<User> employees;
    private CompanyEntity currentCompany;

    private UserCompanyRepository userCompanyRepository;
    private CompanyRepository companyRepository;

    public EmployeeRepository(UserCompanyRepository userCompanyRepository, CompanyRepository companyRepository) {
        this.userCompanyRepository = userCompanyRepository;
        this.companyRepository = companyRepository;
        employees = new ArrayList<>();
    }

    public List<User> getEmployeesList() {
        return employees;
    }

    public Observable<Boolean> getEmployees() {

        return Observable.create(emit -> {
            String[] activeCompanyId = new String[1];

            activeCompanyId[0] = userCompanyRepository.getActiveCompanyId();

            ParseQuery<ParseObject> filterForCompany = ParseQuery.getQuery(ParseClass.USER_COMPANIES);
            filterForCompany.whereEqualTo(ParseFields.userCompaniesCompanies,
                    activeCompanyId[0]);

            ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseClass.PARSE_USER);
            query
                    .whereMatchesKeyInQuery(ParseFields.userId, ParseFields.userCompaniesUserId,
                            filterForCompany)
                    .findInBackground((objects, e) -> {
                        if (objects != null && e == null) {
                            employees.clear();
                            for (ParseObject object : objects) {
                                ParseObject companyAdmin = companyRepository.getCompanyAdmins(object, activeCompanyId[0]);
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
        });
    }

    public Observable<CompanyEntity> getCompany() {
        return Observable.create(emit ->
                companyRepository.getCurrentCompany()
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
        return companyRepository.setAdminStatus(user, currentCompany);
    }

    public Observable<Boolean> fireEmployee(User employee) {
        return Observable.create(emit -> {
            ParseObject userCompany = userCompanyRepository.getUserCompanies(employee);

            JSONArray companies = userCompany.getJSONArray(ParseFields.userCompaniesCompanies);
            String companyId = userCompanyRepository.getActiveCompanyId();

            for (int i = 0; i < companies.length(); i++) {
                if (companies.get(i).equals(companyId)) companies.remove(i);
            }

            userCompany.put(ParseFields.userCompaniesCompanies, companies);
            userCompany.saveEventually(e -> {
                if (e == null) {
                    emit.onNext(true);
                    employees.remove(employee);
                    employee.setAdmin(false);
                    companyRepository.setAdminStatus(employee, currentCompany)
                            .subscribeOn(Schedulers.io())
                            .subscribe();
                } else {
                    emit.onNext(false);
                }
            });
        });
    }
}
