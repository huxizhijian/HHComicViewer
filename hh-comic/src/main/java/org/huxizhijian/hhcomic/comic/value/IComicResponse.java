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

package org.huxizhijian.hhcomic.comic.value;

import java.util.Map;

/**
 * 调用策略时候的返回结果
 *
 * @author huxizhijian
 * @date 2017/10/9
 */
public interface IComicResponse {

    /**
     * 获取结果，通常为{@link org.huxizhijian.hhcomic.comic.bean.Comic}或者它的List集合
     *
     * @return Comic或其集合或Null
     */
    <T> T getComicResponse();

    /**
     * 设置返回结果
     *
     * @param field Comic或其集合
     * @return 链式调用
     */
    IComicResponse setComicResponse(Object field);

    /**
     * 添加返回结果
     *
     * @param key   {@link org.huxizhijian.hhcomic.comic.type.ResponseFieldType}标识的key
     * @param field 结果类
     * @return 链式调用
     */
    IComicResponse addField(int key, Object field);

    /**
     * 获取返回结果
     *
     * @param key {@link org.huxizhijian.hhcomic.comic.type.ResponseFieldType}标识的key
     * @param <T> 结果
     * @return 链式调用
     */
    <T> T getField(int key);

    /**
     * 添加返回结果map集合
     *
     * @param fields 结果map
     * @return 链式调用
     */
    IComicResponse addAllField(Map<Integer, Object> fields);

    /**
     * 获取返回结果map结合
     *
     * @return 结果map
     */
    Map<?, ?> getFields();

}
