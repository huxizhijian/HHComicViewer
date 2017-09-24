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

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;

import net.rdrei.android.dirchooser.DirectoryChooserConfig;
import net.rdrei.android.dirchooser.DirectoryChooserFragment;

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.utils.Constants;
import org.huxizhijian.sdk.sharedpreferences.SharedPreferencesManager;

import java.io.File;

public class DownloadSettingFragment extends PreferenceFragment implements
        DirectoryChooserFragment.OnFragmentInteractionListener {

    private SharedPreferencesManager mPreferencesManager;
    private String mDownloadPath;

    private DirectoryChooserFragment mDialog;

    private Preference mPreference;

    public DownloadSettingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.download_preferences);
        //获取SharedPreferences
        mPreferencesManager = new SharedPreferencesManager(getActivity(), Constants.SHARED_PREFERENCES_NAME);
        //更改下载目录的summary
        mPreference = getPreferenceManager().findPreference("download_path");
        mDownloadPath = mPreferencesManager.getString("download_path", Constants.DEFAULT_DOWNLOAD_PATH);
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
                        activity.checkPermission(PreferenceActivity.OPEN_DIALOG);
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
            case "allow_media":
                CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preference;
                SharedPreferencesManager manager = new SharedPreferencesManager(getActivity());
                manager.putBoolean("allow_media", checkBoxPreference.isChecked());
                PreferenceActivity activity = (PreferenceActivity) getActivity();
                activity.checkPermission(PreferenceActivity.MAKE_NO_MEDIA);
                return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    public void openDirectChooserDialog() {
        DirectoryChooserConfig config = DirectoryChooserConfig.builder()
                .newDirectoryName("HHComicDownload")
                .allowNewDirectoryNameModification(true)
                .initialDirectory(mDownloadPath)
                .build();
        mDialog = DirectoryChooserFragment.newInstance(config);
        mDialog.setTargetFragment(this, 0);
        mDialog.show(getFragmentManager(), null);
    }

    public void onSelectDirectory(@NonNull String path) {
        mDialog.dismiss();
        File directoryPath = new File(path);
        if (directoryPath.canWrite()) {
            //写入配置
            mDownloadPath = directoryPath.getAbsolutePath();
            mPreferencesManager.putString("download_path", mDownloadPath);
            mPreference.setSummary(mDownloadPath);
        } else {
            Toast.makeText(getActivity(), "该目录不可写", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCancelChooser() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }
}
