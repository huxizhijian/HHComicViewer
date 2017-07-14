package org.huxizhijian.hhcomicviewer2.presenter.viewinterface;

import org.huxizhijian.hhcomicviewer2.model.Comic;

import java.util.List;

/**
 * Created by wei on 2017/1/20.
 */

public interface IClassifiesShowActivity {

    void onSuccess(int maxPage, List<Comic> comics);

    void onException(Throwable e);

    void onFailure(int errorCode, String errorMsg);

}
