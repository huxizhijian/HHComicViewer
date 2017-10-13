package org.huxizhijian.hhcomic.comic.source.base;

import org.huxizhijian.core.util.misc.Pair;
import org.huxizhijian.hhcomic.comic.repository.ComicDataSource;
import org.huxizhijian.hhcomic.comic.parser.comic.ComicParseStrategy;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 提供来源插件的基础，整合各种策略并提供给{@link ComicDataSource}使用
 *
 * @Author huxizhijian on 2017/9/25.
 */
public abstract class ComicSource {

    private String mSourceName;
    private String mBaseUrl;

    private final Map<Integer, ComicParseStrategy> STRATEGY_MAP;
    protected final Map<String, String> RANK_TYPE_MAP;
    protected final Map<String, Pair<String, String>> CATEGORY_TYPE_MAP;
    protected final Map<String, String> RECOMMEND_TYPE_MAP;

    public ComicSource() {
        STRATEGY_MAP = new LinkedHashMap<>();
        RANK_TYPE_MAP = new LinkedHashMap<>();
        CATEGORY_TYPE_MAP = new LinkedHashMap<>();
        RECOMMEND_TYPE_MAP = new LinkedHashMap<>();
        mSourceName = setSourceName();
        mBaseUrl = setBaseUrl();
    }

    public abstract String setSourceName();

    public abstract String setBaseUrl();

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

    public Map<String, Pair<String, String>> getCategoryTypeMap() {
        return CATEGORY_TYPE_MAP;
    }

    public Map<String, String> getRecommendTypeMap() {
        return RECOMMEND_TYPE_MAP;
    }

    public class TypeContent {
        public String title;
        public String urlKey;

        public TypeContent() {
        }

        public TypeContent(String title, String urlKey) {
            this.title = title;
            this.urlKey = urlKey;
        }

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
