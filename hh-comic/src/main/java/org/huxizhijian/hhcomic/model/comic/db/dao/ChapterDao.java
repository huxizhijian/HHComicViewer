/*
 * Copyright 2016-2018 huxizhijian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.huxizhijian.hhcomic.model.comic.db.dao;

import org.huxizhijian.hhcomic.model.comic.db.entity.Chapter;

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
 * @date 2018/11/1
 */
@Dao
public interface ChapterDao {

    @Query("SELECT * FROM download_chapters WHERE source_key = :sourceKey AND comic_id = :comicId")
    Flowable<List<Chapter>> getDownloadingAndFinishChapters(String sourceKey, String comicId);

    @Query("SELECT * FROM download_chapters WHERE source_key = :sourceKey AND comic_id = :comicId AND is_download_finish")
    Flowable<List<Chapter>> getFinishChapter(String sourceKey, String comicId);

    @Query("SELECT * FROM download_chapters WHERE source_key = :sourceKey AND chapter_id = :chapterId AND comic_id = :comicId")
    Flowable<Chapter> getChapter(String sourceKey, String comicId, String chapterId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Chapter... comics);

    @Delete
    void delete(Chapter... comics);

    @Update
    void update(Chapter... comics);
}
