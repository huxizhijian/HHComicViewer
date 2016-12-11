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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.turhanoz.android.reactivedirectorychooser.event.OnDirectoryChosenEvent;
import com.turhanoz.android.reactivedirectorychooser.ui.DirectoryChooserFragment;

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.activities.PreferenceActivity;
import org.huxizhijian.hhcomicviewer2.utils.Constants;

import java.io.File;

import static android.content.Context.MODE_PRIVATE;

public class DownloadSettingFragment extends PreferenceFragment {

    private SharedPreferences mPreferences;
    private String mDownloadPath;

    private Preference mPreference;

    public DownloadSettingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.download_preferences);
        //获取SharedPreferences
        mPreferences = getActivity().getSharedPreferences("user", MODE_PRIVATE);
        //更改下载目录的summary
        mPreference = getPreferenceManager().findPreference("download_path");
        mDownloadPath = mPreferences.getString("download_path", Constants.DEFAULT_DOWNLOAD_PATH);
        mPreference.setSummary(mDownloadPath);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, final Preference preference) {
        switch (preference.getKey()) {
            case "download_path":
                Dialog.Builder builder = null;
                builder = new SimpleDialog.Builder() {
                    @Override
                    public void onPositiveActionClicked(DialogFragment fragment) {
                        PreferenceActivity activity = (PreferenceActivity) getActivity();
                        activity.checkPermission();
                        super.onPositiveActionClicked(fragment);
                    }

                    @Override
                    public void onNegativeActionClicked(DialogFragment fragment) {
                        super.onNegativeActionClicked(fragment);
                    }
                };

                ((SimpleDialog.Builder) builder).message("在Android4.4及以后的版本中，无法把数据写入外置SD卡，除非你进行了root操作。" +
                        "不过<SDcard>/Android/data/org.huxizhijian.hhcomicviewer2 这个路径比较特殊，是可以写入的。" +
                        "但是使用此文件夹卸载了程序之后所有下载文件都会删除。")
                        .title("注意")
                        .positiveAction("继续");
                DialogFragment fragment = DialogFragment.newInstance(builder);
                fragment.show(((PreferenceActivity) getActivity()).getSupportFragmentManager(), null);
                return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    public void openDirectChooserDialog() {
        File file = new File(mDownloadPath);
        DirectoryChooserFragment directoryChooserFragment = DirectoryChooserFragment.newInstance(file);
        FragmentTransaction transaction =
                ((PreferenceActivity) getActivity()).getSupportFragmentManager().beginTransaction();
        directoryChooserFragment.show(transaction, "RDC");
    }

    public void onEvent(OnDirectoryChosenEvent onDirectoryChosenEvent) {
        File directoryChosenByUser = onDirectoryChosenEvent.getFile();
        if (directoryChosenByUser.canWrite()) {
            //写入配置
            mDownloadPath = directoryChosenByUser.getAbsolutePath();
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putString("download_path", mDownloadPath);
            editor.apply();
            mPreference.setSummary(mDownloadPath);
        } else {
            Toast.makeText(getActivity(), "该目录不可写", Toast.LENGTH_SHORT).show();
        }
    }
}
