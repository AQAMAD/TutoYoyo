package fr.aqamad.tutoyoyo;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.SearchView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fr.aqamad.commons.youtube.YoutubePlaylist;
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
import fr.aqamad.tutoyoyo.utils.Debug;
import fr.aqamad.tutoyoyo.utils.IntentHelper;
import fr.aqamad.tutoyoyo.utils.UI;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        ,InitialiserFragment.TaskCallbacks
        ,UpdaterFragment.TaskCallbacks
        ,SourceFragment.OnPlaylistSelectedListener
        ,HomeFragment.SponsorsCallbacks

{

    public static final String MAINT_ACT_TAG = "MainAct";
    private static final String TAG_INIT_TASK_FRAGMENT = "fr.aqamad.tutoyoyo.initializer_task_fragment";
    private static final String TAG_UPDT_TASK_FRAGMENT = "fr.aqamad.tutoyoyo.updater_task_fragment";
    Sponsors sponsors;
    private Fragment mTaskFragment;
    private Menu menu;
    private NavigationView navigationView;
    private SourceFragment currentFragment;
    private HomeFragment homeFragment;
    private SettingsFragment settingsFragment;
    private SearchFragment searchFragment;
    private Map<Integer,SourceFragment> fragments=new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //gonna need this
        FragmentManager fragmentManager = getFragmentManager();
        //check the model has been created and perform initialisation otherwise
        super.onCreate(savedInstanceState);
        //default values for preferences
        PreferenceManager.setDefaultValues(this, R.xml.fragment_settings, false);
        //init fragments
        setContentView(R.layout.activity_main);
        //sponsorfragments
        sponsors = Application.getSponsors();
        //sponsors=new Sponsors(this);
        fragments=sponsors.getFragments();
        //homeFragment is directly added to the ui
        //was it kept in the manager ?
        homeFragment= (HomeFragment) fragmentManager.findFragmentByTag(HomeFragment.FRAGMENT_KEY);
        if (homeFragment==null){
            homeFragment=new HomeFragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.frame, homeFragment, HomeFragment.FRAGMENT_KEY);
            fragmentTransaction.commit();
        }
        //restore task fragments
        Fragment tTaskFragment = fragmentManager.findFragmentByTag(TAG_INIT_TASK_FRAGMENT);
        if (tTaskFragment!=null){
            mTaskFragment=tTaskFragment;
        }else{
            tTaskFragment = fragmentManager.findFragmentByTag(TAG_UPDT_TASK_FRAGMENT);
            if (tTaskFragment!=null){
                mTaskFragment=tTaskFragment;
            }
        }
        //act on the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //act on the drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        //drawer has been created
        //add dynamic entries
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //here
        final Menu menu = navigationView.getMenu();
        final String lang = Locale.getDefault().getLanguage();
        Log.d("MA.IMS", "Language id is : " + lang);
        for (Sponsor sp:
             sponsors.values()) {
            //one small refinement, check current language and display additional data
            MenuItem m;
            if (sp.language != null && sp.language.contains(lang)) {
                m = menu.add(Sponsors.R_ID.group.getKey(), sp.navigationId, sp.order, sp.name);
            } else if (sp.language != null && !sp.language.contains(lang)) {
                m = menu.add(Sponsors.R_ID.group.getKey(), sp.navigationId, sp.order, sp.name + " (" + sp.language + ")");
            } else {
                m = menu.add(Sponsors.R_ID.group.getKey(), sp.navigationId, sp.order, sp.name);
            }
            m.setIcon(sp.menuitemIconResId);
        }
        //position group options
        menu.setGroupCheckable(Sponsors.R_ID.group.getKey(),true,true);
        //refresh list
        if (savedInstanceState != null) {
            //Restore the fragment's instance
            Log.d(MAINT_ACT_TAG, "Mainactivity onCreate bundle");
            String state=savedInstanceState.getString("CurrentFragment");
            if (state!=null){
                Log.d(MAINT_ACT_TAG, "Mainactivity onCreate bundle, state=" + state);
            }
        } else {
            //create and prepare different fragments
            Log.d(MAINT_ACT_TAG, "Mainactivity onCreate nobundle");
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
            Log.d(MAINT_ACT_TAG + ".HI","Query for results : " + query);
            doTutorialSearch(query);
        }
    }

    private void doTutorialSearch(String query) {
        //do sql to provide results
        searchFragment=SearchFragment.newInstance(query);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, searchFragment, SearchFragment.FRAGMENT_KEY);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void enableProviderMenus() {
        //activate or deactivate items based on sharedpreferences
        Log.d(MAINT_ACT_TAG, "enableProviderMenus");
        SharedPreferences appPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        for (Integer key :
                sponsors.keySet()) {
            Sponsor sponsor=sponsors.get(key);
            boolean isEnabled = appPreferences.getBoolean(sponsor.preferenceKey, true);
            navigationView.getMenu().findItem(sponsor.navigationId).setVisible(isEnabled);
        }
        navigationView.invalidate();
        navigationView.requestLayout();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(MAINT_ACT_TAG, "onSaveInstanceState");
        if (currentFragment!=null){
            outState.putString("CurrentFragment", currentFragment.getChannelId());
        }
        Log.d(MAINT_ACT_TAG, "Mainactivity onSaveInstanceState");
    }

    private void startUpdaterFragment() {
        //start initialiser fragment
        Log.d(MAINT_ACT_TAG, "Mainactivity StartUpdaterFragment");
        FragmentManager fm = getFragmentManager();
        mTaskFragment = fm.findFragmentByTag(TAG_UPDT_TASK_FRAGMENT);
        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        if (mTaskFragment == null) {
            mTaskFragment = new UpdaterFragment();
            fm.beginTransaction().add(mTaskFragment, TAG_UPDT_TASK_FRAGMENT).commit();
        }
    }

    private void startInitializerFragment() {
        //start initialiser fragment
        Log.d(MAINT_ACT_TAG, "Mainactivity StartInitializerFragment");
        FragmentManager fm = getFragmentManager();
        mTaskFragment = fm.findFragmentByTag(TAG_INIT_TASK_FRAGMENT);
        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        if (mTaskFragment == null) {
            mTaskFragment = new InitialiserFragment();
            fm.beginTransaction().add(mTaskFragment, TAG_INIT_TASK_FRAGMENT).commit();
        }
    }

    @Override
    public void onBackPressed() {
        Log.d(MAINT_ACT_TAG, "Mainactivity onBackPressed");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            Log.d(MAINT_ACT_TAG, "onBackPressed closing drawer");
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //we handled the drawer, now let's sync it
            //sync menus state
            enableProviderMenus();
            Log.d(MAINT_ACT_TAG, "onBackPressed syncing menus");
            //sync fragments
            FragmentManager fragmentManager = getFragmentManager();
            int backCount = fragmentManager.getBackStackEntryCount();
            if (backCount>1){
                Log.d(MAINT_ACT_TAG, "onBackPressed many backstack entries (" + backCount + ")");
                fragmentManager.popBackStackImmediate();
                //need to reset the nav indicator
                resetNavIndicator();
            }else if (backCount>0){
                //fragmentManager.popBackStack();
                Log.d(MAINT_ACT_TAG, "onBackPressed only one backstack entry (" + backCount + ")");
                fragmentManager.popBackStackImmediate();
                //need to reset the nav indicator
                resetNavIndicator();
            }else{
                Log.d(MAINT_ACT_TAG, "onBackPressed no backstack entry (" + backCount + ")");
                //reset to home activity
                super.onBackPressed();
            }
        }
    }

    private void resetNavIndicator() {
        //get menus from navigation view
        Log.d(MAINT_ACT_TAG + ".RNI", "ResetNavigationIndicator called");
        boolean found=false;
        for (Map.Entry<Integer, SourceFragment> e : fragments.entrySet()) {
            Integer key = e.getKey();
            SourceFragment frg = e.getValue();
            if (frg.isVisible()){
                //get the key
                Log.d(MAINT_ACT_TAG + ".RNI", "VisibleFrag : " + frg.getChannelId() + " using key : " + key);
                navigationView.getMenu().findItem(key).setChecked(true); //works if not in a sbumenu
                found=true;
            }
        }
        if (!found){
            for (Integer key :
                    fragments.keySet()) {
                navigationView.getMenu().findItem(key).setChecked(false);
            }
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
                Log.d(MAINT_ACT_TAG + ".S", "onShowSearch");
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                Log.d(MAINT_ACT_TAG + ".S", "onCancelSearch");
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
        Log.d(MAINT_ACT_TAG, "Mainactivity onOptionItemsSelected");
        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }

    private void reportBug() {
        Debug.sendBugReport(this);
    }

    private void showCreditsBox() {
        //full text comes from sponsors
        String creditsConcat="";
        String from=getString(R.string.word_from);
        for (Sponsor sp :
                sponsors.getOrdered()) {
            if (sp.contactNames!=null){
                creditsConcat=creditsConcat + "\t-" + sp.contactNames + " " + from + " " + sp.name +"\n\n";
            }
        }
        String fullContactMessage=getString(R.string.dialog_msg_credits,creditsConcat);
        UI.alertDialog(this, R.string.dialog_title_credits, fullContactMessage);
    }

    private void showHelpBox() {
        UI.alertCustomDialog(this, R.string.dialog_help_title, R.layout.dialog_help, R.id.dialogButtonOK);
    }


    private void showAboutBox() {
        UI.alertDialog(this, R.string.dialog_title_about, R.string.dialog_msg_about);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Log.d(MAINT_ACT_TAG, "Mainactivity onNavigationItemSelected start");

        int id = item.getItemId();

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //Snackbar.make(navigationView, item.getTitle() + " pressed", Snackbar.LENGTH_LONG).show();
        if (handleNavigation(id)) {
            item.setChecked(true);
        }

        handleActions(id);

        drawerLayout.closeDrawer(GravityCompat.START);

        Log.d(MAINT_ACT_TAG, "Mainactivity onNavigationItemSelected end");

        return true;
    }

    private boolean handleActions(int id) {
        if (id == R.id.nav_share) {
            //handle other actions
            //get the "social playlist" and share it
            TutorialPlaylist pl = TutorialPlaylist.getByKey(getString(R.string.LOCAL_SOCIAL_PLAYLIST));
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
            return true;
        } else if (id == R.id.nav_send) {
            //get the "social playlist" and share it
            TutorialPlaylist pl = TutorialPlaylist.getByKey(getString(R.string.LOCAL_SOCIAL_PLAYLIST));
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
            return true;
        } else if (id == R.id.action_settings) {
            Log.d(MAINT_ACT_TAG, "Mainactivity settings selected");
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (settingsFragment == null) {
                Log.d(MAINT_ACT_TAG, "Fragment was null");
                settingsFragment = new SettingsFragment();
            }
            fragmentTransaction.replace(R.id.frame, settingsFragment, SettingsFragment.FRAGMENT_KEY);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            return true;
        } else if (id == R.id.action_about) {
            showAboutBox();
            return true;
        } else if (id == R.id.action_credits) {
            showCreditsBox();
            return true;
        } else if (id == R.id.action_help) {
            showHelpBox();
            return true;
        } else if (id == R.id.action_suggest) {
            IntentHelper.sendMail(this,
                    Uri.fromParts("mailto",
                            getString(R.string.mailto_address), null),
                    getString(R.string.app_name),
                    getString(R.string.mailto_message));
            return true;
        } else if (id == R.id.action_report) {
            reportBug();
            return true;
        }

        return false;

    }

    private boolean handleNavigation(int id) {
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
            return true;
        }
        return false;
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
        //find views and set items
        if (homeFragment!=null){
            homeFragment.displayProgressInfo(info);
        }
    }

    @Override
    public void onProgressUpdate(InitialiserFragment.ProgressInfo progressInfo) {
        //find views and set items
        if (homeFragment!=null){
            homeFragment.displayProgressInfo(progressInfo);
        }
    }

    @Override
    public void onCancelled() {
        Toast.makeText(this,"onCancelled",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPostExecute() {
        //clear task from Activity and fragment manager
        if (mTaskFragment!=null){
            Log.d(MAINT_ACT_TAG, "Mainactivity taskFinished, fragment removed");
            getFragmentManager().beginTransaction().remove(mTaskFragment).commit();
            mTaskFragment=null;
        }
        //hook up homeFragment interface
        if (homeFragment!=null){
            homeFragment.displayHome();
        }
    }

    @Override
    public void OnPlaylistSelected(YoutubePlaylist mPlaylist, String mChannelID, int mBgColor, int mFgColor) {
        Log.d(MAINT_ACT_TAG, "In MainActivity OnPlaylistSelected called for ID : " + mPlaylist.getID());
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

    @Override
    public void onInitializeDB() {
        startInitializerFragment();
    }

    @Override
    public void onUpdateDB() {
        startUpdaterFragment();
    }

    @Override
    public boolean isStillWorking() {
        return (mTaskFragment!=null);
    }
}
