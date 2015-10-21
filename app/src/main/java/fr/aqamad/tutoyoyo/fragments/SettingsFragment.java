package fr.aqamad.tutoyoyo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
                Toast.makeText(preference.getContext(), "Database rebuilt", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        Preference btnClearViewed = (Preference)findPreference(getString(R.string.btn_pref_clear_viewed_key));
        btnClearViewed.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //code for what you want it to do
                TutorialSource.clearViewed();
                Toast.makeText(preference.getContext(), "Viewed statuses cleared", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        Preference btnClearCache = (Preference)findPreference(getString(R.string.btn_pref_clear_cache_key));
        btnClearCache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //code for what you want it to do
                TutorialSource.clearCache(preference.getContext());
                Toast.makeText(preference.getContext(), "Cache cleared", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        Preference btnDelDb = (Preference)findPreference(getString(R.string.btn_pref_del_db_key));
        btnDelDb.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //code for what you want it to do
                TutorialSource.clearDB();
                Toast.makeText(preference.getContext(), "DB Deleted, App will restart", Toast.LENGTH_SHORT).show();
                rebootApp();
                return true;
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.setBackgroundColor(getResources().getColor(android.R.color.white));
        return view;
    }

    private void rebootApp(){
        Intent i = this.getActivity().getBaseContext().getPackageManager()
                .getLaunchIntentForPackage( this.getActivity().getBaseContext().getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.getActivity().startActivity(i);
    }



}
