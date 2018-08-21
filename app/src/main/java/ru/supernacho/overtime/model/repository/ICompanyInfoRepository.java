package ru.supernacho.overtime.model.repository;

import io.reactivex.Observable;
import ru.supernacho.overtime.model.Entity.CompanyEntity;

public interface ICompanyInfoRepository {

    Observable<CompanyEntity> getCompanyInfo();
}
