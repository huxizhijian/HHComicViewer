/*
 * Copyright 2018 huxizhijian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.huxizhijian.hhcomic.comic.bean;

/**
 * 保存在文件中的info
 *
 * @author huxizhijian
 * @date 2017/10/23
 */
public class SpiderInfo {

    private static final String VERSION_STR = "VERSION";
    private static final int VERSION = 1;

    public int startPage = 0;
    public int source = -1;
    public int chapterId = -1;
    public int comicId = -1;
    public int pages = -1;

    /*public static SpiderInfo read(@Nullable File file) {
        if (file == null) {
            return null;
        }

        InputStream is = null;
        try {
            is = new FileInputStream(file);
            return read(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static SpiderInfo read(InputStream is) {

    }*/

}
