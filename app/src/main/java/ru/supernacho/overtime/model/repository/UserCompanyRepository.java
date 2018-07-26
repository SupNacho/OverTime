package ru.supernacho.overtime.model.repository;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import io.reactivex.Observable;
import ru.supernacho.overtime.model.Entity.UserCompany;


public class UserCompanyRepository {

    public ParseObject getCurrentCompany() throws com.parse.ParseException {
        ParseQuery<ParseObject> companyIdQuery = ParseQuery.getQuery(ParseClass.USER_COMPANIES);
        return companyIdQuery.whereEqualTo(ParseFields.userCompaniesUserId, ParseUser.getCurrentUser().getObjectId())
                .getFirst();
    }

    public void addComapanyToUser(ParseObject userCompany, String companyId){
        userCompany.addUnique(ParseFields.userCompaniesCompanies, companyId);
        userCompany.saveEventually();
    }

    public void createUserCompaniesEntity(){
        ParseObject userCompanies = new ParseObject(ParseClass.USER_COMPANIES);
        userCompanies.put(ParseFields.userCompaniesUserId, ParseUser.getCurrentUser().getObjectId());
        userCompanies.saveEventually();
    }

    public Observable<Boolean> createUserCompanies(String companyId){
        return Observable.create(e -> {
            ParseObject companies = new ParseObject(ParseClass.USER_COMPANIES);
            companies.put(ParseFields.userCompaniesUserId, ParseUser.getCurrentUser().getObjectId());
            companies.put(ParseFields.userCompaniesActiveCompany, companyId);
            companies.addUnique(ParseFields.userCompaniesCompanies, companyId);
            companies.saveEventually(e1 -> {
                if (e1 == null) {
                    e.onNext(true);
                } else {
                    e.onNext(false);
                }
            });
        });
    }

    public Observable<UserCompany> userIsAdmin(){
        return Observable.create(emit -> {
            ParseQuery<ParseObject> userCompaniesQuery = ParseQuery.getQuery(ParseClass.USER_COMPANIES);
            userCompaniesQuery.whereEqualTo(ParseFields.userCompaniesUserId, ParseUser.getCurrentUser().getObjectId())
                    .findInBackground((objects, e) -> {
                        if (objects != null && objects.size() > 0) {
                            String companyId = null;
                            for (ParseObject object : objects) {
                                companyId = object.getString(ParseFields.userCompaniesActiveCompany);
                            }
                            // TODO: 25.07.2018 replace with companyRepo
                            String finalCompanyId = companyId;
                            ParseQuery<ParseObject> companyQuery = ParseQuery.getQuery(ParseClass.COMPANY);
                            companyQuery.whereEqualTo(ParseFields.companyId, companyId)
                                    .whereEqualTo(ParseFields.companyAdmins, ParseUser.getCurrentUser().getObjectId())
                                    .findInBackground((objects1, e1) -> {
                                        if (objects1 != null && objects1.size() > 0) {
                                            emit.onNext(new UserCompany(finalCompanyId, true));
                                        } else {
                                            emit.onNext(new UserCompany(finalCompanyId, false));
                                        }
                                    });
                        }
                    });
        });
    }

    public Observable<Boolean> setActiveCompany(String companyId){
        return Observable.create( emit -> {
            ParseQuery<ParseObject> userCompaniesQuery = ParseQuery.getQuery(ParseClass.USER_COMPANIES);
            userCompaniesQuery.whereEqualTo(ParseFields.userCompaniesUserId, ParseUser.getCurrentUser().getObjectId());

            ParseObject userCompanies = userCompaniesQuery.getFirst();
            userCompanies.put(ParseFields.userCompaniesActiveCompany, companyId);
            userCompanies.saveEventually(e -> {
                if (e == null){
                    emit.onNext(true);
                } else {
                    emit.onNext(false);
                }
            });
        });
    }

    public Observable<Boolean> deactivateCompany(){
        // TODO: 25.07.2018 replace with UCRepo
        return Observable.create( emit -> {
            ParseQuery<ParseObject> userCompaniesQuery = ParseQuery.getQuery(ParseClass.USER_COMPANIES);
            userCompaniesQuery.whereEqualTo(ParseFields.userCompaniesUserId, ParseUser.getCurrentUser().getObjectId());

            ParseObject userCompanies = userCompaniesQuery.getFirst();
            userCompanies.put(ParseFields.userCompaniesActiveCompany, "");
            userCompanies.saveEventually(e -> {
                if (e == null){
                    emit.onNext(true);
                } else {
                    emit.onNext(false);
                }
            });
        });
    }
}
