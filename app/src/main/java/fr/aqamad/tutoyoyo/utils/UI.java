package fr.aqamad.tutoyoyo.utils;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import fr.aqamad.tutoyoyo.R;

/**
 * Created by Gregoire on 14/10/2015.
 */
public class UI {


    public static ImageView colorize(ImageView iv,Integer color){
        Context ctx=iv.getContext();
        if (color!=null){
            iv.setColorFilter(ctx.getResources().getColor(color), PorterDuff.Mode.SRC_ATOP);
        }else
        {
            iv.clearColorFilter();
        }
        return iv;
    }


    public static View animRotate(View iv,int degrees,int duration){
        //get current rotation
        float previousRotation=iv.getRotation();
        ObjectAnimator animation1 = ObjectAnimator.ofFloat(iv,
                "rotation", previousRotation,degrees);
        animation1.setDuration(duration);
        animation1.setInterpolator(new AccelerateDecelerateInterpolator());
        animation1.start();
        return iv;
    }

    public static View animRevealExpand(View iv){
        AnimationSet set = new AnimationSet(true);
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(250);
        set.addAnimation(animation);
        animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f
        );
        animation.setDuration(150);
        set.addAnimation(animation);
        iv.startAnimation(set);
        iv.setVisibility(View.VISIBLE);
        return iv;
    }

    public View animHideCollapse(View iv){
        AnimationSet set = new AnimationSet(true);

        Animation animation = new AlphaAnimation(1.0f, 0.0f);
        animation.setDuration(250);
        set.addAnimation(animation);
        animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f
        );
        animation.setDuration(150);
        set.addAnimation(animation);
        iv.startAnimation(set);
        iv.postDelayed(new MyHider(iv), 250);
        return iv;
    }


    public static UI getInstance(){
        return new UI();
    }

    public class MyHider implements Runnable {

        private View iv;

        public MyHider(View parameter) {
            // store parameter for later user
            this.iv=parameter;
        }

        public void run() {
            iv.setVisibility(View.GONE);
        }
    }

    //dialogs are added here
    public class RemoveFromPlaylistDialogFragment extends DialogFragment {

        private String mPlaylistTitle;
        private int mDialogTitle;
        private int mDialogMessage;

        public RemoveFromPlaylistDialogFragment(){

        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(mDialogTitle);
            builder.setMessage(mDialogMessage);
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // FIRE ZE MISSILES!
                }
            });
            builder.setNegativeButton(android.R.string.no, null);
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

}
