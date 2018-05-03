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

/**
 * @author huxizhijian
 * @date 2018/5/2
 */
public interface NavigationManager {

    /**
     * 获取filter和sort的信息
     *
     * @param requestName 导航的名称
     * @return SelectorManager
     */
    SelectorManager getSelectorManager(String requestName);

    /**
     * 获取导航名称列表
     *
     * @return list of name
     */
    List<String> getNavigationNameList();

    /**
     * 获取导航的辅助路径
     *
     * @return pathList
     */
    List<String> getNavigationPathList();

    int getPageSize(String html, String url);

    List<String> parsePageComicList(String html, String url);

    String getUrl(String navigationName, int page, SelectorManager.UserSelector userSelector) throws IllegalArgumentException;

}