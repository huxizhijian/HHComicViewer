package org.huxizhijian.hhcomicviewer2.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.activities.PreferenceActivity;

public class AdvanceSettingFragment extends PreferenceFragment {

    public AdvanceSettingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.advance_preferences);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference.getKey().equals("open_download_setting")) {
            Intent intent = new Intent(getActivity(), PreferenceActivity.class);
            intent.setAction(PreferenceActivity.ACTION_DOWNLOAD);
            startActivity(intent);
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
