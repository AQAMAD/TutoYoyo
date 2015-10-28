package fr.aqamad.tutoyoyo.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import fr.aqamad.tutoyoyo.R;
import fr.aqamad.tutoyoyo.adapters.VideosListViewAdapter;
import fr.aqamad.tutoyoyo.model.ModelConverter;
import fr.aqamad.tutoyoyo.model.TutorialVideo;
import fr.aqamad.tutoyoyo.tasks.GetPlaylistTask;
import fr.aqamad.youtube.YoutubeVideo;

/**
 * Created by Gregoire on 19/10/2015.
 */
public class SearchFragment extends Fragment {
    public static final String QUERY = "fr.aqamad.youtube.playlist.query";

    private String mQuery;

    public SearchFragment(){

    }


    public static SearchFragment newInstance(String query) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(QUERY, query);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        restoreFromBundle(savedInstanceState);
        Bundle b=getArguments();
        restoreFromBundle(b);

    }

    private void restoreFromBundle(Bundle b) {
        if (b != null && b.containsKey(QUERY) ) {
            mQuery=b.getString(QUERY);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save the fragment's state here
        outState.putString(QUERY, mQuery);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView=inflater.inflate(R.layout.fragment_search, container, false);

        return rootView;
    }


    @Override
    public void onStart() {
        //init basic UI
        Log.d("PF.OVC", "SearchFragment onStart, query : " + mQuery);
        fetchVideos(responseHandler, mQuery);
        super.onStart();
    }

    public void fetchVideos(Handler handler, String query) {

        ArrayList<YoutubeVideo> results= (ArrayList<YoutubeVideo>) ModelConverter.fromModel(TutorialVideo.search(query));
        if (results.size()==0){
            //reset search interface
            TextView nbt= (TextView) getActivity().findViewById(R.id.textNbResults);
            nbt.setText(String.format(getString(R.string.tutStats), 0));
            //exit right now
            return;
        }else{
            initSearchViews();
        }
        Message msg = Message.obtain();
        Bundle data = new Bundle();
        data.putSerializable(GetPlaylistTask.PLAYLIST, results);
        msg.setData(data);
        responseHandler.sendMessage(msg);
    }

    // This is the handler that receives the response when the YouTube task has finished
    Handler responseHandler = new Handler() {
        public void handleMessage(Message msg) {
            populateListWithVideos(msg);
        };
    };

    /**
     * This method retrieves the Library of videos from the task and passes them to our ListView
     * @param msg
     */
    public void populateListWithVideos(Message msg) {
        // Retreive the videos are task found from the data bundle sent back
        ArrayList<YoutubeVideo> playlist = (ArrayList<YoutubeVideo>) msg.getData().get(GetPlaylistTask.PLAYLIST);
        // we can just call our custom method with the list of items we want to display
        // Do a bit of data copy to simplify caching and accessing in case of fetch
        //create adapter
        ListAdapter resHandler= new VideosListViewAdapter(getActivity(),playlist,android.R.color.white,android.R.color.black,android.R.color.holo_green_light);
        //set adapter to listview
        ListView resultDisplay= (ListView) getActivity().findViewById(R.id.lstSearchResults);
        resultDisplay.setAdapter(resHandler);
        //display number of filtered results
        TextView nbt= (TextView) getActivity().findViewById(R.id.textNbResults);
        int nbVids = playlist.size();
        nbt.setText(String.format(getString(R.string.tutStats), nbVids));
        //set focus so searchview loses it
        nbt.setFocusable(true);
        nbt.setFocusableInTouchMode(true);
        nbt.requestFocus();
    }

    private void initSearchViews() {
        //set adapter to listview
        ListView resultDisplay= (ListView) getActivity().findViewById(R.id.lstSearchResults);
        resultDisplay.setVisibility(View.VISIBLE);
        //same with header
        TextView resultsHeader= (TextView) getActivity().findViewById(R.id.textSearchTitle);
        resultsHeader.setVisibility(View.VISIBLE);
    }


}
