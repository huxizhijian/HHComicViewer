package org.huxizhijian.hhcomic.comic.sources.base;

import android.support.annotation.Nullable;

import org.huxizhijian.hhcomic.comic.entities.ImageUrl;
import org.huxizhijian.hhcomic.comic.net.ComicRequest;
import org.huxizhijian.hhcomic.comic.net.ComicResponse;

import java.util.List;

/**
 * @author huxizhijian
 * @date 5/21/2018
 */
public interface ImageParser {
    /**
     * Get Image url request.
     *
     * @param cid  comic id
     * @param path chapter path
     * @return chapter page request
     */
    ComicRequest getImageRequest(String cid, String path);

    /**
     * Parse image urls.
     *
     * @param response chapter response
     * @return list of image url
     */
    List<ImageUrl> parseImages(ComicResponse response);

    /**
     * Get chapter specified index page request, if source neet lazy load image url.
     *
     * @param url  chapter page url, set in {@link ImageUrl}, maybe null.
     * @param page chapter page
     * @return chapter page path
     */
    ComicRequest getLazyRequest(@Nullable String url, int page);

    /**
     * Image url lazy parse.
     *
     * @param response lazy chapter page response
     * @param url      chapter url
     * @return image url
     */
    String parseLazy(ComicResponse response, String url);
}
