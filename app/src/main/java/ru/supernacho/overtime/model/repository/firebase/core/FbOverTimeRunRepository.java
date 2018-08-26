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
import ru.supernacho.overtime.model.repository.IOverTimeRunRepository;
import ru.supernacho.overtime.model.repository.ParseClass;
import ru.supernacho.overtime.model.repository.ParseFields;

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
    }

    @Override
    public void addComment(String comment) {
        Query query = getOverTimeQuery();
        query.get().addOnSuccessListener(querySnapshot -> {
            if (!querySnapshot.isEmpty()) {
                DocumentSnapshot overTimeSnapShot = querySnapshot.getDocuments().get(0);
                DocumentReference docUpdate = fireStore.document(ParseClass.OVER_TIME + "/" + overTimeSnapShot.getId());
                formComment(comment, overTimeSnapShot);
                docUpdate.update(ParseFields.comment, sb.toString());
            }
        });
    }

    private void formComment(@NotNull String comment, @NotNull DocumentSnapshot overTimeEntity) {
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

        Query query = getOverTimeQuery();
        query.get().addOnSuccessListener(querySnapshot -> {
            if (!querySnapshot.isEmpty()) {
                DocumentSnapshot overTimeSnapShot = querySnapshot.getDocuments().get(0);
                DocumentReference docUpdate = fireStore.document(ParseClass.OVER_TIME + "/" + overTimeSnapShot.getId());
                formComment(comment, overTimeSnapShot);
                docUpdate.update(ParseFields.comment, sb.toString(), ParseFields.stopDate, new Date());
            }
        });
    }

    @Override
    public Observable<Long> restoreTimerState() {
        return Observable.create(emit -> {
            Query query = getOverTimeQuery();
            query.get().addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    emit.onNext(Objects.requireNonNull(queryDocumentSnapshots.getDocuments().get(0)
                            .getTimestamp(ParseFields.startDate)).toDate().getTime());
                }
            });
        });
    }

    @NonNull
    private Query getOverTimeQuery() {
        CollectionReference collectionReference = fireStore.collection(ParseClass.OVER_TIME);
        return collectionReference.whereEqualTo(ParseFields.createdBy, firebaseUser.getUid())
                .whereEqualTo(ParseFields.stopDate, zeroTime)
                .orderBy(ParseFields.startDate, Query.Direction.DESCENDING)
                .limit(1);
    }
}
