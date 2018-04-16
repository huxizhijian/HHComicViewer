/*
 * Copyright 2016-2018 huxizhijian
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

package org.huxizhijian.hhcomic.oldcomic.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 下载内容
 *
 * @author huxizhijian
 * @date 2017/10/20
 */
public class DownloadInfo implements Parcelable {

    /**
     * 下载状态变量
     */
    public int state;

    /**
     * 暂时不清楚
     */
    public int legacy;

    /**
     * 下载时间
     */
    public long time;

    /**
     * 章节名
     */
    public String title;

    /**
     * 章节ID
     */
    public String chapterId;

    /**
     * ComicId
     */
    public String comicId;

    /**
     * 源
     */
    public int source;

    public static final Creator<DownloadInfo> CREATOR = new Creator<DownloadInfo>() {
        @Override
        public DownloadInfo createFromParcel(Parcel source) {
            return new DownloadInfo(source);
        }

        @Override
        public DownloadInfo[] newArray(int size) {
            return new DownloadInfo[size];
        }
    };

    /**
     * 下载状态标识常量
     */
    public static final int STATE_INVALID = -1;
    public static final int STATE_NONE = 0;
    public static final int STATE_WAIT = 1;
    public static final int STATE_DOWNLOAD = 2;
    public static final int STATE_FINISH = 3;
    public static final int STATE_FAILED = 4;

    /**
     * 下载速度
     */
    public long speed;
    /**
     * 正在下载的图片剩余量
     */
    public long remaining;
    /**
     * 完成图片量
     */
    public int finished;
    /**
     * 下载
     */
    public int downloaded;
    /**
     * 总页数
     */
    public int total;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.state);
        dest.writeInt(this.legacy);
        dest.writeLong(this.time);
        dest.writeString(title);
        dest.writeString(chapterId);
        dest.writeString(comicId);
        dest.writeInt(source);
    }

    protected DownloadInfo(Parcel in) {
        this.state = in.readInt();
        this.legacy = in.readInt();
        this.time = in.readLong();
        this.title = in.readString();
        this.chapterId = in.readString();
        this.comicId = in.readString();
        this.source = in.readInt();
    }

}
