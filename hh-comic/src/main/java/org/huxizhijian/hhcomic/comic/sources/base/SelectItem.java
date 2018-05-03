/*
 * Copyright 2016-2018 huxizhijian
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

package org.huxizhijian.hhcomic.comic.sources.base;

/**
 * 一个过滤或者排序, 该类为不可变(Immutable)的
 *
 * @author huxizhijian
 * @date 2017/10/12
 */
public final class SelectItem {

    public final String name;
    public final String path;

    private SelectItem(String name, String path) {
        this.name = name;
        this.path = path;
    }

    /**
     * 创建一个Item
     *
     * @param name 名称
     * @param path 帮助完成url的字段
     * @return instance
     */
    public static SelectItem create(String name, String path) {
        return new SelectItem(name, path);
    }

    @Override
    public String toString() {
        return name;
    }
}
