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


import org.huxizhijian.sdk.imagedownload.listener.RequestProgressListener;

import java.util.List;

/**
 * @author huxizhijian 2017/3/16
 */
public interface Request extends Comparable<Request> {

    void addTask(ImageDownloadTask task);

    String getUri();

    void setPicList(List<String> picList);

    long getChid();

    List<ImageDownloadTask> getDownloadTasks();

    void pause();

    int getServerId();

    String getDownloadPath();

    void onError(Exception e);

    void setListener(RequestProgressListener listener);

}
