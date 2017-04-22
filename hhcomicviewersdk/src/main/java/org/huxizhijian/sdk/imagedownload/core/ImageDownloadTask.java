package org.huxizhijian.sdk.imagedownload.core;

import android.os.Process;
import android.util.Log;

import org.huxizhijian.sdk.imagedownload.ImageDownloader;
import org.huxizhijian.sdk.imagedownload.core.db.DataBaseAdapter;
import org.huxizhijian.sdk.imagedownload.core.model.TaskInfo;
import org.huxizhijian.sdk.imagedownload.listener.TaskProgressListener;
import org.huxizhijian.sdk.imagedownload.utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * @author huxizhijian 2017/3/15
 */
public class ImageDownloadTask extends Thread {

    private TaskInfo mTaskInfo;
    private List<String> mPicList;
    private TaskProgressListener mListener;
    private OkHttpClient mClient;
    private DataBaseAdapter mAdapter;

    private ImageDownloader mImageDownloader = ImageDownloader.getInstance();

    private int mTaskPageStart;
    private int mTaskPageSize;

    boolean isPause = false;
    boolean isRealPause = false;
    boolean isFinished = false; //标识线程是否下载完毕
    boolean isError = false; //标识下载线程是否都下载错误

    /**
     * @param adapter   db管理适配器
     * @param mTaskInfo model类实例
     * @param picList   完整漫画页面地址列表
     */
    public ImageDownloadTask(DataBaseAdapter adapter, TaskInfo mTaskInfo, List<String> picList) {
        this.mTaskInfo = mTaskInfo;
        this.mPicList = picList;
        this.mAdapter = adapter;
        mListener = getDefaultListener();
        //设置为后台线程（nice值为10）
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        //计算本线程下载的范围
        mTaskPageSize = mPicList.size() / mTaskInfo.getTaskCount();
        mTaskPageStart = mTaskInfo.getTaskPosition() * mTaskPageSize;
        //如果是最后一个线程，则把剩余页数一齐下载
        if (mTaskInfo.getTaskPosition() == mTaskInfo.getTaskCount() - 1) {
            mTaskPageSize = mPicList.size() - mTaskPageSize * (mTaskInfo.getTaskCount() - 1);
        }
        if (mImageDownloader == null) {
            mImageDownloader = ImageDownloader.getInstance();
        }
        if (mImageDownloader.isDebug()) {
            String threadInfo = "ImageDownloadTask: thread - " + mTaskInfo.getTaskPosition();
            Log.i(TAG, threadInfo + " task page size : " + mTaskPageSize);
            Log.i(TAG, threadInfo + " task page start : " + mTaskPageStart);
        }
    }

    /**
     * 设置回调
     *
     * @param listener 下载过程回调
     */
    public void setListener(TaskProgressListener listener) {
        mListener = listener;
    }

    public int getProgress() {
        return mTaskInfo.getDownloadPosition();
    }

