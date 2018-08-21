package ru.supernacho.overtime.model.repository.firebase.core;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;
import java.util.concurrent.ExecutionException;

import io.reactivex.Observable;
import ru.supernacho.overtime.model.Entity.CompanyEntity;
import ru.supernacho.overtime.model.Entity.User;
import ru.supernacho.overtime.model.Entity.UserCompaniesEntity;
import ru.supernacho.overtime.model.repository.IUserCompanyRepository;
import ru.supernacho.overtime.model.repository.ParseClass;
import ru.supernacho.overtime.model.repository.ParseFields;

public class FbUserCompanyRepository implements IUserCompanyRepository {
    private FirebaseFirestore fireStore;
    private FirebaseUser firebaseUser;

    public FbUserCompanyRepository() {
        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        this.fireStore = FirebaseFirestore.getInstance();
    }

    @Override
    public UserCompaniesEntity getCurrentUserCompanies() {
        return getUserCompanies(new User(firebaseUser.getUid()));
    }

    @Override
    public UserCompaniesEntity getUserCompanies(User employee) {
        UserCompaniesEntity userCompaniesEntity = new UserCompaniesEntity();
        if (firebaseUser != null) {
            DocumentSnapshot userCompaniesSnapshot = null;
            try {
                userCompaniesSnapshot = Tasks.await( fireStore.document(ParseClass.USER_COMPANIES + "/"
                        + employee.getObjectId()).get());
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            if (userCompaniesSnapshot != null) userCompaniesEntity = userCompaniesSnapshot.toObject(UserCompaniesEntity.class);
        }
        return userCompaniesEntity;
    }

    @Override
    public Observable<Boolean> addCompanyToUser(CompanyEntity company) {
        return addCompanyToUser(company.getObjectId());
    }

    @Override
    public Observable<Boolean> addCompanyToUser(String companyId) {
        return Observable.create(emitter -> {
            UserCompaniesEntity userCompaniesEntity = getCurrentUserCompanies();
            List<String> companiesIds = userCompaniesEntity.getCompanies();
            if (!companiesIds.contains(companyId)) companiesIds.add(companyId);
            DocumentReference userCompanyDocRef = fireStore.document(ParseClass.USER_COMPANIES + "/" + firebaseUser.getUid());
            userCompanyDocRef.update(ParseFields.userCompaniesCompanies, companiesIds).addOnSuccessListener(aVoid -> emitter.onNext(true))
                    .addOnFailureListener(e -> emitter.onNext(false));
        });
    }

    @Override
    public Observable<Boolean> exitFromCompany(String companyId) {
        return Observable.create(emit -> {
            UserCompaniesEntity userCompaniesEntity = getCurrentUserCompanies();
            userCompaniesEntity.getCompanies().remove(companyId);
            DocumentReference userCompaniesDocRef =
                    fireStore.document(ParseClass.USER_COMPANIES + "/" + firebaseUser.getUid());
            userCompaniesDocRef.set(userCompaniesEntity)
                    .addOnSuccessListener(aVoid -> emit.onNext(true))
                    .addOnFailureListener(e -> emit.onNext(false));
        });
    }

    @Override
    public void createUserCompaniesEntity() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            UserCompaniesEntity userCompaniesEntity = new UserCompaniesEntity(firebaseUser.getUid());
            DocumentReference userCompaniesDocRef = fireStore.document(ParseClass.USER_COMPANIES
                    + "/" + firebaseUser.getUid());
            userCompaniesDocRef.set(userCompaniesEntity);
        }
    }

    @Override
    public Observable<Boolean> setActiveCompany(String companyId) {
        return Observable.create(emit -> {
                    DocumentReference userCompaniesDocRef = fireStore.document(ParseClass.USER_COMPANIES
                            + "/" + firebaseUser.getUid());
                    userCompaniesDocRef.update(ParseFields.userCompaniesActiveCompany, companyId)
                            .addOnSuccessListener(aVoid -> emit.onNext(true))
                            .addOnFailureListener(e -> emit.onNext(false));
        });
    }

    @Override
    public Observable<Boolean> deactivateCompany() {
        return setActiveCompany("");
    }

    @Override
    public List<String> getUserCompaniesArray() {
        return getCurrentUserCompanies().getCompanies();
    }

    @Override
    public String getActiveCompanyId() {
        return getCurrentUserCompanies().getActiveCompany();
    }

    @Override
    public Query getUserCompaniesQuery() {
        CollectionReference userCompaniesCollRef = fireStore.collection(ParseClass.USER_COMPANIES);
        return userCompaniesCollRef
                .whereEqualTo(ParseFields.userCompaniesUserId, firebaseUser.getUid())
                .limit(1);
    }
}
