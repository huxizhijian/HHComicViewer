package org.huxizhijian.hhcomicviewer2.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.activities.PreferenceActivity;

public class AdvanceSettingFragment extends PreferenceFragment {

    SharedPreferences mSharedPreferences;

    public AdvanceSettingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.advance_preferences);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String cacheSize = mSharedPreferences.getString("reading_cache_size", "160MB");
        Preference preference = getPreferenceManager().findPreference("reading_cache_size");
        preference.setSummary(cacheSize);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        switch (preference.getKey()) {
            case "reading_cache_size":
                preference.setSummary(((ListPreference) preference).getValue());
                return true;
            case "open_download_setting":
                Intent intent = new Intent(getActivity(), PreferenceActivity.class);
                intent.setAction(PreferenceActivity.ACTION_DOWNLOAD);
                startActivity(intent);
                return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
