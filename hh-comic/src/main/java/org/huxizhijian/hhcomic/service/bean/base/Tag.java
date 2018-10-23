package org.huxizhijian.hhcomic.service.bean.base;

/**
 * 过滤/排序/标签的基类
 *
 * @author huxizhijian
 * @date 2018/10/10
 */
public abstract class Tag {

    /**
     * 从属的主题，同一主题的过滤/排序只能选择一个
     */
    protected final String mSubtitle;
    /**
     * tag id
     */
    protected final String mId;
    /**
     * 对于该tag的表述
     */
    protected final String mName;
    /**
     * 该tag从属的source id
     */
    protected final String mSourceId;

    public Tag(String subtitle, String id, String name, String sourceId) {
        mSubtitle = subtitle;
        mId = id;
        mName = name;
        mSourceId = sourceId;
    }

    public String getSubtitle() {
        return mSubtitle;
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getSourceId() {
        return mSourceId;
    }
}
