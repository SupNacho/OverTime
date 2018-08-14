package ru.supernacho.overtime.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ru.supernacho.overtime.model.Entity.User;
import ru.supernacho.overtime.model.repository.IEmployeeRepository;
import ru.supernacho.overtime.view.fragments.EmployeesView;
import timber.log.Timber;

@InjectViewState
public class EmployeesPresenter extends MvpPresenter<EmployeesView> {
    private Scheduler uiScheduler;
    private Disposable disposable;

    @Inject
    IEmployeeRepository repository;

    public EmployeesPresenter(Scheduler uiScheduler) {
        this.uiScheduler = uiScheduler;
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        getEmploysList();
    }

    public List<User> getEmploysDataLink(){
        return repository.getEmployeesList();
    }

    public void getEmploysList(){
       disposable = repository.getEmployees()
                .subscribeOn(Schedulers.io())
                .observeOn(uiScheduler)
                .subscribe(employeeStatus -> {
                    if (employeeStatus) {
                        getViewState().viewEmployees();
                    } else {
                        Timber.d("No data");
                    }
                } );
    }

    public void chooseEmployee(String userId){
        getViewState().selectEmployee(userId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposable != null && !disposable.isDisposed()){
            disposable.dispose();
        }
    }
}
