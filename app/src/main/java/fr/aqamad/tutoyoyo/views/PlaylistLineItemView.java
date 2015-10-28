package fr.aqamad.tutoyoyo.views;

import android.content.Context;
import android.util.AttributeSet;

import fr.aqamad.tutoyoyo.R;

/**
 * Created by Gregoire on 28/10/2015.
 */
public class PlaylistLineItemView extends BasePlaylistListItemView {

    public PlaylistLineItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PlaylistLineItemView(Context context) {
        super(context);
    }

    @Override

    protected int getLayoutResId() {
        return R.layout.playlist_item;
    }
}
