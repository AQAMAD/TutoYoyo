package fr.aqamad.tutoyoyo.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import fr.aqamad.tutoyoyo.R;
import fr.aqamad.tutoyoyo.base.SourceFragment;
import fr.aqamad.youtube.YoutubeChannel;
import fr.aqamad.youtube.YoutubePlaylist;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link YoyoThrowerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class YoyoThrowerFragment extends SourceFragment {
    private static final String TRICK_VIDEOS_PLAYLIST = "PLwMQ2twUtKwLoyRtFpECxUSQ6hoRT2j7a";
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param channelID ID of the current local channel.
     * @return A new instance of fragment MyTutsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static YoyoThrowerFragment newInstance(String channelID) {
        YoyoThrowerFragment fragment = new YoyoThrowerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CHANNELID, channelID);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public YoyoThrowerFragment() {
        // Required empty public constructor
        setForeGroundColor(android.R.color.holo_blue_light);
        setBackGroundColor(android.R.color.black);
        setItemRessourceId(R.layout.playlist_item_box);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_yoyo_thrower, container, false);

    }

    @Override
    public AdapterView GetPlaylistView() {
        return (AdapterView) this.getActivity().findViewById(R.id.yoyothrowPlayLists);
    }

    @Override
    public void prepareChannel() {
        //exclude the "Trick videos" playlist
        YoutubeChannel channel = this.getChannel();
        Iterator<YoutubePlaylist> iter= channel.getPlaylists().iterator();
        while(iter.hasNext()){
            YoutubePlaylist pl=iter.next();
            if(pl.getID().equals(TRICK_VIDEOS_PLAYLIST)){
                iter.remove();
            }
        }

        //Sorting by custom order
        //playlist order
        String[] plsOrder={"Beginner Tricks","Sport Ladder","Quick and Easy tricks",
                "Mounts","Binds, Snap starts and string tension","Snap Start Tutorial Series",
                "Difficulty 1","Difficulty 2","Difficulty 3",
                "Difficulty 4","Difficulty 5","4a/Offstring",
                "Repeaters","How to Build a Combo","Pun Tutorials"};
        final List<String> plsList= Arrays.asList(plsOrder);


        Collections.sort(channel.getPlaylists(), new Comparator<YoutubePlaylist>() {
            @Override
            public int compare(YoutubePlaylist pl1, YoutubePlaylist pl2) {
                int pl1Index = plsList.indexOf(pl1.getTitle());
                int pl2Index = plsList.indexOf(pl2.getTitle());
                return pl1Index - pl2Index;
            }
        });

    }
}
