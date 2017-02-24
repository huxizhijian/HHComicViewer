package org.huxizhijian.sdk.network.service.convert;

import org.huxizhijian.sdk.network.http.HttpResponse;

import java.io.IOException;
import java.lang.reflect.Type;


/**
 * Created by huxizhijian on 2016/11/19.
 */

public interface Convert {

    Object parse(HttpResponse response, Type type) throws IOException;

    boolean isCanParse(String contentType);

    Object parse(String content, Type type) throws IOException;

}
