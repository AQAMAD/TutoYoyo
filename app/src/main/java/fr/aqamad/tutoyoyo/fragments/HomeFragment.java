package fr.aqamad.tutoyoyo.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import fr.aqamad.tutoyoyo.R;

/**
 * Created by Gregoire on 19/10/2015.
 */
public class HomeFragment extends Fragment {

    protected SponsorsCallbacks mCallbacks;

    public HomeFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_home,container,false);

        //set up hyperlinks from the home fragment
        ImageView v = (ImageView) rootView.findViewById(R.id.imageBHOP);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (mCallbacks != null) {
                mCallbacks.onSponsorSelected(R.id.nav_bhop);
            }
            }
        });
        v = (ImageView) rootView.findViewById(R.id.imageCLYW);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (mCallbacks != null) {
                mCallbacks.onSponsorSelected(R.id.nav_clyw);
            }

            }
        });
        v = (ImageView) rootView.findViewById(R.id.imageYYB);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (mCallbacks != null) {
                mCallbacks.onSponsorSelected(R.id.nav_yyb);
            }
            }
        });

        v = (ImageView) rootView.findViewById(R.id.imageYYE);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (mCallbacks != null) {
                mCallbacks.onSponsorSelected(R.id.nav_yye);
            }
            }
        });
        v = (ImageView) rootView.findViewById(R.id.imageYYT);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (mCallbacks != null) {
                mCallbacks.onSponsorSelected(R.id.nav_yyt);
            }
            }
        });

        v = (ImageView) rootView.findViewById(R.id.imageSPBY);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (mCallbacks != null) {
                mCallbacks.onSponsorSelected(R.id.nav_spyy);
            }
            }
        });

        return rootView;
    }

    /**
     * Hold a reference to the parent Activity so we can report the
     * task's current progress and results. The Android framework
     * will pass us a reference to the newly created Activity after
     * each configuration change.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (SponsorsCallbacks) activity;
    }

    /**
     * Set the callback to null so we don't accidentally leak the
     * Activity instance.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }
    public interface SponsorsCallbacks{
        public void onSponsorSelected(int navigationId);
    }

}
