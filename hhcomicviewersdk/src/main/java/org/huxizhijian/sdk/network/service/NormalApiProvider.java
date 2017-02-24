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
