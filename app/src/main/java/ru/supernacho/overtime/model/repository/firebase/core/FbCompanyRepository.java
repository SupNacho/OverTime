package ru.supernacho.overtime.model.repository.firebase.core;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.subjects.PublishSubject;
import ru.supernacho.overtime.App;
import ru.supernacho.overtime.model.Entity.CompanyEntity;
import ru.supernacho.overtime.model.Entity.User;
import ru.supernacho.overtime.model.Entity.UserCompany;
import ru.supernacho.overtime.model.repository.ICompanyRepository;
import ru.supernacho.overtime.model.repository.IUserCompanyRepository;
import ru.supernacho.overtime.model.repository.ParseClass;
import ru.supernacho.overtime.model.repository.ParseFields;
import ru.supernacho.overtime.utils.PinGenerator;

public class FbCompanyRepository implements ICompanyRepository {
    private IUserCompanyRepository userCompanyRepository;
    private FirebaseFirestore fireStore;
    private FirebaseUser firebaseUser;
    private PublishSubject<UserCompany> eventBus;

    public FbCompanyRepository(IUserCompanyRepository userCompanyRepository) {
        this.userCompanyRepository = userCompanyRepository;
        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        this.fireStore = FirebaseFirestore.getInstance();
        this.eventBus = PublishSubject.create();
    }

    @Override
    public Observable<UserCompany> registerCompany(String name, String address, String email, String phone, String chief) {
        return Observable.create(emitter -> {
            String pin;
            String[] companyId = {""};
            for (; ; ) {
                String emplPin = PinGenerator.getPin();
                if (checkPin(ParseFields.companyPin, emplPin)) {
                    pin = emplPin;
                    break;
                }
            }
            CompanyEntity companyEntity = new CompanyEntity("", name, address, phone, email, chief, pin);
            companyEntity.addAdmin(firebaseUser.getUid());
            fireStore.collection(ParseClass.COMPANY).add(companyEntity)
                    .addOnSuccessListener(documentReference -> {
                        companyId[0] = documentReference.getId();
                        documentReference.update(ParseFields.companyId, companyId[0]);
                        eventBus.onNext(new UserCompany(documentReference.getId(), true));
                        eventBus.onComplete();
                        userCompanyRepository.addCompanyToUser(companyId[0])
                                .subscribeOn(App.getFbThread())
                                .subscribe();
                    })
                    .addOnFailureListener(e -> {
                        eventBus.onNext(new UserCompany(null, false));
                        eventBus.onComplete();
                    });
        });
    }

