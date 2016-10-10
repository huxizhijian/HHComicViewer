package org.huxizhijian.hhcomicviewer2.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import org.huxizhijian.hhcomicviewer2.R;

public class AboutSettingFragment extends PreferenceFragment {

    public AboutSettingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.about_preferences);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        switch (preference.getKey()) {
            case "app_github":
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(getActivity().getResources().getString(R.string.app_github));
                intent.setData(content_url);
                startActivity(intent);
                return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
