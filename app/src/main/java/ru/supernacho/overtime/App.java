package ru.supernacho.overtime;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;

import net.danlew.android.joda.JodaTimeAndroid;

import io.reactivex.plugins.RxJavaPlugins;
import ru.supernacho.overtime.di.AppComponent;
import ru.supernacho.overtime.di.DaggerAppComponent;
import ru.supernacho.overtime.di.modules.AppModule;
import timber.log.Timber;

public class App extends Application {
    private static App instance;
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Timber.plant(new Timber.DebugTree());

        JodaTimeAndroid.init(this);

        RxJavaPlugins.setErrorHandler(e -> Timber.d("Error %s", e.getMessage()));
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
        Parse.enableLocalDatastore(this);
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId(getResources().getString(R.string.app_id))
                .clientKey(getResources().getString(R.string.client_id))
                .server(getResources().getString(R.string.server_address))
                .build());

        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }

    public static App getInstance() {
        return instance;
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
