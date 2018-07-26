package ru.supernacho.overtime.model.repository;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import io.reactivex.Observable;
import ru.supernacho.overtime.utils.NetworkStatus;

public class OverTimeRunRepository {

    private Date zeroTime;
    private StringBuilder sb;

    public OverTimeRunRepository() {
        this.zeroTime = new Date();
        this.zeroTime.setTime(0L);
        this.sb = new StringBuilder();
    }

    public void addComment(String comment){
        Date zeroTime = new Date();
        zeroTime.setTime(0L);
        ParseQuery<ParseObject> runningTime = ParseQuery.getQuery(ParseClass.OVER_TIME);
        if (NetworkStatus.getStatus() == NetworkStatus.Status.OFFLINE){
            runningTime
                    .whereNotEqualTo(ParseFields.startDate, null)
                    .whereEqualTo(ParseFields.createdBy, ParseUser.getCurrentUser().getObjectId())
                    .whereEqualTo(ParseFields.stopDate, zeroTime)
                    .orderByDescending(ParseFields.createdAt)
                    .fromLocalDatastore();
        } else {
            runningTime
                    .whereNotEqualTo(ParseFields.startDate, null)
                    .whereEqualTo(ParseFields.createdBy, ParseUser.getCurrentUser().getObjectId())
                    .whereEqualTo(ParseFields.stopDate, zeroTime)
                    .orderByDescending(ParseFields.createdAt);
        }

        runningTime.getFirstInBackground((object, e) -> {
            if (e == null && object != null) {
                formComment(comment, object);
                object.saveEventually();
            }
        });
    }

    public void startOverTime(String comment, String companyId){
        ParseObject overtime = new ParseObject("OverTime");
        Date currentDate = new Date();
        String timeStamp = new SimpleDateFormat( "dd-MM-yy HH:mm", Locale.US).format(currentDate);
        String yearStamp = new SimpleDateFormat( "yyyy",Locale.US).format(currentDate);
        String monthStamp = new SimpleDateFormat( "MM",Locale.US).format(currentDate);
        String timeZoneID = TimeZone.getDefault().getID();
        sb.setLength(0);
        sb.append(timeStamp).append(" ").append(comment);
        overtime.put(ParseFields.createdBy, ParseUser.getCurrentUser().getObjectId());
        overtime.put(ParseFields.monthNum, Integer.parseInt(monthStamp));
        overtime.put(ParseFields.yearNum, Integer.parseInt(yearStamp));
        overtime.put(ParseFields.startDate, new Date());
        overtime.put(ParseFields.stopDate, zeroTime);
        overtime.put(ParseFields.comment, sb.toString());
        overtime.put(ParseFields.timeZoneID, timeZoneID);
        overtime.put(ParseFields.forCompany, companyId);
        overtime.saveEventually();
    }

    public void stopOverTime(String comment){
        Date zeroTime = new Date();
        zeroTime.setTime(0L);
        ParseQuery<ParseObject> runningTime = ParseQuery.getQuery(ParseClass.OVER_TIME);
        if(NetworkStatus.getStatus() == NetworkStatus.Status.OFFLINE) {
            runningTime
                    .whereNotEqualTo(ParseFields.startDate, null)
                    .whereEqualTo(ParseFields.createdBy, ParseUser.getCurrentUser().getObjectId())
                    .whereEqualTo(ParseFields.stopDate, zeroTime)
                    .orderByDescending(ParseFields.createdAt)
                    .fromLocalDatastore();
        } else {
            runningTime
                    .whereNotEqualTo(ParseFields.startDate, null)
                    .whereEqualTo(ParseFields.createdBy, ParseUser.getCurrentUser().getObjectId())
                    .whereEqualTo(ParseFields.stopDate, zeroTime)
                    .orderByDescending(ParseFields.createdAt);
        }

        runningTime.getFirstInBackground((object, e) -> {
            if (e == null && object != null) {
                formComment(comment, object);
                object.put(ParseFields.stopDate, new Date());
                object.saveEventually();
            }
        });
    }

    private void formComment(@NotNull String comment, @NotNull ParseObject object) {
        String oldComment = (String) object.get(ParseFields.comment);
        if (!comment.equals(oldComment) && !comment.equals("")){
            String timeStamp = new SimpleDateFormat( "dd-MM-yy HH:mm", Locale.US).format(new Date());
            sb.setLength(0);
            sb
                    .append(oldComment)
                    .append("\n")
                    .append(timeStamp)
                    .append(" ")
                    .append(comment);
            object.put(ParseFields.comment, sb.toString());
        }
    }

    public Observable<Long> restoreTimerState(){
        return Observable.create( emit -> {
            final Long[] startDate = new Long[1];
            ParseQuery<ParseObject> runningTime = ParseQuery.getQuery(ParseClass.OVER_TIME);
            runningTime.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
            runningTime
                    .whereNotEqualTo(ParseFields.startDate, null)
                    .whereEqualTo(ParseFields.createdBy, ParseUser.getCurrentUser().getObjectId())
                    .whereEqualTo(ParseFields.stopDate, zeroTime)
                    .orderByDescending(ParseFields.createdAt);

            runningTime.getFirstInBackground((object, e) -> {
                if (e == null && object != null) {
                    startDate[0] = ((Date) object.get(ParseFields.startDate)).getTime();
                    emit.onNext(startDate[0]);
                }
            });
        });
    }
}
