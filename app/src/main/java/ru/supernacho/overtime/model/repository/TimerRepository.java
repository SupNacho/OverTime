package ru.supernacho.overtime.model.repository;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reactivex.Observable;
import timber.log.Timber;

public class TimerRepository {
    private Date zeroTime;
    private StringBuilder sb;

    public TimerRepository() {
        zeroTime = new Date();
        zeroTime.setTime(0L);
        sb = new StringBuilder();
    }

    public void startOverTime(String comment) {
        ParseObject overtime = new ParseObject("OverTime");
        String timeStamp = new SimpleDateFormat( "dd-MM-yy HH:mm:ss",Locale.US).format(new Date());
        sb.setLength(0);
        sb.append(timeStamp).append(" ").append(comment);
        overtime.put(ParseFileds.createdBy, ParseUser.getCurrentUser().getObjectId());
        overtime.put(ParseFileds.startDate, new Date());
        overtime.put(ParseFileds.stopDate, zeroTime);
        overtime.put(ParseFileds.comment, sb.toString());

        overtime.saveEventually(e -> {
            if (e == null) {
                Timber.d("Hey hey saved");
            } else {
                Timber.d("What? %s", e.getMessage());
            }
        });
    }

    public void stopOverTime(String comment) {
        Date zeroTime = new Date();
        zeroTime.setTime(0L);
        ParseQuery<ParseObject> runningTime = ParseQuery.getQuery("OverTime");

        runningTime
                .whereNotEqualTo(ParseFileds.startDate, null)
                .whereEqualTo(ParseFileds.createdBy, ParseUser.getCurrentUser().getObjectId())
                .whereEqualTo(ParseFileds.stopDate, zeroTime)
                .orderByDescending(ParseFileds.createdAt);

        runningTime.getFirstInBackground((object, e) -> {
            if (e == null && object != null) {
                String oldComment = (String) object.get(ParseFileds.comment);
                if (!comment.equals(oldComment) && !comment.equals("")){
                    String timeStamp = new SimpleDateFormat( "dd-MM-yy HH:mm:ss",Locale.US).format(new Date());
                    sb.setLength(0);
                    sb
                            .append(oldComment)
                            .append("\n")
                            .append(timeStamp)
                            .append(" ")
                            .append(comment);
                    object.put(ParseFileds.comment, sb.toString());
                }
                object.put(ParseFileds.stopDate, new Date());
                object.saveEventually(e1 -> {
                    if (e1 == null) {
                        Timber.d("Hey hey saved");
                    } else {
                        Timber.d("What? %s", e1.getMessage());
                    }
                });
            }
        });

    }

    public Observable<Long> restoreTimerState(){
        return Observable.create( emit -> {
            final Long[] startDate = new Long[1];
            ParseQuery<ParseObject> runningTime = ParseQuery.getQuery("OverTime");
            runningTime
                    .whereNotEqualTo(ParseFileds.startDate, null)
                    .whereEqualTo(ParseFileds.createdBy, ParseUser.getCurrentUser().getObjectId())
                    .whereEqualTo(ParseFileds.stopDate, zeroTime)
                    .orderByDescending(ParseFileds.createdAt);

            runningTime.getFirstInBackground((object, e) -> {
                if (e == null && object != null) {
                    startDate[0] = ((Date) object.get(ParseFileds.startDate)).getTime();
                    emit.onNext(startDate[0]);
                }
            });
        });
    }
}
