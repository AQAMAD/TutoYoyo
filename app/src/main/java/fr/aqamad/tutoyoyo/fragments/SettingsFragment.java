package fr.aqamad.tutoyoyo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import fr.aqamad.tutoyoyo.R;
import fr.aqamad.tutoyoyo.model.Sponsor;
import fr.aqamad.tutoyoyo.model.Sponsors;
import fr.aqamad.tutoyoyo.model.TutorialSource;


/**
 * Created by Gregoire on 15/10/2015.
 */
public class SettingsFragment extends PreferenceFragment {
    public static final String FRAGMENT_KEY = "fr.aqamad.totoyoyo.settingsfragment";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Log.d("SF", "SettingsFragment onCreate called");
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.fragment_settings);
        //add all preferences from fragment
        Sponsors sponsors=new Sponsors(getResources());
        PreferenceCategory cat=(PreferenceCategory)findPreference(getString(R.string.sponsor_preference_category));
        for (Sponsor spo :
                sponsors.values()) {
            if (spo.preferenceKey!=null){
                CheckBoxPreference cb = new CheckBoxPreference(getActivity());
                cb.setKey(spo.preferenceKey);
                cb.setTitle(spo.name);
                cb.setSummary(cb.getContext().getString(R.string.prefUseTutorialsFrom) + " " + spo.name);
                cb.setOrder(spo.order);        //not working...
                cb.setDefaultValue(true);
                Log.d("SF.OC", "Created Preference for " + spo.name + " using order " + spo.order);
                cat.addPreference(cb);
            }
        }

        Preference btnClearCache = findPreference(getString(R.string.btn_pref_clear_cache_key));
        btnClearCache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //code for what you want it to do
                TutorialSource.clearCache(preference.getContext());
                Toast.makeText(preference.getContext(), "Cache cleared", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        Preference btnDelDb = findPreference(getString(R.string.btn_pref_del_db_key));
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
        Log.d("SF", "SettingsFragment onCreateView called");
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.setBackgroundColor(getResources().getColor(android.R.color.white));
        return view;
    }

    private void rebootApp(){
        Intent i = this.getActivity().getBaseContext().getPackageManager()
                .getLaunchIntentForPackage( this.getActivity().getBaseContext().getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.getActivity().startActivity(i);
        System.exit(0);
    }



}
