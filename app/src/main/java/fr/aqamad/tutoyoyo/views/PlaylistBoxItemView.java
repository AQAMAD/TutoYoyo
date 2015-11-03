package fr.aqamad.tutoyoyo.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;

import fr.aqamad.tutoyoyo.R;

/**
 * Created by Gregoire on 28/10/2015.
 */
public class PlaylistBoxItemView extends BasePlaylistListItemView {

    public PlaylistBoxItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setGravity(Gravity.TOP);
    }

    public PlaylistBoxItemView(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.playlist_item_box;
    }


}
