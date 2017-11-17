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

package org.huxizhijian.hhcomic.comic.value;

import org.huxizhijian.hhcomic.comic.type.RequestFieldType;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 传入参数
 *
 * @author huxizhijian
 * @date 2017/10/2
 */
public class ComicRequestValues implements IComicRequest {

    private final ReferenceQueue<LinkedHashMap<Integer, Object>> ITEM_QUEUE = new ReferenceQueue<>();
    private final LinkedHashMap<Integer, Object> MULTIPLE_FIELDS = new LinkedHashMap<>();
    private final SoftReference<LinkedHashMap<Integer, Object>> FIELDS_REFERENCE =
            new SoftReference<>(MULTIPLE_FIELDS, ITEM_QUEUE);

    public ComicRequestValues() {

    }

    @Override
    public ComicRequestValues addAllField(Map<Integer, Object> fields) {
        FIELDS_REFERENCE.get().putAll(fields);
        return this;
    }

    @Override
    public int getComicSourceHashCode() {
        return (int) FIELDS_REFERENCE.get().get(RequestFieldType.COMIC_SOURCE_TYPE);
    }

    @Override
    public IComicRequest setComicSourceHashCode(int type) {
        FIELDS_REFERENCE.get().put(RequestFieldType.COMIC_SOURCE_TYPE, type);
        return this;
    }

    @Override
    public IComicRequest setDataSourceType(int type) {
        FIELDS_REFERENCE.get().put(RequestFieldType.DATA_SOURCE_TYPE, type);
        return this;
    }

    @Override
    public int getDataSourceType() {
        return (int) FIELDS_REFERENCE.get().get(RequestFieldType.DATA_SOURCE_TYPE);
    }

    @Override
    public IComicRequest addField(int key, Object field) {
        FIELDS_REFERENCE.get().put(key, field);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <T> T getField(int key) {
        return (T) FIELDS_REFERENCE.get().get(key);
    }

    @Override
    public final Map<?, ?> getFields() {
        return FIELDS_REFERENCE.get();
    }

}
