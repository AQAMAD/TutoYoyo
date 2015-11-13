package fr.aqamad.tutoyoyo.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.activeandroid.query.Delete;

import java.util.List;

import fr.aqamad.commons.youtube.YoutubeUtils;
import fr.aqamad.commons.youtube.YoutubeVideo;
import fr.aqamad.tutoyoyo.R;
import fr.aqamad.tutoyoyo.model.TutorialPlaylist;
import fr.aqamad.tutoyoyo.model.TutorialSeenVideo;
import fr.aqamad.tutoyoyo.model.TutorialVideo;
import fr.aqamad.tutoyoyo.utils.PicassoHelper;
import fr.aqamad.tutoyoyo.utils.ScreenSize;
import fr.aqamad.tutoyoyo.utils.UI;

/**
 * Created by Gregoire on 28/10/2015.
 */
public class VideoListItemView  extends LinearLayout implements View.OnClickListener {

    protected ImageView btnSeen;
    protected YoutubeVideo boundVideo;
    private ImageView imgThumb;
    private TextView plName;
    private TextView plDesc;
    private TextView plDesc2;
    private TextView plDuration;
    private TextView plID;
    private LinearLayout expandSection;
    private ImageView btnFav;
    private ImageView btnSha;
    private ImageView btnWat;
    private ImageView btnExpandVideo;
    private ImageView btnShareVideo;
    private ScreenSize screenSize;
    private int foreGroundColor=android.R.color.darker_gray;

    public VideoListItemView(Context context) {
        this(context, null);
    }

    public VideoListItemView(Context context, AttributeSet attrs) {
        //context must be an activity context (for dialogbuilder), check and raise exception if not the case
        super(context, attrs);
        screenSize=new ScreenSize(context);
        //basic layout duties
        setOrientation(LinearLayout.VERTICAL);
        //add 5dp padding
        float density=screenSize.getDensity();
        int padding= (int) (density*5);
        setPadding(padding, padding, padding, padding);

        LayoutInflater.from(context).inflate(R.layout.video_item, this);

        imgThumb = (ImageView) findViewById(R.id.imgVidThumb);
        //mNameView = (TextView) findViewById(R.id.name);
        plName = (TextView) findViewById(R.id.vidName);
        plDesc = (TextView) findViewById(R.id.vidDesc);
        plID = (TextView) findViewById(R.id.vidID);
        //hide expanding sections
        expandSection = (LinearLayout) findViewById(R.id.expandSection);
        expandSection.setVisibility(View.GONE);
        plDesc2 = (TextView) findViewById(R.id.vidDesc2);
        //set duration
        plDuration = (TextView) findViewById(R.id.vidDuration);
        //set up clickListeners
        btnFav=(ImageView) findViewById(R.id.btnFavorites);
        btnSha=(ImageView) findViewById(R.id.btnShare);
        btnWat=(ImageView) findViewById(R.id.btnLater);
        btnSeen=(ImageView) findViewById(R.id.btnSeen);
        btnExpandVideo=(ImageView) findViewById(R.id.expandVideoButton);
        btnShareVideo=(ImageView) findViewById(R.id.shareVideoButton);
        imgThumb.setOnClickListener(this);
        plName.setOnClickListener(this);
        btnSha.setOnClickListener(this);
        btnFav.setOnClickListener(this);
        btnWat.setOnClickListener(this);
        btnSeen.setOnClickListener(this);
        btnExpandVideo.setOnClickListener(this);
        btnShareVideo.setOnClickListener(this);
    }

    public int getForeGroundColor() {
        return foreGroundColor;
    }

    public void setForeGroundColor(int foreGroundColor) {
        this.foreGroundColor = foreGroundColor;
    }

