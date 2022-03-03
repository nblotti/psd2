package ch.nblotti.psd2;

import android.content.Context;

import com.google.android.play.core.splitcompat.SplitCompat;
import com.google.android.play.core.splitcompat.SplitCompatApplication;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class AggregatorApplication extends SplitCompatApplication {


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        SplitCompat.install(this);
    }

}
