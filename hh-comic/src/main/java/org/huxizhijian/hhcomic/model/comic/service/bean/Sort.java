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

package org.huxizhijian.hhcomic.model.comic.service.bean;

import org.huxizhijian.hhcomic.model.comic.service.bean.base.BaseListBuilder;
import org.huxizhijian.hhcomic.model.comic.service.bean.base.Tag;

/**
 * @author huxizhijian
 * @date 2018/10/17
 */
public final class Sort extends Tag {

    public Sort(String id, String name, String sourceId) {
        super(null, id, name, sourceId);
    }

    public static class ListBuilder extends BaseListBuilder<Sort> {

        private String mSourceId;

        public ListBuilder(String sourceId) {
            mSourceId = sourceId;
        }

        public ListBuilder add(String id, String name) {
            mList.add(new Sort(id, name, mSourceId));
            return this;
        }
    }
}
