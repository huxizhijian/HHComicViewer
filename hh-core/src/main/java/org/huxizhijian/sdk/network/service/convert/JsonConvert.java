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

package org.huxizhijian.sdk.network.service.convert;

import com.google.gson.Gson;

import org.huxizhijian.sdk.network.http.HttpResponse;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;


/**
 * json转换
 * Created by huxizhijian on 2016/11/19.
 */

public class JsonConvert implements Convert {

    private Gson mGson = new Gson();

    public static final String CONTENT_TYPE ="application/json;charset-utf-8";

    @Override
    public Object parse(HttpResponse response, Type type) throws IOException {
        Reader reader = new InputStreamReader(response.getBody());
        return mGson.fromJson(reader, type);
    }

    @Override
    public boolean isCanParse(String contentType) {
        return CONTENT_TYPE.equals(contentType);
    }

    @Override
    public Object parse(String content, Type type) throws IOException {
        return mGson.fromJson(content, type);
    }
}
