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
