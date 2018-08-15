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
//
//        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
//                .applicationId(getResources().getString(R.string.app_id))
//                .clientKey(getResources().getString(R.string.client_id))
//                .server(getResources().getString(R.string.server_address))
//                .build());

        FirebaseFirestore
                .getInstance()
                .setFirestoreSettings(new FirebaseFirestoreSettings.Builder()
                        .setPersistenceEnabled(true).build());

        Timber.d("Fire store instance %s", FirebaseFirestore.getInstance().toString());
//
//        ParseACL defaultACL = new ParseACL();
//        defaultACL.setPublicReadAccess(true);
//        defaultACL.setPublicWriteAccess(true);
//        ParseACL.setDefaultACL(defaultACL, true);
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
