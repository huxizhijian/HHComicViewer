package org.huxizhijian.hhcomic.comic.source;

import org.huxizhijian.hhcomic.comic.ComicDataSource;
import org.huxizhijian.hhcomic.comic.parser.ParseStrategy;

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

    private final Map<Integer, ParseStrategy> STRATEGY_MAP;

    ComicSource() {
        STRATEGY_MAP = new LinkedHashMap<>();
        mSourceName = setSourceName();
        mBaseUrl = setBaseUrl();
    }

    public abstract String setSourceName();

    public abstract String setBaseUrl();

    public abstract int getSourceType();

    public ComicSource addAllStraegy(Map<Integer, ParseStrategy> strategyMap) {
        if (strategyMap != null) {
            STRATEGY_MAP.putAll(strategyMap);
        }
        return this;
    }

    public ComicSource addStragegy(int strategyKey, ParseStrategy strategy) {
        STRATEGY_MAP.put(strategyKey, strategy);
        return this;
    }

    public ComicSource removeStragegy(int strategyKey) {
        STRATEGY_MAP.remove(strategyKey);
        return this;
    }

    public List<Integer> getAllStragegyKey() {
        List<Integer> keys = new ArrayList<>();
        Set<Map.Entry<Integer, ParseStrategy>> entrySet = STRATEGY_MAP.entrySet();
        for (Map.Entry<Integer, ParseStrategy> entry : entrySet) {
            keys.add(entry.getKey());
        }
        return keys;
    }

    public List<ParseStrategy> getAllStragegy() {
        List<ParseStrategy> strategies = new ArrayList<>();
        Set<Map.Entry<Integer, ParseStrategy>> entrySet = STRATEGY_MAP.entrySet();
        for (Map.Entry<Integer, ParseStrategy> entry : entrySet) {
            strategies.add(entry.getValue());
        }
        return strategies;
    }

    public ParseStrategy getStragey(int strategyKey) {
        return STRATEGY_MAP.get(strategyKey);
    }

    public String getSourceName() {
        return mSourceName;
    }

    public String getBaseUrl() {
        return mBaseUrl;
    }
}
