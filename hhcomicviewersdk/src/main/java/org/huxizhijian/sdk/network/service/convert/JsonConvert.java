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
