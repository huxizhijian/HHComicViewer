package org.huxizhijian.hhcomic.comic.bean;

import android.util.SparseArray;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;

/**
 * @author huxizhijian
 * @date 2018/8/31
 */
public final class ChapterImage {

    private final ChapterImageGet mChapterImageGet;
    private final int mPageCount;
    private final SparseArray<String> mPageUrlArray;

    private ChapterImage(ChapterImageBuilder builder) {
        mChapterImageGet = builder.mChapterImageGet;
        mPageCount = builder.mPageCount;
        if (builder.mImageUrlArray != null && builder.mImageUrlArray.size() != 0) {
            mPageUrlArray = builder.mImageUrlArray.clone();
        } else {
            mPageUrlArray = new SparseArray<>();
        }
        mPageUrlArray.put(builder.mPreparePosition, builder.mPrepareImageUrl);
    }

    /**
     * 获取指定的图片地址
     *
     * @param page 图片位置
     * @return flowable
     */
    public Flowable getPageImageUrl(int page) {
        return Flowable.create(emitter -> {
            String url = mPageUrlArray.get(page);
            if (url == null) {
                url = mChapterImageGet.getPageImageUrl(page);
                if (url != null) {
                    mPageUrlArray.put(page, url);
                } else {
                    emitter.onError(new NullPointerException("cannot get page image url."));
                }
            }
            emitter.onNext(url);
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER);
    }

    /**
     * 从第一张图片地址开始发射直到最后一张图片地址
     *
     * @return flowable
     */
    public Flowable getAllPageImageUrl() {
        return Flowable.create(emitter -> {
            for (int i = 0; i < mPageCount; i++) {
                String url = mPageUrlArray.get(i);
                if (url == null) {
                    url = mChapterImageGet.getPageImageUrl(i);
                    if (url != null) {
                        mPageUrlArray.put(i, url);
                    } else {
                        emitter.onError(new NullPointerException("cannot get page image url."));
                    }
                }
                emitter.onNext(url);
            }
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER);
    }

    public int getPageCount() {
        return mPageCount;
    }

    public static class ChapterImageBuilder {

        ChapterImageGet mChapterImageGet;
        int mPageCount;
        String mPrepareImageUrl;
        int mPreparePosition;
        SparseArray<String> mImageUrlArray;

        /**
         * 如果不能一下子获取到所有图片，调用这个方法，然后在{@link ChapterImageBuilder#imageGet(ChapterImageGet)}
         * 方法中传入获取指定图片地址的回调接口
         *
         * @param page      图片位置
         * @param imageUrl  第page张图片地址
         * @param pageCount 章节图片数量
         * @return this
         */
        public ChapterImageBuilder prepareLazyLoad(int page, String imageUrl, int pageCount) {
            mPreparePosition = page;
            mPrepareImageUrl = imageUrl;
            mPageCount = pageCount;
            return this;
        }

        /**
         * 可以获取所有章节图片时调用
         *
         * @param imageUrlArray 章节图片array
         * @return this
         */
        public ChapterImageBuilder fullImageUrl(SparseArray<String> imageUrlArray) {
            mImageUrlArray = imageUrlArray;
            mPageCount = mImageUrlArray.size();
            return this;
        }

        /**
         * lazy load采用的获取图片地址回调
         *
         * @param imageGet 回调
         * @return this
         */
        public ChapterImageBuilder imageGet(ChapterImageGet imageGet) {
            mChapterImageGet = imageGet;
            return this;
        }

        public ChapterImage build() {
            if (mPageCount == 0) {
                throw new IllegalArgumentException("You should call prepare() and imageGet(), " +
                        "or call fullImageUrl().");
            }
            return new ChapterImage(this);
        }
    }

    public interface ChapterImageGet {
        /**
         * 一个回调接口，可让调用者使用lambda表达式实现
         *
         * @param page 想获取的页码
         * @return image图片的url
         */
        String getPageImageUrl(int page);
    }
}
