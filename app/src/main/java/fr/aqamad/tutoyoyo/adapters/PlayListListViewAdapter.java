package fr.aqamad.tutoyoyo.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import fr.aqamad.commons.youtube.YoutubePlaylist;
import fr.aqamad.tutoyoyo.utils.ScreenSize;
import fr.aqamad.tutoyoyo.utils.ThreadPreconditions;
import fr.aqamad.tutoyoyo.views.BasePlaylistListItemView;
import fr.aqamad.tutoyoyo.views.PlaylistBoxItemView;
import fr.aqamad.tutoyoyo.views.PlaylistLineItemView;

/**
 * Created by Gregoire on 07/10/2015.
 */
public class PlaylistListViewAdapter extends BaseAdapter {

    //Context
    private final Activity activity;
    //model objects
    private List<YoutubePlaylist> mlist;
    private int foreGroundColor;
    private int backGroundColor;
    private boolean displayAsBox=false;
    private ScreenSize screenSize;

    public PlaylistListViewAdapter(Activity act, List<YoutubePlaylist> playlists, ScreenSize screenSize, int foregroundColor, int backgroundColor, boolean displayAsBox) {
        this.activity=act;
        //super(context, 0, playlists);
        mlist=playlists;
        this.foreGroundColor=foregroundColor;
        this.backGroundColor=backgroundColor;
        this.displayAsBox=displayAsBox;
        this.screenSize = screenSize;
    }

    public void updatePlaylists(List<YoutubePlaylist> playlists){
        ThreadPreconditions.checkOnMainThread();
        this.mlist = playlists;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public YoutubePlaylist getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //prepare view variables
        YoutubePlaylist pls = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView!=null){
            //reinit things here
        }else{
            convertView=displayAsBox ? new PlaylistBoxItemView(activity) : new PlaylistLineItemView(activity) ;
        }

        //position colors
        ((BasePlaylistListItemView)convertView).setForeGroundColor(foreGroundColor);
        ((BasePlaylistListItemView)convertView).bind(pls);
       // Return the completed view to render on screen
        return convertView;
    }



}