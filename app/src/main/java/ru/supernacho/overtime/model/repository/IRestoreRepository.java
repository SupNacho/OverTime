package ru.supernacho.overtime.model.repository;

import io.reactivex.Observable;

public interface IRestoreRepository {
    Observable<Boolean> requestRestore(String email);
}
