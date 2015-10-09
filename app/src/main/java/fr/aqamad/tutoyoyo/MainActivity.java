package fr.aqamad.tutoyoyo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.app.FragmentManager;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //check the model has been created and perform initialisation otherwise
        checkDatabase();




        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    private void checkDatabase() {
        //at the moment, delete everything before setting up a new DB

        //test for model creation
        List<TutorialSource> sources=TutorialSource.getAll();
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
            super.onBackPressed();
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
        int id = item.getItemId();

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


        //Snackbar.make(navigationView, item.getTitle() + " pressed", Snackbar.LENGTH_LONG).show();
        item.setChecked(true);

        if (id == R.id.nav_my) {
            //Toast.makeText(getApplicationContext(),"Inbox Selected",Toast.LENGTH_SHORT).show();
            currentFragment = new MyTutsFragment(this);

        } else if (id == R.id.nav_yyb) {

            currentFragment  = new YoyoBlastFragment(this);

        } else if (id == R.id.nav_clyw) {
            currentFragment  = new ClywFragment(this);

        } else if (id == R.id.nav_yye) {
            currentFragment  = new YoyoExpertFragment(this);

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame, currentFragment).commitAllowingStateLoss();

        View frbck=findViewById(R.id.frameBackground);

        frbck.setVisibility(View.INVISIBLE);

        drawerLayout.closeDrawer(GravityCompat.START);

        currentFragment.fetchChannel(responseHandler, this);

        return true;
    }


    // This is the handler that receives the response when the YouTube task has finished
    Handler responseHandler = new Handler() {
        public void handleMessage(Message msg) {
            populateListWithVideos(msg);
        };
    };

    /**
     * This method retrieves the Library of videos from the task and passes them to our ListView
     * @param msg
     */
    private void populateListWithVideos(Message msg) {
        // Retreive the videos are task found from the data bundle sent back
        YoutubeChannel channel = (YoutubeChannel) msg.getData().get(GetYouTubeChannelTask.CHANNEL);
        // Because we have created a custom ListView we don't have to worry about setting the adapter in the activity
        // we can just call our custom method with the list of items we want to display

        //postProcessing
        currentFragment.setChannel(channel);
        currentFragment.prepareChannel();

        PlaylistsAdapter adapter = new PlaylistsAdapter(this, (ArrayList) channel.getPlaylists(), currentFragment.getForeGroundColor());

        ListView listView= currentFragment.GetPlaylistView();
        listView.setAdapter(adapter);

    }

    public void openPlayList(View view) {
        //we got the view, let's just toast something here
        //we will choose the implementation class dependant on the base fragment class type
        //class identification magic
        Class cl=YoutubePlaylistActivity.class;
        if (currentFragment instanceof PrefetchFragment){
            cl=PrefetchPlaylistActivity.class;
        }
        if (currentFragment instanceof LocalFragment){
            cl=LocalPlaylistActivity.class;
        }


        TextView vwID = (TextView) view.findViewById(R.id.plID);
        String plID=vwID.getText().toString();

        Intent intent = new Intent(this, cl);

        for (int i = 0; i < currentFragment.getChannel().getPlaylists().size(); i++) {
            if (plID.equals(currentFragment.getChannel().getPlaylists().get(i).getID())){
                intent.putExtra(PlaylistActivity.PLAYLIST, currentFragment.getChannel().getPlaylists().get(i) );
            }
        }

        intent.putExtra(PlaylistActivity.BGCOLOR,  currentFragment.getBackGroundColor()  );
        intent.putExtra(PlaylistActivity.FGCOLOR, currentFragment.getForeGroundColor() );
        startActivity(intent);
    }


}
