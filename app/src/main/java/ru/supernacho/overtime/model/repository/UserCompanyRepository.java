package ru.supernacho.overtime.model.repository;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;

import io.reactivex.Observable;
import ru.supernacho.overtime.model.Entity.User;


public class UserCompanyRepository {

    public ParseObject getCurrentUserCompanies() {
        ParseQuery<ParseObject> companyIdQuery = ParseQuery.getQuery(ParseClass.USER_COMPANIES);
        ParseObject result = null;
        try {
            result = companyIdQuery.whereEqualTo(ParseFields.userCompaniesUserId, ParseUser.getCurrentUser().getObjectId())
                    .getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
    public ParseObject getUserCompanies(User employee) {
        ParseQuery<ParseObject> companyIdQuery = ParseQuery.getQuery(ParseClass.USER_COMPANIES);
        ParseObject result = null;
        try {
            result = companyIdQuery.whereEqualTo(ParseFields.userCompaniesUserId, employee.getUserId())
                    .getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Observable<Boolean> addCompanyToUser(ParseObject company) {
        ParseObject userCompanies = getCurrentUserCompanies();
        return Observable.create(emit -> {
            userCompanies.addUnique(ParseFields.userCompaniesCompanies, company.getObjectId());
            userCompanies.saveEventually(e -> {
                if (e == null) {
                    emit.onNext(true);
                } else {
                    emit.onNext(false);
                }
            });
        });
    }

    public Observable<Boolean> addCompanyToUser(String companyId) {
        ParseObject userCompanies = getCurrentUserCompanies();
        return Observable.create(emit -> {
            userCompanies.addUnique(ParseFields.userCompaniesCompanies, companyId);
            userCompanies.saveEventually(e -> {
                if (e == null) {
                    emit.onNext(true);
                } else {
                    emit.onNext(false);
                }
            });
        });
    }

    public Observable<Boolean> exitFromCompany(String companyId) {
        return Observable.create(emitter -> {
            ParseObject userCompanies = getCurrentUserCompanies();
            JSONArray companies = userCompanies.getJSONArray(ParseFields.userCompaniesCompanies);
            for (int i = 0; i < companies.length(); i++) {
                if (companies.get(i).equals(companyId)) companies.remove(i);
            }
            userCompanies.put(ParseFields.userCompaniesCompanies, companies);
            userCompanies.saveEventually(e -> {
                if (e == null) {
                    emitter.onNext(true);
                } else {
                    emitter.onNext(false);
                }
            });
        });
    }

    public void createUserCompaniesEntity() {
        ParseObject userCompanies = new ParseObject(ParseClass.USER_COMPANIES);
        userCompanies.put(ParseFields.userCompaniesUserId, ParseUser.getCurrentUser().getObjectId());
        userCompanies.saveEventually();
    }

    public Observable<Boolean> setActiveCompany(String companyId) {
        return Observable.create(emit -> {
            ParseQuery<ParseObject> userCompaniesQuery = ParseQuery.getQuery(ParseClass.USER_COMPANIES);
            userCompaniesQuery.whereEqualTo(ParseFields.userCompaniesUserId, ParseUser.getCurrentUser().getObjectId());

            ParseObject userCompanies = userCompaniesQuery.getFirst();
            userCompanies.put(ParseFields.userCompaniesActiveCompany, companyId);
            userCompanies.saveEventually(e -> {
                if (e == null) {
                    emit.onNext(true);
                } else {
                    emit.onNext(false);
                }
            });
        });
    }

    public Observable<Boolean> deactivateCompany() {
        return Observable.create(emit -> {
            ParseQuery<ParseObject> userCompaniesQuery = ParseQuery.getQuery(ParseClass.USER_COMPANIES);
            userCompaniesQuery.whereEqualTo(ParseFields.userCompaniesUserId, ParseUser.getCurrentUser().getObjectId());

            ParseObject userCompanies = userCompaniesQuery.getFirst();
            userCompanies.put(ParseFields.userCompaniesActiveCompany, "");
            userCompanies.saveEventually(e -> {
                if (e == null) {
                    emit.onNext(true);
                } else {
                    emit.onNext(false);
                }
            });
        });
    }

    public JSONArray getUserCompaniesArray() {
        return getCurrentUserCompanies().getJSONArray(ParseFields.userCompaniesCompanies);
    }

    public String getActiveCompanyId() {
        return getCurrentUserCompanies().getString(ParseFields.userCompaniesActiveCompany);
    }

    public ParseQuery<ParseObject> getUserCompaniesQuery() {
        ParseQuery<ParseObject> userCompaniesQuery = ParseQuery.getQuery(ParseClass.USER_COMPANIES);
        return userCompaniesQuery.whereEqualTo(ParseFields.userCompaniesUserId, ParseUser.getCurrentUser().getObjectId());
    }
}