    @Override
    public void run() {
        RandomAccessFile raf = null;
        Response response = null;
        //如果文件夹不存在，创建写入文件夹
        File filePath = new File(mTaskInfo.getDownloadPath());
        try {
            if (!filePath.exists()) {
                if (mImageDownloader.isDebug()) {
                    Log.i(TAG, "run: make dir : " + filePath.getAbsolutePath());
                }
                filePath.mkdirs();
            }

            while (mTaskInfo.getDownloadPosition() < mTaskPageSize) {
                Request.Builder builder = new Request.Builder().url(mPicList
                        .get(mTaskPageStart + mTaskInfo.getDownloadPosition()));

                if (mClient == null)
                    mClient = mImageDownloader.getClient();

                //初始化文件长度
                if (mTaskInfo.getLength() == -1) {
                    response = mClient.newCall(builder.build()).execute();
                    //从头开始下载
                    long length = -1;
                    //获得文件长度
                    if (response.isSuccessful()) {
                        length = response.body().contentLength();
                    } else {
                        if (mImageDownloader.isDebug()) {
                            Log.i(TAG, "run: 从网络获取文件错误！");
                        }
                        throw new IOException();
                    }
                    File file = null;

                    file = new File(filePath, FileUtil.getPageName(mTaskPageStart +
                            mTaskInfo.getDownloadPosition()));
                    //可以在任意位置进行写入的输出流
                    raf = new RandomAccessFile(file, "rwd");
                    //设置本地文件的长度
                    if (length > 0) {
                        raf.setLength(length);
                    }
                    raf.seek(0);
                    int finished = mTaskInfo.getFinished();
                    InputStream input = response.body().byteStream();
                    byte[] buffer = new byte[1024];
                    int len = -1;
                    while ((len = input.read(buffer)) != -1) {
                        //将数据写入文件
                        raf.write(buffer, 0, len);
                        //累加每个线程完成进度
                        finished += len;
                        mTaskInfo.setFinished(finished);
                        //在下载暂停时将进度保存至数据库
                        if (isPause) {
                            mAdapter.update(mTaskInfo);
                            isRealPause = true;
                            mListener.onPaused(mTaskInfo.getTaskPosition(), mTaskInfo.getDownloadPosition(),
                                    mTaskPageSize);
                            return;
                        }
                    }
                } else {
                    //如果之前进行过本页的下载，设置线程的下载位置
                    int start = mTaskInfo.getFinished();
                    response = mClient.newCall(builder.addHeader("Range",
                            "bytes=" + start + "-" + mTaskInfo.getLength()).build()).execute();
                    //设置写入的文件
                    File file = new File(filePath, FileUtil.getPageName(mTaskPageStart +
                            mTaskInfo.getDownloadPosition()));
                    raf = new RandomAccessFile(file, "rwd");
                    raf.seek(start);
                    int finished = mTaskInfo.getFinished();
                    //开始下载，由于设置了range，返回代码为部分下载(partial)
                    if (response.isSuccessful()) {
                        //读取数据
                        InputStream input = response.body().byteStream();
                        byte[] buffer = new byte[1024];
                        int len = -1;

                        while ((len = input.read(buffer)) != -1) {
                            //将数据写入文件
                            raf.write(buffer, 0, len);
                            //累加每个线程完成进度
                            finished += len;
                            mTaskInfo.setFinished(finished);
                            //在下载暂停时将进度保存至数据库
                            if (isPause) {
                                mAdapter.update(mTaskInfo);
                                isRealPause = true;
                                mListener.onPaused(mTaskInfo.getTaskPosition(), mTaskInfo.getDownloadPosition(),
                                        mTaskPageSize);
                                return;
                            }
                        }
                    } else {
                        if (mImageDownloader.isDebug()) {
                            Log.i(TAG, "run: 从网络获取文件错误！");
                        }
                        throw new IOException();
                    }
                }

                //一页下载完毕
                //重置下载进度
                mTaskInfo.setFinished(0);
                //设置长度为需要重新获取
                mTaskInfo.setLength(-1);
                //将下载进度往前推一页
                if (mImageDownloader.isDebug()) {
                    Log.i(TAG, "chid-" + mTaskInfo.getChid() + "-thread-" + mTaskInfo.getTaskPosition() +
                            " finish page " + mTaskInfo.getDownloadPosition());
                }
                mTaskInfo.setDownloadPosition(mTaskInfo.getDownloadPosition() + 1);
                mAdapter.update(mTaskInfo);
                //回调进度更新
                mListener.onProgressUpdate(mTaskInfo.getTaskPosition(), mTaskInfo.getDownloadPosition(),
                        mTaskPageSize);
            }

            //标识线程执行完毕
            isFinished = true;
            mListener.onFinished(mTaskInfo.getTaskPosition(), mTaskInfo.getDownloadPosition(),
                    mTaskPageSize);
        } catch (IOException e) {
            e.printStackTrace();
            //保存进度
            mAdapter.update(mTaskInfo);
            isError = true;
            mListener.onFailure(mTaskInfo.getTaskPosition(), e, mTaskInfo.getDownloadPosition(),
                    mTaskPageSize);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (raf != null) {
                    raf.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @return 默认listener实现
     */
    private TaskProgressListener getDefaultListener() {
        return new TaskProgressListener() {
            @Override
            public void onProgressUpdate(int taskPosition, int position, int size) {

            }

            @Override
            public void onFinished(int taskPosition, int position, int size) {

            }

            @Override
            public void onFailure(int taskPosition, Throwable throwable, int position, int size) {
                Log.e(TAG, "onFailure: ", throwable);
            }

            @Override
            public void onPaused(int taskPosition, int position, int size) {

            }
        };
    }

}
