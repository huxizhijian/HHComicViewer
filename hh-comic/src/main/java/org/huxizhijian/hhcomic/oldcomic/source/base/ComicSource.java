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

package org.huxizhijian.hhcomic.oldcomic.source.base;

import org.huxizhijian.hhcomic.comic.sources.base.Filter;
import org.huxizhijian.hhcomic.oldcomic.parser.comic.ComicParseStrategy;
import org.huxizhijian.hhcomic.oldcomic.repository.ComicDataSource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 提供来源插件的基础，整合各种策略并提供给{@link ComicDataSource}使用
 *
 * @author huxizhijian
 * @date 2017/9/25.
 */
public abstract class ComicSource {

    private String mSourceName;
    private String mBaseUrl;

    private final Map<Integer, ComicParseStrategy> STRATEGY_MAP;
    protected final Map<String, String> RANK_TYPE_MAP;
    protected final Map<String, Filter<String, String>> CATEGORY_TYPE_MAP;
    protected final Map<String, String> RECOMMEND_TYPE_MAP;

    public ComicSource() {
        STRATEGY_MAP = new LinkedHashMap<>();
        RANK_TYPE_MAP = new LinkedHashMap<>();
        CATEGORY_TYPE_MAP = new LinkedHashMap<>();
        RECOMMEND_TYPE_MAP = new LinkedHashMap<>();
        mSourceName = setSourceName();
        mBaseUrl = setBaseUrl();
    }

    /**
     * 来源网站的名称
     *
     * @return 名称
     */
    public abstract String setSourceName();

    /**
     * 来源网站的主站url
     *
     * @return 主站url
     */
    public abstract String setBaseUrl();

    /**
     * 来源网站的{@link SourceEnum}枚举类的hashcode
     *
     * @return {@link SourceEnum}hashcode
     */
    public abstract int getSourceType();

    public ComicSource addAllStrategy(Map<Integer, ComicParseStrategy> strategyMap) {
        if (strategyMap != null) {
            STRATEGY_MAP.putAll(strategyMap);
        }
        return this;
    }

    public ComicSource addStrategy(int strategyKey, ComicParseStrategy strategy) {
        STRATEGY_MAP.put(strategyKey, strategy);
        return this;
    }

    public ComicSource removeStrategy(int strategyKey) {
        STRATEGY_MAP.remove(strategyKey);
        return this;
    }

    public List<Integer> getAllStrategyKey() {
        List<Integer> keys = new ArrayList<>();
        Set<Map.Entry<Integer, ComicParseStrategy>> entrySet = STRATEGY_MAP.entrySet();
        for (Map.Entry<Integer, ComicParseStrategy> entry : entrySet) {
            keys.add(entry.getKey());
        }
        return keys;
    }

    public List<ComicParseStrategy> getAllStrategy() {
        List<ComicParseStrategy> strategies = new ArrayList<>();
        Set<Map.Entry<Integer, ComicParseStrategy>> entrySet = STRATEGY_MAP.entrySet();
        for (Map.Entry<Integer, ComicParseStrategy> entry : entrySet) {
            strategies.add(entry.getValue());
        }
        return strategies;
    }

    public Map<String, String> getRankTypeMap() {
        return RANK_TYPE_MAP;
    }

    public Map<String, Filter<String, String>> getCategoryTypeMap() {
        return CATEGORY_TYPE_MAP;
    }

    public Map<String, String> getRecommendTypeMap() {
        return RECOMMEND_TYPE_MAP;
    }

    public ComicParseStrategy getStrategy(int strategyKey) {
        return STRATEGY_MAP.get(strategyKey);
    }

    public String getSourceName() {
        return mSourceName;
    }

    public String getBaseUrl() {
        return mBaseUrl;
    }
}
