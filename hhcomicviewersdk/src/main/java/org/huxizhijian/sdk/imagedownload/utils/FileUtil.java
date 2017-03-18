package org.huxizhijian.sdk.imagedownload.utils;

import android.os.Environment;

/**
 * @author huxizhijian 2017/3/16
 */
public class FileUtil {

    /**
     * 获取下载页面的文件名
     *
     * @param position 相对于整个页数的position
     * @return 文件名
     */
    public static String getPageName(int position) {
        String pageName = null;
        if (position < 10) {
            pageName = "000" + position + ".jpg";
        } else if (position < 100) {
            pageName = "00" + position + ".jpg";
        } else if (position < 1000) {
            pageName = "0" + position + ".jpg";
        } else {
            pageName = position + ".jpg";
        }
        return pageName;
    }

    public static String getTestDownloadPath() {
        //获得下载目录
        return Environment.getExternalStorageDirectory().getPath() + "/HHComic" + "/源君物语" + "/第一集";
    }

}
