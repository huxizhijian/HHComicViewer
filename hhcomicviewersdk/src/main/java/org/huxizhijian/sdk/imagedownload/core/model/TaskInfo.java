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

package org.huxizhijian.sdk.imagedownload.core.model;

/**
 * 线程实体类
 * Created by wei on 2016/9/5.
 */
public class TaskInfo {

    private int id; //id
    private int taskPosition; //该线程为第几个线程
    private int taskCount; //线程总数
    private int downloadPosition; //该线程下载到第几个图片
    private int length = -1; //正在下载的图片文件长度
    private int finished = -1; //下载完成的文件长度
    private long chid; //漫画章节id，可以找出同一个漫画章节的所有线程
    private String downloadPath; //漫画下载目录

    public TaskInfo() {
    }

    public TaskInfo(int id, int taskPosition, int taskCount, int downloadPosition, int length, int finished, long chid, String downloadPath) {
        this.id = id;
        this.taskPosition = taskPosition;
        this.taskCount = taskCount;
        this.downloadPosition = downloadPosition;
        this.length = length;
        this.finished = finished;
        this.chid = chid;
        this.downloadPath = downloadPath;
    }

    public TaskInfo(int taskPosition, int taskCount, int downloadPosition, long chid, String downloadPath) {
        this.taskPosition = taskPosition;
        this.taskCount = taskCount;
        this.downloadPosition = downloadPosition;
        this.chid = chid;
        this.downloadPath = downloadPath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTaskPosition() {
        return taskPosition;
    }

    public void setTaskPosition(int taskPosition) {
        this.taskPosition = taskPosition;
    }

    public int getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }

    public int getDownloadPosition() {
        return downloadPosition;
    }

    public void setDownloadPosition(int downloadPosition) {
        this.downloadPosition = downloadPosition;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    public long getChid() {
        return chid;
    }

    public void setChid(long chid) {
        this.chid = chid;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }
}
