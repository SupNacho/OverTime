package ru.supernacho.overtime.model.repository.firebase.core;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.schedulers.Schedulers;
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

    public FbCompanyRepository(IUserCompanyRepository userCompanyRepository) {
        this.userCompanyRepository = userCompanyRepository;
        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        this.fireStore = FirebaseFirestore.getInstance();
    }

    @Override
    public Observable<UserCompany> registerCompany(String name, String address, String email, String phone, String chief) {
        return Observable.create(emitter -> {
            String pin;
            String[] companyId = {""};
            for (; ; ) {
                String emplPin = PinGenerator.getPin();
                if (checkPin(ParseFields.companyEmpPin, emplPin)) {
                    pin = emplPin;
                    break;
                }
            }
            CompanyEntity companyEntity = new CompanyEntity("", name, address, phone, email, chief, pin);
            companyEntity.addAdmin(firebaseUser.getUid());
            fireStore.collection(ParseClass.COMPANY).add(companyEntity)
                    .addOnSuccessListener(documentReference -> {
                        companyId[0] = documentReference.getId();
                        emitter.onNext(new UserCompany(documentReference.getId(), true));
                    })
                    .addOnFailureListener(e ->
                            emitter.onNext(new UserCompany(null, false)));
            userCompanyRepository.addCompanyToUser(companyId[0]);
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
        CompanyEntity[] company = {new CompanyEntity("", "No company", false)};
        DocumentReference companyDocRef = fireStore.document(ParseClass.COMPANY + "/" + id);
        companyDocRef.get().addOnSuccessListener(documentSnapshot ->
                company[0] = documentSnapshot.toObject(CompanyEntity.class));
        return company[0];
    }

    @Override
    public CompanyEntity getCompanyByPin(String pin) {
        CompanyEntity[] company = {new CompanyEntity("", "No company", false)};
        CollectionReference companyCollRef = fireStore.collection(ParseClass.COMPANY);
        companyCollRef.whereEqualTo(ParseFields.companyEmpPin, pin).get()
                .addOnSuccessListener(queryDocumentSnapshots ->
                        company[0] = queryDocumentSnapshots.getDocuments().get(0).toObject(CompanyEntity.class));
        return company[0];
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
        Query companyQuery = companyCollRef.whereEqualTo(ParseFields.companyId, overTime.get(ParseFields.forCompany));
        CompanyEntity companyEntity;
        DocumentSnapshot company = companyQuery.get().getResult().getDocuments().get(0);
        if (company != null) {
            companyEntity = new CompanyEntity(company.getId(),
                    company.getString(ParseFields.companyName),
                    company.getString(ParseFields.companyAddress),
                    company.getString(ParseFields.companyPhone),
                    company.getString(ParseFields.companyEmail),
                    company.getString(ParseFields.companyChief),
                    company.getString(ParseFields.companyEmpPin));
        } else {
            companyEntity = new CompanyEntity("", "No connection", false);
        }
        return companyEntity;
    }

    //    @Override
//    public CompanyEntity getCompanyAdmins(DocumentSnapshot employee, String companyId) {
//        final CompanyEntity[] company = {new CompanyEntity("", "Null company", false)};
//        CollectionReference companyCollRef = fireStore.collection(ParseClass.COMPANY);
//        Query companyQuery = companyCollRef.whereEqualTo(FieldPath.documentId(), companyId)
//                .whereEqualTo(ParseFields.companyAdmins, employee.getId()).limit(1);
//        companyQuery.get().addOnSuccessListener(queryDocumentSnapshots ->
//                company[0] = queryDocumentSnapshots.getDocuments().get(0).toObject(CompanyEntity.class));
//        return company[0];
//    }
    @Override
    public CompanyEntity getCompanyAdmins(DocumentSnapshot employee, String companyId) {
        CollectionReference companyCollRef = fireStore.collection(ParseClass.COMPANY);
        Query companyQuery = companyCollRef.whereEqualTo(FieldPath.documentId(), companyId)
                .whereEqualTo(ParseFields.companyAdmins, employee.getId()).limit(1);
        return companyQuery.get().getResult().getDocuments().get(0).toObject(CompanyEntity.class);
    }

    @Override
    public Observable<Boolean> setAdminStatus(User user, CompanyEntity currentCompany) {
        return Observable.create(emit -> {
            if (user.isAdmin()) {
                currentCompany.addAdmin(user.getUserId());
                saveCompany(currentCompany, emit);
            } else {
                currentCompany.removeAdmin(user.getUserId());
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

    private boolean checkPin(String field, String pin) {
        final boolean[] result = {false};
        CollectionReference companiesCollRef = fireStore.collection(ParseClass.COMPANY);
        Query companyQuery = companiesCollRef.whereEqualTo(field, pin);
        companyQuery.get().addOnSuccessListener(queryDocumentSnapshots ->
                result[0] = queryDocumentSnapshots.getDocuments().size() <= 0);
        return result[0];
    }
}
