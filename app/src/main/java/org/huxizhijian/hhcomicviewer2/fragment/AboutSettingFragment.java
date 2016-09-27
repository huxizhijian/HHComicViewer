package org.huxizhijian.hhcomicviewer2.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import org.huxizhijian.hhcomicviewer2.R;

public class AboutSettingFragment extends PreferenceFragment {

    public AboutSettingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.about_preferences);
    }
}
