/*
 * Copyright 2018 huxizhijian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.huxizhijian.hhcomicviewer.presenter.implpersenter;

import org.huxizhijian.hhcomicviewer.model.ComicChapter;
import org.huxizhijian.hhcomicviewer.presenter.IComicChapterPresenter;
import org.huxizhijian.hhcomicviewer.presenter.viewinterface.IComicChapterListener;
import org.huxizhijian.hhcomicviewer.utils.CommonUtils;
import org.huxizhijian.hhcomicviewer.utils.HHApiProvider;
import org.huxizhijian.sdk.network.service.NormalRequest;
import org.huxizhijian.sdk.network.service.NormalResponse;

import java.io.UnsupportedEncodingException;

/**
 * @Author wei on 2017/1/7.
 */

public class ComicChapterPresenterImpl implements IComicChapterPresenter {

    IComicChapterListener mListener;

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
                    if (mListener != null) {
                        mListener.onSuccess(comicChapter);
                    }
                } catch (UnsupportedEncodingException e) {
                    if (mListener != null) {
                        mListener.onException(e, comicChapter);
                    }
                }
            }

            @Override
            public void fail(int errorCode, String errorMsg) {
                if (mListener != null) {
                    mListener.onFail(errorCode, errorMsg, comicChapter);
                }
            }
        });
    }

    @Override
    public void removeListener() {
        mListener = null;
    }
}
