package org.huxizhijian.sdk.network;


import org.huxizhijian.sdk.network.http.HttpMethod;
import org.huxizhijian.sdk.network.http.HttpRequest;

import java.io.IOException;
import java.net.URI;

/**
 * Created by huxizhijian on 2016/11/16.
 */

public interface HttpRequestFactory {

    HttpRequest createHttpRequest(URI uri, HttpMethod method) throws IOException;

}
