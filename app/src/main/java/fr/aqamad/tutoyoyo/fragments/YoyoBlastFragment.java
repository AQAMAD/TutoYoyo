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

import fr.aqamad.tutoyoyo.R;
import fr.aqamad.tutoyoyo.base.LocalFragment;
import fr.aqamad.tutoyoyo.base.SourceFragment;
import fr.aqamad.tutoyoyo.base.YoutubeFragment;
import fr.aqamad.tutoyoyo.tasks.GetYouTubeChannelTask;
import fr.aqamad.youtube.YoutubeChannel;
import fr.aqamad.youtube.YoutubePlaylist;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link YoyoBlastFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link YoyoBlastFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class YoyoBlastFragment extends YoutubeFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static String YOYOBLAST_CHANNEL="UCJVkKRR1hf0aSZzrELEATRg";

    public YoyoBlastFragment(Activity mainActivity) {
        super(mainActivity);
    }

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment YoyoBlastFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static YoyoBlastFragment newInstance(String param1, String param2) {
        YoyoBlastFragment fragment = new YoyoBlastFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public YoyoBlastFragment() {
        // Required empty public constructor
        setForeGroundColor(R.color.yoyoBlast);
        setBackGroundColor(android.R.color.black);
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
        return inflater.inflate(R.layout.fragment_yoyo_blast, container, false);

    }

    @Override
    public String GetToutubeId() {
        return YOYOBLAST_CHANNEL;
    }

    @Override
    public ListView GetPlaylistView() {
        return (ListView) this.getActivity().findViewById(R.id.yoyoblastPlayLists);
    }


}
