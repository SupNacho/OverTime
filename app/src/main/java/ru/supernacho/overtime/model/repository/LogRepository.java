package ru.supernacho.overtime.model.repository;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
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
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (objects != null && e == null){
                            list = objects;
                            for (ParseObject parseObject : list) {
                                set.add(new DateChooserEntry(parseObject.getInt(ParseFields.monthNum), parseObject.getInt(ParseFields.yearNum)));
                            }
                            emit.onNext(set.toArray());
                        }
                    }
                });
    });

    }
}
