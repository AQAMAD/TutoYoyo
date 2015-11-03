package fr.aqamad.tutoyoyo.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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

import org.parceler.Parcels;

import java.util.List;

import fr.aqamad.tutoyoyo.R;
import fr.aqamad.tutoyoyo.adapters.PlaylistListViewAdapter;
import fr.aqamad.tutoyoyo.model.ModelConverter;
import fr.aqamad.tutoyoyo.model.Sponsor;
import fr.aqamad.tutoyoyo.model.Sponsors;
import fr.aqamad.tutoyoyo.tasks.GetChannelTask;
import fr.aqamad.tutoyoyo.utils.ScreenSize;
import fr.aqamad.youtube.YoutubeChannel;
import fr.aqamad.youtube.YoutubePlaylist;

public class SourceFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_CHANNELID = "fr.aqamad.tutoyoyo.channel.id";
    public static final String ARG_SPONSOR = "fr.aqamad.tutoyoyo.sponsor.object";
    public static final String ARG_CHANNELOBJECT = "fr.aqamad.tutoyoyo.channel.object";
    private final int DEFAULT_BG=android.R.color.white;
    private final int DEFAULT_FG=android.R.color.black;
    private final int DEFAULT_LAYOUT=R.layout.header_simple_logo_left;
    private final int DEFAULT_ITEM_LAYOUT=R.layout.playlist_item;
    private String mChannelID;
    private Sponsor mSponsor;


    private YoutubeChannel mChannel;

    private OnPlaylistSelectedListener mListener;

    private YoutubeChannel getChannel() {
        return mChannel;
    }

    private void setChannel(YoutubeChannel c) {
        mChannel = c;
    }

    public Activity parentActivity;

    public SourceFragment() {
        super();
        Log.d("SF", "SourceFragment constructor");
    }

    public static SourceFragment newInstance(Sponsor s) {
        SourceFragment fragment = new SourceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CHANNELID, s.channelKey);
        args.putParcelable(ARG_SPONSOR, Parcels.wrap(s));
        fragment.setArguments(args);
        Log.d("MTF", "Source newInstance");
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mChannelID = getArguments().getString(ARG_CHANNELID);
            mSponsor = Parcels.unwrap(getArguments().getParcelable(ARG_SPONSOR));
        }
        if (savedInstanceState != null) {
            mChannelID = savedInstanceState.getString(ARG_CHANNELID);
            mSponsor = Parcels.unwrap(getArguments().getParcelable(ARG_SPONSOR));
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
        View header=inflater.inflate(mSponsor.layoutResId, null);
        frame.addView(header);
        //find the logo
        ImageView imgLogo = (ImageView) header.findViewById(R.id.navigableLogo);
        //we have the header view, let's customize it
        //is it a custom or generic header
        if (mSponsor.layoutResId==R.layout.header_simple_logo_left || mSponsor.layoutResId==R.layout.header_simple_logo_right ){
            //it's a simple, let's go
            ViewGroup backgroundView= (ViewGroup) header.findViewById(R.id.headerBackground);
            TextView txtTitle = (TextView) header.findViewById(R.id.headerTitle);
            TextView txtDescription = (TextView) header.findViewById(R.id.headerDescription);
            //set up colors
            backgroundView.setBackgroundColor(getResources().getColor(mSponsor.backGroundColor));
            txtTitle.setTextColor(getResources().getColor(mSponsor.foreGroundColor));
            txtDescription.setTextColor(getResources().getColor(mSponsor.foreGroundColor));
            //set texts
            txtTitle.setText(mSponsor.name);
            txtDescription.setText(mSponsor.description);
            //set image
            Drawable myDrawable = getResources().getDrawable(mSponsor.logoResId);
            imgLogo.setImageDrawable(myDrawable);
        }
        //set grid style and id
        GridView grd= (GridView) rootView.findViewById(R.id.playlistsGridView);
        ListView lst= (ListView) rootView.findViewById(R.id.playListsListView);
        //set base colors
        grd.setBackgroundColor(getResources().getColor(mSponsor.backGroundColor));
        lst.setBackgroundColor(getResources().getColor(mSponsor.backGroundColor));
        if (mSponsor.displayasBoxes){
            grd.setVisibility(View.VISIBLE);
            lst.setVisibility(View.GONE);
        } else{
            grd.setVisibility(View.GONE);
            lst.setVisibility(View.VISIBLE);
        }
        //set logo interactivity
        if (mSponsor.websiteURL!=null && !mSponsor.websiteURL.equals("")){
            imgLogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse(mSponsor.websiteURL));
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
//        if (mChannel == null) {
            fetchChannel(responseHandler, parentActivity);
//        } else {
//            fillView();
//        }
        super.onStart();
    }

    public void fillView() {
        //build the adapter we need
        Log.d("SF", "SourceFragment fillView : " + mChannelID);
        //how many
        //which itemlayout will depend on the fragment
        PlaylistListViewAdapter adapter = new PlaylistListViewAdapter(this.getActivity(), (List) mChannel.getPlaylists(), new ScreenSize(getActivity()), mSponsor.foreGroundColor, mSponsor.backGroundColor, mSponsor.displayasBoxes);

        Log.d("SF", "SourceFragment fillView playlists to display : " + mChannel.getPlaylists().size());

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
        outState.putParcelable(ARG_SPONSOR, Parcels.wrap(mSponsor));
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
            mListener.OnPlaylistSelected(getChannel().findByKey(plID), mChannel.getID(), mSponsor.backGroundColor, mSponsor.foreGroundColor);
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
        Log.d("SF", "SourceFragment populateListWithVideos playlists to display : " + channel.getPlaylists().size());
        // Because we have created a custom ListView we don't have to worry about setting the adapter in the activity
        // we can just call our custom method with the list of items we want to display
        //postProcessing
        this.setChannel(channel);
        this.prepareChannel();
        //after prepare
        Log.d("SF", "SourceFragment populateListWithVideos afterprepare playlists to display : " + channel.getPlaylists().size());
        //caching
        ModelConverter.cacheChannel(channel);
        fillView();

    }

    public void fetchChannel(Handler handler, Activity act) {
        //here we need to determine if playlist expand is needed (for clyw and similar)
        //based on the sponsors api
        Sponsors sponsors=new Sponsors(getResources());
        Sponsor sp=sponsors.getByChannelKey(getChannelId());
        String[] expandPlaylists=sp.expandablePlaylists;
        if (expandPlaylists!=null){
            new Thread(new GetChannelTask(handler,  getChannelId() , expandPlaylists,act )).start();
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
        if (mSponsor.displayasBoxes){
            return (AdapterView) this.getActivity().findViewById(R.id.playlistsGridView);
        }else {
            return (AdapterView) this.getActivity().findViewById(R.id.playListsListView);
        }
    }

}