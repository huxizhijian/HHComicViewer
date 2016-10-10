/*
 * Copyright 2016 huxizhijian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
