package fr.aqamad.tutoyoyo.adapters;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URISyntaxException;
import java.util.ArrayList;

import fr.aqamad.tutoyoyo.R;
import fr.aqamad.youtube.YoutubePlaylist;

/**
 * Created by Gregoire on 07/10/2015.
 */
public class PlaylistsAdapter extends ArrayAdapter<YoutubePlaylist> {

    private ArrayList<YoutubePlaylist> mlist;

    private int foreGroundColor;

    public PlaylistsAdapter(Context context, ArrayList<YoutubePlaylist> playlists,int foregroundColor) {
        super(context, 0, playlists);
        mlist=playlists;
        this.foreGroundColor=foregroundColor;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        YoutubePlaylist pls = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.playlist_item, parent, false);
        }
        // Lookup view for data population
        TextView plName = (TextView) convertView.findViewById(R.id.plName);
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
        plDesc.setTextColor(getContext().getResources().getColor(foreGroundColor));
        //store id for later use
        plID.setText(pls.getID());

        //load image with picasso
        Picasso.with(parent.getContext()).load(pls.getMediumThumb().getUrl().toString())
                .placeholder(R.drawable.waiting)
                .centerCrop()
                .resize(pls.getMediumThumb().getWidth(),pls.getMediumThumb().getHeight())
                .into(imgThumb)
        ;
        // Return the completed view to render on screen
        return convertView;
    }

}