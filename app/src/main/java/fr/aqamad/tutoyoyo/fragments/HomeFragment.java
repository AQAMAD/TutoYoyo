package fr.aqamad.tutoyoyo.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apmem.tools.layouts.FlowLayout;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import fr.aqamad.tutoyoyo.R;
import fr.aqamad.tutoyoyo.model.Sponsor;
import fr.aqamad.tutoyoyo.model.Sponsors;
import fr.aqamad.tutoyoyo.model.TutorialPlaylist;
import fr.aqamad.tutoyoyo.model.TutorialSource;
import fr.aqamad.tutoyoyo.model.TutorialVideo;
import fr.aqamad.tutoyoyo.utils.Debug;
import fr.aqamad.tutoyoyo.utils.NetworkState;
import fr.aqamad.tutoyoyo.views.HomeVideoView;
import fr.aqamad.youtube.YoutubeVideo;

/**
 * Created by Gregoire on 19/10/2015.
 */
public class HomeFragment extends Fragment {

    public static final String FRAGMENT_KEY="fr.aqmad.tutoyoyo.homefragment";

    protected SponsorsCallbacks mCallbacks;

    private View rootView;

    public HomeFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        Log.d("HF.oCV", "HomeFragment onCreateView");
        //add sponsors icons and shortcuts
        createSponsorsView(rootView);
        //keep rootView for future lookups
        this.rootView=rootView;
        //launch database check if not already doing that
        boolean isWorking=checkDatabase();
        //once check is made, we can either display the progress indicators or the home screen
        if (isWorking){
            displayProgress();
        }else{
            displayHome();
        }
        return rootView;
    }

    private void createSponsorsView(View rootView) {
        //let's add some images to the flowLayout
        FlowLayout flSponsors = (FlowLayout) rootView.findViewById(R.id.flSponsors);
        Sponsors sps = new Sponsors(getActivity());
        //exclude the local sponsors from this list
        sps.remove(Sponsors.R_ID.my.getKey());
//        sps.remove(Sponsors.R_ID.frn.getKey());
        //okay, we got everything we need
        ImageView v;
        SharedPreferences appPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        for (final Sponsor sp :
                sps.getOrdered()) {
            //check if provider enabled in settings
            boolean isEnabled = appPreferences.getBoolean(sp.preferenceKey, true);
            if (isEnabled){
                //now we create a new Image view for each of the sponsors
                FlowLayout.LayoutParams lp = new FlowLayout.LayoutParams(
                        FlowLayout.LayoutParams.WRAP_CONTENT,
                        FlowLayout.LayoutParams.WRAP_CONTENT);
                lp.setGravity(Gravity.LEFT);
                v = new ImageView(getActivity());
                v.setLayoutParams(lp);
                v.setImageResource(sp.logoResId);
                //now set up the onclick
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCallbacks != null) {
                            mCallbacks.onSponsorSelected(sp.navigationId);
                        }
                    }
                });
                flSponsors.addView(v);
            }
        }
    }

    /**
     * Hold a reference to the parent Activity so we can report the
     * task's current progress and results. The Android framework
     * will pass us a reference to the newly created Activity after
     * each configuration change.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (SponsorsCallbacks) activity;
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

    public void displayProgressInfo(InitialiserFragment.ProgressInfo progressInfo) {
        displayProgress();
        ProgressBar pbP = (ProgressBar) findViewById(R.id.progressBarPro);
        pbP.setVisibility(View.VISIBLE);
        pbP.setMax(progressInfo.providersMax);
        pbP.setProgress(progressInfo.providersProgress);
        ProgressBar pbL = (ProgressBar) findViewById(R.id.progressBarPlay);
        pbL.setMax(progressInfo.playlistsMax);
        pbL.setProgress(progressInfo.playlistsProgress);
        TextView pT = (TextView) findViewById(R.id.progressText);
        pT.setText(progressInfo.currentlyDoing);
        TextView pT2 = (TextView) findViewById(R.id.progressText2);
        pT2.setText(progressInfo.playlistsProgress + "/" + progressInfo.playlistsMax + " (" + progressInfo.totalVideos + " vids)");
    }

    public void displayProgressInfo(UpdaterFragment.ProgressInfo info) {
        displayProgress();
        ProgressBar pbP = (ProgressBar) findViewById(R.id.progressBarPro);
        pbP.setVisibility(View.GONE);
        ProgressBar pbL = (ProgressBar) findViewById(R.id.progressBarPlay);
        pbL.setMax(info.playlistsMax);
        pbL.setProgress(info.playlistsProgress);
        TextView pT = (TextView) findViewById(R.id.progressText);
        pT.setText(info.currentlyDoing);
        TextView pT2 = (TextView) findViewById(R.id.progressText2);
        pT2.setText(info.playlistsProgress + "/" + info.playlistsMax + " (" + info.totalVideos + " vids)");
    }

    public interface SponsorsCallbacks {
        public void onSponsorSelected(int navigationId);
        public void onInitializeDB();
        public void onUpdateDB();
        public boolean isStillWorking();
    }

    /**
     * Utility method to findviews
     * @param ID
     * @return
     */
    private View findViewById(int ID){
        return rootView.findViewById(ID);
    }

    public void hideProgress(){
        View pi=findViewById(R.id.progressIndicator);
        pi.setVisibility(View.GONE);
        //and welcome Text
        TextView wt= (TextView) findViewById(R.id.welcomeText);
        wt.setVisibility(View.GONE);
    }

    private void displayProgress() {
        View pi=findViewById(R.id.progressIndicator);
        pi.setVisibility(View.VISIBLE);
        //furthermore, show all related ui elements
        //and welcome Text
        TextView wt= (TextView) findViewById(R.id.welcomeText);
        wt.setVisibility(View.VISIBLE);
    }



    private boolean checkDatabase() {
        //at the moment, delete everything before setting up a new DB
        Log.d("HF", "HomeFragment checkDatabase called");
        //first step, is a task going on in MainActivity
        if (mCallbacks != null) {
            Log.d("HF", "Activity available");
            boolean isOnGoing=mCallbacks.isStillWorking();
            Log.d("HF", "Activity isStillWorking=" + isOnGoing);
            if (isOnGoing){
                return true;
            }
        }
        //test for model creation
        List<TutorialSource> sources =TutorialSource.getAll();
        //get preference value for refreshing
        SharedPreferences appPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String refreshPeriod = appPreferences.getString(getString(R.string.lst_pref_refresh_playlist), "7");
        //Log.d("MA.CD","RefreshPeriod : " + refreshPeriod);
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
        //for testing purpose only, position constant in Debug class
        c.add(Calendar.DATE, Debug.debugRefresh);
        refreshDate = c.getTime();
//        Log.d("MA.CD","RefreshPlaylists fetched before : " + refreshDate);
//        Log.d("MA.CD", "RefreshPlaylists fetched before : " + refreshDate.getTime());
        //and get refresh dates for the playlists
        List<TutorialPlaylist> lists =TutorialPlaylist.getOlderThan(refreshDate);
        if (sources.size()<=1){
            if (!NetworkState.isOnline(getActivity())){
                Snackbar.make(rootView, "Network connectivity is required to build cache. Try restarting app when online.", Snackbar.LENGTH_LONG).show();
            } else {
                if (mCallbacks != null) {
                    Log.d("HF", "HomeFragment initialiseDB called");
                    mCallbacks.onInitializeDB();
                }
            }
            return true;
        }else if(lists.size()>0){
            Log.d("HF","Playlists deemed needing refresh : " + lists.size());
            if (!NetworkState.isOnline(getActivity())){
                Snackbar.make(rootView, "Network connectivity is required to build cache. Try restarting app when online.", Snackbar.LENGTH_LONG).show();
            } else {
                if (mCallbacks != null) {
                    Log.d("HF", "HomeFragment updateDB called");
                    mCallbacks.onUpdateDB();
                }
            }
            return true;
        }else {
            //finally, initialize what's happening
            return false;
        }
    }


    public void displayHome() {
        //hide progressinterface
        hideProgress();
        //hideHader
        hideHeader();
        //add stats information to welcome text
        displayTotalVids();
        //get random tut of the day
        displayRandomTut();
        //add next unwatched in "toview" list
        displayNextUnviewed();
        //add random favorite
        displayRandomFavorite();
    }

    private void displayRandomFavorite() {
        FlowLayout fl= (FlowLayout) findViewById(R.id.flRandomView);
        fl.setVisibility(View.VISIBLE);
        TextView tv=new TextView(getActivity());
        tv.setTextColor(getResources().getColor(android.R.color.white));
        tv.setText("Random tutorial in Favorites :");
        tv.setLayoutParams(new FlowLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv.setPadding(10, 0, 0, 0);
        fl.addView(tv);
        //get next
        //then wrap it in a view
        TutorialVideo nextTut=TutorialVideo.getRandomInChannel(getString(R.string.localFavoritesKey));
        if (nextTut==null){
            tv=new TextView(getActivity());
            tv.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            tv.setText("Add videos to 'Favorites' and they will randomly appear here.");
            tv.setLayoutParams(new FlowLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tv.setPadding(10,0,0,0);
            fl.addView(tv);
        } else {
            View convertView=new HomeVideoView(getActivity(), HomeVideoView.BoxType.FAVORITE);
            fl.addView(convertView);
            //position colors
            ((HomeVideoView) convertView).setForeGroundColor(android.R.color.white);
            //bind item
            YoutubeVideo vid=YoutubeVideo.fromModel(nextTut);
            ((HomeVideoView) convertView).bind(vid);
        }
    }

    private void hideHeader() {
        //text disappears
        TextView wt= (TextView) findViewById(R.id.textWelcomeTitle);
        wt.setVisibility(View.GONE);
        //header disappears
        ImageView imgHeader= (ImageView) findViewById(R.id.imgHomeHeader);
        imgHeader.setVisibility(View.GONE);
    }

    private void displayNextUnviewed() {
        FlowLayout fl= (FlowLayout) findViewById(R.id.flRandomView);
        fl.setVisibility(View.VISIBLE);
        TextView tv=new TextView(getActivity());
        tv.setTextColor(getResources().getColor(android.R.color.white));
        tv.setText("Next tutorial in Watch Later :");
        tv.setLayoutParams(new FlowLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv.setPadding(10, 0, 0, 0);
        fl.addView(tv);
        //get next
        //then wrap it in a view
        TutorialVideo nextTut=TutorialVideo.getNextUnviewedInChannel(getString(R.string.localLaterKey));
        if (nextTut==null){
            tv=new TextView(getActivity());
            tv.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            tv.setText("Add videos to 'Watch Later' and they will appear here in the order you selected them (if you haven't seen them already).");
            tv.setLayoutParams(new FlowLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tv.setPadding(10,0,0,0);
            fl.addView(tv);
        } else {
            View convertView=new HomeVideoView(getActivity(), HomeVideoView.BoxType.WATCH);
            fl.addView(convertView);
            //position colors
            ((HomeVideoView) convertView).setForeGroundColor(android.R.color.white);
            //bind item
            YoutubeVideo vid=YoutubeVideo.fromModel(nextTut);
            ((HomeVideoView) convertView).bind(vid);
        }
    }

    private void displayRandomTut() {
        //get the flowlayout to insert the view
        FlowLayout fl= (FlowLayout) findViewById(R.id.flRandomView);
        fl.setVisibility(View.VISIBLE);
        //fill it with first the Random Tut text View
        TextView tv=new TextView(getActivity());
        tv.setTextColor(getResources().getColor(android.R.color.white));
        tv.setText(R.string.randomTut);
        tv.setLayoutParams(new FlowLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv.setPadding(10,0,0,0);
        fl.addView(tv);
        //initialize our random interface with the given tut
        View convertView=new HomeVideoView(getActivity(), HomeVideoView.BoxType.RANDOM);
        fl.addView(convertView);
        //position colors
        ((HomeVideoView) convertView).setForeGroundColor(android.R.color.white);
        //bind item
        TutorialVideo randomTut=TutorialVideo.getRandom();
        YoutubeVideo vid=YoutubeVideo.fromModel(randomTut);
        ((HomeVideoView) convertView).bind(vid);
    }


    private void displayTotalVids() {
        TextView nbt= (TextView) findViewById(R.id.textNbTuts);
        int nbVids = TutorialVideo.countAll();
        nbt.setVisibility(View.VISIBLE);
        nbt.setText(String.format(getString(R.string.tutStats), nbVids));
    }

}
