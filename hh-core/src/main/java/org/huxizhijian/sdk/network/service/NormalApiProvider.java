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

package org.huxizhijian.sdk.network.service;


import org.huxizhijian.sdk.network.http.HttpMethod;
import org.huxizhijian.sdk.network.service.convert.Convert;
import org.huxizhijian.sdk.network.service.convert.JsonConvert;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 上层API
 * Created by huxizhijian on 2016/11/18.
 */

public class NormalApiProvider {

    public static final String ENCODING = "UTF-8";
    private static WorkStation sWorkStation = new WorkStation();
    private static final List<Convert> sConvertList = new ArrayList<>();

    static {
        sConvertList.add(new JsonConvert());
    }

    public static byte[] encodedParam(Map<String, String> value) {
        if (value == null || value.size() == 0) return null;
        StringBuffer buffer = new StringBuffer();
        int count = 0;
        try {
            for (Map.Entry<String, String> entry : value.entrySet()) {
                buffer.append(URLEncoder.encode(entry.getKey(), ENCODING)).append("=")
                        .append(URLEncoder.encode(entry.getValue(), ENCODING));
                if (count != value.size() - 1) {
                    buffer.append("&");
                }
                count++;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        }
        return buffer.toString().getBytes();
    }

    public static void helloworld(String u1, Map<String, String> value, NormalResponse response) {
        NormalRequest request = new NormalRequest();
        WrapperResponse wrapperResponse = new WrapperResponse(response, sConvertList);
        request.setUrl(u1);
        request.setMethod(HttpMethod.POST);
        request.setData(encodedParam(value));
        request.setResponse(wrapperResponse);
        sWorkStation.add(request);
    }
}
