package fr.aqamad.tutoyoyo.utils;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Build;
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

import java.util.concurrent.atomic.AtomicInteger;

import fr.aqamad.tutoyoyo.R;

/**
 * Created by Gregoire on 14/10/2015.
 */
public class UI {

    //used only in older versions (see method below)
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    /**
     * Generate a value suitable for use in View.setId(int).
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    private static int generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    /**
     * Version independent
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    public static int getViewID() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return View.generateViewId();
        } else {
            //before jelly bean
            return UI.generateViewId();
        }
    }


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
            colorize(iv, android.R.color.holo_green_light);
        } else{
            colorize(iv, null);
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

    public static UI getInstance(){
        return new UI();
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

    public static int getColorFromName(String colorName) {
        int colorId = 0;
        switch (colorName) {
            case "black":
                colorId = android.R.color.black;
                break;
            case "white":
                colorId = android.R.color.white;
                break;
            case "orange":
                colorId = android.R.color.holo_orange_dark;
                break;
            case "lightblue":
                colorId = android.R.color.holo_blue_light;
                break;
            case "mikemontybg":
                colorId = R.color.mikemonty_background;
                break;
            case "purple":
                colorId = R.color.slusny_foreground;
                break;
            case "silver":
                colorId = android.R.color.darker_gray;
                break;
            default:
                colorId = android.R.color.holo_red_dark;
        }
        return colorId;
    }

    public View animHideCollapse(View iv) {
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

    public class MyHider implements Runnable {

        private View iv;

        public MyHider(View parameter) {
            // store parameter for later user
            this.iv = parameter;
        }

        public void run() {
            iv.setVisibility(View.GONE);
        }
    }

}
