package ru.supernacho.overtime.model.repository;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import io.reactivex.Observable;
import ru.supernacho.overtime.model.Entity.UserCompany;
import ru.supernacho.overtime.utils.PinGenerator;

public class CompanyRepository {

    public Observable<UserCompany> registerCompany(String name, String address, String email, String phone, String chief){
        return Observable.create( emit -> {

                ParseObject company = new ParseObject(ParseClass.COMPANY);

                company.put(ParseFields.companyName, name);
                company.put(ParseFields.companyAddress, address);
                company.put(ParseFields.companyEmail, email);
                company.put(ParseFields.companyPhone, phone);
                company.put(ParseFields.companyChief, chief);
                company.addUnique(ParseFields.companyAdmins, ParseUser.getCurrentUser().getObjectId());

                for (;;) {
                    String emplPin = PinGenerator.getPin();
                    if (checkPin(ParseFields.companyEmpPin, emplPin)) {
                        company.put(ParseFields.companyEmpPin, emplPin);
                        break;
                    }
                }
                company.saveEventually(e -> {
                    if (e == null){
                        String id = company.getObjectId();
                        emit.onNext(new UserCompany(id, true));
                    } else {
                        emit.onNext(new UserCompany(null, false));
                    }
                });

            // TODO: 25.07.2018 replace with UCRepo getCurrentCompany
                ParseQuery<ParseObject> userCompanies = ParseQuery.getQuery(ParseClass.USER_COMPANIES);
                ParseObject userCompany = userCompanies.whereEqualTo(ParseFields.userCompaniesUserId, ParseUser.getCurrentUser().getObjectId())
                        .getFirst();

            // TODO: 25.07.2018 replace with UCRepo addCompanyToUser
                userCompany.addUnique(ParseFields.userCompaniesCompanies, company.getObjectId());
                userCompany.saveEventually();
        });
    }

    private boolean checkPin(String field, String pin){
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

    public boolean isAdmin(String companyId){
        ParseQuery<ParseObject> companyQuery = ParseQuery.getQuery(ParseClass.COMPANY);
        List<ParseObject> results = null;
        try {
            results = companyQuery.whereEqualTo(ParseFields.companyId, companyId)
                    .whereEqualTo(ParseFields.companyAdmins, ParseUser.getCurrentUser().getObjectId())
                    .find();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (results.size() > 0) {
            return true;
        } else {
            return false;
        }
    }
}
