package fr.aqamad.tutoyoyo.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import fr.aqamad.tutoyoyo.R;
import fr.aqamad.tutoyoyo.adapters.VideosListViewAdapter;
import fr.aqamad.tutoyoyo.model.ModelConverter;
import fr.aqamad.tutoyoyo.model.Sponsor;
import fr.aqamad.tutoyoyo.model.Sponsors;
import fr.aqamad.tutoyoyo.tasks.GetPlaylistTask;
import fr.aqamad.tutoyoyo.utils.PicassoHelper;
import fr.aqamad.tutoyoyo.utils.ScreenSize;
import fr.aqamad.youtube.YoutubePlaylist;

/**
 * Created by Gregoire on 19/10/2015.
 */
public class PlaylistFragment extends Fragment {
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

    public PlaylistFragment(){

    }


    public static PlaylistFragment newInstance(YoutubePlaylist mPlaylist,String mChannelID,int mBgColor,int mFgColor) {
        PlaylistFragment fragment = new PlaylistFragment();
        Bundle args = new Bundle();
        args.putSerializable(PLAYLIST,mPlaylist);
        args.putInt(BGCOLOR, mBgColor);
        args.putInt(FGCOLOR, mFgColor);
        args.putString(CHANNEL, mChannelID);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        restoreFromBundle(savedInstanceState);
        Bundle b=getArguments();
        restoreFromBundle(b);

    }

    private void restoreFromBundle(Bundle b) {
        if (b != null && b.containsKey(PLAYLIST) ) {
            mBgColor=b.getInt(BGCOLOR, android.R.color.white);
            mFgColor=b.getInt(FGCOLOR, android.R.color.black);
            mPlaylist=(YoutubePlaylist) b.getSerializable(PLAYLIST);
            mPlayListID=mPlaylist.getID();
            mChannelID=b.getString(CHANNEL);
            mTitle=mPlaylist.getTitle();
            mDescription=mPlaylist.getDescription();
            mThumb=mPlaylist.getHighThumb().getUrl().toString();
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save the fragment's state here
        outState.putString(CHANNEL, mChannelID);
        outState.putInt(BGCOLOR, mBgColor);
        outState.putInt(FGCOLOR, mFgColor);
        outState.putSerializable(PLAYLIST, mPlaylist);
        Log.d("PF", "PlaylistFragment onSaveInstanceState : " + mChannelID);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_playlist, container, false);

        TextView tvTitle= (TextView) rootView.findViewById(R.id.plTitle);
        tvTitle.setTextColor(getResources().getColor(mFgColor));
        tvTitle.setText(mTitle);

        //set long description
        TextView tvLongDesc= (TextView) rootView.findViewById(R.id.plLongDesc);
        if (mDescription.trim().length()==0){
            tvLongDesc.setVisibility(View.GONE);
        }else {
            tvLongDesc.setText(mDescription);
            tvLongDesc.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            tvLongDesc.setSelected(true);
            tvLongDesc.setSingleLine(true);
            tvLongDesc.setTextColor(getResources().getColor(mFgColor));
        }
        //set colors
        RelativeLayout playContainer=(RelativeLayout) rootView.findViewById(R.id.playContainer);
        playContainer.setBackgroundColor(getResources().getColor(mBgColor));
        AbsListView listView= (AbsListView) rootView.findViewById(android.R.id.list);
        listView.setBackgroundColor(getResources().getColor(mBgColor));
        //get screen size
        ScreenSize size=new ScreenSize(getActivity());
        //a bit of logic to handle orientation
        int tWidth=0;
        int tHeight=0;
        if (size.getHeight()<size.getWidth()){
            //landscape, image should be no more than 1/4 screen height
            tHeight=size.getHeight()/4;
        }else{
            //portrait
            tWidth=size.getWidth()/3;
        }
        ImageView imThumb= (ImageView) rootView.findViewById(R.id.imgPlaylistThumb);
        PicassoHelper.loadWeborDrawable(getActivity(), mThumb)
                .placeholder(R.drawable.waiting)
                .resize(tWidth, tHeight)
                .transform(PicassoHelper.getRoundedCornersTranform(getResources().getColor(mFgColor)))
                .into(imThumb)
        ;
        return rootView;
    }


    @Override
    public void onStart() {
        //init basic UI
        if (mPlaylist.getVideos().size()>0){
            Log.d("PF.OVC","PlaylistFragment onStart, non empty playlist");
            Bundle data = new Bundle();
            data.putSerializable(GetPlaylistTask.PLAYLIST, mPlaylist);
            // Send the Bundle of data (our Library) back to the handler (our Activity)
            Message msg = Message.obtain();
            msg.setData(data);
            responseHandler.sendMessage(msg);
        }else {
            Log.d("PF.OVC","PlaylistFragment onStart, empty playlist");
            fetchVideos(responseHandler, mPlayListID, this.getString(R.string.youtubeapikey));
        }
        super.onStart();
    }

    public void fetchVideos(Handler handler, String playlistId, String apiKey) {
        new Thread(new GetPlaylistTask(handler,  playlistId, this.getActivity())).start();
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
        //get the sponsor
        Sponsors sponsors=new Sponsors(getActivity().getResources());
        Sponsor sp=sponsors.getByChannelKey(mChannelID);
        //go through name cleaning
        if (sp.cleanVideos!=null){
            ModelConverter.cleanVideos(playlist, sp.cleanVideos);
        }
        ModelConverter.cachePlaylist(playlist, mChannelID);
        //playlist is cached, assign  to mPlaylist
        mPlaylist=playlist;
        VideosListViewAdapter adapter = new VideosListViewAdapter(getActivity(), playlist.getVideos(), this.getFgColor(),this.getBgColor(),this.highlightColor);
        AbsListView view= (AbsListView) getActivity().findViewById(android.R.id.list);
        view.setAdapter(adapter);

    }


}
