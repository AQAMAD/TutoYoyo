package fr.aqamad.tutoyoyo;

import android.preference.PreferenceActivity;

import java.util.List;

import fr.aqamad.tutoyoyo.fragments.SettingsFragment;

/**
 * Created by Gregoire on 15/10/2015.
 */
public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onBuildHeaders(List<Header> target)
    {
        loadHeadersFromResource(R.xml.headers_preference, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName)
    {
        return SettingsFragment.class.getName().equals(fragmentName);
    }
}
