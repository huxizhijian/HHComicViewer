/*
 * Copyright 2018 huxizhijian
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

package org.huxizhijian.hhcomicviewer.utils;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.StatFs;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.huxizhijian.hhcomicviewer.app.HHApplication;
import org.huxizhijian.hhcomicviewer.model.Comic;
import org.huxizhijian.hhcomicviewer.model.ComicChapter;
import org.huxizhijian.hhcomicviewer.option.HHComicWebVariable;
import org.huxizhijian.sdk.sharedpreferences.SharedPreferencesManager;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

/**
 * 基础工具类
 * Created by wei on 2016/8/20.
 */
public class CommonUtils {

    public static String getDownloadPath(Context context, ComicChapter comicChapter) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", MODE_PRIVATE);
        String downloadPath = sharedPreferences.getString("download_path", Constants.DEFAULT_DOWNLOAD_PATH);
        //获得下载目录
        StringBuilder path = new StringBuilder();
        String backslash = "/";  //反斜杠
        path.append(downloadPath).append(backslash);
        path.append(comicChapter.getComicTitle()).append(backslash);
        path.append(comicChapter.getChapterName()).append(backslash);
        return path.toString();
    }

    public static int getStorageBlockSpacePercent(String path) {
        try {
            //获取剩余存储空间
            File filePath = new File(path);
            StatFs sf = new StatFs(filePath.getPath());//创建StatFs对象
            long blockSize = sf.getBlockSize();
            long totalBlock = sf.getBlockCount();//获得全部block
            long availableBlock = sf.getAvailableBlocks();//获取可用的block
            return countProgress(totalBlock * blockSize, availableBlock * blockSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int countProgress(long phoneTotalSize, long phoneAvailableSize) {

        double totalSize = phoneTotalSize / (1024 * 3);
        double availabSize = phoneAvailableSize / (1024 * 3);
        //取整相减
        int size = (int) (Math.floor(totalSize) - Math.floor(availabSize));
        double v = (size / Math.floor(totalSize)) * 100;
        return (int) Math.floor(v);
    }


    public static String getStorageBlockSpace(String path) {
        try {
            //获取剩余存储空间
            File filePath = new File(path);
            StatFs sf = new StatFs(filePath.getPath());//创建StatFs对象
            long blockSize = sf.getBlockSize();//获得blockSize
            long totalBlock = sf.getBlockCount();//获得全部block
            long availableBlock = sf.getAvailableBlocks();//获取可用的block
            //用String数组来存放Block信息
            String[] total = fileSize(totalBlock * blockSize);
            String[] available = fileSize(availableBlock * blockSize);
            return "剩余空间：" + available[0] + available[1] + "/" + total[0] + total[1];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    //用来定义存储空间显示格式
    private static String[] fileSize(long size) {
        String s = "";
        if (size > 1024) {
            s = "KB";
            size /= 1024;
            if (size > 1024) {
                s = "MB";
                size /= 1024;
            }
        }
        DecimalFormat df = new DecimalFormat();
        df.setGroupingSize(3);
        String[] result = new String[3];
        result[0] = df.format(size);
        result[1] = s;
        return result;
    }

    public static String getDownloadPathRoot(Comic comic) {
        //获得下载目录
        StringBuilder path = new StringBuilder();
        String backslash = "/";  //反斜杠
        path.append(Constants.DEFAULT_DOWNLOAD_PATH).append(backslash);
        path.append(comic.getTitle()).append(backslash);
        return path.toString();
    }

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

    private static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        return file.isFile() && file.exists() && file.delete();
    }

    public static boolean deleteDirectory(String filePath) {
        boolean flag = false;
        //如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        File[] files = dirFile.listFiles();
        //遍历删除文件夹下的所有文件(包括子目录)
        for (File file : files) {
            if (file.isFile()) {
                //删除子文件
                flag = deleteFile(file.getAbsolutePath());
                if (!flag) break;
            } else {
                //删除子目录
                flag = deleteDirectory(file.getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前空目录
        return dirFile.delete();
    }

    public static boolean deleteDirectoryParent(String filePath) {
        boolean flag = false;
        //如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }

        File parentPath = new File(filePath).getParentFile();

        if (!parentPath.exists() || !parentPath.isDirectory()) {
            return false;
        }
        flag = true;
        File[] files = parentPath.listFiles();
        //遍历删除文件夹下的所有文件(包括子目录)
        for (File file : files) {
            if (file.isFile()) {
                //删除子文件
                flag = deleteFile(file.getAbsolutePath());
                if (!flag) break;
            } else {
                //删除子目录
                flag = deleteDirectory(file.getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前空目录
        return parentPath.delete();
    }

    public static int getwidthPixels(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }

    public static int getheightPixels(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getHeight();
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取当前的网络状态 -1：没有网络 1：WIFI网络2：wap网络3：net网络
     */
    public final static int CMNET = 3;
    public final static int CMWAP = 2;
    public final static int WIFI = 1;
    public final static int NONEWTWORK = -1;

    public static int getAPNType(Context context) {
        int netType = -1;
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            if ("cmnet".equals(networkInfo.getExtraInfo().toLowerCase())) {
                netType = CMNET;
            } else {
                netType = CMWAP;
            }
        }
        if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = WIFI;
        }
        return netType;
    }

    public static String getAPNType_String(int type) {
        String type_status = "网络异常";
        switch (type) {
            case NONEWTWORK:
                type_status = "没有网络 ";
                break;
            case WIFI:
                type_status = "WIFI";
                break;
            case CMWAP:
                type_status = "CMWAP";
                break;
            case CMNET:
                type_status = "CMNET";
                break;
            default:
                break;
        }
        return type_status;
    }

    public static String getNowDate() {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");// 设置日期格式
        // SimpleDateFormat df = new
        // SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String date = df.format(new Date(System.currentTimeMillis()));// new
        // Date()为获取当前系统时间
        return date;
    }

    public static void initActionBar(ActionBar actionBar, int newColor) {
        Drawable colorDrawable = new ColorDrawable(newColor);
        LayerDrawable ld = new LayerDrawable(new Drawable[]{colorDrawable});
        if (actionBar != null) {
//            System.out.println("action bar != null");
            actionBar.setBackgroundDrawable(ld);
        }
    }

    public static void hideInputMethod(View view, Context context) {
        if (context instanceof Activity) {
            InputMethodManager manager = (InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (manager.isActive()) {
                manager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    public static void setStatusBarTint(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) return;
        setTranslucentStatus(activity, true);
        SystemBarTintManager tintManager = new SystemBarTintManager(activity);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintColor(color);
    }

    @TargetApi(19)
    private static void setTranslucentStatus(Activity activity, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    /**
     * 根据页面规律组合出漫画url
     *
     * @param cid
     * @return
     */
    public static String getComicUrl(int cid) {
        HHComicWebVariable variable = HHApplication.getInstance().getHHWebVariable();
        return variable.getCsite() + variable.getPre() + cid + ".html";
    }

    /**
     * 根据页面规律组合出章节url
     *
     * @param cid
     * @param chid
     * @param serverId
     * @return
     */
    public static String getChapterUrl(int cid, long chid, int serverId) {
        HHComicWebVariable variable = HHApplication.getInstance().getHHWebVariable();
        return variable.getChsite() + cid + "/" + chid + variable.getBehind() + serverId;
    }

    /**
     * 实现业务：允许媒体扫描，默认已经在6.0的系统上获取到写sd卡权限
     *
     * @param context 用于获取sp
     * @return 创建或者已经存在返回true，否则返回false
     * @throws IOException 错误不进行处理
     */
    public static boolean createNomediaIfAllow(Context context) throws IOException {
        //指示是否创建nomedia
        boolean exist;

        SharedPreferencesManager manager = new SharedPreferencesManager(context);
        boolean allow = manager.getBoolean("allow_media", false);
        String downloadPath = manager.getString("download_path", Constants.DEFAULT_DOWNLOAD_PATH);
        File dir = new File(downloadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File nomediaFile = new File(dir, ".nomedia");
        exist = nomediaFile.exists();

        if (allow) {
            //如果存在则删除.nomedia文件
            if (exist) {
                exist = !nomediaFile.delete();
            }
        } else {
            //创建.nomedia文件
            if (!exist) {
                exist = nomediaFile.createNewFile();
            }
        }
        return exist;
    }

}
