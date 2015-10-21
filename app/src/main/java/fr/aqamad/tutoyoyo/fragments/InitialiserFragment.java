package fr.aqamad.tutoyoyo.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import fr.aqamad.tutoyoyo.R;
import fr.aqamad.tutoyoyo.model.ModelConverter;
import fr.aqamad.tutoyoyo.model.TutorialSource;
import fr.aqamad.youtube.YoutubeChannel;
import fr.aqamad.youtube.YoutubePlaylist;


/**
 * Created by Gregoire on 20/10/2015.
 */
public class InitialiserFragment extends Fragment {

    /**
     * define the new task class from the newInstance
     * @return
     */

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

    public class ProgressInfo{
        public int providersProgress;
        public int providersMax;
        public int playlistsProgress;
        public int playlistsMax;
        public String currentlyDoing;
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

        /**
         * Note that we do NOT call the callback object's methods
         * directly from the background thread, as this could result
         * in a race condition.
         */
        @Override
        protected Void doInBackground(Void... params) {
            TutorialSource.initializeDB(getActivity());
            ProgressInfo pi=new ProgressInfo();
            pi.providersMax=5;
            //now the db is initialized, we do the first pre-caches
            //first up is yoyoblast, we load him
            YoutubeChannel yyb=ModelConverter.loadChannel(getActivity(), getActivity().getString(R.string.YOYOBLAST_CHANNEL), null, getActivity().getString(R.string.youtubeapikey));
            ModelConverter.prepareChannel(yyb,getActivity());
            ModelConverter.cacheChannel(yyb);
            pi.playlistsMax=yyb.getPlaylists().size();
            pi.providersProgress=1;
            pi.playlistsProgress=0;
            pi.currentlyDoing="Loading Yoyoblast";
            publishProgress(pi);
            //next we move on to the playlists and precache them too
            for (YoutubePlaylist pl :
                    yyb.getPlaylists()) {
                YoutubePlaylist pl2 = ModelConverter.loadPlaylist(getActivity(), pl.getID(), getActivity().getString(R.string.youtubeapikey));
                //data from pl goes to pl2
                pl.copyTo(pl2);
                //got it, now cache it
                ModelConverter.cachePlaylist(pl2, yyb.getID());
                pi.playlistsProgress++;
                publishProgress(pi);
            }
            YoutubeChannel yye=ModelConverter.loadChannel(getActivity(), getActivity().getString(R.string.YOYOEXPERT_CHANNEL), null, getActivity().getString(R.string.youtubeapikey));
            ModelConverter.prepareChannel(yye, getActivity());
            ModelConverter.cacheChannel(yye);
            pi.playlistsMax=yye.getPlaylists().size();
            pi.providersProgress=2;
            pi.playlistsProgress=0;
            pi.currentlyDoing="Loading Yoyoexpert";
            publishProgress(pi);
            //next we move on to the playlists and precache them too
            for (YoutubePlaylist pl :
                    yye.getPlaylists()) {
                YoutubePlaylist pl2 = ModelConverter.loadPlaylist(getActivity(), pl.getID(), getActivity().getString(R.string.youtubeapikey));
                //data from pl goes to pl2
                Log.d("IF.RT", "YYE playlist is " + pl.getTitle());
                Log.d("IF.RT", "YYE loaded playlist is " + pl2.getTitle());
                pl.copyTo(pl2);
                //got it, now cache it
                ModelConverter.cachePlaylist(pl2, yye.getID());
                pi.playlistsProgress++;
                publishProgress(pi);
            }
            YoutubeChannel clyw=ModelConverter.loadChannel(getActivity(), getActivity().getString(R.string.CLYW_CHANNEL), ModelConverter.CABIN_TUTORIALS_PLAYLIST, getActivity().getString(R.string.youtubeapikey));
            ModelConverter.prepareChannel(clyw, getActivity());
            ModelConverter.cacheChannel(clyw);
            pi.playlistsMax=clyw.getPlaylists().size();
            pi.providersProgress=3;
            pi.playlistsProgress=0;
            pi.currentlyDoing="Loading CaribouLodge";
            publishProgress(pi);
            //next we move on to the playlists and precache them too
            for (YoutubePlaylist pl :
                    clyw.getPlaylists()) {
                YoutubePlaylist pl2 = ModelConverter.loadPlaylist(getActivity(), pl.getID(), getActivity().getString(R.string.youtubeapikey));
                //data from pl goes to pl2
                pl.copyTo(pl2);
                //got it, now cache it
                ModelConverter.cachePlaylist(pl2, clyw.getID());
                pi.playlistsProgress++;
                publishProgress(pi);
            }

            YoutubeChannel bhop=ModelConverter.loadChannel(getActivity(), getActivity().getString(R.string.BLACKHOP_CHANNEL), null, getActivity().getString(R.string.youtubeapikey));
            ModelConverter.prepareChannel(bhop, getActivity());
            ModelConverter.cacheChannel(bhop);
            pi.playlistsMax=bhop.getPlaylists().size();
            pi.providersProgress=3;
            pi.playlistsProgress=0;
            pi.currentlyDoing="Loading Blackhop";
            publishProgress(pi);
            //next we move on to the playlists and precache them too
            for (YoutubePlaylist pl :
                    bhop.getPlaylists()) {
                YoutubePlaylist pl2 = ModelConverter.loadPlaylist(getActivity(), pl.getID(), getActivity().getString(R.string.youtubeapikey));
                //data from pl goes to pl2
                pl.copyTo(pl2);
                //got it, now cache it
                ModelConverter.cachePlaylist(pl2, bhop.getID());
                pi.playlistsProgress++;
                publishProgress(pi);
            }

            YoutubeChannel yyt=ModelConverter.loadChannel(getActivity(), getActivity().getString(R.string.BLACKHOP_CHANNEL), null, getActivity().getString(R.string.youtubeapikey));
            ModelConverter.prepareChannel(yyt, getActivity());
            ModelConverter.cacheChannel(yyt);
            pi.playlistsMax=yyt.getPlaylists().size();
            pi.providersProgress=3;
            pi.playlistsProgress=0;
            pi.currentlyDoing="Loading MrYoyoThrower";
            publishProgress(pi);
            //next we move on to the playlists and precache them too
            for (YoutubePlaylist pl :
                    yyt.getPlaylists()) {
                YoutubePlaylist pl2 = ModelConverter.loadPlaylist(getActivity(), pl.getID(), getActivity().getString(R.string.youtubeapikey));
                //data from pl goes to pl2
                pl.copyTo(pl2);
                //got it, now cache it
                ModelConverter.cachePlaylist(pl2, yyt.getID());
                pi.playlistsProgress++;
                publishProgress(pi);
            }
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
