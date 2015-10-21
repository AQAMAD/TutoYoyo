package fr.aqamad.tutoyoyo.fragments;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.activeandroid.query.Delete;

import java.util.ArrayList;

import fr.aqamad.tutoyoyo.R;
import fr.aqamad.tutoyoyo.adapters.VideosAdapter;
import fr.aqamad.tutoyoyo.model.ModelConverter;
import fr.aqamad.tutoyoyo.model.TutorialPlaylist;
import fr.aqamad.tutoyoyo.model.TutorialVideo;
import fr.aqamad.tutoyoyo.tasks.GetPlaylistTask;
import fr.aqamad.tutoyoyo.utils.PicassoHelper;
import fr.aqamad.tutoyoyo.utils.ScreenSize;
import fr.aqamad.tutoyoyo.utils.UI;
import fr.aqamad.youtube.YoutubePlaylist;
import fr.aqamad.youtube.YoutubeUtils;
import fr.aqamad.youtube.YoutubeVideo;

/**
 * Created by Gregoire on 19/10/2015.
 */
public class PlaylistFragment extends ListFragment implements View.OnClickListener {
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

    View currentItem;

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
        TextView tvDesc = (TextView) rootView.findViewById(R.id.plDesc);
        tvDesc.setTextColor(getResources().getColor(mFgColor));
        tvTitle.setText(mTitle);

        String description=mDescription;
        if (description.length()>90){
            description=description.substring(0,89)+" (...)";
        }
        tvDesc.setText(description);
        //hide description
        LinearLayout llEx = (LinearLayout) rootView.findViewById(R.id.expandDescription);
        llEx.setVisibility(View.GONE);
        //set long description
        TextView tvLongDesc= (TextView) rootView.findViewById(R.id.plLongDesc);
        tvLongDesc.setText(mDescription);
        tvLongDesc.setTextColor(getResources().getColor(mFgColor));
        //set colors
        RelativeLayout playContainer=(RelativeLayout) rootView.findViewById(R.id.playContainer);
        playContainer.setBackgroundColor(getResources().getColor(mBgColor));
        ListView listView= (ListView) rootView.findViewById(android.R.id.list);
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
                .resize(tWidth,tHeight)
                .into(imThumb)
        ;
        //set up clickListeners
        View clickable=rootView.findViewById(R.id.expanderButton);
        clickable.setOnClickListener(this);


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

        //go through name cleaning
        ModelConverter.cleanPlaylist(playlist);

        ModelConverter.cachePlaylist(playlist, mChannelID);
        //playlist is cached, assign  to mPlaylist
        mPlaylist=playlist;


        VideosAdapter adapter = new VideosAdapter(this, (ArrayList) playlist.getVideos(), this.getFgColor(),this.getBgColor(),this.highlightColor);
        ListView view= (ListView) getActivity().findViewById(android.R.id.list);
        view.setAdapter(adapter);

