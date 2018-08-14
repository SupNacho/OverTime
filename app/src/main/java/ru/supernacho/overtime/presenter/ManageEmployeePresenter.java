package ru.supernacho.overtime.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Scheduler;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import ru.supernacho.overtime.model.Entity.CompanyEntity;
import ru.supernacho.overtime.model.Entity.User;
import ru.supernacho.overtime.model.repository.IEmployeeRepository;
import ru.supernacho.overtime.view.ManageEmployeeView;

@InjectViewState
public class ManageEmployeePresenter extends MvpPresenter<ManageEmployeeView> implements AlertPresenter{
    private Scheduler uiScheduler;

    @Inject
    IEmployeeRepository repository;

    public ManageEmployeePresenter(Scheduler uiScheduler) {
        this.uiScheduler = uiScheduler;
    }

    public List<User> getEmployeesList(){
        return repository.getEmployeesList();
    }

    public void getEmployees(){
        repository.getEmployees()
                .subscribeOn(Schedulers.io())
                .observeOn(uiScheduler)
                .subscribe(new DisposableObserver<Boolean>() {
                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) getViewState().update();
                    }

                    @Override
                    public void onError(Throwable e) {
                        throw new RuntimeException(e);
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }

    public void getCompany(){
        repository.getCompany()
                .subscribeOn(Schedulers.io())
                .observeOn(uiScheduler)
                .subscribe(new DisposableObserver<CompanyEntity>() {
                    @Override
                    public void onNext(CompanyEntity companyEntity) {
                        getViewState().setCompany(companyEntity);
                    }

                    @Override
                    public void onError(Throwable e) {
                        throw new RuntimeException(e);
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }

    public void grantAdmin(User employee){
        employee.setAdmin(!employee.isAdmin());
        repository.setAdminStatus(employee)
                .subscribeOn(Schedulers.io())
                .observeOn(uiScheduler)
                .subscribe(new DisposableObserver<Boolean>() {
                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            getEmployees();
                        } else {
                            getViewState().grantAdminFailed();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        throw new RuntimeException(e.getCause());
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }

    public void initFireEmployee(User employee){
        getViewState().initFireEmployee(employee);
    }

    @Override
    public void positiveAction(Object object) {
        if (object instanceof User) {
            User employee = (User) object;
            repository.fireEmployee(employee)
                    .subscribeOn(Schedulers.io())
                    .observeOn(uiScheduler)
                    .subscribe(new DisposableObserver<Boolean>() {
                        @Override
                        public void onNext(Boolean aBoolean) {
                            if (aBoolean) {
                                getViewState().fireSuccess(employee);
                            } else {
                                getViewState().fireFailed(employee);
                            }
                            getViewState().update();
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {
                            dispose();
                        }
                    });
        } else {
            throw new RuntimeException("object is not instance of User.class");
        }
    }
}
