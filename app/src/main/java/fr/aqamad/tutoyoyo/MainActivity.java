package fr.aqamad.tutoyoyo;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.aqamad.tutoyoyo.fragments.HomeFragment;
import fr.aqamad.tutoyoyo.fragments.InitialiserFragment;
import fr.aqamad.tutoyoyo.fragments.PlaylistFragment;
import fr.aqamad.tutoyoyo.fragments.SearchFragment;
import fr.aqamad.tutoyoyo.fragments.SettingsFragment;
import fr.aqamad.tutoyoyo.fragments.SourceFragment;
import fr.aqamad.tutoyoyo.fragments.UpdaterFragment;
import fr.aqamad.tutoyoyo.model.Sponsor;
import fr.aqamad.tutoyoyo.model.Sponsors;
import fr.aqamad.tutoyoyo.model.TutorialPlaylist;
import fr.aqamad.tutoyoyo.model.TutorialSource;
import fr.aqamad.tutoyoyo.model.TutorialVideo;
import fr.aqamad.tutoyoyo.utils.Debug;
import fr.aqamad.tutoyoyo.utils.IntentHelper;
import fr.aqamad.tutoyoyo.utils.PicassoHelper;
import fr.aqamad.tutoyoyo.utils.ScreenSize;
import fr.aqamad.tutoyoyo.utils.UI;
import fr.aqamad.youtube.YoutubePlaylist;
import fr.aqamad.youtube.YoutubeUtils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        ,InitialiserFragment.TaskCallbacks
        ,UpdaterFragment.TaskCallbacks
        ,SourceFragment.OnPlaylistSelectedListener
        ,HomeFragment.SponsorsCallbacks

