package org.huxizhijian.hhcomicviewer.ui.debug.usecase;

import org.huxizhijian.hhcomic.comic.value.IComicRequest;
import org.huxizhijian.hhcomic.comic.value.IComicResponse;
import org.huxizhijian.hhcomic.usecase.UseCase;

/**
 * @author huxizhijian
 * @date 2017/11/9
 */
public class GetRecommends extends UseCase<GetRecommends.RequestValues, GetRecommends.ResponseValue> {

    @Override
    protected void executeUseCase(RequestValues requestValues) {

    }

    public static class RequestValues implements UseCase.RequestValues {

        @Override
        public IComicRequest getValues() {
            return null;
        }
    }

    public static class ResponseValue implements UseCase.ResponseValue {

        @Override
        public IComicResponse getValues() {
            return null;
        }
    }
}
