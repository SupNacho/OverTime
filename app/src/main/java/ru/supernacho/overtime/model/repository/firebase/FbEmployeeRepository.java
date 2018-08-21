package ru.supernacho.overtime.model.repository.firebase;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;
import ru.supernacho.overtime.App;
import ru.supernacho.overtime.model.Entity.CompanyEntity;
import ru.supernacho.overtime.model.Entity.User;
import ru.supernacho.overtime.model.Entity.UserCompaniesEntity;
import ru.supernacho.overtime.model.repository.ICompanyRepository;
import ru.supernacho.overtime.model.repository.IEmployeeRepository;
import ru.supernacho.overtime.model.repository.IUserCompanyRepository;
import ru.supernacho.overtime.model.repository.ParseClass;
import ru.supernacho.overtime.model.repository.ParseFields;
import timber.log.Timber;

public class FbEmployeeRepository implements IEmployeeRepository {
    private List<User> employees;
    private CompanyEntity currentCompany;
    private FirebaseFirestore fireStore;
    private IUserCompanyRepository userCompanyRepository;
    private ICompanyRepository companyRepository;

    public FbEmployeeRepository(IUserCompanyRepository userCompanyRepository, ICompanyRepository companyRepository) {
        this.userCompanyRepository = userCompanyRepository;
        this.companyRepository = companyRepository;
        employees = new ArrayList<>();
        this.fireStore = FirebaseFirestore.getInstance();
    }

    @Override
    public List<User> getEmployeesList() {
        return employees;
    }

    @Override
    public Observable<Boolean> getEmployees() {
        return Observable.create(emit -> {
            String[] activeCompanyId = new String[1];
            activeCompanyId[0] = userCompanyRepository.getActiveCompanyId();

            CollectionReference filterForCompany = fireStore.collection(ParseClass.USER_COMPANIES);
            QuerySnapshot filterForCompanySnapshot =
                    Tasks.await(filterForCompany.whereArrayContains(ParseFields.userCompaniesCompanies,
                            activeCompanyId[0]).get());
                if (!filterForCompanySnapshot.isEmpty()) {
                    employees.clear();
                    for (DocumentSnapshot documentSnapshot : filterForCompanySnapshot.getDocuments()) {
                        CompanyEntity company = companyRepository.getCompanyAdmins(documentSnapshot, activeCompanyId[0]);
                        CollectionReference filteredUser = fireStore.collection(ParseClass.PARSE_USER);
                        Query filerUserQuery = filteredUser
                                .whereEqualTo(FieldPath.documentId(),
                                        documentSnapshot.getString(ParseFields.userCompaniesUserId))
                                .limit(1);
                        QuerySnapshot userSnapShot = Tasks.await(filerUserQuery.get());
                        if (!userSnapShot.isEmpty()) {
                            User user = userSnapShot.getDocuments().get(0).toObject(User.class);
                            if (user != null) {
                                user.setAdmin(company.getAdmins().contains(user.getObjectId()));
                                employees.add(user);
                            }
                        }
                    }
                    emit.onNext(true);
                } else {
                    emit.onNext(false);
                }
        });
    }

    @Override
    public Observable<CompanyEntity> getCompany() {
        return Observable.create(emit ->
                companyRepository.getCurrentCompany()
                        .subscribeOn(App.getFbThread())
                        .subscribe(new DisposableObserver<CompanyEntity>() {
                            @Override
                            public void onNext(CompanyEntity companyEntity) {
                                emit.onNext(companyEntity);
                                currentCompany = companyEntity;
                            }

                            @Override
                            public void onError(Throwable e) {
                                throw new RuntimeException(e);
                            }

                            @Override
                            public void onComplete() {
                                dispose();
                            }
                        }));
    }

    @Override
    public Observable<Boolean> setAdminStatus(User user) {
        return companyRepository.setAdminStatus(user, currentCompany);
    }

    @Override
    public Observable<Boolean> fireEmployee(User employee) {
        return Observable.create(emit -> {


            UserCompaniesEntity userCompany = userCompanyRepository.getUserCompanies(employee);

            List<String> companies = userCompany.getCompanies();
            String companyId = userCompanyRepository.getActiveCompanyId();
            companies.remove(companyId);
            DocumentReference userCommpaniesSave = fireStore.document(ParseClass.USER_COMPANIES + "/" + userCompany.getUserId());
            userCommpaniesSave.set(userCompany).addOnSuccessListener(aVoid -> {
                emit.onNext(true);
                employees.remove(employee);
                employee.setAdmin(false);
                companyRepository.setAdminStatus(employee, currentCompany)
                        .subscribeOn(App.getFbThread())
                        .subscribe();
            })
                    .addOnFailureListener(e -> emit.onNext(false));
        });
    }
}
