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

package org.huxizhijian.hhcomic.model.repository.bean;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringDef;

/**
 * 返回值包装类
 *
 * @author huxizhijian
 * @date 2018/11/16
 */
public class Resource<T> {

    public Resource(@State String state, T data, String message, Throwable throwable) {
        this.state = state;
        this.data = data;
        this.message = message;
        this.throwable = throwable;
    }

    /**
     * 返回的数据
     */
    public final T data;

    /**
     * 返回的状态
     */
    @State
    public final String state;

    /**
     * 返回的信息（中文）
     */
    @Nullable
    public final String message;

    /**
     * 发生的异常
     */
    @Nullable
    public final Throwable throwable;

    public static <T> Resource<T> success(@NonNull T data) {
        return new Resource<>(SUCCESS, data, "success", null);
    }

    public static <T> Resource<T> error(String msg, @Nullable T data, Throwable throwable) {
        return new Resource<>(ERROR, data, msg, throwable);
    }

    public static <T> Resource<T> loading(@Nullable T data) {
        return new Resource<>(LOADING, data, "loading", null);
    }

    public static <T> Resource<T> empty() {
        return new Resource<>(EMPTY, null, "empty", null);
    }

    public static <T> Resource<T> noNetwork(){
        return new Resource<>(NO_NETWORK, null, "no network", null);
    }

    public static final String SUCCESS = "SUCCESS";

    public static final String ERROR = "ERROR";

    public static final String EMPTY = "EMPTY";

    public static final String LOADING = "LOADING";

    public static final String NO_NETWORK = "NO_NETWORK";

    @StringDef({SUCCESS, ERROR, EMPTY, LOADING, NO_NETWORK})
    public @interface State {
    }

    @Override
    public String toString() {
        return "Resource{" +
                "data=" + data +
                ", state='" + state + '\'' +
                ", message='" + message + '\'' +
                ", throwable=" + throwable +
                '}';
    }
}
