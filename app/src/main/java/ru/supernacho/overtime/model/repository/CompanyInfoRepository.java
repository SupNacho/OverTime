package ru.supernacho.overtime.model.repository;

import io.reactivex.Observable;
import ru.supernacho.overtime.model.Entity.CompanyEntity;

public class CompanyInfoRepository {
    private CompanyRepository companyRepository;

    public CompanyInfoRepository(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Observable<CompanyEntity> getCompanyInfo(){
        return companyRepository.getCurrentCompany();
    }
}
