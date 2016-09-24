package org.huxizhijian.hhcomicviewer2.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.activities.DownloadManagerActivity;
import org.huxizhijian.hhcomicviewer2.enities.ComicCapture;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 通知工具类
 * Created by wei on 2016/9/14.
 */
public class NotificationUtil {

    private NotificationManager mNotificationManager;
    private Map<Integer, Notification> mNotifications;
    private Context mContext;

    public NotificationUtil(Context context) {
        //获得通知服务
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //创建一个通知的集合
        mNotifications = new HashMap<>();
        this.mContext = context;
    }

    /**
     * 显示通知
     *
     * @param comicCapture
     */
    public void showNotification(ComicCapture comicCapture) {
        if (!mNotifications.containsKey(comicCapture.getId())) {
            //创建通知对象
            Notification notification = new Notification();
            //设置滚动文字
            notification.tickerText = comicCapture.getCaptureName() + " 正在下载";
            //设置通知显示的时间
            notification.when = System.currentTimeMillis();
            //设置图标
            notification.icon = R.mipmap.ic_launcher;
            //设置通知的一些特性，点击清除后也不会清除
            notification.flags = Notification.FLAG_NO_CLEAR;
            //将其放到正在运行的组中
            notification.flags = Notification.FLAG_ONGOING_EVENT;
            //设置点击通知栏之后的操作
            Intent intent = new Intent(mContext, DownloadManagerActivity.class);
            notification.contentIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
            //创建RemoteViews对象
            RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.download_notification);
            //设置textView为文件名称
            remoteViews.setTextViewText(R.id.textView_notification, comicCapture.getCaptureName() + " - 正在下载");
            remoteViews.setTextViewText(R.id.textView_notification_progress, 0 + "/" + comicCapture.getPageCount());
            //设置remoteViews
            notification.contentView = remoteViews;
            //发出通知
            mNotificationManager.notify(comicCapture.getId(), notification);
            //把通知加到集合中
            mNotifications.put(comicCapture.getId(), notification);
        }
    }

    public void cancelNotification(int id) {
        //取消通知
        mNotificationManager.cancel(id);
        //将通知移除集合中
        mNotifications.remove(id);
    }

    public void cancelAll() {
        //取消所有通知
        Collection keys = mNotifications.keySet();
        for (Object key : keys) {
            Integer id = (Integer) key;
            mNotificationManager.cancel(id);
            mNotifications.remove(id);
        }
    }

    /**
     * 通知漫画下载完毕
     *
     * @param comicCapture
     */
    public void finishedNotification(ComicCapture comicCapture) {
        Notification.Builder builder = new Notification.Builder(mContext);
        //小图标及滑动文字
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setTicker(comicCapture.getCaptureName() + " 下载完毕");
        //设置通知消息
        CharSequence contentTitle = comicCapture.getComicTitle(); // 通知栏标题
        CharSequence contentText = comicCapture.getCaptureName() + " 等章节 - 下载完毕"; // 通知栏内容
        //设置点击通知栏之后的操作
        Intent intent = new Intent(mContext, DownloadManagerActivity.class);
        //封装到PendingIntent中
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
        //统一设置
        builder.setContentTitle(contentTitle)
                .setContentText(contentText)
                .setContentIntent(contentIntent);
        //设置点击通知栏的内容后会自动消失
        builder.setAutoCancel(true);
        Notification notification = builder.getNotification();
        //发出通知
        mNotificationManager.notify(Constants.FINISHED_NOTIFICATION_ID, notification);
    }

    /**
     * 更新进度条
     *
     * @param id
     * @param downloadPosition
     */
    public void updateNotification(int id, int downloadPosition, int pageCount) {
        Notification notification = mNotifications.get(id);
        if (notification != null) {
            //如果当前通知是活动的，修改进度条
            int progress = (int) ((float) downloadPosition / (float) pageCount * 100f);
            notification.contentView.setProgressBar(R.id.progress_bar_notification, 100, progress, false);
            //修改下载数字
            notification.contentView.setTextViewText(R.id.textView_notification_progress, downloadPosition + "/" + pageCount);
            mNotificationManager.notify(id, notification);
        }
    }
}
