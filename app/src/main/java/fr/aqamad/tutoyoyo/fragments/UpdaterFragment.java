package fr.aqamad.tutoyoyo.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import fr.aqamad.commons.youtube.YoutubeChannel;
import fr.aqamad.commons.youtube.YoutubePlaylist;
import fr.aqamad.commons.youtube.YoutubeThumbnail;
import fr.aqamad.commons.youtube.YoutubeUtils;
import fr.aqamad.tutoyoyo.Application;
import fr.aqamad.tutoyoyo.R;
import fr.aqamad.tutoyoyo.model.ModelConverter;
import fr.aqamad.tutoyoyo.model.Sponsor;
import fr.aqamad.tutoyoyo.model.Sponsors;
import fr.aqamad.tutoyoyo.model.TutorialPlaylist;
import fr.aqamad.tutoyoyo.model.TutorialSource;
import fr.aqamad.tutoyoyo.utils.Debug;


/**
 * Created by Gregoire on 20/10/2015.
 */
public class UpdaterFragment extends Fragment {

    /**
     * define the new task class from the newInstance
     * @return
     */

    protected TaskCallbacks mCallbacks;
    private UpdaterTask mTask;

    /**
     * Hold a reference to the parent Activity so we can report the
     * task's current progress and results. The Android framework
     * will pass us a reference to the newly created Activity after
     * each configuration change.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (TaskCallbacks) activity;
    }

    /**
     * This method will only be called once when the retained
     * Fragment is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);

        // Create and execute the background task.
        mTask=new UpdaterTask();
        mTask.execute();
    }

    /**
     * Set the callback to null so we don't accidentally leak the
     * Activity instance.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    /**
     * Callback interface through which the fragment will report the
     * task's progress and results back to the Activity.
     */
    public interface TaskCallbacks {
        void onPreExecute();

        void onProgressUpdate(ProgressInfo info);

        void onCancelled();

        void onPostExecute();
    }

    public class ProgressInfo{
        public int playlistsProgress;
        public int playlistsMax;
        public String currentlyDoing;
        public int totalVideos;
    }




    public class UpdaterTask extends AsyncTask<Void, ProgressInfo, Void> {

        public UpdaterTask() {
        }



        @Override
        protected void onPreExecute() {
            if (mCallbacks != null) {
                mCallbacks.onPreExecute();
            }
        }