    public void bind(YoutubeVideo vid) {
        boundVideo=vid;
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
        //mNameView.setText(user.getName());
        plDesc2.setText(vid.getDescription());
        plDesc2.setTextColor(getContext().getResources().getColor(foreGroundColor));
        plDuration.setText(YoutubeUtils.prettyDuration(vid.getDuration()));
        plDuration.setTag(vid.getDuration());
        plDuration.setTextColor(getContext().getResources().getColor(foreGroundColor));
        //detect video presence from local database
        List<TutorialVideo> lst=TutorialVideo.getByKey(vid.getID());
        boolean isFavorite=false;
        boolean isShared=false;
        boolean isLater=false;
        TutorialSeenVideo tsv=TutorialSeenVideo.getByKey(vid.getID());
        boolean isViewed=false;
        //Log.d("VA","TVListbykey size=" + lst.size());
        for (TutorialVideo vi:
                lst) {
            //Log.d("VA","TVListbykey channelkey=" + vi.channel.key);
            if (vi.channel.key.equals(getContext().getResources().getString(R.string.LOCAL_FAVORITES_PLAYLIST))) {
                isFavorite=true;
            } else if (vi.channel.key.equals(getContext().getResources().getString(R.string.LOCAL_LATER_PLAYLIST))) {
                isLater=true;
            } else if (vi.channel.key.equals(getContext().getResources().getString(R.string.LOCAL_SOCIAL_PLAYLIST))) {
                isShared=true;
            }
        }
        if(tsv!=null){
            isViewed=true;
        }
        //3 buttons
        UI.colorizeAndTag(btnFav, isFavorite);
        UI.colorizeAndTag(btnSha, isShared);
        UI.colorizeAndTag(btnWat, isLater);
        //seen
        UI.colorizeAndTag(btnSeen, isViewed);
        //calculate sizes
        int tWidth=0;
        int tHeight=0;
        if (screenSize.getHeight()<screenSize.getWidth()){
            //landscape, image should be no more than 1/4 screen height
            tWidth=screenSize.getWidth()/5;
        }else{
            //portrait
            tWidth=screenSize.getWidth()/3;
        }
        //Log.d("VA.GV","Calculated Width is : " + tWidth);
        //load image with picasso
        PicassoHelper.with(getContext()).load(vid.getHighThumb().getUrl())
                .placeholder(R.drawable.waiting)
                .resize(tWidth, tHeight)
                .transform(PicassoHelper.getRoundedCornersTranform(getContext().getResources().getColor(foreGroundColor)))
                .into(imgThumb)
        ;
        imgThumb.setTag(R.id.defaultThumb, vid.getDefaultThumb().getUrl());
        imgThumb.setTag(R.id.mediumThumb, vid.getMediumThumb().getUrl());
        imgThumb.setTag(R.id.highThumb, vid.getHighThumb().getUrl());
        if (vid.getStandardThumb()!=null){
            imgThumb.setTag(R.id.standardThumb,vid.getStandardThumb().getUrl());
        }
        //reset expandable section
        int visibility = expandSection.getVisibility();
        if (visibility == View.VISIBLE) {
            expandSection.setVisibility(GONE);
            btnExpandVideo.setRotation(0);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imgVidThumb:
                openVideo(v);
                break;
            case R.id.vidName:
                openVideo(v);
                break;
            case R.id.btnSeen:
                Boolean status = (Boolean) v.getTag();
                TutorialSeenVideo.markSeen(boundVideo.getID(), !status);
                UI.colorizeAndTag(btnSeen, !status);
                break;
            case R.id.btnShare:
                addShare(v);
                break;
            case R.id.btnFavorites:
                addFavorite(v);
                break;
            case R.id.btnLater:
                addLater(v);
                break;
            case R.id.expandVideoButton:
                expandVideo(v);
                break;
            case R.id.shareVideoButton:
                shareVideo(v);
                break;
        }
    }

    private void addLater(View v) {
        //get button tag
        addToPrivatePlaylist(btnWat, R.string.LOCAL_LATER_PLAYLIST
                ,R.string.dialog_msg_remove_later);
    }
    private void addShare(View v) {
        //get button tag
        addToPrivatePlaylist(btnSha, R.string.LOCAL_SOCIAL_PLAYLIST
                ,R.string.dialog_msg_remove_share);
    }
    private void addFavorite(View v) {
        //get button tag
        addToPrivatePlaylist(btnFav, R.string.LOCAL_FAVORITES_PLAYLIST
                ,R.string.dialog_msg_remove_favorite);
    }

    private void addToPrivatePlaylist(ImageView v,int playlistResId,int messageConfirmResId) {
        boolean is=(boolean)v.getTag();
        String playlistKey=getResources().getString(playlistResId);
        //obtain videoID
        if (is){
            //toast some confirmation message and remove if necessary
            confirmRemove(messageConfirmResId, v, playlistKey);
        }else{
            addToLocalChannel(v, playlistKey);
        }
    }


    private void shareVideo(View v) {
        YoutubeUtils.ShareYoutubeVideo(boundVideo.getID(), getContext());
    }

    private void openVideo(View v) {
        TutorialSeenVideo.seenThisVideo(boundVideo.getID());
        Log.d("VLIV.OPI", "Adding video to seen videos");
        YoutubeUtils.PlayYoutubeVideo(boundVideo.getID(), getContext());
        UI.colorizeAndTag(btnSeen, true);
    }

    public void expandVideo(View view) {
        int visibility=expandSection.getVisibility();
        if (visibility==View.VISIBLE){
            UI.getInstance().animHideCollapse(expandSection);
            UI.animRotate(view, 0, 400);
        }else{
            //animate visibility
            UI.animRevealExpand(expandSection);
            UI.animRotate(view, 90, 400);
        }
    }

    public void confirmRemove(int confirmMessageResId,ImageView view, String playlistKey){
        final String vidID = boundVideo.getID();
        final String theKey=playlistKey;
        final ImageView theview=view;
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.dialog_title_remove)
                .setMessage(confirmMessageResId)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //call model code to remove from list
                        removeFromLocalChannel(theview,vidID,theKey);
                    }})
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void removeFromLocalChannel(ImageView btn,String vidId,String playlistKey) {
        //let's delete
        TutorialPlaylist later = TutorialPlaylist.getByKey(playlistKey);
        new Delete().from(TutorialVideo.class).where("Key = ? and Channel = ?", vidId, later.getId()).execute();
        Snackbar.make(this, R.string.msg_video_removed, Snackbar.LENGTH_SHORT).show();
        UI.colorizeAndTag(btn, false);
    }

    private void addToLocalChannel(ImageView view, String playlistKey) {
        //add to local channel via callback
        //add to local channel
        Log.d("PF.ATLC","Add to local Channel " + boundVideo.getID());
        //get data from model and not from view
        TutorialVideo oVid=TutorialVideo.getByKey(boundVideo.getID()).get(0);
        oVid.addToLocal(playlistKey);
        Snackbar.make(this, R.string.msg_video_added, Snackbar.LENGTH_SHORT).show();
        UI.colorizeAndTag(view, true);
    }
}
