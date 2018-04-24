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

import java.util.List;
import java.util.Map;

/**
 * @author huxizhijian
 * @date 2018/4/23
 */
public interface FilterManager {

    List<String> getFilterClassify();

    Map<String, String> getOneClassifyFilters(String classify);

    Map<String, Map<String, String>> getAllClassifyFilters();

    public interface FilterSelector {

        List<String> getSelectFilterClassify();

        Map<String, String> getSelectFilter(String classify);

        List<Map<String, String>> getAllSelectFitlter();

        public interface Builder {

            FilterSelector build();

            Builder addSelect(String classify, Map<String, String> filter);
        }

    }
}
