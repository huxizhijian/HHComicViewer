package org.huxizhijian.hhcomic.comic.sources.base;

import org.huxizhijian.hhcomic.comic.entities.Chapter;
import org.huxizhijian.hhcomic.comic.entities.Comic;
import org.huxizhijian.hhcomic.comic.net.ComicRequest;
import org.huxizhijian.hhcomic.comic.net.ComicResponse;

import java.util.List;

/**
 * Comic info parser
 *
 * @author huxizhijian
 * @date 5/21/2018
 */
public interface ComicInfoParser {
    /**
     * Get comic more info page.
     *
     * @param cid comic id, that can use to generate url
     * @return request
     */
    ComicRequest getComicInfoRequest(String cid);

    /**
     * Parse html to comic model.
     *
     * @param response comic info response, it can get response body, header and so on.
     * @param comic    comic model
     */
    void parseInfo(ComicResponse response, Comic comic);

    /**
     * Chapter list request
     *
     * @param response comic info response
     * @param cid      comic id
     * @return If get chapter list should get with once more connection, return a request, else return null.
     */
    ComicRequest getChapterRequest(ComicResponse response, String cid);

    /**
     * Parse response to chapter model.
     *
     * @param response if getChapterRequest return not null, it should be comic info response,
     *                 else it is chapter info response
     * @return list of chapter model
     */
    List<Chapter> parseChapter(ComicResponse response);

    /**
     * Make a comic update check request, it usually the same with {@link #getComicInfoRequest}.
     *
     * @return check update request
     */
    ComicRequest getCheckRequest(String cid);

    /**
     * parse check update response.
     *
     * @param response response of {@link #getCheckRequest}
     * @return update time
     */
    String parseCheck(ComicResponse response);
}
