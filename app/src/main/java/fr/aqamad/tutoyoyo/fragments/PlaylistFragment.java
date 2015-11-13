package fr.aqamad.tutoyoyo.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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

import com.mikepenz.iconics.IconicsDrawable;

import java.util.List;

import fr.aqamad.commons.youtube.YoutubePlaylist;
import fr.aqamad.tutoyoyo.Application;
import fr.aqamad.tutoyoyo.R;
import fr.aqamad.tutoyoyo.adapters.VideosListViewAdapter;
import fr.aqamad.tutoyoyo.model.ModelConverter;
import fr.aqamad.tutoyoyo.model.Sponsor;
import fr.aqamad.tutoyoyo.model.Sponsors;
import fr.aqamad.tutoyoyo.model.TutorialPlaylist;
import fr.aqamad.tutoyoyo.tasks.GetPlaylistTask;
import fr.aqamad.tutoyoyo.utils.PicassoHelper;
import fr.aqamad.tutoyoyo.utils.ScreenSize;
import fr.aqamad.tutoyoyo.utils.UI;

/**
 * Created by Gregoire on 19/10/2015.
 */
public class PlaylistFragment extends Fragment implements View.OnClickListener {
    public static final String PLAYLIST = "fr.aqamad.youtube.playlist";
    public static final String CHANNEL = "fr.aqamad.youtube.playlist.channel";
    public static final String BGCOLOR="fr.aqamad.youtube.playlist.bgcolor";
    public static final String FGCOLOR="fr.aqamad.youtube.playlist.fgcolor";

