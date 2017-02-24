package org.huxizhijian.sdk.network;


import org.huxizhijian.sdk.network.http.HttpHeader;
import org.huxizhijian.sdk.network.http.HttpRequest;
import org.huxizhijian.sdk.network.http.HttpResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author huxizhijian
 */

public abstract class AbstractHttpRequest implements HttpRequest {

    private static final String GZIP = "gzip";

    private HttpHeader mHeader = new HttpHeader();

    private ZipOutputStream mZip;

    private boolean executed;

    public HttpHeader getHeaders() {
        return mHeader;
    }

    public OutputStream getBody() {
        OutputStream body = getBodyOutputStream();
        if (isGzip()) {

            return getGzipOutStream(body);
        }
        return body;
    }

    private OutputStream getGzipOutStream(OutputStream body) {
        if (this.mZip == null) {
            this.mZip = new ZipOutputStream(body);
        }
        return mZip;
    }

    private boolean isGzip() {

        String contentEncoding = getHeaders().getContentEncoding();
        if (GZIP.equals(contentEncoding)) {
            return true;
        }
        return false;
    }

    public HttpResponse execute() throws IOException {
        if (mZip != null) {
            mZip.close();
        }
        HttpResponse response = executeInternal(mHeader);
        executed = true;
        return response;
    }

    protected abstract HttpResponse executeInternal(HttpHeader mHeader) throws IOException;

    protected abstract OutputStream getBodyOutputStream();
}
