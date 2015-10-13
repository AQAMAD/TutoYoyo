package fr.aqamad.tutoyoyo.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.Collections;
import java.util.Comparator;

import fr.aqamad.tutoyoyo.MainActivity;
import fr.aqamad.tutoyoyo.R;
import fr.aqamad.tutoyoyo.base.LocalFragment;
import fr.aqamad.tutoyoyo.base.SourceFragment;
import fr.aqamad.tutoyoyo.tasks.GetLocalChannelTask;
import fr.aqamad.youtube.YoutubeChannel;
import fr.aqamad.youtube.YoutubePlaylist;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyTutsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyTutsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyTutsFragment extends LocalFragment {

    public MyTutsFragment() {
        Log.d("MTF", "MyTutsFragment constructor");
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param channelID ID of the current local channel.
     * @return A new instance of fragment MyTutsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyTutsFragment newInstance(String channelID) {
        MyTutsFragment fragment = new MyTutsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CHANNELID, channelID);
        fragment.setArguments(args);
        Log.d("MTF", "MyTutsFragment newInstance");
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("MTF", "MyTutsFragment onCreateView");
        return inflater.inflate(R.layout.fragment_my_tuts, container, false);
    }


    @Override
    public ListView GetPlaylistView() {
        Log.d("MTF", "MyTutsFragment GetPlaylistView");
        return (ListView) this.getActivity().findViewById(R.id.myPlayLists);
    }


}
