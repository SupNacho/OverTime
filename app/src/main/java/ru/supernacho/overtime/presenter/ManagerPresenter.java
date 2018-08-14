package ru.supernacho.overtime.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import ru.supernacho.overtime.model.Entity.User;
import ru.supernacho.overtime.model.repository.IEmployeeRepository;
import ru.supernacho.overtime.view.fragments.ManagerView;

@InjectViewState
public class ManagerPresenter extends MvpPresenter<ManagerView> {
    private Scheduler uiScheduler;
    private Disposable disposable;

    @Inject
    IEmployeeRepository repository;

    public ManagerPresenter(Scheduler uiScheduler) {
        this.uiScheduler = uiScheduler;
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
//        getEmploysList();
    }

    public List<User> getEmploysDataLink(){
        return repository.getEmployeesList();
    }

//    public void getEmploysList(){
//       disposable = repository.getEmployeesList()
//                .subscribeOn(Schedulers.io())
//                .observeOn(uiScheduler)
//                .subscribe(employeeStatus -> {
//                    if (employeeStatus) {
//                        getViewState().viewEmployees();
//                    } else {
//                        Timber.d("No data");
//                    }
//                } );
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposable != null && !disposable.isDisposed()){
            disposable.dispose();
        }
    }
}
