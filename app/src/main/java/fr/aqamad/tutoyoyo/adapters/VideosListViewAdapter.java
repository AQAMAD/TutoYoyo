package fr.aqamad.tutoyoyo.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import fr.aqamad.tutoyoyo.utils.ScreenSize;
import fr.aqamad.tutoyoyo.views.VideoListItemView;
import fr.aqamad.youtube.YoutubeVideo;

/**
 * Created by Gregoire on 07/10/2015.
 */
public class VideosListViewAdapter extends BaseAdapter {

    private List<YoutubeVideo> mlist;

    private int foreGroundColor;
    private int backGroundColor;
    private int highlightColor;
    private ScreenSize screenSize;
    private Activity mAct;

    public VideosListViewAdapter(Activity act, List<YoutubeVideo> videos, int foregroundColor, int backGroundColor, int highlightColor) {
        //super(act.getBaseContext(), 0, videos);
        mAct=act;
        mlist=videos;
        this.foreGroundColor=foregroundColor;
        this.backGroundColor=backGroundColor;
        this.highlightColor=highlightColor;
        this.screenSize = new ScreenSize(act);
    }


    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public YoutubeVideo getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        YoutubeVideo vid = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView!=null){
            //reinit things here
        }else{
            convertView=new VideoListItemView(mAct);
        }
        //position colors
        ((VideoListItemView)convertView).setForeGroundColor(foreGroundColor);
        ((VideoListItemView)convertView).bind(vid);
//        // Return the completed view to render on screen
        return convertView;
    }

}