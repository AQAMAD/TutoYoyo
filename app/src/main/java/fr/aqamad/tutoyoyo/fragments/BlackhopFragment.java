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
 * Use the {@link BlackhopFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BlackhopFragment extends SourceFragment {
    private static final String MEDIUM_PLAYLIST = "PL8C046415EB38C4F4";
    private static final String BEGINNER_PLAYLIST = "PL31896775C716B8B3";
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param channelID ID of the current local channel.
     * @return A new instance of fragment MyTutsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BlackhopFragment newInstance(String channelID) {
        BlackhopFragment fragment = new BlackhopFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CHANNELID, channelID);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public BlackhopFragment() {
        // Required empty public constructor
        setForeGroundColor(android.R.color.white);
        setBackGroundColor(android.R.color.black);
        setItemRessourceId(R.layout.playlist_item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blackhop, container, false);

    }

    @Override
    public AdapterView GetPlaylistView() {
        return (AdapterView) this.getActivity().findViewById(R.id.blackhopPlayLists);
    }

    @Override
    public void prepareChannel() {
        //exclude the "Trick videos" playlist
        YoutubeChannel channel = this.getChannel();
        Iterator<YoutubePlaylist> iter= channel.getPlaylists().iterator();
        while(iter.hasNext()){
            YoutubePlaylist pl=iter.next();
            if(!(pl.getID().equals(BEGINNER_PLAYLIST) || pl.getID().equals(MEDIUM_PLAYLIST)) ){
                iter.remove();
            }
        }

        //Sorting by custom order
        //playlist order
        String[] plsOrder={"Tutos yoyo débutant","Tutos yoyo intermédiaire"};
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
