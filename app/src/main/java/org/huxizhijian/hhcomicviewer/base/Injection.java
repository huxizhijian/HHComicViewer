package org.huxizhijian.hhcomicviewer.base;

import org.huxizhijian.hhcomic.comic.repository.ComicRepository;
import org.huxizhijian.hhcomic.comic.repository.LocalComicRepository;
import org.huxizhijian.hhcomic.comic.repository.RemoteComicRepository;
import org.huxizhijian.hhcomic.usecase.UseCaseHandler;
import org.huxizhijian.hhcomicviewer.ui.debug.usecase.GetRecommendsUseCase;

/**
 * @author huxizhijian
 * @date 2017/11/13
 */
public class Injection {

    public static ComicRepository provideComicRepository() {
        return ComicRepository.getInstance(LocalComicRepository.getInstance(),
                RemoteComicRepository.getInstance());
    }

    public static UseCaseHandler provideUseCaseHandler() {
        return UseCaseHandler.getInstance();
    }

    public static GetRecommendsUseCase provideGetRecommendsUseCase() {
        return new GetRecommendsUseCase(provideComicRepository());
    }
}
