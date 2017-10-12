package org.huxizhijian.hhcomic.comic.misc;

/**
 * 展示一个关系
 *
 * @Author huxizhijian on 2017/10/12.
 */

public class Pair<F, S> {

    public F first;
    public S second;

    private Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public static <F, S> Pair<F, S> create(F first, S second) {
        return new Pair<>(first, second);
    }

    @Override
    public String toString() {
        return first.toString();
    }

}
