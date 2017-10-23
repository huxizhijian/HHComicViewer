package org.huxizhijian.hhcomic.comic.download;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.huxizhijian.hhcomic.comic.bean.DownloadInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 下载管理类
 *
 * @author huxizhijian
 * @date 2017/10/20
 */
public class DownloadManager {

    /**
     * 缓存所有downloadinfo
     */
    private final LinkedList<DownloadInfo> mAllInfoList;

    private final Map<String, DownloadInfo> mAllInfoMap;

    private final LinkedList<DownloadInfo> mWaitList;

    private DownloadListener mDownloadListener;

    private final List<DownloadInfoListener> mDownloadInfoListeners;

    @Nullable
    private DownloadInfo mCurrentTask;
    @Nullable
    private SpiderQueen mCurrentSpider;

    public DownloadManager() {
        // 获取到所有info
//        mAllInfoList =
        mAllInfoList = new LinkedList<>();

        // 创建infoMap
        mAllInfoMap = new HashMap<>();
        int size = mAllInfoMap.size();
        // 添加info到MAP中
        for (int i = 0; i < size; i++) {
            DownloadInfo info = mAllInfoList.get(i);
            mAllInfoMap.put(info.source + info.chapterId, info);
        }

        mWaitList = new LinkedList<>();
        mDownloadInfoListeners = new ArrayList<>();

    }

    public boolean containDownloadInfo(int source, String chapterId) {
        return mAllInfoMap.containsKey(source + chapterId);
    }

    public DownloadInfo getDownloadInfo(int source, String chapterId) {
        return mAllInfoMap.get(source + chapterId);
    }

    public int getDownloadState(int source, String chapterId) {
        DownloadInfo info = mAllInfoMap.get(source + chapterId);
        if (null != info) {
            return info.state;
        } else {
            return DownloadInfo.STATE_INVALID;
        }
    }

    public void addDownloadInfoListener(@Nullable DownloadInfoListener downloadInfoListener) {
        mDownloadInfoListeners.add(downloadInfoListener);
    }

    public void removeDownloadInfoListener(@Nullable DownloadInfoListener downloadInfoListener) {
        mDownloadInfoListeners.remove(downloadInfoListener);
    }

    public void setDownloadListener(@Nullable DownloadListener downloadListener) {
        mDownloadListener = downloadListener;
    }

    /**
     * todo
     * 确认接下来的下载任务
     */
    private void ensureDownload() {
        if (mCurrentTask != null) {
            // 只应有一个任务正在运行
            return;
        }

        // 从WaitList中获取接下来的下载任务
        if (!mWaitList.isEmpty()) {
            DownloadInfo info = mWaitList.removeFirst();
            // 创建一个解析下载类的实例
            SpiderQueen spider = new SpiderQueen();
            mCurrentTask = info;
            mCurrentSpider = spider;
            // 添加监听事件
            // 初始化下载info
            info.state = DownloadInfo.STATE_DOWNLOAD;
            info.speed = -1;
            info.remaining = -1;
            info.total = -1;
            info.finished = 0;
            info.downloaded = 0;
            info.legacy = -1;
            // 添加该info到db中
            // 通知该下载任务开始下载
            if (mDownloadListener != null) {
                mDownloadListener.onStart(info);
            }
            // 通知info状态改变
            List<DownloadInfo> list = mAllInfoList;
            if (list != null) {
                for (DownloadInfoListener l : mDownloadInfoListeners) {
                    l.onUpdate(info, list);
                }
            }

        }
    }

    public interface DownloadInfoListener {
        /**
         * 将指定info加入指定位置
         *
         * @param info     指定info
         * @param list     infoList
         * @param position 位置
         */
        void onAdd(@NonNull DownloadInfo info, @NonNull List<DownloadInfo> list, int position);

        /**
         * 指定的info有变化
         *
         * @param info info
         * @param list infoList
         */
        void onUpdate(@NonNull DownloadInfo info, @NonNull List<DownloadInfo> list);

        /**
         * 可能所有info改变了，但是size没变
         */
        void onUpdateAll();

        /**
         * 可能也许所有数据改变了，list也改变了
         */
        void onReload();

        /**
         * 移除指定位置的info
         *
         * @param info     info
         * @param list     infoList
         * @param position 位置
         */
        void onRemove(@NonNull DownloadInfo info, @NonNull List<DownloadInfo> list, int position);
    }


    public interface DownloadListener {

        /**
         * 开始下载
         *
         * @param info info
         */
        void onStart(DownloadInfo info);

        /**
         * 更新下载速度
         *
         * @param info info
         */
        void onDownload(DownloadInfo info);

        /**
         * 更新下载页数
         *
         * @param info info
         */
        void onGetPage(DownloadInfo info);

        /**
         * 下载完成
         *
         * @param info info
         */
        void onFinish(DownloadInfo info);

        /**
         * 下载取消
         *
         * @param info info
         */
        void onCancel(DownloadInfo info);
    }
}
