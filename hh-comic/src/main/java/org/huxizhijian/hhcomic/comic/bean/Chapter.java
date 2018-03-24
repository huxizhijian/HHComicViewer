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

package org.huxizhijian.hhcomic.comic.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 章节的实体类
 *
 * @author huxizhijian
 * @date 2017/9/24
 */
public class Chapter implements Parcelable {

    public String title;
    public String chapterId;
    public int count;
    public boolean complete;
    public boolean download;
    public long tid;

    public Chapter(String title, String chapterId, int count, boolean complete, boolean download, long tid) {
        this.title = title;
        this.chapterId = chapterId;
        this.count = count;
        this.complete = complete;
        this.download = download;
        this.tid = tid;
    }

    public Chapter(String title, String path, long tid) {
        this(title, path, 0, false, false, tid);
    }

    public Chapter(String title, String path) {
        this(title, path, 0, false, false, -1);
    }

    public Chapter(Parcel source) {
        this(source.readString(), source.readString(), source.readInt(),
                source.readByte() == 1, source.readByte() == 1, source.readLong());
    }

    public String getTitle() {
        return title;
    }

    public String getChapterId() {
        return chapterId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public boolean isDownload() {
        return download;
    }

    public void setDownload(boolean download) {
        this.download = download;
    }

    public long getTid() {
        return tid;
    }

    public void setTid(long tid) {
        this.tid = tid;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Chapter && ((Chapter) o).chapterId.equals(chapterId);
    }

    @Override
    public int hashCode() {
        return chapterId.hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(chapterId);
        dest.writeInt(count);
        dest.writeByte((byte) (complete ? 1 : 0));
        dest.writeByte((byte) (download ? 1 : 0));
        dest.writeLong(tid);
    }

    public final static Parcelable.Creator<Chapter> CREATOR = new Parcelable.Creator<Chapter>() {
        @Override
        public Chapter createFromParcel(Parcel source) {
            return new Chapter(source);
        }

        @Override
        public Chapter[] newArray(int size) {
            return new Chapter[size];
        }
    };

}
