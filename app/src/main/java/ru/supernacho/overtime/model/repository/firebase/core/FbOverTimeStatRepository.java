package ru.supernacho.overtime.model.repository.firebase.core;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import ru.supernacho.overtime.App;
import ru.supernacho.overtime.R;
import ru.supernacho.overtime.model.Entity.CompanyEntity;
import ru.supernacho.overtime.model.Entity.DateChooserEntry;
import ru.supernacho.overtime.model.Entity.OverTimeEntity;
import ru.supernacho.overtime.model.Entity.User;
import ru.supernacho.overtime.model.Entity.UserCompanyStat;
import ru.supernacho.overtime.model.repository.ICompanyRepository;
import ru.supernacho.overtime.model.repository.IOverTimeStatRepository;
import ru.supernacho.overtime.model.repository.IUserCompanyRepository;
import ru.supernacho.overtime.model.repository.ParseClass;
import ru.supernacho.overtime.model.repository.ParseFields;
import ru.supernacho.overtime.utils.charts.DurationToStringConverter;

public class FbOverTimeStatRepository implements IOverTimeStatRepository {
    private List<UserCompanyStat> stats;
    private Map<User, Long> usersMap;
    private List<OverTimeEntity> overTimesList;
    private String userId = ParseFields.userZero;
    private List<DocumentSnapshot> list;
    private Set<DateChooserEntry> set;
    private IUserCompanyRepository userCompanyRepository;
    private ICompanyRepository companyRepository;
    private FirebaseFirestore fireStore;
    private FirebaseUser firebaseUser;

    public FbOverTimeStatRepository(IUserCompanyRepository userCompanyRepository, ICompanyRepository companyRepository) {
        this.userCompanyRepository = userCompanyRepository;
        this.companyRepository = companyRepository;
        this.stats = new ArrayList<>();
        this.usersMap = new HashMap<>();
        this.overTimesList = new ArrayList<>();
        this.set = new HashSet<>();
        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        this.fireStore = FirebaseFirestore.getInstance();
    }

    @Override
    public Observable<Object[]> getMonthsByUserId(String userId) {
        return Observable.create(emit -> {
            String user;
            if (userId.equals(ParseFields.userZero)) {
                user = firebaseUser.getUid();
            } else {
                user = userId;
            }
            formDatesArray(emit, user, ParseFields.createdBy);
        });
    }

