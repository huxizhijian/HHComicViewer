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

package org.huxizhijian.hhcomic.oldcomic.parser.chapter;

/**
 * 章节分析器，解析漫画图片地址
 *
 * @author huxizhijian
 * @date 2017/10/12
 */

public interface IChapterParser {

    /**
     * 是否有下一页
     */
    boolean hasNext();

    /**
     * 获取下一页的ImageUrl
     */
    String moveToNext();

}
