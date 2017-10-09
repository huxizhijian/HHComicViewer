/*
 * Copyright 2017 huxizhijian
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
 * @author huxizhijian 2017/9/24
 */
public class Chapter implements Parcelable {

    private String title;
    private String path;
    private int count;
    private boolean complete;
    private boolean download;
    private long tid;

    public Chapter(String title, String path, int count, boolean complete, boolean download, long tid) {
        this.title = title;
        this.path = path;
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

    public String getPath() {
        return path;
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
        return o instanceof Chapter && ((Chapter) o).path.equals(path);
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(path);
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
