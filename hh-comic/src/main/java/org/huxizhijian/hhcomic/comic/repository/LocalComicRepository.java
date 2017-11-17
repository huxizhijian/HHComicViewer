package org.huxizhijian.hhcomic.comic.repository;

import org.huxizhijian.hhcomic.comic.bean.Comic;
import org.huxizhijian.hhcomic.comic.db.ComicDBHelper;
import org.huxizhijian.hhcomic.comic.source.base.ComicSource;
import org.huxizhijian.hhcomic.comic.type.DataSourceType;
import org.huxizhijian.hhcomic.comic.type.ComicDataState;
import org.huxizhijian.hhcomic.comic.type.RequestFieldType;
import org.huxizhijian.hhcomic.comic.value.ComicResponseValues;
import org.huxizhijian.hhcomic.comic.value.IComicRequest;
import org.huxizhijian.hhcomic.comic.value.IComicResponse;

import java.util.List;

/**
 * 本地仓库，本质为Comic的数据库操作
 *
 * @author huxizhijian
 * @date 2017/10/12.
 */
public class LocalComicRepository implements ComicDataSource {

    private ComicDBHelper mComicDBHelper = ComicDBHelper.getInstance();

    private LocalComicRepository() {
    }

    private static final class Holder {
        private static final LocalComicRepository INSTANCE = new LocalComicRepository();
    }

    public static LocalComicRepository getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public void get(ComicSource source, IComicRequest requestValues, ComicDataCallback callback) {
        int order = requestValues.getField(RequestFieldType.ORDER);
        boolean highLight = requestValues.getField(RequestFieldType.HIGH_LIGHT);
        List<Comic> comics;
        switch (requestValues.getDataSourceType()) {
            case DataSourceType.DB_DOWNLOADED:
                comics = mComicDBHelper.getDownloadList(order);
                break;
            case DataSourceType.DB_FAVORITE:
                comics = mComicDBHelper.getFavoriteList(order, highLight);
                break;
            case DataSourceType.DB_HISTORY:
                comics = mComicDBHelper.getHistoryList(order);
                break;
            default:
                callback.onError(new NullPointerException("Type is not exist!"));
                return;
        }
        if (comics == null) {
            callback.onDataNotAvailable();
        }
        IComicResponse values = new ComicResponseValues();
        values.setComicResponse(comics);
        callback.onSuccess(values);
    }

    @Override
    public void saveComic(Comic comic, ComicDataState state) {
        if (mComicDBHelper.get(comic.getSource(), comic.getCid()) != null) {
            setComicState(comic, state, System.currentTimeMillis());
            mComicDBHelper.update(comic);
        } else {
            mComicDBHelper.insert(comic);
        }
    }

    @Override
    public void deleteComic(ComicSource source, String comicId, ComicDataState state) {
        Comic comic = mComicDBHelper.get(source.getSourceType(), comicId);
        if (comic != null) {
            setComicState(comic, state, null);
            mComicDBHelper.update(comic);
        }
    }

    @Override
    public void deleteComic(Comic comic, ComicDataState state) {
        if (mComicDBHelper.get(comic.getSource(), comic.getCid()) != null) {
            setComicState(comic, state, null);
            mComicDBHelper.update(comic);
        }
    }

    @Override
    public void deleteComicsRange(ComicSource source, String[] comicIds, ComicDataState state) {
        for (String comicId : comicIds) {
            deleteComic(source, comicId, state);
        }
    }

    @Override
    public void deleteComicsRange(List<Comic> comics, ComicDataState state) {
        for (Comic comic : comics) {
            deleteComic(comic, state);
        }
    }

    @Deprecated
    @Override
    public void checkMarkComicUpdate(ComicDataCallback callback) {

    }

    private void setComicState(Comic comic, ComicDataState state, Long value) {
        if (state == ComicDataState.DOWNLOADED) {
            comic.setDownload(value);
        } else if (state == ComicDataState.FAVORITE) {
            comic.setFavorite(value);
        } else if (state == ComicDataState.HISTORY) {
            comic.setFavorite(value);
        }
    }

}
