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

package org.huxizhijian.hhcomicviewer2.adapter.entity;

import org.huxizhijian.hhcomicviewer2.model.Comic;

import java.io.Serializable;
import java.util.List;

/**
 * 首页推荐bean类
 * Created by wei on 2017/1/4.
 */

public class ComicTabList implements Serializable {

    private List<Comic> comics;
    private String tabName;

    public ComicTabList(List<Comic> comics, String tabName) {
        this.comics = comics;
        this.tabName = tabName;
    }

    public List<Comic> getComics() {
        return comics;
    }

    public void setComics(List<Comic> comics) {
        this.comics = comics;
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

}
