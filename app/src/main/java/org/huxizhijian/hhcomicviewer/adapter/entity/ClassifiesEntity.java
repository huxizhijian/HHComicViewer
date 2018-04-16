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

package org.huxizhijian.hhcomicviewer.adapter.entity;

/**
 * Created by wei on 2017/1/9.
 */

public class ClassifiesEntity {

    private String classifiesName;
    private String classifiesUrl;
    private String classifiesPicUrl;

    public ClassifiesEntity() {
    }

    public ClassifiesEntity(String classifiesName, String classifiesUrl, String classifiesPicUrl) {
        this.classifiesName = classifiesName;
        this.classifiesUrl = classifiesUrl;
        this.classifiesPicUrl = classifiesPicUrl;
    }

    public String getClassifiesName() {
        return classifiesName;
    }

    public void setClassifiesName(String classifiesName) {
        this.classifiesName = classifiesName;
    }

    public String getClassifiesUrl() {
        return classifiesUrl;
    }

    public void setClassifiesUrl(String classifiesUrl) {
        this.classifiesUrl = classifiesUrl;
    }

    public String getClassifiesPicUrl() {
        return classifiesPicUrl;
    }

    public void setClassifiesPicUrl(String classifiesPicUrl) {
        this.classifiesPicUrl = classifiesPicUrl;
    }
}
