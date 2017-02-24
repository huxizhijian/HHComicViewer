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
package org.huxizhijian.sdk.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * @author huxizhijian 2017/2/22
 */
public class SharedPreferencesManager {

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    public SharedPreferencesManager(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mEditor = mSharedPreferences.edit();
    }

    public SharedPreferencesManager(Context context, String name) {
        mSharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    public SharedPreferencesManager(Context context, String name, int mode) {
        mSharedPreferences = context.getSharedPreferences(name, mode);
        mEditor = mSharedPreferences.edit();
    }

    public void putString(String key, String value) {
        mEditor.putString(key, value);
        mEditor.apply();
    }

    public String getString(String key, String defaultValue) {
        return mSharedPreferences.getString(key, defaultValue);
    }

    public void putInt(String key, int value) {
        mEditor.putInt(key, value);
        mEditor.apply();
    }

    public int getInt(String key, int defaultValue) {
        return mSharedPreferences.getInt(key, defaultValue);
    }

    public void putBoolean(String key, boolean value) {
        mEditor.putBoolean(key, value);
        mEditor.apply();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return mSharedPreferences.getBoolean(key, defaultValue);
    }

}
