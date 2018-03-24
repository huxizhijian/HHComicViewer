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

package org.huxizhijian.hhcomic.comic;

import org.huxizhijian.hhcomic.comic.source.base.ComicSource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Comic路由，通过注册ComicSource，让ui知道加载什么source
 *
 * @author huxizhijian
 * @date 2017/10/1
 */
public class ComicRouter {

    private final static Map<Integer, ComicSource> SOURCE_MAP = new LinkedHashMap<>();

    private ComicRouter() {
    }

    public static ComicRouter getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final ComicRouter INSTANCE = new ComicRouter();
    }

    public void addSource(int key, ComicSource comicSource) {
        SOURCE_MAP.put(key, comicSource);
    }

    public void removeSource(int key) {
        SOURCE_MAP.remove(key);
    }

    public ComicSource getSource(int key) {
        return SOURCE_MAP.get(key);
    }

    public List<ComicSource> getAllSource() {
        List<ComicSource> sourceList = new ArrayList<>();
        for (Map.Entry<Integer, ComicSource> entry : SOURCE_MAP.entrySet()) {
            sourceList.add(entry.getValue());
        }
        return sourceList;
    }

    public Map<Integer, ComicSource> getSourceMap() {
        return SOURCE_MAP;
    }

}