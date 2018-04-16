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

package org.huxizhijian.hhcomic.comic.sources;

import org.huxizhijian.annotations.SourceImpl;
import org.huxizhijian.hhcomic.comic.net.ComicRequest;
import org.huxizhijian.hhcomic.comic.sources.base.Source;

import static org.huxizhijian.hhcomic.comic.sources.HHManhua.HH_MANHUA;

/**
 * 汗汗漫画策略实现类
 *
 * @author huxizhijian
 * @date 2018/4/9
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
@SourceImpl(name = HH_MANHUA)
public class HHManhua implements Source {
    public static final String HH_MANHUA = "汗汗漫画";

    public void createRequest(){
    }
}
