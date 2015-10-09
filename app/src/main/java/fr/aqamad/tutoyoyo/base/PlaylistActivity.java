package fr.aqamad.tutoyoyo.base;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import fr.aqamad.tutoyoyo.R;
import fr.aqamad.tutoyoyo.adapters.VideosAdapter;
import fr.aqamad.tutoyoyo.tasks.GetLocalPlaylistTask;
import fr.aqamad.tutoyoyo.tasks.GetYouTubePlaylistTask;
import fr.aqamad.youtube.YoutubeChannel;
import fr.aqamad.youtube.YoutubePlaylist;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
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
        //we got the view, let's just toast something here
        TextView vwID = (TextView) view.findViewById(R.id.vidID);
        String vidID=vwID.getText().toString();

//        Intent intent = new Intent(this, VideoActivity.class);
//        intent.putExtra(VideoActivity.VIDEO_ID, plID );
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + vidID));
        this.startActivity(intent);

//        Intent intent = new Intent(null, Uri.parse("ytv://"+vidID), this,
//                OpenYouTubePlayerActivity.class);
//        startActivity(intent);
        //Snackbar.make(navigationView, plID + " pressed", Snackbar.LENGTH_LONG).show();

    }
}
