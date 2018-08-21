package ru.supernacho.overtime.model.repository;

import io.reactivex.Observable;
import ru.supernacho.overtime.model.Entity.CompanyEntity;

public class CompanyInfoRepository implements ICompanyInfoRepository{
    private ICompanyRepository companyRepository;

    public CompanyInfoRepository(ICompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public Observable<CompanyEntity> getCompanyInfo(){
        return companyRepository.getCurrentCompany();
    }
}
