package ru.supernacho.overtime.model.repository;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import ru.supernacho.overtime.model.Entity.CompanyEntity;

public class ChooseCompanyRepository {

    private List<CompanyEntity> companies = new ArrayList<>();

    public Observable<List<CompanyEntity>> getCompanies(){
        return Observable.create( emit ->{

            companies.clear();
            ParseQuery<ParseObject> userCompaniesQuery = ParseQuery.getQuery(ParseClass.USER_COMPANIES);
            userCompaniesQuery.whereEqualTo(ParseFields.userCompaniesUserId, ParseUser.getCurrentUser().getObjectId());
            ParseObject userCompanies = userCompaniesQuery.getFirst();
            if (userCompanies == null){
                ParseObject newUserCompanies = new ParseObject(ParseClass.USER_COMPANIES);
                newUserCompanies.put(ParseFields.userCompaniesUserId, ParseUser.getCurrentUser().getObjectId());
                newUserCompanies.save();
            }

            String activeCompanyId = userCompanies.getString(ParseFields.userCompaniesActiveCompany);
            ParseQuery<ParseObject> companyQuery = ParseQuery.getQuery(ParseClass.COMPANY);

            JSONArray jsonArray = userCompanies.getJSONArray(ParseFields.userCompaniesCompanies);
            if (jsonArray != null && jsonArray.length() > 0){
                for (int i = 0; i < jsonArray.length(); i++) {
                    ParseObject company = companyQuery.whereEqualTo(ParseFields.companyId, jsonArray.getString(i))
                            .getFirst();
                    companies.add(new CompanyEntity(company.getObjectId(), company.getString(ParseFields.companyName),
                            company.getObjectId().equals(activeCompanyId)));
                }
            }

            emit.onNext(companies);
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

    public Observable<Boolean> joinCompany(String pin){
        return Observable.create( emit -> {
           ParseQuery<ParseObject> companyQuery = ParseQuery.getQuery(ParseClass.COMPANY);
           ParseObject company = companyQuery.whereEqualTo(ParseFields.companyEmpPin, pin).getFirst();

           ParseQuery<ParseObject> userCompanyQuery = ParseQuery.getQuery(ParseClass.USER_COMPANIES);
           ParseObject userCompanies = userCompanyQuery.whereEqualTo(ParseFields.userCompaniesUserId,
                   ParseUser.getCurrentUser().getObjectId())
                   .getFirst();
           userCompanies.addUnique(ParseFields.userCompaniesCompanies, company.getObjectId());
           userCompanies.saveEventually(e -> {
               if (e == null){
                   emit.onNext(true);
               } else {
                   emit.onNext(false);
               }
           });
        });
    }

    public Observable<Boolean> exitFromCompany(String companyId){
        return Observable.create( emitter -> {
            ParseQuery<ParseObject> userCompanyQuery = ParseQuery.getQuery(ParseClass.USER_COMPANIES);
            ParseObject userCompanies = userCompanyQuery.whereEqualTo(ParseFields.userCompaniesUserId,
                    ParseUser.getCurrentUser().getObjectId()).getFirst();
            JSONArray companies = userCompanies.getJSONArray(ParseFields.userCompaniesCompanies);
            for (int i = 0; i < companies.length(); i++) {
                if (companies.get(i).equals(companyId)) companies.remove(i);
            }
            userCompanies.put(ParseFields.userCompaniesCompanies, companies);
            userCompanies.saveEventually( e -> {
                if (e == null){
                    emitter.onNext(true);
                } else {
                    emitter.onNext(false);
                }
            });
        });
    }
}