        //does not work ?!?!
        setListAdapter(adapter);

    }



    private View getListItemFromView(View v){
        ViewGroup parent = (ViewGroup) v.getParent();
        while (parent.getId() != R.id.parentofExpandable) {
            parent = (ViewGroup) parent.getParent();
        }
        return parent;
    }

    private String getVideoIdFromView(View v){
        View parent = getListItemFromView(v);
        TextView vwID = (TextView) parent.findViewById(R.id.vidID);
        return vwID.getText().toString();
    }

    public void expandDescription(View view) {

        LinearLayout llex = (LinearLayout) getActivity().findViewById(R.id.expandDescription);
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

    public void expandVideo(View view) {
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

    public void shareVideo(View view) {
        String vidID = getVideoIdFromView(view);
        //
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, YoutubeUtils.getVideoPlayUrl(vidID));
        intent.setType("text/plain");
        startActivity(intent);
    }

    public void openVideo(View view) {
        View parent= getListItemFromView(view);
        //we got the view, let's just toast something here
        String vidID=getVideoIdFromView(view);
        YoutubeUtils.PlayYoutubeVideo(vidID, this.getActivity());
    }

    public void addLater(View view) {
        //get button tag
        boolean is=(boolean)view.getTag();

        //obtain videoID

        if (is){
            //already later
            //toast some confirmation message and remove if necessary
            currentItem=view;
            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.dialog_title_remove)
                    .setMessage(R.string.dialog_msg_remove_later)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //call model code to remove from list
                            removeFromLocalChannel(PlaylistFragment.this.getActivity().getString(R.string.localLaterKey));
                        }
                    })
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
            currentItem=view;
            new AlertDialog.Builder(this.getActivity())
                    .setTitle(R.string.dialog_title_remove)
                    .setMessage(R.string.dialog_msg_remove_share)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            //call model code to remove from list
                            removeFromLocalChannel(currentItem.getResources().getString(R.string.localSocialKey));
                        }})
                    .setNegativeButton(android.R.string.no, null).show();

        }else{
            addToLocalChannel(view, view.getResources().getString(R.string.localSocialKey));
        }

    }






    public void addFavorite(View view) {
        //get button tag
        boolean is=(boolean)view.getTag();

        //obtain videoID

        if (is){
            //already later
            //toast some confirmation message and remove if necessary
            currentItem=view;

            new AlertDialog.Builder(this.getActivity())
                    .setTitle(R.string.dialog_title_remove)
                    .setMessage(R.string.dialog_msg_remove_favorite)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            //call model code to remove from list
                            removeFromLocalChannel(currentItem.getResources().getString(R.string.localFavoritesKey));
                        }})
                    .setNegativeButton(android.R.string.no, null).show();

        }else{
            //
            addToLocalChannel(view, view.getResources().getString(R.string.localFavoritesKey));
        }

    }


    private void removeFromLocalChannel(String string) {
        ViewGroup parent = (ViewGroup) currentItem.getParent();
        while (parent.getId() != R.id.parentofExpandable) {
            parent = (ViewGroup) parent.getParent();
        }
        TextView vwID = (TextView) parent.findViewById(R.id.vidID);
        String vidID = vwID.getText().toString();
        //let's delete
        TutorialPlaylist later = TutorialPlaylist.getByKey(string);
        new Delete().from(TutorialVideo.class).where("Key = ? and Channel = ?", vidID, later.getId()).execute();
        currentItem.setTag(false);
        UI.colorize((ImageView) currentItem,null);
    }


    private void addToLocalChannel(View view, String string) {
        String vidID = getVideoIdFromView(view);
        //add to local channel
        Log.d("PF.ATLC","Add to local Channel " + vidID);
        //get data from model and not from view
        YoutubeVideo mVid=mPlaylist.findByKey(vidID);
        //add to channel
        TutorialPlaylist later = TutorialPlaylist.getByKey(string);
        //channel is ok, build video model
        TutorialVideo vid = new TutorialVideo();
        vid.channel = later;
        vid.key = vidID;
        //get name from interface
        vid.name = mVid.getTitle();
        vid.description = mVid.getDescription();
        vid.defaultThumbnail = mVid.getDefaultThumb().getUrl();
        vid.mediumThumbnail = mVid.getMediumThumb().getUrl();
        vid.highThumbnail = mVid.getHighThumb().getUrl();
        vid.duration= mVid.getDuration();
        vid.save();
        view.setTag(true);
        UI.colorize((ImageView) view, highlightColor);
        Snackbar.make(view, R.string.msg_video_added, Snackbar.LENGTH_SHORT).show();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.expanderButton:
                expandDescription(v);
                break;
            case R.id.imgVidThumb:
                openVideo(v);
                break;
            case R.id.vidName:
                openVideo(v);
                break;
            case R.id.btnShare:
                addShare(v);
                break;
            case R.id.btnFavorites:
                addFavorite(v);
                break;
            case R.id.btnLater:
                addLater(v);
                break;
            case R.id.expandVideoButton:
                expandVideo(v);
                break;
            case R.id.shareVideoButton:
                shareVideo(v);
                break;

        }
    }


    public interface Callbacks{
        public void onPlayItem(YoutubeVideo vid);
        public void onShareItem(YoutubeVideo vid);
        public void onAddLater(YoutubeVideo vid);
        public void onAddMy(YoutubeVideo vid);
        public void onAddSocial(YoutubeVideo vid);
    }

}
