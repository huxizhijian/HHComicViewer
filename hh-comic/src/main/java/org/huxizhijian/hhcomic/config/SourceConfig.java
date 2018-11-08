package org.huxizhijian.hhcomic.config;

/**
 * 源设置实体
 * @author huxizhijian
 * @date 2018/11/2
 */
public class SourceConfig {

    /**
     * 源key
     */
    private String mSourceKey;

    /**
     * 源名称
     */
    private String mSourceName;

    /**
     * 是否开启
     */
    private boolean mActive;

    public SourceConfig() {
    }

    public SourceConfig(String sourceKey, boolean active) {
        mSourceKey = sourceKey;
        mActive = active;
    }

    public String getSourceKey() {
        return mSourceKey;
    }

    public void setSourceKey(String sourceKey) {
        mSourceKey = sourceKey;
    }

    public String getSourceName() {
        return mSourceName;
    }

    public void setSourceName(String sourceName) {
        mSourceName = sourceName;
    }

    public boolean isActive() {
        return mActive;
    }

    public void setActive(boolean active) {
        mActive = active;
    }

    @Override
    public String toString() {
        return "SourceConfig{" +
                "mSourceKey='" + mSourceKey + '\'' +
                ", mSourceName='" + mSourceName + '\'' +
                ", mActive=" + mActive +
                '}';
    }
}
