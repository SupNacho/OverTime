package ru.supernacho.overtime.model.repository.firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import ru.supernacho.overtime.model.Entity.CompanyEntity;
import ru.supernacho.overtime.model.Entity.User;
import ru.supernacho.overtime.model.Entity.UserCompany;
import ru.supernacho.overtime.model.repository.ICompanyRepository;
import ru.supernacho.overtime.model.repository.ILoginRepository;
import ru.supernacho.overtime.model.repository.IUserCompanyRepository;
import ru.supernacho.overtime.model.repository.ParseClass;
import ru.supernacho.overtime.model.repository.ParseFields;
import ru.supernacho.overtime.model.repository.RepoEvents;
import timber.log.Timber;

public class FbLoginRepository implements ILoginRepository {
    private PublishSubject<RepoEvents> repoEventBus;
    private IUserCompanyRepository userCompanyRepository;
    private ICompanyRepository companyRepository;
    private FirebaseAuth auth;
    private FirebaseUser fbUser;

    public FbLoginRepository(IUserCompanyRepository userCompanyRepository, ICompanyRepository companyRepository) {
        this.userCompanyRepository = userCompanyRepository;
        this.companyRepository = companyRepository;
        this.repoEventBus = PublishSubject.create();
        this.auth = FirebaseAuth.getInstance();
    }

    @Override
    public void registerUser(User user, String password) {
        auth.createUserWithEmailAndPassword(user.getEmail(), password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        sendEmailVerification();
                        repoEventBus.onNext(RepoEvents.REGISTRATION_SUCCESS);
                        DocumentReference doc = FirebaseFirestore.getInstance()
                                .collection(ParseClass.PARSE_USER)
                                .document(fbUser.getUid());
                        Map<String, Object> dataToSave = new HashMap<>();
                        dataToSave.put(ParseFields.userId, fbUser.getUid());
                        dataToSave.put(ParseFields.fullName, user.getFullName());
                        dataToSave.put(ParseFields.userEmail, fbUser.getEmail());
                        doc.set(dataToSave);
                        userCompanyRepository.createUserCompaniesEntity();
                    }
                })
                .addOnFailureListener(task -> {
                    repoEventBus.onNext(RepoEvents.REGISTRATION_FAILED);
                    Timber.d("%s", task.getMessage());
                });
    }

    private void sendEmailVerification() {
        fbUser = auth.getCurrentUser();
        if (fbUser != null) fbUser.sendEmailVerification();
    }

    @Override
    public void loginIn(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> repoEventBus.onNext(RepoEvents.LOGIN_SUCCESS))
                .addOnFailureListener(e -> repoEventBus.onNext(RepoEvents.LOGIN_FAILED_WRONG_PASS));
    }

    @Override
    public Observable<Boolean> addCompanyToUser(String companyId) {
        return userCompanyRepository.addCompanyToUser(companyId);
    }

    @Override
    public void checkLoginStatus() {
        if (fbUser == null) {
            repoEventBus.onNext(RepoEvents.LOGIN_NEEDED);
        } else {
            repoEventBus.onNext(RepoEvents.LOGIN_SUCCESS);
        }
    }

    @Override
    public Observable<String> getUserData() {
        return Observable.create(emit -> {
            String uid;
            if (auth.getCurrentUser() != null) {
                uid = auth.getCurrentUser().getUid();
            } else {
                uid = "";
            }
            DocumentReference userDoc = FirebaseFirestore.getInstance()
                    .document(ParseClass.PARSE_USER + "/" + uid);
            userDoc.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists())
                    emit.onNext(Objects.requireNonNull(documentSnapshot.get(ParseFields.fullName)).toString());
            });
        });
    }

    @Override
    public Observable<UserCompany> userIsAdmin() {
        return companyRepository.userIsAdmin();
    }

    @Override
    public Observable<CompanyEntity> getCurrentCompany() {
        return companyRepository.getCurrentCompany();
    }

    @Override
    public PublishSubject<RepoEvents> getRepoEventBus() {
        return repoEventBus;
    }

    @Override
    public Observable<Boolean> logout() {
        return Observable.create(emit -> {
                    auth.signOut();
                    emit.onNext(true);
                }
        );
    }
}
