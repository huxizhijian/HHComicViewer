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

package org.huxizhijian.sdk.imagedownload.listener;

import org.huxizhijian.sdk.imagedownload.core.Request;

/**
 * @author huxizhijian 2017/3/17
 */
public interface ImageDownloadListener {

    void onStart(Request request);

    void onProgress(Request request, int progress, int size);

    void onFailure(Request request, Throwable throwable, int progress, int size);

    void onCompleted(Request request, int progress, int size);

    void onPaused(Request request, int progress, int size);

    void onDeleted(Request request);

    void onAllFinished();

    void onAddToQueue(Request request);

}
