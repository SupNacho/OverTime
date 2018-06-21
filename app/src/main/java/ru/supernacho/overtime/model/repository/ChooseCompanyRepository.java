package ru.supernacho.overtime.model.repository;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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
}
