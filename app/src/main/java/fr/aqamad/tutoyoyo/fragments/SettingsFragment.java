package fr.aqamad.tutoyoyo.fragments;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import fr.aqamad.tutoyoyo.R;
import fr.aqamad.tutoyoyo.model.TutorialSource;


/**
 * Created by Gregoire on 15/10/2015.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.fragment_settings);

        Preference btnClearDb = (Preference)findPreference(getString(R.string.btn_pref_clear_database_key));
        btnClearDb.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //code for what you want it to do
                TutorialSource.rebuildDB(preference.getContext());
                Toast.makeText(preference.getContext(),"Database rebuilt",Toast.LENGTH_SHORT);
                return true;
            }
        });

        Preference btnClearViewed = (Preference)findPreference(getString(R.string.btn_pref_clear_viewed_key));
        btnClearViewed.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //code for what you want it to do
                TutorialSource.clearViewed();
                Toast.makeText(preference.getContext(), "Viewed statuses cleared", Toast.LENGTH_SHORT);
                return true;
            }
        });

        Preference btnClearCache = (Preference)findPreference(getString(R.string.btn_pref_clear_cache_key));
        btnClearCache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //code for what you want it to do
                TutorialSource.clearCache(preference.getContext());
                Toast.makeText(preference.getContext(), "Cache cleared", Toast.LENGTH_SHORT);
                return true;
            }
        });

    }




}
