package fr.aqamad.tutoyoyo.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import fr.aqamad.tutoyoyo.R;
import fr.aqamad.tutoyoyo.base.SourceFragment;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link YoyoBlastFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link YoyoBlastFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class YoyoBlastFragment extends SourceFragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param channelID ID of the current local channel.
     * @return A new instance of fragment MyTutsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static YoyoBlastFragment newInstance(String channelID) {
        YoyoBlastFragment fragment = new YoyoBlastFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CHANNELID, channelID);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public YoyoBlastFragment() {
        // Required empty public constructor
        setForeGroundColor(R.color.yoyoBlast);
        setBackGroundColor(android.R.color.black);
        setItemRessourceId(R.layout.playlist_item_box);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_yoyo_blast, container, false);

    }

    @Override
    public AdapterView GetPlaylistView() {
        return (AdapterView) this.getActivity().findViewById(R.id.yoyoblastPlayLists);
    }


}
