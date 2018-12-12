/*
 * Copyright 2016-2018 huxizhijian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.huxizhijian.hhcomic.model.comic.config;

/**
 * 源设置实体
 *
 * @author huxizhijian
 * @date 2018/11/2
 */
public class SourceConfig {

    /**
     * 源key
     */
    private String mSourceKey;

    /**
     * 源名称
     */
    private String mSourceName;

    /**
     * 是否开启
     */
    private boolean mActive;

    public SourceConfig() {
    }

    public SourceConfig(String sourceKey, String sourceName, boolean active) {
        mSourceKey = sourceKey;
        mSourceName = sourceName;
        mActive = active;
    }

    public String getSourceKey() {
        return mSourceKey;
    }

    public void setSourceKey(String sourceKey) {
        mSourceKey = sourceKey;
    }

    public String getSourceName() {
        return mSourceName;
    }

    public void setSourceName(String sourceName) {
        mSourceName = sourceName;
    }

    public boolean isActive() {
        return mActive;
    }

    public void setActive(boolean active) {
        mActive = active;
    }

    @Override
    public String toString() {
        return "SourceConfig{" +
                "mSourceKey='" + mSourceKey + '\'' +
                ", mSourceName='" + mSourceName + '\'' +
                ", mActive=" + mActive +
                '}';
    }
}
