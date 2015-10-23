package fr.aqamad.tutoyoyo;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
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
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import fr.aqamad.tutoyoyo.fragments.InitialiserFragment;
import fr.aqamad.tutoyoyo.fragments.PlaylistFragment;
import fr.aqamad.tutoyoyo.fragments.SettingsFragment;
import fr.aqamad.tutoyoyo.fragments.SourceFragment;
import fr.aqamad.tutoyoyo.model.TutorialPlaylist;
import fr.aqamad.tutoyoyo.model.TutorialSource;
import fr.aqamad.tutoyoyo.model.TutorialVideo;
import fr.aqamad.tutoyoyo.utils.PicassoHelper;
import fr.aqamad.tutoyoyo.utils.ScreenSize;
import fr.aqamad.youtube.YoutubePlaylist;
import fr.aqamad.youtube.YoutubeUtils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        ,InitialiserFragment.TaskCallbacks
        ,SourceFragment.OnPlaylistSelectedListener

{

    private static final String TAG_TASK_FRAGMENT = "initializer_task_fragment";

    private InitialiserFragment mTaskFragment;


    private NavigationView navigationView;

    private SourceFragment currentFragment;

    private SourceFragment mMyTuts;
    private SourceFragment mClyw;
    private SourceFragment mYYE;
    private SourceFragment mYYB;
    private SourceFragment mMrYo;
    private SourceFragment mBhop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //check the model has been created and perform initialisation otherwise
        super.onCreate(savedInstanceState);
        //default values for preferences
        PreferenceManager.setDefaultValues(this, R.xml.fragment_settings, false);
        //init fragments
        mMyTuts=SourceFragment.newInstance(getString(R.string.localChannelKey),R.layout.header_mytuts,false,android.R.color.black,android.R.color.white,"");
        mYYB=SourceFragment.newInstance(getString(R.string.YOYOBLAST_CHANNEL),R.layout.header_yoyoblast,true,android.R.color.black,R.color.yoyoBlast,"http://www.yoyoblast.com");
        mYYE=SourceFragment.newInstance(getString(R.string.YOYOEXPERT_CHANNEL),R.layout.header_yoyoexpert,true,android.R.color.black,R.color.yoyoExpert,"http://www.yoyoexpert.com");
        mClyw=SourceFragment.newInstance(getString(R.string.CLYW_CHANNEL),R.layout.header_clyw,false,android.R.color.white,R.color.my,"http://www.clyw.ca");
        mMrYo=SourceFragment.newInstance(getString(R.string.YOYOTHROWER_CHANNEL),R.layout.header_yoyothrower,true,android.R.color.black,android.R.color.holo_blue_light,"http://www.mryoyothrower.com");
        mBhop=SourceFragment.newInstance(getString(R.string.BLACKHOP_CHANNEL),R.layout.header_blackhop,false,android.R.color.black,android.R.color.white,"https://www.youtube.com/user/hadoq");

        setContentView(R.layout.activity_main);

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //based on preferences
        enableProviderMenus();
    }

    private void enableProviderMenus() {
        //activate or deactivate items based on sharedpreferences
        SharedPreferences appPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean yybEnabled = appPreferences.getBoolean(getString(R.string.chk_pref_yoyoblast), true);
        boolean yyeEnabled = appPreferences.getBoolean(getString(R.string.chk_pref_yoyoexpert), true);
        boolean clywEnabled = appPreferences.getBoolean(getString(R.string.chk_pref_clyw), true);
        boolean yytEnabled = appPreferences.getBoolean(getString(R.string.chk_pref_yoyothrower), true);
        boolean bhoEnabled = appPreferences.getBoolean(getString(R.string.chk_pref_blackhop), true);
        navigationView.getMenu().findItem(R.id.nav_yyb).setVisible(yybEnabled);
        navigationView.getMenu().findItem(R.id.nav_yye).setVisible(yyeEnabled);
        navigationView.getMenu().findItem(R.id.nav_clyw).setVisible(clywEnabled);
        navigationView.getMenu().findItem(R.id.nav_yyt).setVisible(yytEnabled);
        navigationView.getMenu().findItem(R.id.nav_bhop).setVisible(bhoEnabled);
    }

    private void sendMail(Uri mailto, String string, String string2) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, mailto);
        intent.putExtra(Intent.EXTRA_SUBJECT, string);
        intent.putExtra(Intent.EXTRA_TEXT, string2);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("MA", "onSaveInstanceState");
        if (mMyTuts.isAdded()){
            getFragmentManager().putFragment(outState, "mMyTuts", mMyTuts);
        }
        if (mClyw.isAdded()){
            getFragmentManager().putFragment(outState, "mClyw", mClyw);
        }
        if (mYYE.isAdded()){
            getFragmentManager().putFragment(outState, "mYYE", mYYE);
        }
        if (mYYB.isAdded()){
            getFragmentManager().putFragment(outState, "mYYB", mYYB);
        }
        if (mMrYo.isAdded()){
            getFragmentManager().putFragment(outState, "mMrYo", mMrYo);
        }
        if (mBhop.isAdded()){
            getFragmentManager().putFragment(outState, "mBhop", mBhop);
        }
        if (currentFragment!=null){
            outState.putString("CurrentFragment", currentFragment.getChannelId());
        }
        Log.d("MA", "Mainactivity onSaveInstanceState");
    }

    private void checkDatabase() {
        //at the moment, delete everything before setting up a new DB

        //test for model creation
        List<TutorialSource> sources =TutorialSource.getAll();
        if (sources.size()==0){
            if (!isOnline()){
                Snackbar.make(navigationView, "Network connectivity is required to build cache. Try restarting app when online.", Snackbar.LENGTH_LONG).show();
            } else {
                //start initialiser fragment
                FragmentManager fm = getFragmentManager();
                mTaskFragment = (InitialiserFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);
                // If the Fragment is non-null, then it is currently being
                // retained across a configuration change.
                if (mTaskFragment == null) {
                    mTaskFragment = new InitialiserFragment();
                    fm.beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();
                }
            }
        }else{
            //hide progressbar
            hideProgress();
            //and welcome Text
            TextView wt= (TextView) findViewById(R.id.welcomeText);
            wt.setVisibility(View.GONE);
            //add stats information to welcome text
            wt= (TextView) findViewById(R.id.textWelcomeTitle);
            int nbVids =TutorialVideo.countAll();
            //get random tut of the day
            TutorialVideo randomTut=TutorialVideo.getRandom();
            //we got the random tut, load it's thumbnail into main header for instance...
            ImageView imgHeader= (ImageView) findViewById(R.id.imgHomeHeader);
            //create text
            wt.setText(String.format(getString(R.string.randomTutAndStats), randomTut.name, nbVids));
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
            }else if (backCount>0){
                fragmentManager.popBackStack();
                fragmentManager.popBackStackImmediate();
                //need to reset the nav indicator
                resetNavIndicator();
            }else{
                super.onBackPressed();
            }
        }
        Log.d("MA", "Mainactivity onBackPressed");
    }

    private void resetNavIndicator() {
        //get menus from navigation view
        Log.d("MA.RNI", "ResetNavigationIndicator called");
        if (mMyTuts.isVisible()){
            //reset navigation drawer selected id
            Log.d("MA.RNI", "My tuts should be checked");
            navigationView.getMenu().findItem(R.id.nav_my).setChecked(true);
        } else if (mYYB.isVisible()){
            //reset navigation drawer selected id
            Log.d("MA.RNI","YYB should be checked");
            navigationView.getMenu().findItem(R.id.nav_yyb).setChecked(true);
        } else if (mYYE.isVisible()){
            //reset navigation drawer selected id
            Log.d("MA.RNI","YYE should be checked");
            navigationView.getMenu().findItem(R.id.nav_yye).setChecked(true);
        } else if (mClyw.isVisible()){
            //reset navigation drawer selected id
            Log.d("MA.RNI","CLYW should be checked");
            navigationView.getMenu().findItem(R.id.nav_clyw).setChecked(true);
        } else if (mMrYo.isVisible()){
            //reset navigation drawer selected id
            Log.d("MA.RNI","YYT should be checked");
            navigationView.getMenu().findItem(R.id.nav_yyt).setChecked(true);
        } else if (mBhop.isVisible()){
            //reset navigation drawer selected id
            Log.d("MA.RNI","BH should be checked");
            navigationView.getMenu().findItem(R.id.nav_bhop).setChecked(true);
        } else {
            //reset all indicators
            navigationView.getMenu().findItem(R.id.nav_my).setChecked(false);
            navigationView.getMenu().findItem(R.id.nav_yyb).setChecked(false);
            navigationView.getMenu().findItem(R.id.nav_yye).setChecked(false);
            navigationView.getMenu().findItem(R.id.nav_clyw).setChecked(false);
            navigationView.getMenu().findItem(R.id.nav_yyt).setChecked(false);
            navigationView.getMenu().findItem(R.id.nav_bhop).setChecked(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
//            hideHome();
            return true;
        }else if (id==R.id.action_about){
            showAboutBox();
            return true;
        }else if (id==R.id.action_credits){
            showCreditsBox();
            return true;
        }else if (id==R.id.action_suggest){
            sendMail(Uri.fromParts("mailto",
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
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("text/plain");
        //add email address
        intent.putExtra(Intent.EXTRA_EMAIL,
                new String[]{getString(R.string.mailto_address)});
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) + " [Bug Report] ");
        //and bodytext
//        ArrayList<String> extra_text = new ArrayList<String>();
//        extra_text.add(getString(R.string.mailto_bugmessage));
//        intent.putStringArrayListExtra(Intent.EXTRA_TEXT, extra_text);
        //above code does not work, below code works but gets a warning in the logcat
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.mailto_bugmessage));
        //get the db filename here
        String dbName="YoyoTuts.db";
        //backup the database
        File backupDB = null;
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//" + getApplicationContext().getPackageName()
                        + "//databases//" + dbName + "";
                File currentDB = new File(data, currentDBPath);
                backupDB = new File(sd, dbName);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB)
                            .getChannel();
                    FileChannel dst = new FileOutputStream(backupDB)
                            .getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
                else
                {
                    Log.d("MA.RB","Database not found at path : " + currentDBPath);
                }
            } else
            {
                Log.d("MA.RB","Unwritable sd : " + sd.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //log the db path
        Log.d("MA.RB","Backup DatabasePath was : " + backupDB.getAbsolutePath() );
        //now get the logcat
        File filename = new File(Environment.getExternalStorageDirectory()+"/mylog.log");
        try {
            filename.createNewFile();
            String cmd = "logcat -d -f"+filename.getAbsolutePath();
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //logically logcat was created
        Log.d("MA.RB","Logfile Path was : " + filename.getAbsolutePath() );
        //prepare array
        //has to be an ArrayList
        ArrayList<Uri> uris = new ArrayList<Uri>();
        //convert from paths to Android friendly Parcelable Uri's
        uris.add(Uri.fromFile(backupDB));
        uris.add(Uri.fromFile(filename));
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void showCreditsBox() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(getString(R.string.dialog_title_credits));
        alertDialog.setMessage(getString(R.string.dialog_msg_credits));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void showAboutBox() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(getString(R.string.dialog_title_about));
        alertDialog.setMessage(getString(R.string.dialog_msg_about));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
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
                ArrayList<String> videoUrls = new ArrayList<String>();
                for (TutorialVideo vid :
                        pl.videos()) {
                    videoUrls.add(YoutubeUtils.getVideoPlayUrl(vid.key));
                }
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
                ArrayList<String> videoUrls = new ArrayList<String>();
                for (TutorialVideo vid :
                        pl.videos()) {
                    videoUrls.add(YoutubeUtils.getVideoPlayUrl(vid.key));
                }
                //like sending through mail for instance
                sendMail(Uri.fromParts("mailto",
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

        if (id == R.id.nav_my) {
            //Toast.makeText(getApplicationContext(),"Inbox Selected",Toast.LENGTH_SHORT).show();
            newFragment = mMyTuts;
        } else if (id == R.id.nav_yyb) {
            newFragment  = mYYB;
        } else if (id == R.id.nav_clyw) {
            newFragment  = mClyw;
        } else if (id == R.id.nav_yye) {
            newFragment  = mYYE;
        } else if (id == R.id.nav_yyt) {
            newFragment  = mMrYo;
        } else if (id == R.id.nav_bhop) {
            newFragment  = mBhop;
        }

        if (newFragment!=null){
            currentFragment=newFragment;
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame, currentFragment);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
//            hideHome();
        }
    }

//    private void hideHome() {
//        View frbck = findViewById(R.id.frameBackground);
//        frbck.setVisibility(View.INVISIBLE);
//        Log.d("MA", "Mainactivity hideHome");
//    }


    public void showCredits(View view) {
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
    public void onProgressUpdate(InitialiserFragment.ProgressInfo progressInfo) {
        //find views and set items
        ProgressBar pbP = (ProgressBar) findViewById(R.id.progressBarPro);
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
        hideProgress();
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
}
