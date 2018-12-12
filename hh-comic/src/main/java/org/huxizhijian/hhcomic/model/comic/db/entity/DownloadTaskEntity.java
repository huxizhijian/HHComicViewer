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

package org.huxizhijian.hhcomic.model.comic.db.entity;

/**
 * 章节下载任务信息实体类
 * TODO 等待下载模块完成
 *
 * @author huxizhijian
 * @date 2018/8/31
 */
public class DownloadTaskEntity {

    /**
     * 源Key
     */
    private String mSourceKey;

    /**
     * 漫画id
     */
    private String mComicId;

    /**
     * 下载的章节id
     */
    private String mChapterId;

    /**
     * 是否下载完毕
     */
    private boolean mIsFinished;

    /**
     * 下载到第几个图片
     */
    private int mDownloadingPosition = -1;

    /**
     * 正在下载的图片文件长度
     */
    private int length = -1;

    /**
     * 已经下载的文件大小
     */
    private int finished = -1;

}
