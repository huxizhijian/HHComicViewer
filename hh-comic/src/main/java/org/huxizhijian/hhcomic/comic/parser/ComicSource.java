package org.huxizhijian.hhcomic.comic.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 提供分析器{@link BaseParser}的基本实现
 *
 * @Author huxizhijian on 2017/9/25.
 */
public abstract class ComicSource {

    protected String mSourceName;

    private Map<Integer, ParseStrategy> mStrategyMap;

    private ComicSource() {
        mStrategyMap = new HashMap<>();
        mSourceName = setSourceName();
    }

    protected abstract String setSourceName();

    public String getSourceName() {
        return mSourceName;
    }

    public ComicSource addStratgy(int type, ParseStrategy parseStrategy) {
        mStrategyMap.put(type, parseStrategy);
        return this;
    }

    public ComicSource addAllStratgys(Map<Integer, ParseStrategy> strategyMap) {
        if (strategyMap == null) return this;
        mStrategyMap.putAll(strategyMap);
        return this;
    }

    public ParseStrategy getStrategy(int type) {
        return mStrategyMap.get(type);
    }

    public List<ParseStrategy> getAllStrategys() {
        List<ParseStrategy> strategyList = new ArrayList<>();
        for (Map.Entry<Integer, ParseStrategy> entry : mStrategyMap.entrySet()) {
            strategyList.add(entry.getValue());
        }
        return strategyList;
    }

}
