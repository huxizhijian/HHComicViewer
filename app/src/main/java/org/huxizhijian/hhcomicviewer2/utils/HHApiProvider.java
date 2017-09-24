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

package org.huxizhijian.hhcomicviewer2.utils;

import android.util.Log;

import org.huxizhijian.hhcomicviewer2.app.HHApplication;
import org.huxizhijian.hhcomicviewer2.option.HHComicWebVariable;
import org.huxizhijian.sdk.network.http.HttpMethod;
import org.huxizhijian.sdk.network.service.NormalRequest;
import org.huxizhijian.sdk.network.service.NormalResponse;
import org.huxizhijian.sdk.network.service.WorkStation;
import org.huxizhijian.sdk.network.service.WrapperResponse;
import org.huxizhijian.sdk.network.service.convert.Convert;
import org.huxizhijian.sdk.network.service.convert.JsonConvert;

import java.util.ArrayList;
import java.util.List;

/**
 * 使用sdk中封装的网络连接方法
 *
 * @author huxizhijian 2017/2/23
 */
public class HHApiProvider {

    private static volatile HHApiProvider sInstance;
    private static WorkStation sWorkStation;
    private static final List<Convert> sConvertList = new ArrayList<>();

    static {
        sConvertList.add(new JsonConvert());
    }

    private HHApiProvider() {
        sWorkStation = new WorkStation();
    }

    public static HHApiProvider getInstance() {
        if (sInstance == null) {
            synchronized (HHApiProvider.class) {
                if (sInstance == null) {
                    sInstance = new HHApiProvider();
                }
            }
        }
        return sInstance;
    }

    public void getWebContentAsyn(String url, final NormalResponse response) {
        NormalRequest request = new NormalRequest();
        request.setUrl(url);
        request.setMethod(HttpMethod.GET);
        request.setResponse(response);
        sWorkStation.add(request);
    }

    public void updateVariable() {
        NormalRequest request = new NormalRequest();
        request.setUrl(Constants.HH_VARIABLE_SITE);
        request.setMethod(HttpMethod.GET);
        //进行json转换
        WrapperResponse wrapperResponse = new WrapperResponse(new NormalResponse<HHComicWebVariable>() {
            @Override
            public void success(NormalRequest request, HHComicWebVariable variable) {
                //更新sp
                variable.updatePreferences();
                //更新application中的variable
                HHApplication.getInstance().setHHWebVariable(variable);
                Log.i("onSuccess", "update variable");
            }

            @Override
            public void fail(int errorCode, String errorMsg) {
                Log.e("onFail", errorCode + ":" + errorMsg);
            }
        }, sConvertList);
        request.setResponse(wrapperResponse);
        sWorkStation.add(request);
    }
}
