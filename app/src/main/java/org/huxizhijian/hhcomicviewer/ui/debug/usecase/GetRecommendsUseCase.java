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
        mComicRepository.get(ComicRouter.getInstance().getSource(request.getRequestType()), request, new ComicDataSource.ComicDataCallback() {
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