    private String mPlayListID;
    private int mBgColor;
    private int mFgColor;
    private int highlightColor=android.R.color.holo_green_light;
    private String mTitle;
    private String mDescription;
    private String mThumb;
    private YoutubePlaylist mPlaylist;
    private String mChannelID;
    private boolean isFavorites = false;
    private boolean isSocial = false;
    private boolean isLater = false;
    // This is the handler that receives the response when the YouTube task has finished
    Handler responseHandler = new Handler() {
        public void handleMessage(Message msg) {
            populateListWithVideos(msg);
        }
    };
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
            //initialize booleans from context
            isFavorites = mPlayListID.equals(getResources().getString(R.string.LOCAL_FAVORITES_PLAYLIST));
            isLater = mPlayListID.equals(getResources().getString(R.string.LOCAL_LATER_PLAYLIST));
            isSocial = mPlayListID.equals(getResources().getString(R.string.LOCAL_SOCIAL_PLAYLIST));


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
        //set up the buttons
        ImageView btnFav = (ImageView) rootView.findViewById(R.id.btnFavorites);
        ImageView btnSha = (ImageView) rootView.findViewById(R.id.btnShare);
        ImageView btnWat = (ImageView) rootView.findViewById(R.id.btnLater);
        ImageView btnSeen = (ImageView) rootView.findViewById(R.id.btnSeen);
        ImageView btnShareList = (ImageView) rootView.findViewById(R.id.shareVideoButton);
        //set up statuses for the buttons
        UI.colorizeAndTag(btnSeen, TutorialPlaylist.isSeen(mPlayListID));
        UI.colorizeAndTag(btnSha, TutorialPlaylist.isOther(mPlayListID, getString(R.string.LOCAL_SOCIAL_PLAYLIST)));
        UI.colorizeAndTag(btnWat, TutorialPlaylist.isOther(mPlayListID, getString(R.string.LOCAL_LATER_PLAYLIST)));
        UI.colorizeAndTag(btnFav, TutorialPlaylist.isOther(mPlayListID, getString(R.string.LOCAL_FAVORITES_PLAYLIST)));
        //set up listeners for the buttons
        btnSha.setOnClickListener(this);
        btnFav.setOnClickListener(this);
        btnWat.setOnClickListener(this);
        btnSeen.setOnClickListener(this);
        btnShareList.setOnClickListener(this);
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
        Sponsors sponsors = Application.getSponsors();
        Sponsor sp=sponsors.getByChannelKey(mChannelID);
        //go through name cleaning
        if (sp.cleanVideos!=null){
            ModelConverter.cleanVideos(playlist, sp.cleanVideos);
        }
        ModelConverter.cachePlaylist(playlist, mChannelID);
        //playlist is cached, assign  to mPlaylist
        mPlaylist=playlist;
        displayItems(playlist);

    }

    private void displayItems(YoutubePlaylist playlist) {
        VideosListViewAdapter adapter = new VideosListViewAdapter(getActivity(), playlist.getVideos(), this.getFgColor(),this.getBgColor(),this.highlightColor);
        AbsListView view= (AbsListView) getActivity().findViewById(android.R.id.list);
        view.setAdapter(adapter);
        //details, if 0 videos in playlist, can't share or mark as seen
        morphHeader(playlist);
    }

    private void morphHeader(YoutubePlaylist playlist) {
        ImageView btnFav = (ImageView) getActivity().findViewById(R.id.btnFavorites);
        ImageView btnSha = (ImageView) getActivity().findViewById(R.id.btnShare);
        ImageView btnWat = (ImageView) getActivity().findViewById(R.id.btnLater);
        ImageView btnSeen = (ImageView) getActivity().findViewById(R.id.btnSeen);
        ImageView btnShareList = (ImageView) getActivity().findViewById(R.id.shareVideoButton);
        //display according to local lists
        if (isFavorites) {
            //make it into a trashcan
            btnFav.setImageDrawable(new IconicsDrawable(btnFav.getContext(), "faw-trash").color(Color.LTGRAY));
            UI.colorize(btnFav, null);
            //hide transfer options
            btnSha.setVisibility(View.GONE);
            btnWat.setVisibility(View.GONE);
            //see how it goes from there
        }
        if (isSocial) {
            //make it into a trashcan
            btnSha.setImageDrawable(new IconicsDrawable(btnFav.getContext(), "faw-trash").color(Color.LTGRAY));
            UI.colorize(btnSha, null);
            //hide transfer options
            btnFav.setVisibility(View.GONE);
            btnWat.setVisibility(View.GONE);
            //see how it goes from there
        }
        if (isLater) {
            //make it into a trashcan
            btnWat.setImageDrawable(new IconicsDrawable(btnFav.getContext(), "faw-trash").color(Color.LTGRAY));
            UI.colorize(btnWat, null);
            //hide transfer options
            btnSha.setVisibility(View.GONE);
            btnFav.setVisibility(View.GONE);
            //see how it goes from there
        }
        boolean hasVids = (playlist.getVideos().size() > 0);
        if (!hasVids) {
            btnSeen.setVisibility(View.GONE);
            btnShareList.setVisibility(View.GONE);
            if (isFavorites) {
                btnFav.setVisibility(View.GONE);
            }
            if (isSocial) {
                btnSha.setVisibility(View.GONE);
            }
            if (isLater) {
                btnWat.setVisibility(View.GONE);
            }
        } else {
            btnSeen.setVisibility(View.VISIBLE);
            btnShareList.setVisibility(View.VISIBLE);
            if (isFavorites) {
                btnFav.setVisibility(View.VISIBLE);
            }
            if (isSocial) {
                btnSha.setVisibility(View.VISIBLE);
            }
            if (isLater) {
                btnWat.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSeen:
                markSeen(v);
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
            case R.id.shareVideoButton:
                sharePlaylist(v);
                break;
        }
    }

    private void sharePlaylist(View v) {
        TutorialPlaylist pl = TutorialPlaylist.getByKey(mPlayListID);
        //build array
        List<String> videoUrls = pl.getVideoUrls();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, TextUtils.join("\n", videoUrls));
        intent.setType("text/plain");
        startActivity(intent);
    }

    private void addLater(View v) {
        Boolean status = (Boolean) v.getTag();
        if (status) {
            confirmMarkPlaylist(v, v.getContext().getString(R.string.dialog_title_remove), v.getContext().getString(R.string.dialog_message_removeall_later));
        } else {
            confirmMarkPlaylist(v, v.getContext().getString(R.string.dialog_title_add), v.getContext().getString(R.string.dialog_message_addall_later));
        }
    }

    private void addFavorite(View v) {
        Boolean status = (Boolean) v.getTag();
        if (status) {
            confirmMarkPlaylist(v, v.getContext().getString(R.string.dialog_title_remove), v.getContext().getString(R.string.dialog_message_removeall_favorites));
        } else {
            confirmMarkPlaylist(v, v.getContext().getString(R.string.dialog_title_add), v.getContext().getString(R.string.dialog_message_addall_favorites));
        }
    }

    private void addShare(View v) {
        Boolean status = (Boolean) v.getTag();
        if (status) {
            confirmMarkPlaylist(v, v.getContext().getString(R.string.dialog_title_remove), v.getContext().getString(R.string.dialog_message_removeall_share));
        } else {
            confirmMarkPlaylist(v, v.getContext().getString(R.string.dialog_title_add), v.getContext().getString(R.string.dialog_message_addall_share));
        }
    }

    private void markSeen(View v) {
        Boolean status = (Boolean) v.getTag();
        if (status) {
            confirmMarkPlaylist(v, v.getContext().getString(R.string.dialog_title_mark), v.getContext().getString(R.string.dialog_message_mark_seen));
        } else {
            confirmMarkPlaylist(v, v.getContext().getString(R.string.dialog_title_mark), v.getContext().getString(R.string.dialog_message_mark_unseen));
        }
    }

    private void confirmMarkPlaylist(View v, String title, String message) {
        final String theKey = mPlayListID;
        final ImageView theview = (ImageView) v;
        new AlertDialog.Builder(v.getContext())
                .setTitle(title)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //call model code to remove from list
                        markAs(theview, theKey);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void markAs(ImageView theview, String theKey) {
        Log.d("PF.MA", "Mark as attempted, key was : " + theKey);
        TutorialPlaylist list = TutorialPlaylist.getByKey(theKey);
        //remember if refresh is needed
        boolean refreshNeeded = false;
        //base on tag to know status to set
        Boolean status = (Boolean) theview.getTag();
        switch (theview.getId()) {
            case R.id.btnSeen:
                //add or remove to list
                list.markSeen(!status);
                break;
            case R.id.btnShare:
                if (!status) {
                    list.addToLocal(getString(R.string.LOCAL_SOCIAL_PLAYLIST));
                } else {
                    list.removeFromLocal(getString(R.string.LOCAL_SOCIAL_PLAYLIST));
                }
                if (isSocial) {
                    refreshNeeded = true;
                }
                break;
            case R.id.btnFavorites:
                if (!status) {
                    list.addToLocal(getString(R.string.LOCAL_FAVORITES_PLAYLIST));
                } else {
                    list.removeFromLocal(getString(R.string.LOCAL_FAVORITES_PLAYLIST));
                }
                if (isFavorites) {
                    refreshNeeded = true;
                }
                break;
            case R.id.btnLater:
                if (!status) {
                    list.addToLocal(getString(R.string.LOCAL_LATER_PLAYLIST));
                } else {
                    list.removeFromLocal(getString(R.string.LOCAL_LATER_PLAYLIST));
                }
                if (isLater) {
                    refreshNeeded = true;
                }
                break;
        }
        //Snackbar.make(this, R.string.msg_video_removed, Snackbar.LENGTH_SHORT).show();
        UI.colorizeAndTag(theview, !status);
        if (refreshNeeded) {
            //doesn't work
            //fetchVideos(responseHandler, mPlayListID, this.getString(R.string.youtubeapikey));
            //so do by hand on cached content
            mPlaylist.getVideos().clear();
        }
        displayItems(mPlaylist);
    }
}
