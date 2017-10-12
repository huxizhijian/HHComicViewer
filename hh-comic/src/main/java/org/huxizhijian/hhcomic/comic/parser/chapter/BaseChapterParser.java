package org.huxizhijian.hhcomic.comic.parser.chapter;

import android.util.SparseArray;

import org.huxizhijian.core.app.ConfigKeys;
import org.huxizhijian.core.app.HHEngine;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @Author huxizhijian on 2017/10/12.
 */

public abstract class BaseChapterParser implements IChapterParser {

    private final SparseArray<String> mImageUrlArray = new SparseArray<>();

    protected abstract String getChapterUrl(String urlKey, int page);

    private BaseChapterParser() {

    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public String moveToNext() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    private byte[] getWebContent(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        OkHttpClient client = HHEngine.getConfiguration(ConfigKeys.OKHTTP_CLIENT);
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException();
        }
        return response.body().bytes();
    }

}
