package ru.supernacho.overtime.model.repository;

import com.parse.ParseObject;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import ru.supernacho.overtime.model.Entity.CompanyEntity;

public class ChooseCompanyRepository {

    private List<CompanyEntity> companies;
    private UserCompanyRepository userCompanyRepository;
    private CompanyRepository companyRepository;

    public ChooseCompanyRepository(UserCompanyRepository userCompanyRepository, CompanyRepository companyRepository) {
        this.userCompanyRepository = userCompanyRepository;
        this.companyRepository = companyRepository;
        companies = new ArrayList<>();
    }

    public Observable<List<CompanyEntity>> getCompanies(){
        return Observable.create( emit ->{

            companies.clear();
            ParseObject userCompanies = userCompanyRepository.getCurrentUserCompanies();
            if (userCompanies == null){
                userCompanyRepository.createUserCompaniesEntity();
            }

            String activeCompanyId = userCompanyRepository.getActiveCompanyId();
            JSONArray jsonArray = userCompanyRepository.getUserCompaniesArray();
            if (jsonArray != null && jsonArray.length() > 0){
                for (int i = 0; i < jsonArray.length(); i++) {
                    ParseObject company = companyRepository.getCompanyById(jsonArray.getString(i));
                    companies.add(new CompanyEntity(company.getObjectId(), company.getString(ParseFields.companyName),
                            company.getObjectId().equals(activeCompanyId)));
                }
            }

            emit.onNext(companies);
        });
    }

    public Observable<Boolean> setActiveCompany(String companyId){
        return userCompanyRepository.setActiveCompany(companyId);
    }

    public Observable<Boolean> deactivateCompany(){
        return userCompanyRepository.deactivateCompany();
    }

    public Observable<Boolean> joinCompany(String pin){
        ParseObject company = companyRepository.getCompanyByPin(pin);
        return userCompanyRepository.addCompanyToUser(company);
    }

    public Observable<Boolean> exitFromCompany(String companyId){
        return userCompanyRepository.exitFromCompany(companyId);
    }
}
