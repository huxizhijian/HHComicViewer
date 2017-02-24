package org.huxizhijian.sdk.network.service;


import org.huxizhijian.sdk.network.HttpRequestProvider;
import org.huxizhijian.sdk.network.http.HttpRequest;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 网络请求队列管理
 * Created by huxizhijian on 2016/11/18.
 */

public class WorkStation {

    public static final int MAX_REQUEST_SIZE = 60;

    private static final ThreadPoolExecutor sThreadPool = new ThreadPoolExecutor(0,
            Integer.MAX_VALUE, 60, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new ThreadFactory() {
        private AtomicInteger index = new AtomicInteger();

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("http thread#" + index.getAndIncrement());
            return thread;
        }
    });

    private Deque<NormalRequest> mRunning = new ArrayDeque<>();
    private Deque<NormalRequest> mCache = new ArrayDeque<>();

    private HttpRequestProvider mRequestProvider;

    public WorkStation() {
        this.mRequestProvider = new HttpRequestProvider();
    }

    public void add(NormalRequest request) {
        if (mRunning.size() > MAX_REQUEST_SIZE) {
            mCache.add(request);
        } else {
            doHttpRequest(request);
        }
    }

    private void doHttpRequest(NormalRequest request) {
        HttpRequest httpRequest = null;
        try {
            httpRequest = mRequestProvider.getHttpRequest(URI.create(request.getUrl()), request.getMethod());
        } catch (IOException e) {
            e.printStackTrace();
        }
        sThreadPool.execute(new HttpRunnable(httpRequest, request, this));
        mRunning.add(request);
    }

    public void finish(NormalRequest request) {
        mRunning.remove(request);
        if (mRunning.size() > MAX_REQUEST_SIZE) {
            return;
        }
        if (mCache.size() == 0) {
            return;
        }
        Iterator<NormalRequest> iterator = mCache.iterator();
        while (iterator.hasNext()) {
            NormalRequest next = iterator.next();
            iterator.remove();
            doHttpRequest(next);
        }
    }

}