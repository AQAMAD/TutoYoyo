package fr.aqamad.tutoyoyo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.activeandroid.query.Delete;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import fr.aqamad.tutoyoyo.adapters.VideosAdapter;
import fr.aqamad.tutoyoyo.model.TutorialPlaylist;
import fr.aqamad.tutoyoyo.model.TutorialSource;
import fr.aqamad.tutoyoyo.model.TutorialVideo;
import fr.aqamad.tutoyoyo.tasks.GetPlaylistTask;
import fr.aqamad.tutoyoyo.utils.PicassoHelper;
import fr.aqamad.tutoyoyo.utils.UI;
import fr.aqamad.youtube.YoutubeChannel;
import fr.aqamad.youtube.YoutubePlaylist;
import fr.aqamad.youtube.YoutubeUtils;
import fr.aqamad.youtube.YoutubeVideo;

public class PlaylistActivity extends AppCompatActivity {

    public static final String PLAYLIST = "fr.aqamad.youtube.playlist";
    public static final String CHANNEL = "fr.aqamad.youtube.playlist.channel";
    public static final String BGCOLOR="fr.aqamad.youtube.playlist.bgcolor";
    public static final String FGCOLOR="fr.aqamad.youtube.playlist.fgcolor";

    private String mPlayListID;

    public int getBgColor() {
        return mBgColor;
    }

    public void setBgColor(int mBgColor) {
        this.mBgColor = mBgColor;
    }

    public int getFgColor() {
        return mFgColor;
    }

    public void setFgColor(int mFgColor) {
        this.mFgColor = mFgColor;
    }

    private int mBgColor;
    private int mFgColor;
    private int highlightColor=android.R.color.holo_green_light;
    private String mTitle;
    private String mDescription;
    private String mThumb;
    private YoutubePlaylist mPlaylist;
    private String mChannelID;

