package org.huxizhijian.hhcomicviewer2.utils;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import org.huxizhijian.hhcomicviewer2.enities.Comic;
import org.huxizhijian.hhcomicviewer2.enities.ComicCapture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 基础工具类
 * Created by wei on 2016/8/20.
 */
public class BaseUtils {

    public static String getDownloadPath(ComicCapture comicCapture) {
        //获得下载目录
        StringBuilder path = new StringBuilder();
        String backslash = "/";  //反斜杠
        path.append(Constants.DEFAULT_DOWNLOAD_PATH).append(backslash);
        path.append(comicCapture.getComicTitle()).append(backslash);
        path.append(comicCapture.getCaptureName()).append(backslash);
        return path.toString();
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
            if (networkInfo.getExtraInfo().toLowerCase().equals("cmnet")) {
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
}
