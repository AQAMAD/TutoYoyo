package fr.aqamad.tutoyoyo.base;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
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

import fr.aqamad.tutoyoyo.R;
import fr.aqamad.tutoyoyo.adapters.VideosAdapter;
import fr.aqamad.tutoyoyo.model.TutorialChannel;
import fr.aqamad.tutoyoyo.model.TutorialVideo;
import fr.aqamad.tutoyoyo.tasks.GetLocalPlaylistTask;
import fr.aqamad.tutoyoyo.tasks.GetYouTubePlaylistTask;
import fr.aqamad.youtube.YoutubeChannel;
import fr.aqamad.youtube.YoutubePlaylist;
import fr.aqamad.youtube.YoutubeUtils;

public abstract class PlaylistActivity extends AppCompatActivity {

    public static final String PLAYLIST = "fr.aqamad.youtube.playlist";
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
    private String mTitle;
    private String mDescription;
    private String mThumb;
    private YoutubePlaylist mPlaylist;

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
        tvDesc.setText(mDescription);

        RelativeLayout container=(RelativeLayout) findViewById(R.id.playContainer);
        container.setBackgroundColor(getResources().getColor(mBgColor));
        ListView listView= (ListView) findViewById(R.id.videos);
        listView.setBackgroundColor(getResources().getColor(mBgColor));

        Picasso.with(this).load(mThumb)
                .placeholder(R.drawable.waiting)
                .into(imThumb)
        ;

        if (mPlaylist.getVideos().size()>0){
            Bundle data = new Bundle();
            data.putSerializable(GetLocalPlaylistTask.PLAYLIST, mPlaylist);
            // Send the Bundle of data (our Library) back to the handler (our Activity)
            Message msg = Message.obtain();
            msg.setData(data);
            responseHandler.sendMessage(msg);
        }else {
            fetchVideos(responseHandler, mPlayListID, this.getString(R.string.youtubeapikey));
        }



    }

    public abstract void fetchVideos(Handler handler,String playlistId,String apiKey);

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
        YoutubePlaylist playlist = (YoutubePlaylist) msg.getData().get(GetYouTubePlaylistTask.PLAYLIST);
        // Because we have created a custom ListView we don't have to worry about setting the adapter in the activity
        // we can just call our custom method with the list of items we want to display

        VideosAdapter adapter = new VideosAdapter(this, (ArrayList) playlist.getVideos(), this.getFgColor());
//
        ListView listView= (ListView) findViewById(R.id.videos);
        listView.setAdapter(adapter);

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
            vwID.setVisibility(View.GONE);
            view.setRotation(270);
        }else{
            vwID.setVisibility(View.VISIBLE);
            view.setRotation(0);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // API 5+ solution
                NavUtils.navigateUpFromSameTask(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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
                    .setTitle("Alert")
                    .setMessage("Do you really want to remove this video from your 'Watch Later' list ?")
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
                    .setTitle("Alert")
                    .setMessage("Do you really want to remove this video from your 'Social' list ?")
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
        TutorialChannel later = TutorialChannel.getByKey(string);
        new Delete().from(TutorialVideo.class).where("Key = ? and Channel = ?", vidID, later.getId()).execute();
        currentView.setTag(false);
        ((ImageView) currentView).setColorFilter(currentView.getContext().getResources().getColor(android.R.color.holo_blue_light), PorterDuff.Mode.SRC_ATOP);
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
                    .setTitle("Alert")
                    .setMessage("Do you really want to remove this video from your 'Favorites' list ?")
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
        TutorialChannel later = TutorialChannel.getByKey(string);
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
        vid.save();
        view.setTag(true);
        ((ImageView) view).setColorFilter(view.getContext().getResources().getColor(android.R.color.holo_green_light), PorterDuff.Mode.SRC_ATOP);
    }
}