        /**
         * Note that we do NOT call the callback object's methods
         * directly from the background thread, as this could result
         * in a race condition.
         */
        @Override
        protected Void doInBackground(Void... params) {
            //depends on preferences
            //get preference value for refreshing
            //prepare sources for refteching
            SharedPreferences appPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String refreshPeriod = appPreferences.getString(getString(R.string.lst_pref_refresh_playlist), "7");
            Log.d("MA.CD", "RefreshPeriod : " + refreshPeriod);
            List<Sponsor> sponsorsToRefresh = new ArrayList<Sponsor>();
            Date refreshDate=new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(refreshDate);
            switch (refreshPeriod){
                case "1":
                    c.add(Calendar.DATE, -1);
                    break;
                case "7":
                    c.add(Calendar.DATE, -7);
                    break;
                case "30":
                    c.add(Calendar.DATE, -30);
                    break;
            }
            //for testing purpose only
            c.add(Calendar.DATE, Debug.debugRefresh);
            refreshDate = c.getTime();
            Log.d("MA.CD","RefreshPlaylists fetched before : " + refreshDate);
            Log.d("MA.CD","RefreshPlaylists fetched before : " + refreshDate.getTime());
            //and get refresh dates for the playlists
            List<TutorialPlaylist> lists =TutorialPlaylist.getOlderThan(refreshDate);
            //get sponsors
            Sponsors sponsors = Application.getSponsors();
            //we'eve got the playlists
            ProgressInfo pi=new ProgressInfo();
            pi.playlistsMax=lists.size();
            pi.playlistsProgress=0;
            pi.totalVideos=0;
            for (TutorialPlaylist pl :
                    lists) {
                //how to refetch playlist ?
                pi.currentlyDoing="Refreshing " + pl.name;
                //find out in key if playlist can be refetched like that
                //test the source
                TutorialSource source=pl.source;
                //equate it witrh a sponsor
                Sponsor sp=sponsors.getByChannelKey(source.key);
                if (sp.expandablePlaylists==null){
                    //let's be badass
                    publishProgress(pi);
                    //first we delete all videos
                    TutorialPlaylist.clearVideos(pl.key);
                    //the we reload from source
                    YoutubePlaylist pl2 = ModelConverter.loadPlaylist(getActivity(), pl.key, getActivity().getString(R.string.youtubeapikey));
                    //next we cache it
                    //prettify it before
                    pl2.setID(pl.key);
                    pl2.setTitle(pl.name);
                    pl2.setDescription(pl.description);
                    pl2.setPublishedAt(pl.publishedAt);
                    pl2.setHighThumb(new YoutubeThumbnail(pl.highThumbnail, YoutubeUtils.HIGH_WIDTH, YoutubeUtils.HIGH_HEIGHT));
                    pl2.setMediumThumb(new YoutubeThumbnail(pl.mediumThumbnail,YoutubeUtils.MEDIUM_WIDTH, YoutubeUtils.MEDIUM_HEIGHT));
                    pl2.setDefaultThumb(new YoutubeThumbnail(pl.defaultThumbnail,YoutubeUtils.DEFAULT_WIDTH, YoutubeUtils.DEFAULT_HEIGHT));
                    //go through name cleaning
                    if (sp.cleanVideos!=null){
                        ModelConverter.cleanVideos(pl2, sp.cleanVideos);
                    }
                    //store in cache
                    ModelConverter.cachePlaylist(pl2, pl.source.key);
                    pl.fetchedAt=new Date();
                    pl.save();
                    pi.playlistsProgress++;
                    pi.totalVideos=pi.totalVideos+pl2.getVideos().size();
                    publishProgress(pi);
                    Log.d("MA.CD", "Playlist fetchedAt : " + pl.fetchedAt);
                } else {
                    //for CLYW and similars,
                    //the whole channel must reload and expand
                    //so we MUST destroy all playlists available for this provider
                    //but only once since they will be rebuilt
                    //so we add this to a "doLater" queue
                    if (!sponsorsToRefresh.contains(sp)) {
                        sponsorsToRefresh.add(sp);
                    }
                }
            }
            //all simple playlists done, refetch full content providers
            for (Sponsor sp :
                    sponsorsToRefresh) {
                //We must delete the entire source in database
                TutorialSource.deleteSource(sp.channelKey);
                YoutubeChannel channel = ModelConverter.loadChannel(getActivity(), sp.channelKey, sp.expandablePlaylists, getActivity().getString(R.string.youtubeapikey));
                ModelConverter.prepareChannel(channel, getActivity());
                ModelConverter.cacheChannel(channel);
                //next we move on to the playlists and precache them too
                for (YoutubePlaylist clpl :
                        channel.getPlaylists()) {
                    YoutubePlaylist pl2 = ModelConverter.loadPlaylist(getActivity(), clpl.getID(), getActivity().getString(R.string.youtubeapikey));
                    //data from pl goes to pl2
                    clpl.copyTo(pl2);
                    //got it, now cache it
                    ModelConverter.cachePlaylist(pl2, channel.getID());
                    pi.playlistsProgress++;
                    pi.totalVideos = pi.totalVideos + pl2.getVideos().size();
                    publishProgress(pi);
                }
            }
            //now the db is initialized, we do the first pre-caches
            return null;
        }

        @Override
        protected void onProgressUpdate(ProgressInfo... pi) {
            if (mCallbacks != null) {
                mCallbacks.onProgressUpdate(pi[0]);
            }
        }

        @Override
        protected void onCancelled() {
            if (mCallbacks != null) {
                mCallbacks.onCancelled();
            }
        }

        @Override
        protected void onPostExecute(Void ignore) {
            if (mCallbacks != null) {
                mCallbacks.onPostExecute();
            }
        }

    }


}
