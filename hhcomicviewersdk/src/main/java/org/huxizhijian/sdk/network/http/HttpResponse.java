package org.huxizhijian.sdk.network.http;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author huxizhijian
 */
public interface HttpResponse extends Header, Closeable {

    HttpStatus getStatus();

    String getStatusMsg();

    InputStream getBody() throws IOException;

    void close();

    long getContentLength();

}