    @Override
    public Observable<UserCompany> userIsAdmin() {
        return Observable.create(emit -> {
            String companyId = userCompanyRepository.getActiveCompanyId();
            DocumentReference companyDocRef = fireStore.document(ParseClass.COMPANY + "/" + companyId);
            companyDocRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    CompanyEntity companyEntity = documentSnapshot.toObject(CompanyEntity.class);
                    if (companyEntity != null) {
                        if (companyEntity.getAdmins().contains(firebaseUser.getUid()))
                            emit.onNext(new UserCompany(companyId, true));
                        else emit.onNext(new UserCompany(companyId, false));
                    }
                }
            });
        });
    }

    @Override
    public CompanyEntity getCompanyById(String id) {
        CompanyEntity company = new CompanyEntity("", "No company", false);
        DocumentSnapshot companySnapshot = null;
        try {
            companySnapshot = Tasks.await(fireStore.document(ParseClass.COMPANY + "/" + id).get());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        if (companySnapshot != null) company = companySnapshot.toObject(CompanyEntity.class);
        return company;
    }

    @Override
    public CompanyEntity getCompanyByPin(String pin) {
        CompanyEntity company = new CompanyEntity("", "No company", false);
        CollectionReference companyCollRef = fireStore.collection(ParseClass.COMPANY);
        try {
            QuerySnapshot querySnapshot = Tasks.await(companyCollRef.whereEqualTo(ParseFields.companyPin, pin).get());
            if (!querySnapshot.isEmpty())
                company = querySnapshot.getDocuments().get(0).toObject(CompanyEntity.class);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return company;
    }

    @Override
    public Observable<CompanyEntity> getCurrentCompany() {
        return Observable.create(emit -> {
            String activeCompanyId = userCompanyRepository.getCurrentUserCompanies().getActiveCompany();
            emit.onNext(getCompanyById(activeCompanyId));
        });
    }

    @Override
    public CompanyEntity getCompanyByOverTime(DocumentSnapshot overTime) {
        CollectionReference companyCollRef = fireStore.collection(ParseClass.COMPANY);
        CompanyEntity companyEntity = new CompanyEntity("", "No company", false);
        QuerySnapshot companyQuerySnapshot = null;
        try {
            companyQuerySnapshot = Tasks.await(companyCollRef.whereEqualTo(ParseFields.companyId,
                    overTime.get(ParseFields.forCompany)).get());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        if (companyQuerySnapshot != null && !companyQuerySnapshot.isEmpty()) {
            DocumentSnapshot company = companyQuerySnapshot.getDocuments().get(0);
            if (company != null) {
                companyEntity = new CompanyEntity(company.getId(),
                        company.getString(ParseFields.companyName),
                        company.getString(ParseFields.companyAddress),
                        company.getString(ParseFields.companyPhone),
                        company.getString(ParseFields.companyEmail),
                        company.getString(ParseFields.companyChief),
                        company.getString(ParseFields.companyPin));
            }
        }
        return companyEntity;
    }

    @Override
    public CompanyEntity getCompanyAdmins(DocumentSnapshot employee, String companyId) {
        CompanyEntity company = new CompanyEntity("", "No company", false);
        CollectionReference companyCollRef = fireStore.collection(ParseClass.COMPANY);
        QuerySnapshot companySnapshot;
        try {
            companySnapshot = Tasks.await(companyCollRef.whereEqualTo(FieldPath.documentId(), companyId)
                    .whereArrayContains(ParseFields.companyAdmins, employee.getId()).limit(1).get());
            if (!companySnapshot.isEmpty())
                company = companySnapshot.getDocuments().get(0).toObject(CompanyEntity.class);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return company;
    }

    @Override
    public Observable<Boolean> setAdminStatus(User user, CompanyEntity currentCompany) {
        return Observable.create(emit -> {
            if (user.isAdmin()) {
                currentCompany.addAdmin(user.getObjectId());
                saveCompany(currentCompany, emit);
            } else {
                currentCompany.removeAdmin(user.getObjectId());
                saveCompany(currentCompany, emit);
            }
        });
    }

    private void saveCompany(@NotNull CompanyEntity currentCompany, ObservableEmitter<Boolean> emit) {
        DocumentReference companyDocRef =
                fireStore.document(ParseClass.COMPANY + "/" + currentCompany.getObjectId());
        companyDocRef.set(currentCompany)
                .addOnSuccessListener(aVoid -> emit.onNext(true))
                .addOnFailureListener(e -> emit.onNext(false));
    }

    @Override
    public void addCompanyToUser(String companyId) {
        userCompanyRepository.addCompanyToUser(companyId)
                .subscribeOn(App.getFbThread())
                .subscribe();
    }

    @Override
    public PublishSubject<UserCompany> subscribeToEBus() {
        if (eventBus == null) this.eventBus = PublishSubject.create();
        return eventBus;
    }

    private boolean checkPin(String field, String pin) {
        CollectionReference companiesCollRef = fireStore.collection(ParseClass.COMPANY);
        Query companyQuery = companiesCollRef.whereEqualTo(field, pin);
        try {
            QuerySnapshot querySnapshot = Tasks.await(companyQuery.get());
            return querySnapshot.getDocuments().size() <= 0;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}
