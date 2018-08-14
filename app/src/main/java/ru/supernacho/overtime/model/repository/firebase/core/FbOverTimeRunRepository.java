package ru.supernacho.overtime.model.repository.firebase.core;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import io.reactivex.Observable;
import ru.supernacho.overtime.model.Entity.OverTimeEntity;
import ru.supernacho.overtime.model.repository.IOverTimeRunRepository;
import ru.supernacho.overtime.model.repository.ParseClass;
import ru.supernacho.overtime.model.repository.ParseFields;
import timber.log.Timber;

public class FbOverTimeRunRepository implements IOverTimeRunRepository {

    private Date zeroTime;
    private StringBuilder sb;
    private FirebaseFirestore fireStore;
    private FirebaseUser firebaseUser;


    public FbOverTimeRunRepository() {
        this.zeroTime = new Date();
        this.zeroTime.setTime(0L);
        this.sb = new StringBuilder();
        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        this.fireStore = FirebaseFirestore.getInstance();
        Timber.d("Fire store instance %s", FirebaseFirestore.getInstance().toString());
        Timber.d("Fire store instance %s", fireStore.toString());
    }

    @Override
    public void addComment(String comment) {
//        Date zeroTime = new Date();
//        zeroTime.setTime(0L);
//        CollectionReference overTimeCollRef = fireStore.collection(ParseClass.OVER_TIME);

        Query overTimeQuery = getOverTimeQuery(ParseFields.startDate);
//        Query overTimeQuery = overTimeCollRef.whereEqualTo(ParseFields.createdBy, firebaseUser.getUid())
//                .whereEqualTo(ParseFields.stopDate, zeroTime)
//                .orderBy(ParseFields.startDate, Query.Direction.DESCENDING)
//                .limit(1);
        overTimeQuery.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.getDocuments().get(0) != null) {
                String overTimeId = queryDocumentSnapshots.getDocuments().get(0).getId();
                DocumentSnapshot overTimeEntity = queryDocumentSnapshots.getDocuments().get(0);
                if (overTimeEntity != null) {
                    Map<String, Object> overTimeUpdate = new HashMap<>();
                    overTimeUpdate.put(ParseFields.comment, overTimeEntity.getString(ParseFields.comment));
                    overTimeUpdate.put(ParseFields.createdBy, overTimeEntity.getString(ParseFields.createdBy));
                    overTimeUpdate.put(ParseFields.forCompany, overTimeEntity.getString(ParseFields.forCompany));
                    overTimeUpdate.put(ParseFields.timeZoneID, overTimeEntity.getString(ParseFields.timeZoneID));
                    overTimeUpdate.put(ParseFields.monthNum, overTimeEntity.getLong(ParseFields.monthNum));
                    overTimeUpdate.put(ParseFields.yearNum, overTimeEntity.getLong(ParseFields.yearNum));
                    overTimeUpdate.put(ParseFields.startDate, overTimeEntity.getDate(ParseFields.startDate));
                    overTimeUpdate.put(ParseFields.stopDate, overTimeEntity.getDate(ParseFields.stopDate));
                    formComment(comment, overTimeEntity, overTimeUpdate);
                    DocumentReference overTimeDocRef = fireStore.document(ParseClass.OVER_TIME + "/" + overTimeId);
                    overTimeDocRef.set(overTimeUpdate);
                }
            }
        })
        .addOnFailureListener(e -> {
            Timber.d("+++ Fail query %s", e.getMessage());
        });
    }

    private void formComment(@NotNull String comment, @NotNull DocumentSnapshot overTimeEntity, Map<String, Object> newOverTime) {
        String oldComment = overTimeEntity.getString(ParseFields.comment);
        if (!comment.equals(oldComment) && !comment.equals("")) {
            String timeStamp = new SimpleDateFormat("dd-MM-yy HH:mm", Locale.US).format(new Date());
            sb.setLength(0);
            sb
                    .append(oldComment)
                    .append("\n")
                    .append(timeStamp)
                    .append(" ")
                    .append(comment);
            newOverTime.put(ParseFields.comment, sb.toString());
        }
    }

    @Override
    public void startOverTime(String comment, String companyId) {
        Date currentDate = new Date();
        String timeStamp = new SimpleDateFormat("dd-MM-yy HH:mm", Locale.US).format(currentDate);
        String yearStamp = new SimpleDateFormat("yyyy", Locale.US).format(currentDate);
        String monthStamp = new SimpleDateFormat("MM", Locale.US).format(currentDate);
        String timeZoneID = TimeZone.getDefault().getID();
        sb.setLength(0);
        sb.append(timeStamp).append(" ").append(comment);
        Map<String, Object> overtime = new HashMap<>();
        overtime.put(ParseFields.createdBy, firebaseUser.getUid());
        overtime.put(ParseFields.monthNum, Integer.parseInt(monthStamp));
        overtime.put(ParseFields.yearNum, Integer.parseInt(yearStamp));
        overtime.put(ParseFields.startDate, new Date());
        overtime.put(ParseFields.stopDate, zeroTime);
        overtime.put(ParseFields.comment, sb.toString());
        overtime.put(ParseFields.timeZoneID, timeZoneID);
        overtime.put(ParseFields.forCompany, companyId);
        fireStore.collection(ParseClass.OVER_TIME).add(overtime);
    }

    @Override
    public void stopOverTime(String comment) {
        Query overTimeQuery = getOverTimeQuery(ParseFields.createdAt);
        overTimeQuery.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.getDocuments().get(0) != null) {
                String overTimeId = queryDocumentSnapshots.getDocuments().get(0).getId();
                DocumentSnapshot overTimeEntity = queryDocumentSnapshots.getDocuments().get(0);
                if (overTimeEntity != null) {
                    Map<String, Object> overTimeUpdate = new HashMap<>();
                    overTimeUpdate.put(ParseFields.comment, overTimeEntity.getString(ParseFields.comment));
                    overTimeUpdate.put(ParseFields.createdBy, overTimeEntity.getString(ParseFields.createdBy));
                    overTimeUpdate.put(ParseFields.forCompany, overTimeEntity.getString(ParseFields.forCompany));
                    overTimeUpdate.put(ParseFields.timeZoneID, overTimeEntity.getString(ParseFields.timeZoneID));
                    overTimeUpdate.put(ParseFields.monthNum, overTimeEntity.getLong(ParseFields.monthNum));
                    overTimeUpdate.put(ParseFields.yearNum, overTimeEntity.getLong(ParseFields.yearNum));
                    overTimeUpdate.put(ParseFields.startDate, overTimeEntity.getDate(ParseFields.startDate));
                    overTimeUpdate.put(ParseFields.stopDate, new Date());
                    formComment(comment, overTimeEntity, overTimeUpdate);
                    DocumentReference overTimeDocRef = fireStore.document(ParseClass.OVER_TIME + "/" + overTimeId);
                    overTimeDocRef.set(overTimeUpdate).addOnSuccessListener(aVoid -> {
                        Timber.d("UPDATED");
                    })
                    .addOnFailureListener( e -> {
                        Timber.d("FAIL %s", e.getMessage());
                    });
                }
//                if (overTimeEntity != null) {
//                    formComment(comment, overTimeEntity);
//                    fireStore.collection(ParseClass.OVER_TIME)
//                            .document(overTimeId).set(overTimeEntity);
//                }
            }
        });
    }

    @NonNull
    private Query getOverTimeQuery(String orderField) {
        Date zeroTime = new Date();
        zeroTime.setTime(0L);
        CollectionReference overTimeCollRef = fireStore.collection(ParseClass.OVER_TIME);
        return overTimeCollRef.whereEqualTo(ParseFields.createdBy, firebaseUser.getUid())
                .whereEqualTo(ParseFields.stopDate, zeroTime)
                .orderBy(orderField, Query.Direction.DESCENDING)
                .limit(1);
    }

    @Override
    public Observable<Long> restoreTimerState() {
        return Observable.create(emit -> {
            Query overTimeQuery = getOverTimeQuery(ParseFields.createdAt);
            overTimeQuery.get().addOnSuccessListener(queryDocumentSnapshots ->
                    emit.onNext(Objects.requireNonNull(queryDocumentSnapshots.getDocuments().get(0).getDate(ParseFields.startDate)).getTime()));
        });
    }
}
