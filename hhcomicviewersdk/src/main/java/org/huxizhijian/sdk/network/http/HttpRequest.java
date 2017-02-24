package org.huxizhijian.sdk.network.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

/**
 * @author huxizhijian
 */

public interface HttpRequest extends Header {

    HttpMethod getMethod();

    URI getUri();

    OutputStream getBody();

    HttpResponse execute() throws IOException;

}
