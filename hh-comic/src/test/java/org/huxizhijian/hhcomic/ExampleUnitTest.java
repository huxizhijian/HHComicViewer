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

package org.huxizhijian.hhcomic;

import org.huxizhijian.hhcomic.model.comic.db.entity.Chapter;
import org.huxizhijian.hhcomic.model.comic.db.entity.convert.MapJSONConvert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void test() throws IOException {
        String type = "章节";
        List<Chapter> chapterList = new ArrayList<>();
        Chapter chapter = new Chapter("HHComic", "12345",
                "1542365", type, "第一话", 12);
        chapterList.add(chapter);
        Map<String, List<Chapter>> chapterMap = new HashMap<>();
        chapterMap.put(type, chapterList);
        String json = MapJSONConvert.chapterMapToJson(chapterMap);
        System.out.println(json);
        Map<String, List<Chapter>> chapterMapJson = MapJSONConvert.jsonToChapterMap(json);
        System.out.println(chapterMapJson.get(type));
    }
}