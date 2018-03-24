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

package org.huxizhijian.sdk.imagedownload.core;

import android.support.annotation.NonNull;
import android.util.Log;

import org.huxizhijian.sdk.imagedownload.ImageDownloader;
import org.huxizhijian.sdk.imagedownload.core.model.ImageEntity;
import org.huxizhijian.sdk.imagedownload.listener.RequestProgressListener;
import org.huxizhijian.sdk.imagedownload.listener.TaskProgressListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * 将所有下载线程统一管理，称为下载请求
 *
 * @author huxizhijian 2017/3/16
 */
public class ImageDownloadRequest implements Request, TaskProgressListener, Comparable<Request> {

    private ImageEntity mEntity;
    private List<ImageDownloadTask> mDownloadTasks;
    private RequestProgressListener mListener;
    private List<Integer> mProgressList;
    private boolean mIsAllPause = false;

    public ImageDownloadRequest(ImageEntity entity) {
        mDownloadTasks = new ArrayList<>();
        mProgressList = new ArrayList<>();
        mEntity = entity;
        mListener = getDefaultListener();
    }

    @Override
    public void addTask(ImageDownloadTask task) {
        task.setListener(this);
        mDownloadTasks.add(task);
        mProgressList.add(task.getProgress());
    }

    @Override
    public void setListener(RequestProgressListener listener) {
        mListener = listener;
    }

    @Override
    public String getUri() {
        return mEntity.getUri();
    }

    @Override
    public long getChid() {
        return mEntity.getChid();
    }

    @Override
    public void setPicList(List<String> picList) {
        mEntity.setPicList(picList);
    }

    @Override
    public List<ImageDownloadTask> getDownloadTasks() {
        return mDownloadTasks;
    }

    @Override
    public void pause() {
        for (ImageDownloadTask task : mDownloadTasks) {
            task.isPause = true;
        }
        //默认5秒后没有暂停则强制停止线程
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(ImageDownloader.getInstance().getPauseTimeout());
                    if (!mIsAllPause) {
                        for (ImageDownloadTask task : mDownloadTasks) {
                            task.interrupt();
                        }
                        mIsAllPause = true;
                        mListener.onPaused(mEntity.getChid(), getTotalProgress(), 0);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public int getServerId() {
        return mEntity.getServerId();
    }

    @Override
    public String getDownloadPath() {
        return mEntity.getDownloadPath();
    }

    @Override
    public void onError(Exception e) {
        mListener.onFailure(mEntity.getChid(), e, 0, 0);
    }

    @Override
    public int compareTo(@NonNull Request otherRequest) {
        return Long.valueOf(mEntity.getChid()).compareTo(otherRequest.getChid());
    }

    @Override
    public void onProgressUpdate(int taskPosition, int position, int size) {
        mProgressList.set(taskPosition, position);
        mListener.onProgress(mEntity.getChid(), getTotalProgress(), mEntity.getPicList().size());
    }

    @Override
    public void onFinished(int taskPosition, int position, int size) {
        mProgressList.set(taskPosition, position);
        boolean allFinished = true;
        for (ImageDownloadTask task : mDownloadTasks) {
            if (!task.isFinished) {
                allFinished = false;
                break;
            }
        }
        if (allFinished) {
            mListener.onCompleted(mEntity.getChid(), getTotalProgress(), mEntity.getPicList().size());
        }
    }

    @Override
    public void onFailure(int taskPosition, Throwable throwable, int position, int size) {
        mProgressList.set(taskPosition, position);
        pause();
        mListener.onFailure(mEntity.getChid(), throwable, getTotalProgress(), mEntity.getPicList().size());
    }

    @Override
    public void onPaused(int taskPosition, int position, int size) {
        mProgressList.set(taskPosition, position);
        boolean allPaused = true;
        for (ImageDownloadTask task : mDownloadTasks) {
            if (!task.isRealPause) {
                allPaused = false;
                break;
            }
        }
        if (allPaused) {
            mListener.onPaused(mEntity.getChid(), getTotalProgress(), mEntity.getPicList().size());
            mIsAllPause = true;
        }
    }

    /**
     * @return 默认listener实现类
     */
    private RequestProgressListener getDefaultListener() {
        return new RequestProgressListener() {

            @Override
            public void onProgress(long chid, int progress, int size) {
            }

            @Override
            public void onFailure(long chid, Throwable throwable, int progress, int size) {
                Log.e(TAG, "onFailure: ", throwable);
            }

            @Override
            public void onCompleted(long chid, int progress, int size) {
            }

            @Override
            public void onPaused(long chid, int progress, int size) {

            }

        };
    }

    private int getTotalProgress() {
        int totalProgress = 0;
        for (int progress : mProgressList) {
            totalProgress = totalProgress + progress;
        }
        return totalProgress;
    }

    @Override
    public boolean equals(Object obj) {
        ImageDownloadRequest otherRequest = (ImageDownloadRequest) obj;
        return mEntity.getChid() == otherRequest.getChid();
    }

    @Override
    public int hashCode() {
        return Long.valueOf(mEntity.getChid()).hashCode();
    }

}
