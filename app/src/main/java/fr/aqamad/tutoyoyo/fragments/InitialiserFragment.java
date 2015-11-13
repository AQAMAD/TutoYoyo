package fr.aqamad.tutoyoyo.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;

import java.util.ArrayList;

import fr.aqamad.commons.youtube.YoutubeChannel;
import fr.aqamad.commons.youtube.YoutubePlaylist;
import fr.aqamad.tutoyoyo.Application;
import fr.aqamad.tutoyoyo.R;
import fr.aqamad.tutoyoyo.model.ModelConverter;
import fr.aqamad.tutoyoyo.model.Sponsor;
import fr.aqamad.tutoyoyo.model.Sponsors;
import fr.aqamad.tutoyoyo.model.TutorialSource;


/**
 * Created by Gregoire on 20/10/2015.
 */
public class InitialiserFragment extends Fragment {

    /**
     * define the new task class from the newInstance
     * @return
     */

    protected TaskCallbacks mCallbacks;
    private InitialiserTask mTask;

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
        mTask=new InitialiserTask();
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
        public int providersProgress;
        public int providersMax;
        public int playlistsProgress;
        public int playlistsMax;
        public String currentlyDoing;
        public int totalVideos;
    }




    public class InitialiserTask extends AsyncTask<Void, ProgressInfo, Void> {

        public InitialiserTask() {
        }



        @Override
        protected void onPreExecute() {
            if (mCallbacks != null) {
                mCallbacks.onPreExecute();
            }
        }


        private int countBools(boolean... vars) {
            int count = 0;
            for (boolean var : vars) {
                count += (var ? 1 : 0);
            }
            return count;
        }


        /**
         * Note that we do NOT call the callback object's methods
         * directly from the background thread, as this could result
         * in a race condition.
         */
        @Override
        protected Void doInBackground(Void... params) {
            TutorialSource.initializeDB(getActivity());
            //depends on preferences
            SharedPreferences appPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            //drive all this via the sponsors collection
            Sponsors sponsors = Application.getSponsors().getCopy();
            //remove local sponsor
            sponsors.remove(Sponsors.R_ID.my);
            //iterate and get numbers of sponsors
            ArrayList<Sponsor> enabledSponsors=new ArrayList<>();
            for (Sponsor sp :
                    sponsors.getOrdered()) {
                if (appPreferences.getBoolean(sp.preferenceKey, true)){
                    enabledSponsors.add(sp);
                }
            }
            ProgressInfo pi=new ProgressInfo();
            pi.providersMax=enabledSponsors.size();
            pi.providersProgress=0;
            pi.totalVideos=0;

            //now the db is initialized, we do the first pre-caches
            //first up is yoyoblast, we load him
            for (Sponsor sp :
                    enabledSponsors) {
                //look up
                loadChannel(pi, sp.channelKey, sp.expandablePlaylists, sp.name);
            }

            return null;
        }

        private void loadChannel(ProgressInfo pi,String channelID,String[] expandPlaylists,String displayName) {
            YoutubeChannel channel= ModelConverter.loadChannel(getActivity(), channelID, expandPlaylists, getActivity().getString(R.string.youtubeapikey));
            ModelConverter.prepareChannel(channel,getActivity());
            ModelConverter.cacheChannel(channel);
            pi.playlistsMax=channel.getPlaylists().size();
            pi.providersProgress++;
            pi.playlistsProgress=0;
            pi.currentlyDoing=getActivity().getString(R.string.loading) + " " + displayName;
            publishProgress(pi);
            //next we move on to the playlists and precache them too
            for (YoutubePlaylist pl :
                    channel.getPlaylists()) {
                YoutubePlaylist pl2 = ModelConverter.loadPlaylist(getActivity(), pl.getID(), getActivity().getString(R.string.youtubeapikey));
                //data from pl goes to pl2
                pl.copyTo(pl2);
                //got it, now cache it
                ModelConverter.cachePlaylist(pl2, channel.getID());
                pi.playlistsProgress++;
                pi.totalVideos=pi.totalVideos+pl2.getVideos().size();
                publishProgress(pi);
            }
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
