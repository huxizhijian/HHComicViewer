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

package org.huxizhijian.hhcomicviewer.ui.user;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.widget.Toast;

import org.huxizhijian.hhcomicviewer.R;
import org.huxizhijian.sdk.SDKConstant;
import org.huxizhijian.sdk.imageloader.ImageLoaderOptions;
import org.huxizhijian.sdk.sharedpreferences.SharedPreferencesManager;

/**
 * @author huxizhijian
 */
public class AdvanceSettingFragment extends PreferenceFragment {

    SharedPreferencesManager mPreferencesManager;

    public AdvanceSettingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.advance_preferences);
        mPreferencesManager = new SharedPreferencesManager(getActivity());
        String cacheSize = mPreferencesManager.getString("reading_cache_size", "160MB");
        Preference preference = getPreferenceManager().findPreference("reading_cache_size");
        preference.setSummary(cacheSize);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        switch (preference.getKey()) {
            case "reading_cache_size":
                preference.setSummary(((ListPreference) preference).getValue());
                new ImageLoaderOptions(getActivity())
                        .setCacheSize(preference.getKey(), SDKConstant.DEFAULT_CACHE_NAME);
                return true;
            case "clear_cache":
                //清除在线阅读缓存
                new AsyncTask<Context, Void, Void>() {
                    @Override
                    protected Void doInBackground(Context... params) {
                        Context context = params[0];
                        ImageLoaderOptions.getImageLoaderManager().clearDiskCache(context);
                        return null;
                    }
                }.execute(getActivity());
                Toast.makeText(getActivity(), "清除缓存成功", Toast.LENGTH_SHORT).show();
                return true;
            case "update_variable":
                Toast.makeText(getActivity(), "本功能暂未实装", Toast.LENGTH_SHORT).show();
                return true;
            default:
                break;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
