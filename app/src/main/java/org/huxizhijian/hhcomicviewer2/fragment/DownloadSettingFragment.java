package org.huxizhijian.hhcomicviewer2.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.turhanoz.android.reactivedirectorychooser.event.OnDirectoryChosenEvent;
import com.turhanoz.android.reactivedirectorychooser.ui.DirectoryChooserFragment;

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.activities.PreferenceActivity;
import org.huxizhijian.hhcomicviewer2.utils.Constants;

import java.io.File;

import static android.content.Context.MODE_PRIVATE;

public class DownloadSettingFragment extends PreferenceFragment {

    SharedPreferences mPreferences;
    String mDownloadPath;

    Preference mPreference;

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
        if (preference.getKey().equals("download_path")) {
            final MaterialDialog materialDialog = new MaterialDialog(getActivity());
            materialDialog.btnText("继续");
            materialDialog.btnNum(1);
            materialDialog.setTitle("注意");
            materialDialog.content("在Android4.4及以后的版本中，无法把数据写入外置SD卡，除非你进行了root操作。" +
                    "不过<SDcard>/Android/data/org.huxizhijian.hhcomicviewer2 这个路径比较特殊，是可以写入的。" +
                    "但是使用此文件夹卸载了程序之后所有下载文件都会删除。");
            materialDialog.setOnBtnClickL(new OnBtnClickL() {
                @Override
                public void onBtnClick() {
                    materialDialog.dismiss();
                    PreferenceActivity activity = (PreferenceActivity) getActivity();
                    activity.checkPermission();
                }
            });
            materialDialog.setCancelable(false);
            materialDialog.show();
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
