package org.huxizhijian.hhcomicviewer2.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.widget.Toast;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.utils.Constants;
import org.huxizhijian.hhcomicviewer2.widget.FilePicker;

import java.io.File;

import static android.content.Context.MODE_PRIVATE;

public class DownloadSettingFragment extends PreferenceFragment {

    SharedPreferences mPreferences;
    String mDownloadPath;

    public DownloadSettingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.download_preferences);
        //获取SharedPreferences
        mPreferences = getActivity().getSharedPreferences("user", MODE_PRIVATE);
        //更改下载目录的summary
        Preference preference = getPreferenceManager().findPreference("download_path");
        mDownloadPath = mPreferences.getString("download_path", Constants.DEFAULT_DOWNLOAD_PATH);
        preference.setSummary(mDownloadPath);
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
                    "但是使用此文件夹卸载了程序之后所有下载文件都会删除。" + "如果要下载在新的文件夹中，要先在系统自带的文件管理中创建。");
            materialDialog.setOnBtnClickL(new OnBtnClickL() {
                @Override
                public void onBtnClick() {
                    materialDialog.dismiss();
                    FilePicker picker = new FilePicker(getActivity(), FilePicker.DIRECTORY);
                    picker.setRootPath(mDownloadPath);
                    picker.setOnFilePickListener(new FilePicker.OnFilePickListener() {
                        @Override
                        public void onFilePicked(String currentPath) {
                            File file = new File(currentPath);
                            if (file.canWrite()) {
                                //写入配置
                                mDownloadPath = currentPath;
                                SharedPreferences.Editor editor = mPreferences.edit();
                                editor.putString("download_path", currentPath);
                                editor.apply();
                                preference.setSummary(currentPath);
                            } else {
                                Toast.makeText(getActivity(), "该目录不可写", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    picker.show();
                }
            });
            materialDialog.setCancelable(false);
            materialDialog.show();
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
