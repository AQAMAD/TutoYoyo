package fr.aqamad.tutoyoyo.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import fr.aqamad.tutoyoyo.R;
import fr.aqamad.tutoyoyo.base.PrefetchFragment;
import fr.aqamad.tutoyoyo.base.SourceFragment;
import fr.aqamad.tutoyoyo.tasks.GetYouTubeChannelTask;
import fr.aqamad.youtube.YoutubeChannel;
import fr.aqamad.youtube.YoutubePlaylist;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link YoyoExpertFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link YoyoExpertFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class YoyoExpertFragment extends PrefetchFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static String YOYOEXPERT_CHANNEL="YoyoExpertChannel";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyTutsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static YoyoExpertFragment newInstance(String param1, String param2) {
        YoyoExpertFragment fragment = new YoyoExpertFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public YoyoExpertFragment() {
        // Required empty public constructor
        setForeGroundColor(R.color.yoyoExpert);
        setBackGroundColor(android.R.color.black);
    }

    public YoyoExpertFragment(Activity mainActivity) {
        super(mainActivity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_yoyo_expert, container, false);
    }

    @Override
    public String GetToutubeId() {
        return YOYOEXPERT_CHANNEL;
    }

    @Override
    public ListView GetPlaylistView() {
        return (ListView) this.getActivity().findViewById(R.id.yoyoexpertPlayLists);
    }

    @Override
    public void prepareChannel() {
        YoutubeChannel channel=this.getChannel();

        Iterator<YoutubePlaylist> iter= channel.getPlaylists().iterator();
        while(iter.hasNext()){
            YoutubePlaylist pl=iter.next();
            //clean the names
            pl.setTitle(pl.getTitle().replace("Expert Village Yo Yo Tricks: ",""));
            pl.setTitle(pl.getTitle().replace("Expert Village Yo Yo tricks: ",""));
            pl.setTitle(pl.getTitle().replace("Expert village Yo Yo Tricks: ",""));
            pl.setTitle(pl.getTitle().replace("Expert Village: ",""));
            //pikotaku a une playlist en trop, on ne conserve que celles que l'on connait
            if(pl.getID().equals("LLjt9-TsmzQl1P9Vpt8sHYCA")){
                iter.remove();
            }
            if(pl.getID().equals("FLjt9-TsmzQl1P9Vpt8sHYCA")){
                iter.remove();
            }
        }

        //Sorting by title
        Collections.sort(channel.getPlaylists(), new Comparator<YoutubePlaylist>() {
            @Override
            public int compare(YoutubePlaylist pl1, YoutubePlaylist pl2) {
                return pl1.getTitle().compareTo(pl2.getTitle());
            }
        });
    }


}
