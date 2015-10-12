package fr.aqamad.tutoyoyo.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import fr.aqamad.tutoyoyo.R;
import fr.aqamad.tutoyoyo.model.TutorialVideo;
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
        //hide expanding sections
        LinearLayout ll = (LinearLayout) convertView.findViewById(R.id.expandSection);
        ll.setVisibility(View.GONE);

        TextView plDesc2 = (TextView) convertView.findViewById(R.id.vidDesc2);
        plDesc2.setText(vid.getDescription());
        //detect video presence from local database
        List<TutorialVideo> lst=TutorialVideo.getByKey(vid.getID());
        boolean isFavorite=false;
        boolean isShared=false;
        boolean isLater=false;
        //Log.d("VA","TVListbykey size=" + lst.size());
        for (TutorialVideo vi:
             lst) {
            //Log.d("VA","TVListbykey channelkey=" + vi.channel.key);
            if (vi.channel.key.equals(getContext().getResources().getString(R.string.localFavoritesKey))){
                isFavorite=true;
            }else if (vi.channel.key.equals(getContext().getResources().getString(R.string.localLaterKey))){
                isLater=true;
            }else if (vi.channel.key.equals(getContext().getResources().getString(R.string.localSocialKey))){
                isShared=true;
            }
        }
        //3 buttons
        ImageView btnFav=(ImageView) convertView.findViewById(R.id.btnFavorites);
        int color= isFavorite ? android.R.color.holo_green_light : android.R.color.holo_blue_light ;
        btnFav.setColorFilter(getContext().getResources().getColor(color), PorterDuff.Mode.SRC_ATOP);
        btnFav.setTag(isFavorite);
        ImageView btnSha=(ImageView) convertView.findViewById(R.id.btnShare);
        color= isShared ? android.R.color.holo_green_light : android.R.color.holo_blue_light ;
        btnSha.setColorFilter(getContext().getResources().getColor(color), PorterDuff.Mode.SRC_ATOP);
        btnSha.setTag(isShared);
        ImageView btnWat=(ImageView) convertView.findViewById(R.id.btnLater);
        color= isLater ? android.R.color.holo_green_light : android.R.color.holo_blue_light ;
        btnWat.setColorFilter(getContext().getResources().getColor(color), PorterDuff.Mode.SRC_ATOP);
        btnWat.setTag(isLater);
        //load image with picasso
        Picasso.with(parent.getContext()).load(vid.getHighThumb().getUrl().toString())
                .placeholder(R.drawable.waiting)
                .into(imgThumb)
        ;
        imgThumb.setTag(R.id.defaultThumb, vid.getDefaultThumb().getUrl().toString());
        imgThumb.setTag(R.id.mediumThumb,vid.getMediumThumb().getUrl().toString());
        imgThumb.setTag(R.id.highThumb, vid.getHighThumb().getUrl().toString());
        if (vid.getStandardThumb()!=null){
            imgThumb.setTag(R.id.standardThumb,vid.getStandardThumb().getUrl().toString());
        }
        // Return the completed view to render on screen
        return convertView;
    }

}