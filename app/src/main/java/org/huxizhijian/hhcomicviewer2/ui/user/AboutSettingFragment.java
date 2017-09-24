/*
 * Copyright 2017 huxizhijian
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

package org.huxizhijian.hhcomicviewer2.ui.user;

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
        Intent intent = null;
        switch (preference.getKey()) {
            case "app_github":
                intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(getActivity().getResources().getString(R.string.app_github));
                intent.setData(content_url);
                startActivity(intent);
                return true;
            case "open_reading_setting":
                intent = new Intent(getActivity(), PreferenceActivity.class);
                intent.setAction(PreferenceActivity.ACTION_READING);
                startActivity(intent);
                return true;
            case "open_advance_setting":
                intent = new Intent(getActivity(), PreferenceActivity.class);
                intent.setAction(PreferenceActivity.ACTION_ADVANCE);
                startActivity(intent);
                return true;
            case "open_download_setting":
                intent = new Intent(getActivity(), PreferenceActivity.class);
                intent.setAction(PreferenceActivity.ACTION_DOWNLOAD);
                startActivity(intent);
                return true;
            default:
                break;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
