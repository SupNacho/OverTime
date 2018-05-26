package ru.supernacho.overtime.model.repository;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.Observable;
import ru.supernacho.overtime.model.Entity.DateChooserEntry;

public class LogRepository {
    private List<ParseObject> list;
    private Set<DateChooserEntry> set = new HashSet<>();


    public Observable<Object[]> getMonths(){
        return Observable.create(emit -> {
            ParseQuery<ParseObject> query = new ParseQuery<>("OverTime");
            query.whereEqualTo(ParseFields.createdBy, ParseUser.getCurrentUser().getObjectId());
                query.findInBackground((objects, e) -> {
                    if (objects != null && e == null){
                        set.clear();
                        list = objects;
                        for (ParseObject parseObject : list) {
                            set.add(new DateChooserEntry(parseObject.getInt(ParseFields.monthNum), parseObject.getInt(ParseFields.yearNum)));
                        }
                        emit.onNext(set.toArray());
                    }
                });
    });

    }
}
