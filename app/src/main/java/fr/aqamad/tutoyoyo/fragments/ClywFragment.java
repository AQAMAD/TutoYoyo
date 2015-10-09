package fr.aqamad.tutoyoyo.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import fr.aqamad.tutoyoyo.R;
import fr.aqamad.tutoyoyo.base.SourceFragment;
import fr.aqamad.tutoyoyo.base.YoutubeFragment;
import fr.aqamad.tutoyoyo.tasks.GetYouTubeChannelFilledTask;
import fr.aqamad.tutoyoyo.tasks.GetYouTubeChannelTask;
import fr.aqamad.youtube.YoutubeChannel;
import fr.aqamad.youtube.YoutubePlaylist;
import fr.aqamad.youtube.YoutubeVideo;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ClywFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ClywFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClywFragment extends YoutubeFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String CLYW_CHANNEL = "UCXB5-ZaTfNXF6P3CBQfvE5g";
    private static final String CABIN_TUTORIALS_PLAYLIST="PLVLLF_sWPwMxR8TQv_FpKZC_W6knxdiln";
    private static final int PAGE_SIZE=25;

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
    public static ClywFragment newInstance(String param1, String param2) {
        ClywFragment fragment = new ClywFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ClywFragment() {
        // Required empty public constructor
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
        return inflater.inflate(R.layout.fragment_clyw, container, false);
    }


    @Override
    public String GetToutubeId() {
        return CLYW_CHANNEL;
    }

    @Override
    public ListView GetPlaylistView() {
        return (ListView) this.getActivity().findViewById(R.id.clywPlayLists);
    }

    @Override
    public void fetchChannel(Handler handler,Activity act) {
        //autoload cabin tutorials
        new Thread(new GetYouTubeChannelFilledTask(handler,  GetToutubeId() , CABIN_TUTORIALS_PLAYLIST , act.getString(R.string.youtubeapikey))).start();
    }

    @Override
    public void prepareChannel() {
        //todo : reparse the cabin tutorials channel to a more manageable size
        YoutubeChannel channel=this.getChannel();
        YoutubePlaylist playlist=null;

        Iterator<YoutubePlaylist> iter= channel.getPlaylists().iterator();
        while(iter.hasNext()){
            YoutubePlaylist pl=iter.next();
            //clean the names
            //CLYW n'a qu'une playlist qui nous intéresse
            if(!pl.getID().equals(CABIN_TUTORIALS_PLAYLIST)){
                iter.remove();
            }else{
                playlist=pl;
            }
        }
        //do we have a playlist, if yes, then split it
        if (playlist!=null){
            //remove it from the channel before continuing
            channel.getPlaylists().remove(0);
            //get item count
            //more precise from playlist.getVids
            //int count=playlist.getNumberToFetch();
            int count=playlist.getVideos().size();
            int pages=count / PAGE_SIZE;
            int remainder=count-(pages*PAGE_SIZE);
            if (remainder!=0){
                pages++;
            }
            //iterate over number of pages and create virtual playlists
            for (int i = 1; i <= pages ; i++) {
                YoutubePlaylist tpl=playlist.clone();
                //must create unique ID
                tpl.setID(tpl.getID()+"(" + i + ")");
                tpl.setTitle(tpl.getTitle() + " (" + i + ")");
                tpl.setPageNumber(i);
                if (i==pages){
                    tpl.setNumberToFetch(remainder);
                }else{
                    tpl.setNumberToFetch(PAGE_SIZE);
                }
                int start=((i-1)*PAGE_SIZE);
                int end=(i*PAGE_SIZE)-1;
                if (end>=count){
                    end=count;
                }
                Log.d("CLYWF.PC", "sublist " + start + "/" + end);
                List<YoutubeVideo> lst=playlist.getVideos().subList(start, end);
                for (int j = 0; j < lst.size(); j++) {
                    tpl.getVideos().add(lst.get(j));
                }
                channel.getPlaylists().add(tpl);
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
