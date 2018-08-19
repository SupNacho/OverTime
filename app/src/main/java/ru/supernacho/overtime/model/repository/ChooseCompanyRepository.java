package ru.supernacho.overtime.model.repository;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import ru.supernacho.overtime.model.Entity.CompanyEntity;
import ru.supernacho.overtime.model.Entity.UserCompaniesEntity;

public class ChooseCompanyRepository implements IChooseCompanyRepository {

    private List<CompanyEntity> companies;
    private IUserCompanyRepository userCompanyRepository;
    private ICompanyRepository companyRepository;

    public ChooseCompanyRepository(IUserCompanyRepository userCompanyRepository, ICompanyRepository companyRepository) {
        this.userCompanyRepository = userCompanyRepository;
        this.companyRepository = companyRepository;
        companies = new ArrayList<>();
    }

    @Override
    public Observable<List<CompanyEntity>> getCompanies() {
        return Observable.create(emit -> {

            companies.clear();
            UserCompaniesEntity userCompanies = userCompanyRepository.getCurrentUserCompanies();
            if (userCompanies == null) {
                userCompanyRepository.createUserCompaniesEntity();
            }

            String activeCompanyId = userCompanyRepository.getActiveCompanyId();
            List<String> companiesIdList = userCompanyRepository.getUserCompaniesArray();
            for (int i = 0; i < companiesIdList.size(); i++) {
                CompanyEntity company = companyRepository.getCompanyById(companiesIdList.get(i));
                companies.add(new CompanyEntity(company.getObjectId(), company.getName(),
                        company.getObjectId().equals(activeCompanyId)));
            }
            emit.onNext(companies);
        });
    }

    @Override
    public Observable<Boolean> setActiveCompany(String companyId) {
        return userCompanyRepository.setActiveCompany(companyId);
    }

    @Override
    public Observable<Boolean> deactivateCompany() {
        return userCompanyRepository.deactivateCompany();
    }

    @Override
    public Observable<Boolean> joinCompany(String pin) {
        return Observable.create( emitter -> {
            CompanyEntity company = companyRepository.getCompanyByPin(pin);
            userCompanyRepository.addCompanyToUser(company).subscribe(emitter::onNext);
        });
    }

    @Override
    public Observable<Boolean> exitFromCompany(String companyId) {
        return userCompanyRepository.exitFromCompany(companyId);
    }
}
