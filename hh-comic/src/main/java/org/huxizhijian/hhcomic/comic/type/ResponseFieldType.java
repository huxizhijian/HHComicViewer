/*
 * Copyright 2017 huxizhijian
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

package org.huxizhijian.hhcomic.comic.type;

/**
 * @author huxizhijian 2017/10/2
 */
@SuppressWarnings("unused")
public class ResponseFieldType {
    /**
     * 结果（可以是Comic单个实例或者list）
     */
    public static final int COMIC_RESPONSE = 0x0;
    /**
     * 页码
     */
    public static final int PAGE = 0x1;
    /**
     * 页数
     */
    public static final int PAGE_COUNT = 0x2;
    /**
     * 章节列表
     */
    public static final int CHAPTER_LIST = 0x3;
    /**
     * 返回结果的名称
     */
    public static final int RESULT_NAME = 0x4;
}
