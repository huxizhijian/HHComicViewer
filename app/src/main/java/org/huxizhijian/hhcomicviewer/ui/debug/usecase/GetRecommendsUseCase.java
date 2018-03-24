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

package org.huxizhijian.hhcomicviewer.ui.debug.usecase;

import org.huxizhijian.hhcomic.comic.ComicRouter;
import org.huxizhijian.hhcomic.comic.repository.ComicDataSource;
import org.huxizhijian.hhcomic.comic.repository.ComicRepository;
import org.huxizhijian.hhcomic.comic.value.IComicRequest;
import org.huxizhijian.hhcomic.comic.value.IComicResponse;
import org.huxizhijian.hhcomic.usecase.UseCase;

import static org.huxizhijian.hhcomic.util.Preconditions.checkNotNull;

/**
 * @author huxizhijian
 * @date 2017/11/9
 */
public class GetRecommendsUseCase extends UseCase<UseCase.RequestValues, UseCase.ResponseValue> {

    /**
     * 依赖注入
     */
    private final ComicRepository mComicRepository;

    public GetRecommendsUseCase(ComicRepository comicRepository) {
        mComicRepository = checkNotNull(comicRepository, "comicRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        IComicRequest request = requestValues.getValues();
        mComicRepository.get(ComicRouter.getInstance().getSource(request.getComicSourceHashCode()),
                request, new ComicDataSource.ComicDataCallback() {
                    @Override
                    public void onSuccess(IComicResponse responseValues) {
                        getUseCaseCallback().onSuccess(() -> responseValues);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        throwable.printStackTrace();
                        getUseCaseCallback().onError();
                    }

                    @Override
                    public void onDataNotAvailable() {
                        getUseCaseCallback().onError();
                    }
                });
    }
}
