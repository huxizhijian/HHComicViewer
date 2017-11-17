package org.huxizhijian.hhcomicviewer.ui.debug;

import org.huxizhijian.hhcomic.comic.ComicRouter;
import org.huxizhijian.hhcomic.comic.bean.Comic;
import org.huxizhijian.hhcomic.comic.repository.ComicRepository;
import org.huxizhijian.hhcomic.comic.type.DataSourceType;
import org.huxizhijian.hhcomic.comic.type.RequestFieldType;
import org.huxizhijian.hhcomic.comic.type.ResponseFieldType;
import org.huxizhijian.hhcomic.comic.value.ComicRequestValues;
import org.huxizhijian.hhcomic.comic.value.IComicRequest;
import org.huxizhijian.hhcomic.comic.value.IComicResponse;
import org.huxizhijian.hhcomic.usecase.UseCase;
import org.huxizhijian.hhcomic.usecase.UseCaseHandler;
import org.huxizhijian.hhcomicviewer.ui.debug.usecase.GetRecommendsUseCase;

import java.util.List;
import java.util.Map;

/**
 * @author huxizhijian
 * @date 2017/11/10
 */
public class GetRecommendsPresenter implements GetRecommendsContract.Presenter {

    private final ComicRepository mComicRepository;
    private final UseCaseHandler mUseCaseHandler;
    private final GetRecommendsUseCase mUseCase;
    private final GetRecommendsContract.View mView;
    private final int mSourceType;

    private int mStartIndex = 0;
    private int mPage;
    private int mPageCount;

    public GetRecommendsPresenter(ComicRepository repository, GetRecommendsContract.View view,
                                  UseCaseHandler handler, GetRecommendsUseCase useCase, int sourceType) {
        mComicRepository = repository;
        mView = view;
        view.setPresenter(this);
        mUseCaseHandler = handler;
        mUseCase = useCase;
        mSourceType = sourceType;
    }

    @Override
    public void start() {
        mPage = mStartIndex;
        getComicList(false);
    }

    @Override
    public void getNextPage() {
        if (mPageCount != -1 && mPage >= mPageCount) {
            mView.onNoMore();
        } else {
            mPage++;
            getComicList(true);
        }
    }

    @Override
    public void getSpecifyPage(int page) {
        if (mPageCount != -1 && page >= mPageCount) {
            mView.onEmptyList();
            return;
        }
        mPage = page;
        mStartIndex = page;
        getComicList(false);
    }

    private void getComicList(boolean isLoadMore) {
        IComicRequest request = new ComicRequestValues();
        request.addField(RequestFieldType.PAGE, mPage);
        request.setComicSourceHashCode(mSourceType);
        request.setDataSourceType(DataSourceType.WEB_RECOMMENDED);
        //todo 优化
        Map<String, String> type = ComicRouter.getInstance().getSource(mSourceType).getRecommendTypeMap();
        String recommendType = "";
        if (type.entrySet().iterator().hasNext()) {
            recommendType = type.entrySet().iterator().next().getKey();
        }
        request.addField(RequestFieldType.RECOMMEND_TYPE, recommendType);
        mUseCaseHandler.execute(mUseCase, () -> request, new UseCase.UseCaseCallback<UseCase.ResponseValue>() {
            @Override
            public void onSuccess(UseCase.ResponseValue responseValue) {
                IComicResponse response = responseValue.getValues();
                List<Comic> comicList = response.getComicResponse();
                mPageCount = response.getField(ResponseFieldType.PAGE_COUNT);
                boolean hasMore = true;
                if (mPageCount != -1) {
                    hasMore = (mPage < mPageCount - 1);
                }
                if (isLoadMore) {
                    mView.onGetNextPageSuccess(comicList, hasMore);
                } else {
                    mView.onGetComicList(comicList, hasMore);
                }
            }

            @Override
            public void onError() {
                mView.onError();
            }
        });
    }

    @Override
    public void refresh() {
        mPage = mStartIndex;
        getComicList(false);
    }

    @Override
    public int getPage() {
        return mPage;
    }

    @Override
    public int getPageCount() {
        return mPageCount;
    }
}
