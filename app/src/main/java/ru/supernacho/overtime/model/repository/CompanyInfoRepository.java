package ru.supernacho.overtime.model.repository;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import io.reactivex.Observable;
import ru.supernacho.overtime.model.Entity.CompanyEntity;

public class CompanyInfoRepository {
    public Observable<CompanyEntity> getCompanyInfo(){
        return Observable.create( emit ->{
            ParseQuery<ParseObject> userCompaniesQuery = ParseQuery.getQuery(ParseClass.USER_COMPANIES);
            userCompaniesQuery.whereEqualTo(ParseFields.userCompaniesUserId, ParseUser.getCurrentUser().getObjectId());

            ParseQuery<ParseObject> companyQuery = ParseQuery.getQuery(ParseClass.COMPANY);
            ParseObject company = companyQuery.whereMatchesKeyInQuery(ParseFields.companyId, ParseFields.userCompaniesActiveCompany, userCompaniesQuery)
                    .getFirst();
            if (company != null) {
                emit.onNext(new CompanyEntity(
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
}
