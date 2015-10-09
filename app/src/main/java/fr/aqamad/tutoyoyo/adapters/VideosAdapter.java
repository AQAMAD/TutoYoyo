package fr.aqamad.tutoyoyo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import fr.aqamad.tutoyoyo.R;
import fr.aqamad.youtube.YoutubePlaylist;
import fr.aqamad.youtube.YoutubeVideo;

/**
 * Created by Gregoire on 07/10/2015.
 */
public class VideosAdapter extends ArrayAdapter<YoutubeVideo> {

    private ArrayList<YoutubeVideo> mlist;

    private int foreGroundColor;

    public VideosAdapter(Context context, ArrayList<YoutubeVideo> videos, int foregroundColor) {
        super(context, 0, videos);
        mlist=videos;
        this.foreGroundColor=foregroundColor;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        YoutubeVideo vid = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.video_item, parent, false);
        }
        // Lookup view for data population
        TextView plName = (TextView) convertView.findViewById(R.id.vidName);
        TextView plDesc = (TextView) convertView.findViewById(R.id.vidDesc);
        TextView plID = (TextView) convertView.findViewById(R.id.vidID);
        ImageView imgThumb = (ImageView) convertView.findViewById(R.id.imgVidThumb);
        // Populate the data into the template view using the data object
        plName.setText(vid.getTitle());
        String description=vid.getDescription();
        if (description.length()>50){
            description=description.substring(0,49)+" (...)";
        }
        plDesc.setText(description);
        //set colors
        plName.setTextColor(getContext().getResources().getColor(foreGroundColor));
        plDesc.setTextColor(getContext().getResources().getColor(foreGroundColor));
        //store id for later use
        plID.setText(vid.getID());

        //load image with picasso
        Picasso.with(parent.getContext()).load(vid.getMediumThumb().getUrl().toString())
                .placeholder(R.drawable.waiting)
                .into(imgThumb)
        ;
        // Return the completed view to render on screen
        return convertView;
    }

}