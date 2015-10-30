package fr.aqamad.tutoyoyo.utils;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

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

    public static ImageView colorizeAndTag(ImageView iv,boolean state){
        if (state){
            colorize(iv,android.R.color.holo_green_light)    ;
        } else{
            colorize(iv,null)    ;
        }
        iv.setTag(state);
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

    /**
     * Sets ListView height dynamically based on the height of the items.
     *
     * @param listView to be resized
     * @return true if the listView is successfully resized, false otherwise
     */
    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }
            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }

    }

    public static void alertDialog(Activity act,int title, int message){
        AlertDialog alertDialog = new AlertDialog.Builder(act).create();
        alertDialog.setTitle(act.getString(title));
        alertDialog.setMessage(act.getString(message));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public static void alertDialog(Activity act,int title, String message){
        AlertDialog alertDialog = new AlertDialog.Builder(act).create();
        alertDialog.setTitle(act.getString(title));
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }


    public static void alertCustomDialog(Activity act,int title, int layoutresid,int dismissbuttonid){

        AlertDialog.Builder builder;
        final AlertDialog alertDialog;

        LayoutInflater inflater = (LayoutInflater)act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(layoutresid,null);

        builder = new AlertDialog.Builder(act);
        builder.setView(layout);
        builder.setTitle(act.getString(title));
        alertDialog = builder.create();
        alertDialog.show();

        Button dialogButton = (Button) alertDialog.findViewById(dismissbuttonid);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

}
