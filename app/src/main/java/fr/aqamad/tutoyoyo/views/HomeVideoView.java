package fr.aqamad.tutoyoyo.views;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

import com.mikepenz.iconics.IconicsDrawable;

import fr.aqamad.commons.youtube.YoutubeUtils;
import fr.aqamad.commons.youtube.YoutubeVideo;
import fr.aqamad.tutoyoyo.R;
import fr.aqamad.tutoyoyo.model.ModelConverter;
import fr.aqamad.tutoyoyo.model.TutorialSeenVideo;
import fr.aqamad.tutoyoyo.model.TutorialVideo;

/**
 * Created by Gregoire on 28/10/2015.
 */
public class HomeVideoView extends VideoListItemView {

    private BoxType boxType=BoxType.RANDOM;

    public HomeVideoView(Context context) {
        super(context);
    }

    public HomeVideoView(Context context,BoxType boxType) {
        super(context);
        this.boxType=boxType;
    }

    @Override
    public void bind(YoutubeVideo vid) {
        super.bind(vid);
        if (boxType==BoxType.RANDOM){
            //vid was bound, let's implement the random icon instead of the viewed icon
            this.btnSeen.setImageDrawable(new IconicsDrawable(this.getContext(), "faw-recycle").color(Color.LTGRAY));
            this.btnSeen.setOnClickListener(new OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    TutorialVideo randomTut=TutorialVideo.getRandom();
                                                    YoutubeVideo vid = ModelConverter.fromModel(randomTut);
                                                    HomeVideoView.this.bind(vid);
                                                }
                                            }
            );
        }
        if (boxType==BoxType.WATCH){
            //vid was bound, let's implement the random icon instead of the viewed icon
            this.btnSeen.setImageDrawable(new IconicsDrawable(this.getContext(), "faw-eye").color(Color.LTGRAY));
            this.btnSeen.setOnClickListener(new OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    TutorialSeenVideo.seenThisVideo(boundVideo.getID());
                                                    Log.d("VLIV.OPI", "Adding video to seen videos");
                                                    YoutubeUtils.PlayYoutubeVideo(boundVideo.getID(), getContext());
                                                    TutorialVideo randomTut = TutorialVideo.getNextUnviewedInChannel(HomeVideoView.this.getContext().getString(R.string.LOCAL_LATER_PLAYLIST));
                                                    YoutubeVideo vid = ModelConverter.fromModel(randomTut);
                                                    HomeVideoView.this.bind(vid);
                                                }
                                            }
            );
        }
        if (boxType==BoxType.FAVORITE){
            //vid was bound, let's implement the random icon instead of the viewed icon
            this.btnSeen.setImageDrawable(new IconicsDrawable(this.getContext(), "faw-refresh").color(Color.LTGRAY));
            this.btnSeen.setOnClickListener(new OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    TutorialVideo randomTut = TutorialVideo.getRandomInChannel(HomeVideoView.this.getContext().getString(R.string.LOCAL_FAVORITES_PLAYLIST));
                                                    YoutubeVideo vid = ModelConverter.fromModel(randomTut);
                                                    HomeVideoView.this.bind(vid);
                                                }
                                            }
            );
        }
    }


    public enum BoxType {
        RANDOM,
        FAVORITE,
        WATCH
    }

}
