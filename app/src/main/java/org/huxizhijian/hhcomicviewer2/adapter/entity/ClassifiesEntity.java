package org.huxizhijian.hhcomicviewer2.adapter.entity;

/**
 * Created by wei on 2017/1/9.
 */

public class ClassifiesEntity {

    private String classifiesName;
    private String classifiesUrl;
    private String classifiesPicUrl;

    public ClassifiesEntity() {
    }

    public ClassifiesEntity(String classifiesName, String classifiesUrl, String classifiesPicUrl) {
        this.classifiesName = classifiesName;
        this.classifiesUrl = classifiesUrl;
        this.classifiesPicUrl = classifiesPicUrl;
    }

    public String getClassifiesName() {
        return classifiesName;
    }

    public void setClassifiesName(String classifiesName) {
        this.classifiesName = classifiesName;
    }

    public String getClassifiesUrl() {
        return classifiesUrl;
    }

    public void setClassifiesUrl(String classifiesUrl) {
        this.classifiesUrl = classifiesUrl;
    }

    public String getClassifiesPicUrl() {
        return classifiesPicUrl;
    }

    public void setClassifiesPicUrl(String classifiesPicUrl) {
        this.classifiesPicUrl = classifiesPicUrl;
    }
}
