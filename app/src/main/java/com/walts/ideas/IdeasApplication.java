package com.walts.ideas;

import android.app.Application;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

@ReportsCrashes(
        mailTo = "valeri_ivanov35@hotmail.com",
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.something_went_wrong)
public class IdeasApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ACRA.init(this);
    }

}
