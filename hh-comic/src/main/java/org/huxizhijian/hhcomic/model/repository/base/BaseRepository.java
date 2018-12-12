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

package org.huxizhijian.hhcomic.model.repository.base;

import androidx.annotation.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * 仓库基类，整合一个可取消的请求列表，子类在请求RxJava连接后调用addDisposable，可在clear时取消所有连接
 *
 * @author huxizhijian
 * @date 2018/11/15
 */
public abstract class BaseRepository {

    private CompositeDisposable mCompositeDisposable;

    public BaseRepository() {
        mCompositeDisposable = new CompositeDisposable();
    }

    public void clear() {
        mCompositeDisposable.clear();
    }

    protected void addDisposable(@NonNull Disposable disposable) {
        mCompositeDisposable.add(disposable);
    }
}
