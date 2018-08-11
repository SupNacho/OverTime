package ru.supernacho.overtime.model.repository;

import com.parse.ParseUser;

import io.reactivex.Observable;

public class RestoreRepository {
    public Observable<Boolean> requestRestore(String email){
        return Observable.create( emit -> {
            ParseUser.requestPasswordResetInBackground(email, e -> {
                if (e == null) emit.onNext(true);
                else emit.onNext(false);
            });
        });
    }
}
