package ru.supernacho.overtime;

import android.app.Application;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.parse.Parse;
import com.parse.ParseACL;

import net.danlew.android.joda.JodaTimeAndroid;

import io.reactivex.Scheduler;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import ru.supernacho.overtime.di.AppComponent;
import ru.supernacho.overtime.di.DaggerAppComponent;
import ru.supernacho.overtime.di.modules.AppModule;
import timber.log.Timber;

public class App extends Application {
    private static App instance;
    private AppComponent appComponent;
    private static Scheduler fireStoreScheduller;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        fireStoreScheduller = Schedulers.single();
        Timber.plant(new Timber.DebugTree());

        JodaTimeAndroid.init(this);

        RxJavaPlugins.setErrorHandler(e -> Timber.d("Error %s", e.getMessage()));

        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);
    }

    public static App getInstance() {
        return instance;
    }

    public static Scheduler getFbThread(){
        return fireStoreScheduller;
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
