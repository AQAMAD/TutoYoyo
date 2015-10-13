package fr.aqamad.tutoyoyo;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import fr.aqamad.tutoyoyo.adapters.PlaylistsAdapter;
import fr.aqamad.tutoyoyo.base.PlaylistActivity;
import fr.aqamad.tutoyoyo.base.LocalFragment;
import fr.aqamad.tutoyoyo.base.PrefetchFragment;
import fr.aqamad.tutoyoyo.base.SourceFragment;
import fr.aqamad.tutoyoyo.fragments.ClywFragment;
import fr.aqamad.tutoyoyo.fragments.MyTutsFragment;
import fr.aqamad.tutoyoyo.fragments.YoyoBlastFragment;
import fr.aqamad.tutoyoyo.fragments.YoyoExpertFragment;
import fr.aqamad.tutoyoyo.model.TutorialSource;
import fr.aqamad.tutoyoyo.tasks.GetYouTubeChannelTask;
import fr.aqamad.youtube.YoutubeChannel;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private SourceFragment currentFragment;

    private MyTutsFragment mMyTuts;
    private ClywFragment mClyw;
    private YoyoExpertFragment mYYE;
    private YoyoBlastFragment mYYB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //check the model has been created and perform initialisation otherwise
        checkDatabase();
        super.onCreate(savedInstanceState);
        mMyTuts=MyTutsFragment.newInstance(getString(R.string.localChannelKey));
        mClyw=ClywFragment.newInstance(getString(R.string.CLYW_CHANNEL));
        mYYE=YoyoExpertFragment.newInstance(getString(R.string.YOYOEXPERT_CHANNEL));
        mYYB=YoyoBlastFragment.newInstance(getString(R.string.YOYOBLAST_CHANNEL));

        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            //Restore the fragment's instance
            Log.d("MA", "Mainactivity onCreate bundle");
            String state=savedInstanceState.getString("CurrentFragment");
            if (state!=null){
                if (state.equals("")){
                    View frbck = findViewById(R.id.frameBackground);
                    frbck.setVisibility(View.VISIBLE);
                }else{
                    hideHome();
                }
            }
        } else {
            //create and prepare different fragments
            Log.d("MA", "Mainactivity onCreate nobundle");
        }



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, "contact@aqamad.fr");
                intent.putExtra(Intent.EXTRA_SUBJECT, "TutoYoyo");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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
        if (currentFragment!=null){
            outState.putString("CurrentFragment",currentFragment.getChannelId());
        }
        Log.d("MA", "Mainactivity onSaveInstanceState");
    }

    private void checkDatabase() {
        //at the moment, delete everything before setting up a new DB

        //test for model creation
        List<TutorialSource> sources =TutorialSource.getAll();
        if (sources.size()==0){
            TutorialSource.initializeDB(this.getApplication());
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            FragmentManager fragmentManager = getFragmentManager();
            int backCount = fragmentManager.getBackStackEntryCount();
            if (backCount>1){
                fragmentManager.popBackStack();
            }else if (backCount>0){
                fragmentManager.popBackStack();
                View frbck = findViewById(R.id.frameBackground);
                frbck.setVisibility(View.VISIBLE);
            }else{
                super.onBackPressed();
            }
        }
        Log.d("MA", "Mainactivity onBackPressed");
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
            return true;
        }

        return super.onOptionsItemSelected(item);
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

        if (id == R.id.nav_my) {
            //Toast.makeText(getApplicationContext(),"Inbox Selected",Toast.LENGTH_SHORT).show();
            currentFragment = mMyTuts;

        } else if (id == R.id.nav_yyb) {

            currentFragment  = mYYB;

        } else if (id == R.id.nav_clyw) {
            currentFragment  = mClyw;

        } else if (id == R.id.nav_yye) {
            currentFragment  = mYYE;

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        FragmentManager fragmentManager = getFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.frame, currentFragment);

        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();

        hideHome();

        drawerLayout.closeDrawer(GravityCompat.START);

        Log.d("MA", "Mainactivity onNavigationItemSelected end");

        return true;
    }

    private void hideHome() {
        View frbck = findViewById(R.id.frameBackground);
        frbck.setVisibility(View.INVISIBLE);
        Log.d("MA", "Mainactivity hideHome");
    }


}