{

    private static final String TAG_INIT_TASK_FRAGMENT = "initializer_task_fragment";
    private static final String TAG_UPDT_TASK_FRAGMENT = "initializer_task_fragment";

    private Fragment mTaskFragment;

    private Menu menu;

    private NavigationView navigationView;

    private SourceFragment currentFragment;

    private Map<Integer,SourceFragment> fragments=new HashMap<>();

    Sponsors sponsors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //check the model has been created and perform initialisation otherwise
        super.onCreate(savedInstanceState);


        //default values for preferences
        PreferenceManager.setDefaultValues(this, R.xml.fragment_settings, false);
        //init fragments
        sponsors=new Sponsors(this);
        fragments=sponsors.getFragments();

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        checkDatabase();

        if (savedInstanceState != null) {
            //Restore the fragment's instance
            Log.d("MA", "Mainactivity onCreate bundle");
            String state=savedInstanceState.getString("CurrentFragment");
            if (state!=null){
                Log.d("MA", "Mainactivity onCreate bundle, state=" + state);
            }
        } else {
            //create and prepare different fragments
            Log.d("MA", "Mainactivity onCreate nobundle");
        }

        //based on preferences
        enableProviderMenus();

        //handle search requests
        handleIntent(getIntent());

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        // Get the intent, verify the action and get the query
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    YoyoTutsSuggestionProvider.AUTHORITY, YoyoTutsSuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);
            Log.d("MA.HI","Query for results : " + query);
            doTutorialSearch(query);
        }
    }

    private void doTutorialSearch(String query) {
        //do sql to provide results
        SearchFragment sf=SearchFragment.newInstance(query);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, sf);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();


    }




    private void enableProviderMenus() {
        //activate or deactivate items based on sharedpreferences
        SharedPreferences appPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        for (Integer key :
                sponsors.keySet()) {
            Sponsor sponsor=sponsors.get(key);
            boolean isEnabled = appPreferences.getBoolean(sponsor.preferenceKey, true);
            navigationView.getMenu().findItem(sponsor.navigationId).setVisible(isEnabled);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("MA", "onSaveInstanceState");
        if (currentFragment!=null){
            outState.putString("CurrentFragment", currentFragment.getChannelId());
        }
        Log.d("MA", "Mainactivity onSaveInstanceState");
    }

    private void checkDatabase() {
        //at the moment, delete everything before setting up a new DB
        //test for model creation
        List<TutorialSource> sources =TutorialSource.getAll();
        //get preference value for refreshing
        SharedPreferences appPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String refreshPeriod = appPreferences.getString(getString(R.string.lst_pref_refresh_playlist), "7");
        Log.d("MA.CD","RefreshPeriod : " + refreshPeriod);
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
        Log.d("MA.CD","RefreshPlaylists fetched before : " + refreshDate);
        Log.d("MA.CD", "RefreshPlaylists fetched before : " + refreshDate.getTime());
        //and get refresh dates for the playlists
        List<TutorialPlaylist> lists =TutorialPlaylist.getOlderThan(refreshDate);
        if (sources.size()<=1){
            if (!isOnline()){
                Snackbar.make(navigationView, "Network connectivity is required to build cache. Try restarting app when online.", Snackbar.LENGTH_LONG).show();
            } else {
                //start initialiser fragment
                FragmentManager fm = getFragmentManager();
                mTaskFragment = fm.findFragmentByTag(TAG_INIT_TASK_FRAGMENT);
                // If the Fragment is non-null, then it is currently being
                // retained across a configuration change.
                if (mTaskFragment == null) {
                    mTaskFragment = new InitialiserFragment();
                    fm.beginTransaction().add(mTaskFragment, TAG_INIT_TASK_FRAGMENT).commit();
                }
            }
        }else if(lists.size()>0){
            Log.d("MA.CD","Playlists deemed needing refresh : " + lists.size());
            if (!isOnline()){
                Snackbar.make(navigationView, "Network connectivity is required to build cache. Try restarting app when online.", Snackbar.LENGTH_LONG).show();
            } else {
                //start initialiser fragment
                FragmentManager fm = getFragmentManager();
                mTaskFragment = (UpdaterFragment) fm.findFragmentByTag(TAG_UPDT_TASK_FRAGMENT);
                // If the Fragment is non-null, then it is currently being
                // retained across a configuration change.
                if (mTaskFragment == null) {
                    mTaskFragment = new UpdaterFragment();
                    fm.beginTransaction().add(mTaskFragment, TAG_UPDT_TASK_FRAGMENT).commit();
                }
            }
        }else {
            //finally, initialize what's happening
            initScreen();
        }
    }

    private void initScreen() {
        //hide progressbar
        hideProgress();
        //and welcome Text
        TextView wt= (TextView) findViewById(R.id.welcomeText);
        wt.setVisibility(View.GONE);
        //add stats information to welcome text
        wt= (TextView) findViewById(R.id.textWelcomeTitle);
        TextView nbt= (TextView) findViewById(R.id.textNbTuts);
        int nbVids = TutorialVideo.countAll();
        //get random tut of the day
        TutorialVideo randomTut=TutorialVideo.getRandom();
        //we got the random tut, load it's thumbnail into main header for instance...
        ImageView imgHeader= (ImageView) findViewById(R.id.imgHomeHeader);
        //create text
//            wt.setText(String.format(getString(R.string.randomTutAndStats), randomTut.name, nbVids));
        wt.setText(String.format(getString(R.string.randomTut), randomTut.name));
        nbt.setText(String.format(getString(R.string.tutStats), nbVids));
        //determine size based on screen
        ScreenSize size=new ScreenSize(this);
        int tWidth=0;
        int tHeight=0;
        if (size.getHeight()<size.getWidth()){
            //landscape, image should be no more than 1/2 screen height
            tHeight=size.getHeight()/2;
        }else{
            //portrait
            tWidth=size.getWidth()/2;
        }
        //use our picassohelper to load the thumbnail
        PicassoHelper.loadWeborDrawable(this, randomTut.highThumbnail)
                .placeholder(R.drawable.waiting)
                .resize(tWidth, tHeight)
                .transform(PicassoHelper.getRoundedCornersTranform(Color.WHITE))
                .into(imgHeader)
        ;
        //set image click listener
        imgHeader.setTag(randomTut.key);
        imgHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //we got the view, let's just toast something here
                String vidID= (String) v.getTag();
                YoutubeUtils.PlayYoutubeVideo(vidID, MainActivity.this);
            }
        });
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //we handled the drawer, now let's sync it
            //sync menus state
            enableProviderMenus();
            //sync fragments
            FragmentManager fragmentManager = getFragmentManager();
            int backCount = fragmentManager.getBackStackEntryCount();
            if (backCount>1){
                fragmentManager.popBackStackImmediate();
                //need to reset the nav indicator
                resetNavIndicator();
//                //hide the search interface
//                MenuItem searchItem=menu.findItem(R.id.action_search);
//                searchItem.setVisible(false);
            }else if (backCount>0){
                //fragmentManager.popBackStack();
                fragmentManager.popBackStackImmediate();
                //need to reset the nav indicator
                resetNavIndicator();
//                //show search interface
//                MenuItem searchItem=menu.findItem(R.id.action_search);
//                searchItem.setVisible(true);
            }else{
                super.onBackPressed();
            }
        }
        Log.d("MA", "Mainactivity onBackPressed");
    }

    private void resetNavIndicator() {
        //get menus from navigation view
        Log.d("MA.RNI", "ResetNavigationIndicator called");
        boolean found=false;
        for (Map.Entry<Integer, SourceFragment> e : fragments.entrySet()) {
            Integer key = e.getKey();
            SourceFragment frg = e.getValue();
            if (frg.isVisible()){
                //get the key
                navigationView.getMenu().findItem(key).setChecked(true);
                found=true;
            }
        }
        if (!found){
            for (Integer key :
                    fragments.keySet()) {
                navigationView.getMenu().findItem(key).setChecked(false);
            }
            //restore search interface
            MenuItem searchItem=menu.findItem(R.id.action_search);
            searchItem.setVisible(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu=menu;
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSubmitButtonEnabled(true);
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        //can we cancel the searchview ?
        MenuItem searchItem=menu.findItem(R.id.action_search);
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                Log.d("MA.S", "onShowSearch");
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                Log.d("MA.S", "onCancelSearch");
                onBackPressed();
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Log.d("MA", "Mainactivity onOptionItemsSelected");

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame, new SettingsFragment());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            return true;
        }else if (id==R.id.action_about){
            showAboutBox();
            return true;
        }else if (id==R.id.action_credits){
            showCreditsBox();
            return true;
        }else if (id==R.id.action_help){
            showHelpBox();
            return true;
        }else if (id==R.id.action_suggest){
            IntentHelper.sendMail(this,
                    Uri.fromParts("mailto",
                    getString(R.string.mailto_address), null),
                    getString(R.string.app_name),
                    getString(R.string.mailto_message));
            return true;
        }else if (id==R.id.action_report){
            reportBug();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void reportBug() {
        Debug.sendBugReport(this);
    }

    private void showCreditsBox() {
        UI.alertDialog(this, R.string.dialog_title_credits, R.string.dialog_msg_credits);
    }

    private void showHelpBox() {
        UI.alertCustomDialog(this, R.string.dialog_help_title, R.layout.dialog_help, R.id.dialogButtonOK);
    }


    private void showAboutBox() {
        UI.alertDialog(this, R.string.dialog_title_about, R.string.dialog_msg_about);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Log.d("MA", "Mainactivity onNavigationItemSelected start");

        int id = item.getItemId();

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //Snackbar.make(navigationView, item.getTitle() + " pressed", Snackbar.LENGTH_LONG).show();
        item.setChecked(true);

        handleNavigation(id);

        if (id == R.id.nav_share) {
            //handle other actions
            //get the "social playlist" and share it
            TutorialPlaylist pl=TutorialPlaylist.getByKey(getString(R.string.localSocialKey));
            if (pl.videos().size()==0){
                Snackbar.make(navigationView, "First add something to your social playlist !!", Snackbar.LENGTH_LONG).show();
            }else{
                //build array
                List<String> videoUrls = pl.getVideoUrls();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, TextUtils.join("\n", videoUrls));
                intent.setType("text/plain");
                startActivity(intent);
            }
        } else if (id == R.id.nav_send) {
            //get the "social playlist" and share it
            TutorialPlaylist pl=TutorialPlaylist.getByKey(getString(R.string.localSocialKey));
            if (pl.videos().size()==0){
                Snackbar.make(navigationView, "First add something to your social playlist !!", Snackbar.LENGTH_LONG).show();
            }else {
                //build array
                List<String> videoUrls = pl.getVideoUrls();
                //like sending through mail for instance
                IntentHelper.sendMail(this,
                        Uri.fromParts("mailto",
                        getString(R.string.mailto_examplefriend), null),
                        getString(R.string.mailto_letmeshare) + " " + getString(R.string.app_name),
                        getString(R.string.mailto_letmesharesubject) + " " +
                        getString(R.string.app_name) +
                        "\n\n" +
                        TextUtils.join("\n", videoUrls));
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        Log.d("MA", "Mainactivity onNavigationItemSelected end");

        return true;
    }

    private void handleNavigation(int id) {
        SourceFragment newFragment=null;

        newFragment=fragments.get(id);

        if (newFragment!=null){
            //hide the search menu
//            MenuItem searchItem=menu.findItem(R.id.action_search);
//            searchItem.setVisible(false);
            //one of the channel fragments
            currentFragment=newFragment;
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame, currentFragment);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }


    public void showCredits(View view) {
        showCreditsBox();
    }
    public void showHelp(View view) {
        showCreditsBox();
    }

    public void showProgress(){
        View pi=findViewById(R.id.progressIndicator);
        pi.setVisibility(View.VISIBLE);
    }

    public void hideProgress(){
        View pi=findViewById(R.id.progressIndicator);
        pi.setVisibility(View.GONE);
    }


    /**
     * Implement the initialiser task callbacks
     */

    @Override
    public void onPreExecute() {
        //Toast.makeText(this,"preExecute",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProgressUpdate(UpdaterFragment.ProgressInfo info) {
        //find views and set items
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

    @Override
    public void onProgressUpdate(InitialiserFragment.ProgressInfo progressInfo) {
        //find views and set items
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

    @Override
    public void onCancelled() {
        Toast.makeText(this,"onCancelled",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPostExecute() {
        initScreen();
    }

    @Override
    public void OnPlaylistSelected(YoutubePlaylist mPlaylist, String mChannelID, int mBgColor, int mFgColor) {
        Log.d("SFFV", "In MainActivity OnPlaylistSelected called for ID : " + mPlaylist.getID());
        //switch to fragment based UI
        Fragment newFragment = PlaylistFragment.newInstance(mPlaylist, mChannelID, mBgColor, mFgColor);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.frame, newFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onSponsorSelected(int navigationId) {
        handleNavigation(navigationId);
    }
}
