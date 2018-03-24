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

import org.huxizhijian.hhcomicviewer.model.Comic;
import org.huxizhijian.hhcomicviewer.presenter.IComicDetailsPresenter;
import org.huxizhijian.hhcomicviewer.presenter.viewinterface.IComicDetailsActivity;
import org.huxizhijian.hhcomicviewer.utils.CommonUtils;
import org.huxizhijian.hhcomicviewer.utils.HHApiProvider;
import org.huxizhijian.sdk.network.service.NormalRequest;
import org.huxizhijian.sdk.network.service.NormalResponse;

import java.io.UnsupportedEncodingException;

/**
 * Created by huxizhijian on 2017/1/3.
 */

public class ComicDetailsPresenter implements IComicDetailsPresenter {

    private IComicDetailsActivity mComicDetailsActivity;

    public ComicDetailsPresenter(IComicDetailsActivity activity) {
        this.mComicDetailsActivity = activity;
    }

    public String getComicUrl(int cid) {
        return CommonUtils.getComicUrl(cid);
    }

    @Override
    public void getComic(final int cid, final Comic oldComic) {
        /*final Request request = new Request.Builder().get()
                .url(CommonUtils.getComicUrl(cid))
                .build();
        HHApplication.getInstance().getClient().newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        mComicDetailsActivity.onFailure(e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Comic comic = null;
                        try {
                            String content = new String(response.body().bytes(), "utf-8");
                            //初始化
                            if (oldComic == null) {
                                comic = new Comic(cid, content);
                            } else {
                                comic = oldComic;
                                comic.checkUpdate(content);
                            }
                            mComicDetailsActivity.onResponse(comic);
                        } catch (UnsupportedEncodingException e) {
                            mComicDetailsActivity.onFailure(e);
                        }
                    }
                });*/
        HHApiProvider.getInstance().getWebContentAsyn(CommonUtils.getComicUrl(cid), new NormalResponse<byte[]>() {
            @Override
            public void success(NormalRequest request, byte[] data) {
                Comic comic = null;
                try {
                    String content = new String(data, "utf-8");
                    //初始化
                    if (oldComic == null) {
                        comic = new Comic(cid, content);
                    } else {
                        comic = oldComic;
                        comic.checkUpdate(content);
                    }
                    if (mComicDetailsActivity != null)
                        mComicDetailsActivity.onResponse(comic);
                } catch (UnsupportedEncodingException e) {
                    if (mComicDetailsActivity != null)
                        mComicDetailsActivity.onException(e);
                }
            }

            @Override
            public void fail(int errorCode, String errorMsg) {
                if (mComicDetailsActivity != null)
                    mComicDetailsActivity.onFailure(errorCode, errorMsg);
            }
        });
    }

    @Override
    public void removeListener() {
        mComicDetailsActivity = null;
    }


}
