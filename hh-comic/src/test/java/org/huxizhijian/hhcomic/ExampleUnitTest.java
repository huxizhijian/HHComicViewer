package org.huxizhijian.hhcomic;

import org.huxizhijian.hhcomic.comic.source.base.Source;
import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void main() {
        System.out.println("Dmzj hashcode = " + Source.Dmzj.hashCode());
        System.out.println("HHManhua hashcode = " + Source.HHManHua.hashCode());
    }
}