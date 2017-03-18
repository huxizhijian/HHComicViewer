package org.huxizhijian.sdk.imagedownload.core;


import android.os.Process;

import org.huxizhijian.sdk.imagedownload.ImageDownloader;
import org.huxizhijian.sdk.imagedownload.core.db.DataBaseAdapter;
import org.huxizhijian.sdk.imagedownload.core.model.TaskInfo;
import org.huxizhijian.sdk.imagedownload.utils.ParsePicUrlList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Response;

/**
 * 下载线程调度器，同时用于获取下载的地址列表
 *
 * @author huxizhijian 2017/3/16
 */
public class ImageDownloadDispatcher extends Thread {

    private int mTaskCount;
    private ExecutorService mExecutorService;
    private final BlockingQueue<Request> mQueue; //请求事件队列
    private boolean mQuit = false; //线程是否推出
    public Request request;
    private DataBaseAdapter mAdapter;

    /**
     * 初始化
     *
     * @param taskCount 用户设置的下载线程数
     * @param queue     下载阻塞队列
     */
    public ImageDownloadDispatcher(int taskCount, BlockingQueue<Request> queue, DataBaseAdapter adapter) {
        mTaskCount = taskCount;
        mQueue = queue;
        mAdapter = adapter;
        mExecutorService = Executors.newCachedThreadPool();
    }

    /**
     * 无限循环取任务
     */
    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        while (true) {
            try {
                request = mQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
                if (mQuit) {
                    return;
                }
                continue;
            }
            //1、获取网络中地址列表
            okhttp3.Request requestChapter = new okhttp3.Request.Builder().url(request.getUri()).get().build();
            try {
                Response response = ImageDownloader.getInstance().getClient().newCall(requestChapter).execute();
                final String content = new String(response.body().bytes(), "gb2312");
                ArrayList<String> picList = ParsePicUrlList.scanPicInPage(request.getServerId(), content);
                //2、使request知晓地址集的大小
                request.setPicList(picList);
                //3、查找或者创建Task对象，放入request中并在线程池中开始线程
                List<TaskInfo> infos = mAdapter.findByChid(request.getChid());
                //如果已经存在数据库中，则继续下载
                if (infos != null && infos.size() != 0) {
                    for (TaskInfo info : infos) {
                        ImageDownloadTask task = new ImageDownloadTask(mAdapter,
                                info, picList);
                        request.addTask(task);
                        mExecutorService.execute(task);
                    }
                } else {
                    //如果不存在数据库中，则创建
                    if ((picList.size() / mTaskCount) != 0) {
                        for (int i = 0; i < mTaskCount; i++) {
                            TaskInfo info = new TaskInfo(i, mTaskCount, 0, request.getChid(), request.getDownloadPath());
                            //加入数据库中
                            mAdapter.add(info);
                            ImageDownloadTask task = new ImageDownloadTask(mAdapter,
                                    info, picList);
                            request.addTask(task);
                            mExecutorService.execute(task);
                        }
                    } else {
                        TaskInfo info = new TaskInfo(0, 1, 0, request.getChid(), request.getDownloadPath());
                        mAdapter.add(info);
                        ImageDownloadTask task = new ImageDownloadTask(mAdapter,
                                info, picList);
                        request.addTask(task);
                        mExecutorService.execute(task);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                request.onError(e);
            }
        }
    }

    public void quit() {
        mQuit = true;
        interrupt();
    }

}