    View currentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getIntent().getExtras() != null) {
            mBgColor=getIntent().getIntExtra(BGCOLOR, android.R.color.white);
            mFgColor=getIntent().getIntExtra(FGCOLOR, android.R.color.black);
            mPlaylist=(YoutubePlaylist) getIntent().getSerializableExtra(PLAYLIST);
            mPlayListID=mPlaylist.getID();
            mChannelID=getIntent().getStringExtra(CHANNEL);
            mTitle=mPlaylist.getTitle();
            mDescription=mPlaylist.getDescription();
            mThumb=mPlaylist.getHighThumb().getUrl().toString();
        }

        TextView tvTitle= (TextView) findViewById(R.id.plTitle);
        tvTitle.setTextColor(getResources().getColor(mFgColor));
        TextView tvDesc= (TextView) findViewById(R.id.plDesc);
        tvDesc.setTextColor(getResources().getColor(mFgColor));
        ImageView imThumb= (ImageView) findViewById(R.id.imgPlaylistThumb);
        tvTitle.setText(mTitle);

        String description=mDescription;
        if (description.length()>90){
            description=description.substring(0,89)+" (...)";
        }
        tvDesc.setText(description);
        //hide description
        LinearLayout llEx = (LinearLayout) this.findViewById(R.id.expandDescription);
        llEx.setVisibility(View.GONE);
        //set long description
        TextView tvLongDesc= (TextView) findViewById(R.id.plLongDesc);
        tvLongDesc.setText(mDescription);
        tvLongDesc.setTextColor(getResources().getColor(mFgColor));
        //set colors
        RelativeLayout container=(RelativeLayout) findViewById(R.id.playContainer);
        container.setBackgroundColor(getResources().getColor(mBgColor));
        ListView listView= (ListView) findViewById(R.id.videos);
        listView.setBackgroundColor(getResources().getColor(mBgColor));

        PicassoHelper.loadWeborDrawable(this, mThumb).centerCrop()
                .placeholder(R.drawable.waiting)
                .resize(YoutubeUtils.HIGH_WIDTH,YoutubeUtils.HIGH_HEIGHT)
                .into(imThumb)
        ;

        if (mPlaylist.getVideos().size()>0){
            Bundle data = new Bundle();
            data.putSerializable(GetPlaylistTask.PLAYLIST, mPlaylist);
            // Send the Bundle of data (our Library) back to the handler (our Activity)
            Message msg = Message.obtain();
            msg.setData(data);
            responseHandler.sendMessage(msg);
        }else {
            fetchVideos(responseHandler, mPlayListID, this.getString(R.string.youtubeapikey));
        }



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
    public void populateListWithVideos(Message msg) {
        // Retreive the videos are task found from the data bundle sent back
        YoutubePlaylist playlist = (YoutubePlaylist) msg.getData().get(GetPlaylistTask.PLAYLIST);
        // we can just call our custom method with the list of items we want to display
        // Do a bit of data copy to simplify caching and accessing in case of fetch
        playlist.setID(mPlaylist.getID());
        playlist.setTitle(mPlaylist.getTitle());
        playlist.setDescription(mPlaylist.getDescription());
        playlist.setHighThumb(mPlaylist.getHighThumb());
        playlist.setMediumThumb(mPlaylist.getMediumThumb());
        playlist.setDefaultThumb(mPlaylist.getDefaultThumb());
        playlist.setPublishedAt(mPlaylist.getPublishedAt());

        //go through name cleaning
        cleanPlaylist(playlist);

        cachePlaylist(playlist);


        VideosAdapter adapter = new VideosAdapter(this, (ArrayList) playlist.getVideos(), this.getFgColor(),this.getBgColor(),this.highlightColor);
//

        ListView listView= (ListView) findViewById(R.id.videos);
        listView.setAdapter(adapter);

    }

    private void cleanPlaylist(YoutubePlaylist playlist) {
        Iterator<YoutubeVideo> iter= playlist.getVideos().iterator();
        while(iter.hasNext()){
            YoutubeVideo vid=iter.next();
            String title=vid.getTitle();
            //get the ressources for cleaning
            title=(title.replace(" - YoyoBlast", ""))
                    .replace("CLYW Cabin Tutorials - ", "")
                    .replace("CLYW Cabin Tutorial - ", "")
                    .replace("Cabin Tutorial - ","")
                    .replace("Learn to Yo-yo ", "")
                    .replace("yoyo-france.net - tuto yoyo - debutant - ", "")
                    .replace("yoyo-france.net - tuto yoyo - intermédiaire - ", "")
                    .replace("yoyo-france.net - tuto yoyo - ", "")
                    .replace("blackhop.com - tuto yoyo intermediaire - ", "")
                    .replace("blackhop.com - tuto intermediaire - ", "")
                    .replace("Yoyo Trick Tutorial - ", "")
                    .replace("Yoyo Trick Tutorial-", "")
                    .replace("Yoyo trick tutorial-", "")
                    .replace("yoyo tutorial", "")
                    .replace("Yoyo Tutorial - ", "")
                    .replace("Yoyo Trick Tutorial ", "")
                    .replace("1a Tutorial - ", "")
                    .replace("1a Tutorial ", "")
                    .replace("1a tutorial ", "")
                    .replace("4a Tutorial - ", "4a - ");
            //prettify first letter
            title = title.substring(0, 1).toUpperCase() + title.substring(1);
            vid.setTitle(title);
        }
    }


    public void openVideo(View view) {
        ViewGroup parent= (ViewGroup) view.getParent();
        while (parent.getId()!=R.id.parentofExpandable){
            parent=(ViewGroup) parent.getParent();
        }
        //we got the view, let's just toast something here

        TextView vwID = (TextView) parent.findViewById(R.id.vidID);
        String vidID=vwID.getText().toString();

//        Intent intent = new Intent(this, VideoActivity.class);
//        intent.putExtra(VideoActivity.VIDEO_ID, plID );
        YoutubeUtils.PlayYoutubeVideo(vidID, this);


    }

    public void expandSection(View view) {
        //il faut retrouver le layout qu'on aime bien
        ViewGroup parent= (ViewGroup) view.getParent();
        while (parent.getId()!=R.id.parentofExpandable){
            parent=(ViewGroup) parent.getParent();
        }

        LinearLayout vwID = (LinearLayout) parent.findViewById(R.id.expandSection);
        int visibility=vwID.getVisibility();
        if (visibility==View.VISIBLE){
            UI.getInstance().animHideCollapse(vwID);
            UI.animRotate(view, 270, 400);
        }else{
            //animate visibility
            UI.animRevealExpand(vwID);
            UI.animRotate(view, 360, 400);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // end activity
                this.finish();
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed() {
        // finish() is called in super: we only override this method to be able to override the transition
        super.onBackPressed();

        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }

    public void addLater(View view) {
        //get button tag
        boolean is=(boolean)view.getTag();

        //obtain videoID

        if (is){
            //already later
            //toast some confirmation message and remove if necessary
            currentView=view;
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_title_remove)
                    .setMessage(R.string.dialog_msg_remove_later)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            //call model code to remove from list
                            removeFromLocalChannel(currentView.getResources().getString(R.string.localLaterKey));
                        }})
                    .setNegativeButton(android.R.string.no, null).show();

        }else{
            addToLocalChannel(view, view.getResources().getString(R.string.localLaterKey));
        }


    }
    public void addShare(View view) {
        //get button tag
        boolean is=(boolean)view.getTag();

        //obtain videoID

        if (is){
            //already later
            //toast some confirmation message and remove if necessary
            currentView=view;
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_title_remove)
                    .setMessage(R.string.dialog_msg_remove_share)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            //call model code to remove from list
                            removeFromLocalChannel(currentView.getResources().getString(R.string.localSocialKey));
                        }})
                    .setNegativeButton(android.R.string.no, null).show();

        }else{
            addToLocalChannel(view, view.getResources().getString(R.string.localSocialKey));
        }

    }

    private void removeFromLocalChannel(String string) {
        ViewGroup parent = (ViewGroup) currentView.getParent();
        while (parent.getId() != R.id.parentofExpandable) {
            parent = (ViewGroup) parent.getParent();
        }
        TextView vwID = (TextView) parent.findViewById(R.id.vidID);
        String vidID = vwID.getText().toString();
        //let's delete
        TutorialPlaylist later = TutorialPlaylist.getByKey(string);
        new Delete().from(TutorialVideo.class).where("Key = ? and Channel = ?", vidID, later.getId()).execute();
        currentView.setTag(false);
        UI.colorize((ImageView) currentView,null);
    }

    public void addFavorite(View view) {
        //get button tag
        boolean is=(boolean)view.getTag();

        //obtain videoID

        if (is){
            //already later
            //toast some confirmation message and remove if necessary
            currentView=view;
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_title_remove)
                    .setMessage(R.string.dialog_msg_remove_favorite)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            //call model code to remove from list
                            removeFromLocalChannel(currentView.getResources().getString(R.string.localFavoritesKey));
                        }})
                    .setNegativeButton(android.R.string.no, null).show();

        }else{
            //
            addToLocalChannel(view, view.getResources().getString(R.string.localFavoritesKey));
        }

    }

    private void addToLocalChannel(View view, String string) {
        ViewGroup parent = (ViewGroup) view.getParent();
        while (parent.getId() != R.id.parentofExpandable) {
            parent = (ViewGroup) parent.getParent();
        }
        TextView vwID = (TextView) parent.findViewById(R.id.vidID);
        String vidID = vwID.getText().toString();
        //add to channel
        TutorialPlaylist later = TutorialPlaylist.getByKey(string);
        //channel is ok, build video model
        TutorialVideo vid = new TutorialVideo();
        vid.channel = later;
        vid.key = vidID;
        //get name from interface
        TextView vwName = (TextView) parent.findViewById(R.id.vidName);
        TextView vwDesc = (TextView) parent.findViewById(R.id.vidDesc2);
        vid.name = (String) vwName.getText();
        vid.description = (String) vwDesc.getText();
        ImageView imgThumb = (ImageView) parent.findViewById(R.id.imgVidThumb);
        vid.defaultThumbnail = (String) imgThumb.getTag(R.id.defaultThumb);
        vid.mediumThumbnail = (String) imgThumb.getTag(R.id.mediumThumb);
        vid.highThumbnail = (String) imgThumb.getTag(R.id.highThumb);
        TextView vwDuration = (TextView) parent.findViewById(R.id.vidDuration);
        vid.duration= (String) vwDuration.getTag();
        vid.save();
        view.setTag(true);
        UI.colorize((ImageView) view, highlightColor);
        Snackbar.make(view, R.string.msg_video_added, Snackbar.LENGTH_SHORT).show();
    }

    public void expandDescription(View view) {

        LinearLayout llex = (LinearLayout) this.findViewById(R.id.expandDescription);
        int visibility=llex.getVisibility();
        if (visibility==View.VISIBLE){
            UI.getInstance().animHideCollapse(llex);
            UI.animRotate(view, 270, 400);
        }else{
            //animate visibility
            UI.animRevealExpand(llex);
            UI.animRotate(view, 360, 400);
        }

    }

    public void fetchVideos(Handler handler, String playlistId, String apiKey) {
        new Thread(new GetPlaylistTask(handler,  playlistId, this)).start();
    }


    private void cachePlaylist(YoutubePlaylist playlist) {
        Log.d("PACP", "Called Playlistactivity cachePlaylist for " + playlist.getID() + " aka " + playlist.getTitle());
        TutorialPlaylist tutorialPlaylist = TutorialPlaylist.getByKey(playlist.getID());
        if (tutorialPlaylist == null) {
            Log.d("PACP","Playlist not found, caching");
            YoutubeUtils.logPlaylist(playlist);
            //loading source
            TutorialSource source = TutorialSource.getByKey(mChannelID);
            //we did a lookup, store in the database to avoid reparsing json
            tutorialPlaylist = new TutorialPlaylist();
            tutorialPlaylist.name = playlist.getTitle();
            tutorialPlaylist.description = playlist.getDescription();
            tutorialPlaylist.fetchedAt = new Date();
            tutorialPlaylist.publishedAt=playlist.getPublishedAt();
            tutorialPlaylist.defaultThumbnail=playlist.getDefaultThumb().getUrl();
            tutorialPlaylist.mediumThumbnail=playlist.getMediumThumb().getUrl();
            tutorialPlaylist.highThumbnail=playlist.getHighThumb().getUrl();
            tutorialPlaylist.source= source;///problem, how to get the source...
            tutorialPlaylist.key = playlist.getID();
            tutorialPlaylist.save();
            Log.d("PACP", "Playlist cached");
            //now iterate to store playlists
            Log.d("PACP", "Caching videos");
            //if the playlists have videos, we store them too
            for (YoutubeVideo vid :
                    playlist.getVideos()  ) {
                //repeat the deal
                TutorialVideo vi = new TutorialVideo();
                Log.d("PACP", "Caching video " + vid.getID() + " aka " + vid.getTitle());
                vi.channel = tutorialPlaylist;
                vi.key = vid.getID();
                vi.name = vid.getTitle();
                vi.description = vid.getDescription();
                vi.duration=vid.getDuration();
                vi.publishedAt = vid.getPublishedAt();
                vi.defaultThumbnail = vid.getDefaultThumb().getUrl().toString();
                vi.mediumThumbnail = vid.getMediumThumb().getUrl().toString();
                vi.highThumbnail = vid.getHighThumb().getUrl().toString();
                vi.caption=vid.getCaption();
                vi.save();
                Log.d("PACP", "Video " + vi.name + " cached");
            }

        }else{
            Log.d("PACP", "Playlist " + tutorialPlaylist.name + " found");
        }
    }

    public void shareVideo(View view) {
        ViewGroup parent = (ViewGroup) view.getParent();
        while (parent.getId() != R.id.parentofExpandable) {
            parent = (ViewGroup) parent.getParent();
        }
        TextView vwID = (TextView) parent.findViewById(R.id.vidID);
        String vidID = vwID.getText().toString();
        //
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, YoutubeUtils.getVideoPlayUrl(vidID));
        intent.setType("text/plain");
        startActivity(intent);
    }
}
