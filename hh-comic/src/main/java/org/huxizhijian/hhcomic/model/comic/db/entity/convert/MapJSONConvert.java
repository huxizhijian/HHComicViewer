package org.huxizhijian.hhcomic.model.comic.db.entity.convert;

import com.alibaba.fastjson.JSON;

import org.huxizhijian.hhcomic.model.comic.db.entity.Chapter;

import java.util.List;
import java.util.Map;

import androidx.room.TypeConverter;

/**
 * fast json
 *
 * @author huxizhijian
 * @date 2018/10/31
 */
public class MapJSONConvert {

    @TypeConverter
    public Map<String, List<Chapter>> chapterJsonToChapterMap(String chapterJson) {
        return (Map<String, List<Chapter>>) JSON.parse(chapterJson);
    }

    @TypeConverter
    public String chapterMapToChapterJson(Map<String, List<Chapter>> chapterMap) {
        return JSON.toJSONString(chapterMap);
    }
}
