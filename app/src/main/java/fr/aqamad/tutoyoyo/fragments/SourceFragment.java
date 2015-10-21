package fr.aqamad.tutoyoyo.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import fr.aqamad.tutoyoyo.R;
import fr.aqamad.tutoyoyo.adapters.PlaylistsAdapter;
import fr.aqamad.tutoyoyo.model.ModelConverter;
import fr.aqamad.tutoyoyo.tasks.GetChannelTask;
import fr.aqamad.tutoyoyo.utils.ScreenSize;
import fr.aqamad.youtube.YoutubeChannel;
import fr.aqamad.youtube.YoutubePlaylist;

public class SourceFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_CHANNELID = "fr.aqamad.tutoyoyo.channel.id";
    public static final String ARG_HEADERLAYOUT = "fr.aqamad.tutoyoyo.channel.headerlayout";
    public static final String ARG_BGCOLOR = "fr.aqamad.tutoyoyo.channel.background";
    public static final String ARG_FGCOLOR = "fr.aqamad.tutoyoyo.channel.foreground";
    public static final String ARG_VIEWASGRID = "fr.aqamad.tutoyoyo.channel.viewasgrid";
    public static final String ARG_CHANNELOBJECT = "fr.aqamad.tutoyoyo.channel.object";
    public static final String ARG_LOGONAVURL = "fr.aqamad.tutoyoyo.channel.logonavigationurl";
    private String mChannelID;

    private int foreGroundColor = R.color.my;
    private int backGroundColor = android.R.color.white;

    protected int headerLayoutId = R.layout.header_mytuts;

    protected boolean viewAsGrid = false;

    protected String logoNavigationUrl = "";

    public int getItemRessourceId() {
        return itemRessourceId;
    }

    public void setItemRessourceId(int itemRessourceId) {
        this.itemRessourceId = itemRessourceId;
    }

    private int itemRessourceId = R.layout.playlist_item;

    private YoutubeChannel mChannel;

    private OnPlaylistSelectedListener mListener;

    public int getForeGroundColor() {
        return foreGroundColor;
    }

    public void setForeGroundColor(int i) {
        foreGroundColor = i;
    }

    public int getBackGroundColor() {
        return backGroundColor;
    }

    public void setBackGroundColor(int i) {
        backGroundColor = i;
    }

    public YoutubeChannel getChannel() {
        return mChannel;
    }

    public void setChannel(YoutubeChannel c) {
        mChannel = c;
    }

    public Activity parentActivity;

    public SourceFragment() {
        super();
        Log.d("SF", "SourceFragment constructor");
    }

    public static SourceFragment newInstance(String channelID,int layoutHeader, boolean showasGrid ) {
        SourceFragment fragment = new SourceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CHANNELID, channelID);
        args.putInt(ARG_HEADERLAYOUT, layoutHeader);
        args.putInt(ARG_BGCOLOR, android.R.color.white);
        args.putInt(ARG_FGCOLOR, R.color.my);
        args.putString(ARG_LOGONAVURL, "");
        args.putBoolean(ARG_VIEWASGRID, showasGrid);
        fragment.setArguments(args);
        Log.d("MTF", "Source newInstance");
        return fragment;
    }

    public static SourceFragment newInstance(String channelID,
                                             int layoutHeader,
                                             boolean showasGrid,
                                             int backGroundColor,
                                             int  foreGroundColor,
                                             String logoNavigationUrl) {
        SourceFragment fragment = new SourceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CHANNELID, channelID);
        args.putInt(ARG_HEADERLAYOUT, layoutHeader);
        args.putInt(ARG_BGCOLOR, backGroundColor);
        args.putInt(ARG_FGCOLOR, foreGroundColor);
        args.putString(ARG_LOGONAVURL, logoNavigationUrl);
        args.putBoolean(ARG_VIEWASGRID, showasGrid);
        fragment.setArguments(args);
        Log.d("MTF", "Source newInstance w/c");
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mChannelID = getArguments().getString(ARG_CHANNELID);
            headerLayoutId = getArguments().getInt(ARG_HEADERLAYOUT);
            backGroundColor = getArguments().getInt(ARG_BGCOLOR);
            foreGroundColor = getArguments().getInt(ARG_FGCOLOR);
            logoNavigationUrl = getArguments().getString(ARG_LOGONAVURL);
            viewAsGrid = getArguments().getBoolean(ARG_VIEWASGRID);
        }
        if (savedInstanceState != null) {
            mChannelID = savedInstanceState.getString(ARG_CHANNELID);
            headerLayoutId = savedInstanceState.getInt(ARG_HEADERLAYOUT);
            backGroundColor = savedInstanceState.getInt(ARG_BGCOLOR);
            foreGroundColor = savedInstanceState.getInt(ARG_FGCOLOR);
            viewAsGrid = savedInstanceState.getBoolean(ARG_VIEWASGRID);
            logoNavigationUrl = savedInstanceState.getString(ARG_LOGONAVURL);
            mChannel = (YoutubeChannel) savedInstanceState.getSerializable(ARG_CHANNELOBJECT);
            Log.d("SF", "SourceFragment onCreate with saved state");
        } else {
            Log.d("SF", "SourceFragment onCreate");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.fragment_source, container, false);
        //get header frame and dynamically load it
        FrameLayout frame= (FrameLayout) rootView.findViewById(R.id.frameHeader);
        //inflate header
        View header=inflater.inflate(headerLayoutId, null);
        frame.addView(header);
        //set grid style and id
        GridView grd= (GridView) rootView.findViewById(R.id.playlistsGridView);
        ListView lst= (ListView) rootView.findViewById(R.id.playListsListView);
        //set base colors
        grd.setBackgroundColor(getResources().getColor(backGroundColor));
        lst.setBackgroundColor(getResources().getColor(backGroundColor));
        if (viewAsGrid){
            grd.setVisibility(View.VISIBLE);
            itemRessourceId=R.layout.playlist_item_box;
            lst.setVisibility(View.GONE);
        } else{
            grd.setVisibility(View.GONE);
            lst.setVisibility(View.VISIBLE);
            itemRessourceId=R.layout.playlist_item;
        }
        //set logo interactivity
        if (!logoNavigationUrl.equals("")){
        ImageView i = (ImageView) rootView.findViewById(R.id.navigableLogo);
        i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(logoNavigationUrl));
                startActivity(intent);
            }
        });
        }
        //done
        return rootView;
    }

    @Override
    public void onStart() {
        Log.d("SF", "SourceFragment onStart");
        if (mChannel == null) {
            fetchChannel(responseHandler, parentActivity);
        } else {
            fillView();
        }
        super.onStart();
    }

    public void fillView() {
        //build the adapter we need
        Log.d("SF", "SourceFragment fillView : " + mChannelID);
        //which itemlayout will depend on the fragment
        PlaylistsAdapter adapter = new PlaylistsAdapter(parentActivity, (ArrayList) mChannel.getPlaylists(), new ScreenSize(getActivity()), this.getForeGroundColor(), this.getBackGroundColor(), this.getItemRessourceId());
        AdapterView listView = GetPlaylistView();
        listView.setAdapter(adapter);
        //hook up event listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //we got the view, let's just toast something here
                //we will choose the implementation class dependant on the base fragment class type
                //class identification magic
                SourceFragment.this.onPlaylistSelected(view);
                //
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
        outState.putInt(ARG_HEADERLAYOUT, headerLayoutId);
        outState.putInt(ARG_BGCOLOR, backGroundColor);
        outState.putInt(ARG_FGCOLOR, foreGroundColor);
        outState.putString(ARG_LOGONAVURL, logoNavigationUrl);
        outState.putBoolean(ARG_VIEWASGRID, viewAsGrid);
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
    public interface OnPlaylistSelectedListener {
        // TODO: Update argument type and name
        public void OnPlaylistSelected(YoutubePlaylist mPlaylist,String mChannelID,int mBgColor,int mFgColor);
    }


    public void onPlaylistSelected(View view) {
        if (mListener != null) {
            TextView vwID = (TextView) view.findViewById(R.id.plID);
            String plID = vwID.getText().toString();
            mListener.OnPlaylistSelected(getChannel().findByKey(plID), mChannel.getID(), getBackGroundColor(), getForeGroundColor());
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        parentActivity = activity;
        try {
            mListener = (OnPlaylistSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnPlaylistSelectedListener");
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
        }

        ;
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
        ModelConverter.cacheChannel(channel);
        fillView();

    }

    public void fetchChannel(Handler handler, Activity act) {
        //here we need to determine if playlist expand is needed (for clyw and similar)
        String expandPlaylist=ModelConverter.getExpandPlayList(getChannelId(),act);
        if (expandPlaylist!=null){
            new Thread(new GetChannelTask(handler,  getChannelId() , expandPlaylist,act )).start();
        }else{
            new Thread(new GetChannelTask(handler, getChannelId(), act)).start();
        }
    }

    public void prepareChannel() {
        //todo : par défaut, on trie les playlists, voir pour modelConverter tout ca...
        YoutubeChannel channel = this.getChannel();
        //Sorting by title
        ModelConverter.prepareChannel(channel,getActivity());
    }

    public AdapterView GetPlaylistView(){
        if (viewAsGrid){
            return (AdapterView) this.getActivity().findViewById(R.id.playlistsGridView);
        }else {
            return (AdapterView) this.getActivity().findViewById(R.id.playListsListView);
        }
    }

}