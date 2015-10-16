package fr.aqamad.tutoyoyo.base;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import fr.aqamad.tutoyoyo.PlaylistActivity;
import fr.aqamad.tutoyoyo.R;
import fr.aqamad.tutoyoyo.adapters.PlaylistsAdapter;
import fr.aqamad.tutoyoyo.model.TutorialPlaylist;
import fr.aqamad.tutoyoyo.model.TutorialSource;
import fr.aqamad.tutoyoyo.model.TutorialVideo;
import fr.aqamad.tutoyoyo.tasks.GetChannelTask;
import fr.aqamad.youtube.YoutubeChannel;
import fr.aqamad.youtube.YoutubePlaylist;
import fr.aqamad.youtube.YoutubeVideo;

public abstract class SourceFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_CHANNELID = "fr.aqamad.tutoyoyo.channel.id";
    public static final String ARG_CHANNELOBJECT = "fr.aqamad.tutoyoyo.channel.object";
    private String mChannelID;

    private int foreGroundColor= R.color.my;
    private int backGroundColor=android.R.color.white;

    public int getItemRessourceId() {
        return itemRessourceId;
    }

    public void setItemRessourceId(int itemRessourceId) {
        this.itemRessourceId = itemRessourceId;
    }

    private int itemRessourceId=R.layout.playlist_item;

    private YoutubeChannel mChannel;

    public abstract AdapterView GetPlaylistView();

    private OnFragmentInteractionListener mListener;

    public int getForeGroundColor() {
        return foreGroundColor;
    }

    public void setForeGroundColor(int i) {
        foreGroundColor=i;
    }

    public int getBackGroundColor() {
        return backGroundColor;
    }

    public void setBackGroundColor(int i) {
        backGroundColor=i;
    }

    public YoutubeChannel getChannel() {
        return mChannel;
    }

    public void setChannel(YoutubeChannel c) {
        mChannel=c;
    }

    public Activity parentActivity;

    public SourceFragment() {
        super();
        Log.d("SF","SourceFragment constructor");
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mChannelID = getArguments().getString(ARG_CHANNELID);
        }
        if (savedInstanceState != null) {
            mChannelID = savedInstanceState.getString(ARG_CHANNELID);
            mChannel = (YoutubeChannel) savedInstanceState.getSerializable(ARG_CHANNELOBJECT);
            Log.d("SF","SourceFragment onCreate with saved state");
        }else{
            Log.d("SF", "SourceFragment onCreate");
        }
    }

    @Override
    public void onStart() {
        Log.d("SF", "SourceFragment onStart");
        if (mChannel == null){
            fetchChannel(responseHandler,parentActivity);
        } else {
            fillView();
        }
        super.onStart();
    }

    public void fillView() {
        //build the adapter we need
        //which itemlayout will depend on the fragment
        PlaylistsAdapter adapter = new PlaylistsAdapter(parentActivity, (ArrayList) mChannel.getPlaylists(), this.getForeGroundColor(),this.getBackGroundColor(),this.getItemRessourceId());
        AdapterView listView=GetPlaylistView();
        listView.setAdapter(adapter);
        //hook up event listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //we got the view, let's just toast something here
                //we will choose the implementation class dependant on the base fragment class type
                //class identification magic
                TextView vwID = (TextView) view.findViewById(R.id.plID);
                String plID = vwID.getText().toString();
                Log.d("SFFV","In sourceFragment fillView, onItemClickListener called for ID : " + plID);
                Intent intent = new Intent(parentActivity, PlaylistActivity.class);

                for (int i = 0; i < getChannel().getPlaylists().size(); i++) {
                    if (plID.equals(getChannel().getPlaylists().get(i).getID())) {
                        intent.putExtra(PlaylistActivity.PLAYLIST, getChannel().getPlaylists().get(i));
                    }
                }

                //intent.putExtra(PlaylistActivity.PLAYLIST, plID);
                intent.putExtra(PlaylistActivity.BGCOLOR, getBackGroundColor());
                intent.putExtra(PlaylistActivity.FGCOLOR, getForeGroundColor());
                intent.putExtra(PlaylistActivity.CHANNEL, mChannel.getID());
                startActivity(intent);

                parentActivity.overridePendingTransition(R.anim.activity_out, R.anim.activity_in);
            }
        });
    }

    public String getChannelId() {
        Log.d("SF", "SourceFragment getChannelID : " + mChannelID);
        return mChannelID;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save the fragment's state here
        outState.putString(ARG_CHANNELID, mChannelID);
        outState.putSerializable(ARG_CHANNELOBJECT, mChannel);
        Log.d("SF", "SourceFragment onSaveInstanceState : " + mChannelID);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(View view);
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onPlaylistSelected(View view) {
        if (mListener != null) {
            mListener.onFragmentInteraction(view);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        parentActivity=activity;
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            //throw new ClassCastException(activity.toString()
            //        + " must implement OnFragmentInteractionListener");
        }
        Log.d("SF", "SourceFragment onAttach : " + mChannelID);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        Log.d("SF", "SourceFragment onDetach : " + mChannelID);
    }

    // This is the handler that receives the response when the YouTube task has finished
    Handler responseHandler = new Handler() {
        public void handleMessage(Message msg) {
            populateListWithVideos(msg);
        };
    };

    private void populateListWithVideos(Message msg) {
        // Retreive the videos are task found from the data bundle sent back
        YoutubeChannel channel = (YoutubeChannel) msg.getData().get(GetChannelTask.CHANNEL);
        // Because we have created a custom ListView we don't have to worry about setting the adapter in the activity
        // we can just call our custom method with the list of items we want to display
        //postProcessing
        this.setChannel(channel);
        this.prepareChannel();
        //caching
        cacheChannel(channel);
        fillView();

    }

    private void cacheChannel(YoutubeChannel channel) {
        Log.d("SFCC", "Called SourceFragment cacheChannel for " + channel.getID() + " aka " + channel.getTitle());
        TutorialSource source = TutorialSource.getByKey(channel.getID());
        if (source == null) {
            Log.d("SFCC","Channel not found, caching");
            //we did a lookup, store in the database to avoid reparsing json
            source = new TutorialSource();
            source.name = channel.getTitle();
            source.description = channel.getDescription();
            source.lastRefreshed = new Date();
            source.key = channel.getID();
            source.save();
            Log.d("SFCC", "Channel cached");
            //now iterate to store playlists
            Log.d("SFCC", "Caching playlists");
            for (YoutubePlaylist pl :
                    channel.getPlaylists()) {
                TutorialPlaylist ch = new TutorialPlaylist();
                Log.d("SFCC", "Caching playlist" + pl.getID() + " aka " + pl.getTitle());
                ch.source = source;
                ch.key = pl.getID();
                ch.name = pl.getTitle();
                ch.description = pl.getDescription();
                ch.fetchedAt = new Date();
                ch.publishedAt = pl.getPublishedAt();
                ch.defaultThumbnail = pl.getDefaultThumb().getUrl().toString();
                ch.mediumThumbnail = pl.getMediumThumb().getUrl().toString();
                ch.highThumbnail = pl.getHighThumb().getUrl().toString();
                ch.save();
                Log.d("SFCC", "Playlist " + ch.name + " cached");
                Log.d("SFCC", "Caching videos");
                //if the playlists have videos, we store them too
                for (YoutubeVideo vid :
                    pl.getVideos()  ) {
                    //repeat the deal
                    TutorialVideo vi = new TutorialVideo();
                    Log.d("SFCC", "Caching video " + vid.getID() + " aka " + vid.getTitle());
                    vi.channel = ch;
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
                    Log.d("SFCC", "Video " + vi.name + " cached");
                }
            }

        }else{
            Log.d("SFCC", "Source " + source.name + " found");
        }
    }

    public void fetchChannel(Handler handler,Activity act) {
        new Thread(new GetChannelTask(handler,  getChannelId() ,act)).start();
    }

    public void prepareChannel() {
        //todo : par défaut, on trie les playlists
        YoutubeChannel channel=this.getChannel();
        //Sorting by title
        Collections.sort(channel.getPlaylists(), new Comparator<YoutubePlaylist>() {
            @Override
            public int compare(YoutubePlaylist pl1, YoutubePlaylist pl2) {
                return pl1.getTitle().compareTo(pl2.getTitle());
            }
        });
    }

}