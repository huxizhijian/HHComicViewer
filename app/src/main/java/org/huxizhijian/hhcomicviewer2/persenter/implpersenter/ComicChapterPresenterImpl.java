package org.huxizhijian.hhcomicviewer2.persenter.implpersenter;

import org.huxizhijian.hhcomicviewer2.model.ComicChapter;
import org.huxizhijian.hhcomicviewer2.persenter.IComicChapterPreenter;
import org.huxizhijian.hhcomicviewer2.persenter.viewinterface.IComicChapterListener;
import org.huxizhijian.hhcomicviewer2.utils.CommonUtils;
import org.huxizhijian.hhcomicviewer2.utils.HHApiProvider;
import org.huxizhijian.sdk.network.service.NormalRequest;
import org.huxizhijian.sdk.network.service.NormalResponse;

import java.io.UnsupportedEncodingException;

/**
 * Created by wei on 2017/1/7.
 */

public class ComicChapterPresenterImpl implements IComicChapterPreenter {

    private IComicChapterListener mListener;

    public ComicChapterPresenterImpl(IComicChapterListener listener) {
        mListener = listener;
    }

    @Override
    public void getComicChapter(final ComicChapter comicChapter) {
        /*Request request = new Request.Builder().get()
                .url(CommonUtils.getChapterUrl(comicChapter.getCid(), comicChapter.getChid(),
                        comicChapter.getServerId()))
                .build();
        HHApplication.getInstance().getClient().newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        mListener.onFail(e, comicChapter);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            final String content = new String(response.body().bytes(), "gb2312");
                            comicChapter.updatePicList(comicChapter.getServerId(), content);
                            mListener.onSuccess(comicChapter);
                        } catch (UnsupportedEncodingException e) {
                            mListener.onFail(e, comicChapter);
                        }
                    }
                });*/

        HHApiProvider.getInstance().getWebContentAsyn(CommonUtils.getChapterUrl(comicChapter.getCid(),
                comicChapter.getChid(), comicChapter.getServerId()), new NormalResponse<byte[]>() {
            @Override
            public void success(NormalRequest request, byte[] data) {
                try {
                    final String content = new String(data, "gb2312");
                    comicChapter.updatePicList(comicChapter.getServerId(), content);
                    for (int i = 0; i < comicChapter.getPicList().size(); i++) {
                        System.out.println(comicChapter.getPicList().get(i));
                    }
                    mListener.onSuccess(comicChapter);
                } catch (UnsupportedEncodingException e) {
                    mListener.onException(e, comicChapter);
                }
            }

            @Override
            public void fail(int errorCode, String errorMsg) {
                mListener.onFail(errorCode, errorMsg, comicChapter);
            }
        });
    }
}
