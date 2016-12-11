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

package org.huxizhijian.hhcomicviewer2.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.activities.DownloadManagerActivity;
import org.huxizhijian.hhcomicviewer2.enities.ComicChapter;
import org.huxizhijian.hhcomicviewer2.service.DownloadManagerService;

/**
 * 通知工具类
 * Created by wei on 2016/9/14.
 */
public class NotificationUtil {

    private static NotificationManager sNotificationManager;
    private static NotificationUtil sNotificationUtil;
    private Context mContext;
    private Bitmap mBitmap = null;

    private NotificationUtil(Context context) {
        //获得通知服务
        sNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //创建一个通知的集合
        this.mContext = context;
        this.mBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher);
    }

    public static NotificationUtil getInstance(Context context) {
        if (sNotificationUtil == null) {
            sNotificationUtil = new NotificationUtil(context);
        }
        return sNotificationUtil;
    }

    /**
     * 显示通知
     *
     * @param comicChapter
     */
    public void showNotification(DownloadManagerService service, ComicChapter comicChapter) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        //统一设置
        builder.setSmallIcon(android.R.drawable.stat_sys_download)
                .setLargeIcon(mBitmap)
                .setAutoCancel(false)
                .setOngoing(true)
                .setCategory(NotificationCompat.CATEGORY_PROGRESS);
        builder.setTicker(comicChapter.getChapterName() + " 开始下载");
        //设置通知消息
        CharSequence contentTitle = comicChapter.getChapterName() + " - 开始下载"; // 通知栏标题
        CharSequence contentText = 0 + "/" + comicChapter.getPageCount(); // 通知栏内容
        //设置点击通知栏之后的操作
        Intent intent = new Intent(mContext, DownloadManagerActivity.class);
        //封装到PendingIntent中
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
        //统一设置
        builder.setContentTitle(contentTitle)
                .setContentText(contentText)
                .setContentIntent(contentIntent);
        builder.setProgress(comicChapter.getPageCount(), 0, true); //false为带刻度的进度条，true不明确进度
        Notification notification = builder.build();
        //发出通知，将service设为前台
        service.startForeground(comicChapter.getId(), notification);
    }

    public void cancelNotification(DownloadManagerService service, int id) {
        //取消通知
//        sNotificationManager.cancel(id);
        //将service的前台活动通知取消
        service.stopForeground(true);
    }

    /**
     * 通知漫画下载完毕
     *
     * @param comicChapter
     */
    public void finishedNotification(ComicChapter comicChapter) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        //小图标及滑动文字
        builder.setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setLargeIcon(mBitmap)
                .setTicker(comicChapter.getChapterName() + " 下载完毕")
                .setOngoing(false)
                .setAutoCancel(true);
        //设置通知消息
        CharSequence contentTitle = comicChapter.getComicTitle(); // 通知栏标题
        CharSequence contentText = comicChapter.getChapterName() + " 等 - 下载完毕"; // 通知栏内容
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
        Notification notification = builder.build();
        //发出通知
        sNotificationManager.notify(Constants.FINISHED_NOTIFICATION_ID, notification);
    }

    /**
     * 更新进度条
     */
    public void updateNotification(int id, ComicChapter comicChapter) {
            /*//如果当前通知是活动的，修改进度条
            int progress = (int) ((float) downloadPosition / (float) pageCount * 100f);
            notification.contentView.setProgressBar(R.id.progress_bar_notification, 100, progress, false);
            //修改下载数字
            notification.contentView.setTextViewText(R.id.textView_notification_progress, downloadPosition + "/" + pageCount);*/
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        //统一设置
        builder.setSmallIcon(android.R.drawable.stat_sys_download)
                .setLargeIcon(mBitmap)
                .setAutoCancel(false)
                .setOngoing(true)
                .setCategory(NotificationCompat.CATEGORY_PROGRESS);
        builder.setTicker(comicChapter.getChapterName() + " 正在下载");
        //设置通知消息
        CharSequence contentTitle = comicChapter.getChapterName() + " - 正在下载"; // 通知栏标题
        CharSequence contentText = comicChapter.getDownloadPosition() + "/" + comicChapter.getPageCount(); // 通知栏内容
        //设置点击通知栏之后的操作
        Intent intent = new Intent(mContext, DownloadManagerActivity.class);
        //封装到PendingIntent中
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
        //统一设置
        builder.setContentTitle(contentTitle)
                .setContentText(contentText)
                .setContentIntent(contentIntent);
        builder.setProgress(comicChapter.getPageCount(), comicChapter.getDownloadPosition(), false); //false为带刻度的进度条
        Notification notification = builder.build();
        //更新通知
        sNotificationManager.notify(id, notification);
    }
}
