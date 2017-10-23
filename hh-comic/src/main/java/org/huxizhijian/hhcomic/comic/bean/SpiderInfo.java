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
