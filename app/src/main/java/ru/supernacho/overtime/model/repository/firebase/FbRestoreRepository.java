package ru.supernacho.overtime.model.repository.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.parse.ParseUser;

import io.reactivex.Observable;
import ru.supernacho.overtime.model.repository.IRestoreRepository;

public class FbRestoreRepository implements IRestoreRepository{
    @Override
    public Observable<Boolean> requestRestore(String email){
        return Observable.create( emit -> FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnSuccessListener(aVoid -> emit.onNext(true))
                .addOnFailureListener( e -> emit.onNext(false)));
    }
}
