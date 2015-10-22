package fr.aqamad.tutoyoyo.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import fr.aqamad.tutoyoyo.R;
import fr.aqamad.tutoyoyo.utils.PicassoHelper;
import fr.aqamad.tutoyoyo.utils.ScreenSize;
import fr.aqamad.youtube.YoutubePlaylist;

/**
 * Created by Gregoire on 07/10/2015.
 */
public class PlaylistsAdapter extends ArrayAdapter<YoutubePlaylist> {

    private ArrayList<YoutubePlaylist> mlist;

    private int foreGroundColor;
    private int backGroundColor;
    private int itemResId;
    private ScreenSize screenSize;

    public PlaylistsAdapter(Context context, ArrayList<YoutubePlaylist> playlists, ScreenSize screenSize,int foregroundColor,int backgroundColor,int ItemResId) {
        super(context, 0, playlists);
        mlist=playlists;
        this.foreGroundColor=foregroundColor;
        this.backGroundColor=backgroundColor;
        this.itemResId=ItemResId;
        this.screenSize = screenSize;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        YoutubePlaylist pls = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(itemResId, parent, false);
        }
        // Lookup view for data population
        TextView plName = (TextView) convertView.findViewById(R.id.plName);
        //fill up details
        TextView plDetails = (TextView) convertView.findViewById(R.id.plDetails);
        plDetails.setText(pls.getVideos().size() + " tuts / " + pls.getTotalLength() );


        TextView plDesc = (TextView) convertView.findViewById(R.id.plDesc);
        TextView plID = (TextView) convertView.findViewById(R.id.plID);
        ImageView imgThumb = (ImageView) convertView.findViewById(R.id.imgThumb);

        // Populate the data into the template view using the data object
        plName.setText(pls.getTitle());
        String description=pls.getDescription();
        if (description.length()>50){
            description=description.substring(0,49)+" (...)";
        }
        plDesc.setText(description);
        //set colors
        plName.setTextColor(getContext().getResources().getColor(foreGroundColor));
        plDetails.setTextColor(getContext().getResources().getColor(foreGroundColor));
        plDesc.setTextColor(getContext().getResources().getColor(foreGroundColor));
        //store id for later use
        plID.setText(pls.getID());
        //calculate sizes
        int tWidth= (int) (screenSize.getWidth()/3);
        Log.d("PA.GV", "Calculated Width is : " + tWidth);
        String thumb=pls.getHighThumb().getUrl().toString();
        //try rounded transformation

        PicassoHelper.loadWeborDrawable(parent.getContext(), thumb)
//                .centerCrop()
                .placeholder(R.drawable.waiting)
                .resize(tWidth,0)
//                .resize(pls.getHighThumb().getWidth(),pls.getHighThumb().getHeight())
                .transform(PicassoHelper.getRoundedCornersTranform(getContext().getResources().getColor(foreGroundColor)))
                .into(imgThumb);

        // Return the completed view to render on screen
        return convertView;
    }

}