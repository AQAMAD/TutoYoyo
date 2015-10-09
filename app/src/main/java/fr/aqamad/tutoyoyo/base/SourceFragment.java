package fr.aqamad.tutoyoyo.base;


import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Handler;
import android.widget.ListView;

import fr.aqamad.tutoyoyo.R;
import fr.aqamad.youtube.YoutubeChannel;

public abstract class SourceFragment extends Fragment {
    private int foreGroundColor= R.color.my;
    private int backGroundColor=android.R.color.white;

    private YoutubeChannel mChannel;

    public abstract String GetToutubeId();
    public abstract ListView GetPlaylistView();

    public abstract void fetchChannel(Handler handler,Activity act);

    public abstract Class getPlaylistActivityClass();

    public abstract void prepareChannel();

    private OnFragmentInteractionListener mListener;

    public int getForeGroundColor() {
        return foreGroundColor;
    }

    public void setForeGroundColor(int i) {
        foreGroundColor=i;
    }

    public int getBackGroundColor() {
        return backGroundColor;
    }

    public void setBackGroundColor(int i) {
        backGroundColor=i;
    }

    public YoutubeChannel getChannel() {
        return mChannel;
    }

    public void setChannel(YoutubeChannel c) {
        mChannel=c;
    }

    public Activity parentActivity;

    public SourceFragment(Activity parentActivity) {
        this.parentActivity = parentActivity;
    }

    public SourceFragment() {
        super();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            //throw new ClassCastException(activity.toString()
            //        + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}