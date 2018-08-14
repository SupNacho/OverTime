package ru.supernacho.overtime.model.repository.parseplatform;

import com.parse.ParseUser;

import io.reactivex.Observable;
import ru.supernacho.overtime.model.repository.IRestoreRepository;

public class RestoreRepository implements IRestoreRepository{
    @Override
    public Observable<Boolean> requestRestore(String email){
        return Observable.create( emit -> {
            ParseUser.requestPasswordResetInBackground(email, e -> {
                if (e == null) emit.onNext(true);
                else emit.onNext(false);
            });
        });
    }
}
