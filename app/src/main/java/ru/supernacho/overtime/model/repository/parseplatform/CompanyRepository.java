package ru.supernacho.overtime.model.repository.parseplatform;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import ru.supernacho.overtime.model.Entity.CompanyEntity;
import ru.supernacho.overtime.model.Entity.User;
import ru.supernacho.overtime.model.Entity.UserCompany;
import ru.supernacho.overtime.model.repository.ICompanyRepository;
import ru.supernacho.overtime.model.repository.ParseClass;
import ru.supernacho.overtime.model.repository.ParseFields;
import ru.supernacho.overtime.utils.PinGenerator;

public class CompanyRepository {
    private UserCompanyRepository userCompanyRepository;

    public CompanyRepository(UserCompanyRepository userCompanyRepository) {
        this.userCompanyRepository = userCompanyRepository;
    }

    public Observable<UserCompany> registerCompany(String name, String address, String email, String phone, String chief) {
        return Observable.create(emit -> {

            ParseObject company = new ParseObject(ParseClass.COMPANY);

            company.put(ParseFields.companyName, name);
            company.put(ParseFields.companyAddress, address);
            company.put(ParseFields.companyEmail, email);
            company.put(ParseFields.companyPhone, phone);
            company.put(ParseFields.companyChief, chief);
            company.addUnique(ParseFields.companyAdmins, ParseUser.getCurrentUser().getObjectId());

            for (; ; ) {
                String emplPin = PinGenerator.getPin();
                if (checkPin(ParseFields.companyEmpPin, emplPin)) {
                    company.put(ParseFields.companyEmpPin, emplPin);
                    break;
                }
            }
            company.saveEventually(e -> {
                if (e == null) {
                    String id = company.getObjectId();
                    emit.onNext(new UserCompany(id, true));
                } else {
                    emit.onNext(new UserCompany(null, false));
                }
            });
            userCompanyRepository.addCompanyToUser(company);
        });
    }

    private boolean checkPin(String field, String pin) {
        List<ParseObject> list;
        ParseQuery<ParseObject> checkPin = ParseQuery.getQuery(ParseClass.COMPANY);
        try {
            list = checkPin.whereEqualTo(field, pin)
                    .find();
            return list == null || list.size() <= 0;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Observable<UserCompany> userIsAdmin() {
        return Observable.create(emit -> {
            String companyId = userCompanyRepository.getActiveCompanyId();
            ParseQuery<ParseObject> companyQuery = ParseQuery.getQuery(ParseClass.COMPANY);
            companyQuery.whereEqualTo(ParseFields.companyId, companyId)
                    .whereEqualTo(ParseFields.companyAdmins, ParseUser.getCurrentUser().getObjectId())
                    .findInBackground((objects1, e1) -> {
                        if (objects1 != null && objects1.size() > 0) {
                            emit.onNext(new UserCompany(companyId, true));
                        } else {
                            emit.onNext(new UserCompany(companyId, false));
                        }
                    });

        });
    }

    public ParseObject getCompanyById(String id) {
        ParseQuery<ParseObject> companyQuery = ParseQuery.getQuery(ParseClass.COMPANY);
        ParseObject result = null;
        try {
            result = companyQuery.whereEqualTo(ParseFields.companyId, id)
                    .getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public ParseObject getCompanyByPin(String pin) {
        ParseQuery<ParseObject> companyQuery = ParseQuery.getQuery(ParseClass.COMPANY);
        ParseObject result = null;
        try {
            result = companyQuery.whereEqualTo(ParseFields.companyEmpPin, pin).getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Observable<CompanyEntity> getCurrentCompany() {
        return Observable.create(emit -> {
            ParseQuery<ParseObject> companyQuery = ParseQuery.getQuery(ParseClass.COMPANY);
            ParseObject company = companyQuery.whereMatchesKeyInQuery(ParseFields.companyId,
                    ParseFields.userCompaniesActiveCompany, userCompanyRepository.getUserCompaniesQuery())
                    .getFirst();
            if (company != null) {
                emit.onNext(new CompanyEntity(
                        company.getObjectId(),
                        company.getString(ParseFields.companyName),
                        company.getString(ParseFields.companyAddress),
                        company.getString(ParseFields.companyPhone),
                        company.getString(ParseFields.companyEmail),
                        company.getString(ParseFields.companyChief),
                        company.getString(ParseFields.companyEmpPin)
                ));
            }
        });
    }

    public CompanyEntity getCompanyByOverTime(ParseObject overTime) {
        ParseQuery<ParseObject> companyQuery = ParseQuery.getQuery(ParseClass.COMPANY);
        ParseObject company = null;
        CompanyEntity companyEntity;
        try {
            company = companyQuery.whereEqualTo(ParseFields.companyId, overTime.get(ParseFields.forCompany))
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
            companyEntity = new CompanyEntity("", "No connection", false);
        }
        return companyEntity;
    }

    public ParseObject getCompanyAdmins(ParseObject employee, String companyId) {
        ParseQuery<ParseObject> companyQuery = ParseQuery.getQuery(ParseClass.COMPANY);
        ParseObject result = null;
        try {
            result = companyQuery.whereEqualTo(ParseFields.companyId, companyId)
                    .whereEqualTo(ParseFields.companyAdmins, employee.getObjectId())
                    .getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Observable<Boolean> setAdminStatus(User user, CompanyEntity currentCompany) {
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

    public void addCompanyToUser(String companyId){
        userCompanyRepository.addCompanyToUser(companyId)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }
}
