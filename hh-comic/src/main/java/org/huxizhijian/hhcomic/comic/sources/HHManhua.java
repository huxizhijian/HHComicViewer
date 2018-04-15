package org.huxizhijian.hhcomic.comic.sources;

import org.huxizhijian.annotations.SourceImpl;
import org.huxizhijian.hhcomic.comic.net.ComicRequest;
import org.huxizhijian.hhcomic.comic.sources.base.Source;

import static org.huxizhijian.hhcomic.comic.sources.HHManhua.HH_MANHUA;

/**
 * 汗汗漫画策略实现类
 *
 * @author huxizhijian
 * @date 2018/4/9
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
@SourceImpl(name = HH_MANHUA)
public class HHManhua implements Source {
    public static final String HH_MANHUA = "汗汗漫画";

    public void createRequest(){
    }
}
