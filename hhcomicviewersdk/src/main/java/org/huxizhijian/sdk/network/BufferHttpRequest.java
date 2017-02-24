package org.huxizhijian.sdk.network;


import org.huxizhijian.sdk.network.http.HttpHeader;
import org.huxizhijian.sdk.network.http.HttpResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author huxizhijian
 */

public abstract class BufferHttpRequest extends AbstractHttpRequest {

    private ByteArrayOutputStream mByteArray = new ByteArrayOutputStream();

    protected OutputStream getBodyOutputStream() {
        return mByteArray;
    }

    protected HttpResponse executeInternal(HttpHeader header) throws IOException {
        byte[] data = mByteArray.toByteArray();
        return executeInternal(header, data);
    }

    protected abstract HttpResponse executeInternal(HttpHeader header, byte[] data) throws IOException;
}
