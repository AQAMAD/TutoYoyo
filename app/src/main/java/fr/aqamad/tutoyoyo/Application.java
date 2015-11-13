package fr.aqamad.tutoyoyo;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;
import com.evernote.android.job.JobManager;

import fr.aqamad.tutoyoyo.model.Sponsors;

/**
 * Created by Gregoire on 13/11/2015.
 */
public class Application extends com.activeandroid.app.Application {

    //maintain singleton instance
    private static Application singleton;
    //singleton sponsors instance
    private static Sponsors sponsors;

    public static Sponsors getSponsors() {
        return sponsors;
    }

    public Application getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        sponsors = new Sponsors(getApplicationContext());
        JobManager.create(this, new YoyoTutsJobCreator());
    }

    //evernote job manager will help with sync issues
    private static class YoyoTutsJobCreator implements JobCreator {

        @Override
        public Job create(String tag) {
            switch (tag) {
//                case TestJob.TAG:
//                    return new TestJob();
                default:
                    throw new RuntimeException("Cannot find job for tag " + tag);
            }
        }
    }
}