    private void formDatesArray(ObservableEmitter<Object[]> emit, String fieldContent, String fieldName) {
        CollectionReference overTimeCollRef = fireStore.collection(ParseClass.OVER_TIME);
        Query overTimeQuery = overTimeCollRef.whereEqualTo(fieldName, fieldContent);
        overTimeQuery.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                set.clear();
                list = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot documentSnapshot : list) {
                    set.add(new DateChooserEntry(documentSnapshot.getLong(ParseFields.monthNum).intValue(),
                            documentSnapshot.getLong(ParseFields.yearNum).intValue()));
                }
                emit.onNext(set.toArray());
            }
        });
    }

    @Override
    public Observable<Object[]> getAllEmployeesMonths() {
        return Observable.create(emit -> {
            String companyId = userCompanyRepository.getActiveCompanyId();
            formDatesArray(emit, companyId, ParseFields.forCompany);
        });
    }

    @Override
    public Observable<List<UserCompanyStat>> getStats(int month, int year) {
        usersMap.clear();
        stats.clear();
        return Observable.create(emit -> {
            CollectionReference overTimeCollRef = fireStore.collection(ParseClass.OVER_TIME);
            QuerySnapshot querySnapshot = Tasks.await(overTimeCollRef.whereEqualTo(ParseFields.monthNum, month)
                    .whereEqualTo(ParseFields.yearNum, year)
                    .whereEqualTo(ParseFields.forCompany, userCompanyRepository.getActiveCompanyId()).get());
            if (!querySnapshot.isEmpty()) {
                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                    User userEntity = getUser(documentSnapshot);
                    addToMap(documentSnapshot, userEntity);
                }
                for (Map.Entry<User, Long> entry : usersMap.entrySet()) {
                    stats.add(new UserCompanyStat(entry.getKey(), entry.getValue()));
                }
                emit.onNext(stats);
            }
        });
    }

    @Override
    public Observable<String> getFullStatForShare() {
        return Observable.create(emit -> {
            StringBuilder stringBuilder = new StringBuilder();
            for (UserCompanyStat stat : stats) {
                formSummaryStrings(stringBuilder, stat);
                List<DocumentSnapshot> overTimes = getOverTimes(stat);
                formDetailStrings(stringBuilder, overTimes);
            }
            emit.onNext(stringBuilder.toString());
        });
    }

    @Override
    public Observable<List<OverTimeEntity>> getOverTimesByUserId(int month, int year, String userId, String forCompany) {
        return Observable.create(emit -> {
            Date zeroTime = new Date();
            zeroTime.setTime(0);
            CollectionReference collectionReference = fireStore.collection(ParseClass.OVER_TIME);
            if (userId != null) {
                this.userId = userId;
            } else {
                this.userId = firebaseUser.getUid();
            }
            QuerySnapshot overTimesSnapshot;
            if (forCompany != null) {
                overTimesSnapshot = Tasks.await(collectionReference
                        .whereEqualTo(ParseFields.createdBy, this.userId)
                        .whereEqualTo(ParseFields.monthNum, month)
                        .whereEqualTo(ParseFields.yearNum, year)
                        .whereEqualTo(ParseFields.forCompany, forCompany)
                        .whereGreaterThan(ParseFields.stopDate, zeroTime).get());
            } else {
                overTimesSnapshot = Tasks.await(collectionReference
                        .whereEqualTo(ParseFields.createdBy, this.userId)
                        .whereEqualTo(ParseFields.monthNum, month)
                        .whereEqualTo(ParseFields.yearNum, year)
                        .whereGreaterThan(ParseFields.stopDate, zeroTime).get());
            }
            if (!overTimesSnapshot.isEmpty()) {
                overTimesList.clear();
                for (DocumentSnapshot documentSnapshot : overTimesSnapshot.getDocuments()) {
                    Date start = documentSnapshot.getDate(ParseFields.startDate);
                    Date stop = documentSnapshot.getDate(ParseFields.stopDate);
                    String timeZoneID = documentSnapshot.getString(ParseFields.timeZoneID);
                    long duration = 0;
                    if (stop != null && start != null) {
                        duration = stop.getTime() - start.getTime();
                    }
                    CompanyEntity companyEntity = companyRepository.getCompanyByOverTime(documentSnapshot);

                    overTimesList.add(new OverTimeEntity(start, stop, timeZoneID, duration,
                            documentSnapshot.getString(ParseFields.comment), companyEntity));
                }
                emit.onNext(overTimesList);
            }
        });
    }

    private void addToMap(@NotNull DocumentSnapshot overTime, User userEntity) {
        long duration = Objects.requireNonNull(overTime.getDate(ParseFields.stopDate)).getTime()
                - Objects.requireNonNull(overTime.getDate(ParseFields.startDate)).getTime();
        if (usersMap.containsKey(userEntity)) {
            usersMap.put(userEntity, usersMap.get(userEntity)
                    + duration);
        } else {
            usersMap.put(userEntity, duration);
        }
    }

    @NonNull
    private User getUser(@NotNull DocumentSnapshot overTime) {
        String userId = overTime.getString(ParseFields.createdBy);
        CollectionReference collectionReference = fireStore.collection(ParseClass.PARSE_USER);
        Query query = collectionReference
                .whereEqualTo(FieldPath.documentId(), userId)
                .limit(1);
        QuerySnapshot querySnapshot = null;
        try {
            querySnapshot = Tasks.await(query.get());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        if (querySnapshot != null && !querySnapshot.isEmpty()) {
            DocumentSnapshot user = querySnapshot.getDocuments().get(0);
            return new User(Objects.requireNonNull(user).getId(),
                    user.getString(ParseFields.userName),
                    user.getString(ParseFields.fullName),
                    user.getString(ParseFields.userEmail),
                    false);
        } else {
            return new User();
        }
    }

    private List<DocumentSnapshot> getOverTimes(@NotNull UserCompanyStat stat) {
        CollectionReference collectionReference = fireStore.collection(ParseClass.OVER_TIME);
        Query query = collectionReference
                .whereEqualTo(ParseFields.createdBy, stat.getUser().getObjectId())
                .whereEqualTo(ParseFields.forCompany, userCompanyRepository.getActiveCompanyId());
        QuerySnapshot querySnapshot = null;
        try {
            querySnapshot = Tasks.await(query.get());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return Objects.requireNonNull(querySnapshot).getDocuments();
    }

    private void formSummaryStrings(StringBuilder stringBuilder, UserCompanyStat stat) {
        Context context = App.getInstance().getApplicationContext();
        stringBuilder
                .append(context.getResources().getString(R.string.employee)).append(stat.getUser().getFullName()).append("\n")
                .append(context.getResources().getString(R.string.total_overtime)).append(DurationToStringConverter.convert(stat.getTimeSummary()))
                .append("\n");
    }

    private void formDetailStrings(StringBuilder stringBuilder, List<DocumentSnapshot> overTimes) {
        Context context = App.getInstance().getApplicationContext();
        for (DocumentSnapshot overTime : overTimes) {
            Date start = overTime.getDate(ParseFields.startDate);
            Date stop = overTime.getDate(ParseFields.stopDate);
            String timeZoneID = overTime.getString(ParseFields.timeZoneID);
            String otComment = overTime.getString(ParseFields.comment);
            OverTimeEntity overTimeEntity = null;
            if (stop != null && start != null) {
                overTimeEntity = new OverTimeEntity(start, stop, timeZoneID,
                        stop.getTime() - start.getTime(), otComment, null);
            }
            stringBuilder
                    .append("\n")
                    .append(context.getResources().getString(R.string.employee_start_time))
                    .append(overTimeEntity != null ? overTimeEntity.getStartDateTimeLabel() : null)
                    .append("\n").append(context.getResources().getString(R.string.employee_stop_time))
                    .append(overTimeEntity != null ? overTimeEntity.getStopDateTimeLabel() : null)
                    .append("\n").append(context.getResources().getString(R.string.employee_duration))
                    .append(overTimeEntity != null ? overTimeEntity.getDurationString() : null)
                    .append("\n").append(context.getResources().getString(R.string.employee_over_time_comment))
                    .append("\n")
                    .append(overTimeEntity != null ? overTimeEntity.getComment() : null)
                    .append("\n");
        }
        stringBuilder.append(context.getResources().getString(R.string.employee_over_time_delimiter)).append("\n\n");
    }
}
