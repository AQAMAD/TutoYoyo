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
 * {@link YoyoExpertFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link YoyoExpertFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class YoyoExpertFragment extends SourceFragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param channelID Parameter 1.
     * @return A new instance of fragment MyTutsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static YoyoExpertFragment newInstance(String channelID) {
        YoyoExpertFragment fragment = new YoyoExpertFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CHANNELID, channelID);
        fragment.setArguments(args);
        return fragment;
    }

    public YoyoExpertFragment() {
        // Required empty public constructor
        setForeGroundColor(R.color.yoyoExpert);
        setBackGroundColor(android.R.color.black);
        setItemRessourceId(R.layout.box_playlist_item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_yoyo_expert, container, false);
    }

    @Override
    public AdapterView GetPlaylistView() {
        return (AdapterView) this.getActivity().findViewById(R.id.yoyoexpertPlayLists);
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

        //Sorting by custom order
        //playlist order
        String[] plsOrder={"Yo-Yo Techniques for the Beginner","Basic","Yo Yo Maintenance",
                "Intermediate","Advanced (Part 1)","Advanced (Part 2)",
                "Expert (Part 1)","Expert (Part 2)","Master",
                "Looping","Offstring","Counterweight",
                "Double hand looping (2A)"};
        final List<String> plsList= Arrays.asList(plsOrder);


        Collections.sort(channel.getPlaylists(), new Comparator<YoutubePlaylist>() {
            @Override
            public int compare(YoutubePlaylist pl1, YoutubePlaylist pl2) {
                int pl1Index=plsList.indexOf(pl1.getTitle());
                int pl2Index=plsList.indexOf(pl2.getTitle());
                return pl1Index-pl2Index;
            }
        });


    }


}
