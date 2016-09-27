package org.huxizhijian.hhcomicviewer2.fragment;


import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;

import org.huxizhijian.hhcomicviewer2.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReadingSettingFragment extends PreferenceFragment {


    public ReadingSettingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.reading_preferences);
    }

}
