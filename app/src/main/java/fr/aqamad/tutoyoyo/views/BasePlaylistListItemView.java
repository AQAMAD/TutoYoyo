package fr.aqamad.tutoyoyo.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import fr.aqamad.tutoyoyo.R;
import fr.aqamad.tutoyoyo.utils.PicassoHelper;
import fr.aqamad.tutoyoyo.utils.ScreenSize;
import fr.aqamad.youtube.YoutubePlaylist;

/**
 * Created by Gregoire on 28/10/2015.
 */
public abstract class BasePlaylistListItemView extends LinearLayout  {

    ImageView imgThumb;
    TextView plID;
    TextView plName;
    TextView plDesc;
    TextView plDetails;

    private int foreGroundColor=android.R.color.darker_gray;
    private YoutubePlaylist boundPlaylist;
    private ScreenSize screenSize;


    /**
     * getters and setters
     */
    public int getForeGroundColor() {
        return foreGroundColor;
    }

    public void setForeGroundColor(int foreGroundColor) {
        this.foreGroundColor = foreGroundColor;
    }



    public BasePlaylistListItemView(Context context) {
        this(context, null);
    }

    public BasePlaylistListItemView(Context context, AttributeSet attrs) {
        //context must be an activity context (for dialogbuilder), check and raise exception if not the case
        super(context, attrs);
        screenSize=new ScreenSize(context);
        //basic layout duties
        setOrientation(LinearLayout.VERTICAL);
        //add 5dp padding
        float density=screenSize.getDensity();
        int padding= (int) (density*5);
        setPadding(padding, padding, padding, padding);

        LayoutInflater.from(context).inflate(getLayoutResId(),this);

        imgThumb = (ImageView) findViewById(R.id.imgThumb);
        plName = (TextView) findViewById(R.id.plName);
        plDesc = (TextView) findViewById(R.id.plDesc);
        plID = (TextView) findViewById(R.id.plID);
        plDetails=(TextView) findViewById(R.id.plDetails);
    }

    protected abstract int getLayoutResId();

    public void bind(YoutubePlaylist pls) {
        boundPlaylist=pls;
        // Populate the data into the template view using the data object
        //store id for later use
        plID.setText(pls.getID());
        plName.setText(pls.getTitle());
        plDetails.setText(pls.getVideos().size() + " tuts / " + pls.getTotalLength());
        String description=pls.getDescription();
        if (description.length()>50){
            description=description.substring(0,49)+" (...)";
        }
        plDesc.setText(description);
        //set colors
        plName.setTextColor(getContext().getResources().getColor(foreGroundColor));
        plDetails.setTextColor(getContext().getResources().getColor(foreGroundColor));
        plDesc.setTextColor(getContext().getResources().getColor(foreGroundColor));
        //calculate sizes
        int tWidth= (int) (screenSize.getWidth()/3);
        //Log.d("PA.GV", "Calculated Width is : " + tWidth);
        String thumb=pls.getHighThumb().getUrl();
        //try rounded transformation
        PicassoHelper.loadWeborDrawable(this.getContext(), thumb)
//                .centerCrop()
                .placeholder(R.drawable.waiting)
                .resize(tWidth,0)
//                .resize(pls.getHighThumb().getWidth(),pls.getHighThumb().getHeight())
                .transform(PicassoHelper.getRoundedCornersTranform(getContext().getResources().getColor(foreGroundColor)))
                .into(imgThumb);
        // Return the completed view to render on screen
    }


}
