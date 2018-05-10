package ru.supernacho.overtime.model.repository;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import java.util.Date;

import timber.log.Timber;

public class TimerRepository {
    private Date zeroTime;

    public TimerRepository() {
        zeroTime = new Date();
        zeroTime.setTime(0L);
    }

    public void startOverTime() {
        ParseObject overtime = new ParseObject("OverTime");
        overtime.put(ParseFileds.createdBy, ParseUser.getCurrentUser().getObjectId());
        overtime.put(ParseFileds.startDate, new Date());
        overtime.put(ParseFileds.stopDate, zeroTime);

        overtime.put(ParseFileds.comment, "metrak");

        overtime.saveEventually(e -> {
            if (e == null) {
                Timber.d("Hey hey saved");
            } else {
                Timber.d("What? %s", e.getMessage());
            }
        });
    }

    public void stopOverTime() {
        Date zeroTime = new Date();
        zeroTime.setTime(0L);
        ParseQuery<ParseObject> runningTime = ParseQuery.getQuery("OverTime");

        runningTime
                .whereNotEqualTo(ParseFileds.startDate, null)
                .whereEqualTo(ParseFileds.createdBy, ParseUser.getCurrentUser().getObjectId())
                .whereEqualTo(ParseFileds.stopDate, zeroTime);

        runningTime.getFirstInBackground((object, e) -> {
            if (e == null && object != null) {
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
}
