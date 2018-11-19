package org.huxizhijian.hhcomic.model.comic.db.dao;

import org.huxizhijian.hhcomic.model.comic.db.entity.Comic;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import io.reactivex.Flowable;

/**
 * @author huxizhijian
 * @date 2018/10/25
 */
@Dao
public interface ComicDao {

    @Query("SELECT * FROM comics WHERE is_favorite LIKE :isFavorite ORDER BY last_time DESC")
    Flowable<List<Comic>> getByFavorite(boolean isFavorite);

    @Query("SELECT * FROM comics WHERE has_download_chapter LIKE :isDownload ORDER BY last_download_time DESC")
    Flowable<List<Comic>> getByDownload(boolean isDownload);

    @Query("SELECT * FROM comics WHERE source_key = :sourceKey AND comic_id = :comicId LIMIT 1")
    Flowable<Comic> getComic(String sourceKey, String comicId);

    @Query("SELECT * FROM comics WHERE id = :id LIMIT 1")
    Flowable<Comic> getComic(int id);

    /**
     * 根据最后一次观看时间降序排列（ASC为默认的升序）
     *
     * @return comic list
     */
    @Query("SELECT * FROM comics ORDER BY last_time DESC")
    Flowable<List<Comic>> getComicListByReadTime();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Comic... comics);

    @Delete
    void delete(Comic... comics);

    @Update
    void update(Comic... comics);
}
