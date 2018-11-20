package org.huxizhijian.hhcomic.model.repository.base;

import androidx.annotation.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * 仓库基类，整合一个可取消的请求列表，子类在请求RxJava连接后调用addDisposable，可在clear时取消所有连接
 *
 * @author huxizhijian
 * @date 2018/11/15
 */
public abstract class BaseRepository {

    private CompositeDisposable mCompositeDisposable;

    public BaseRepository() {
        mCompositeDisposable = new CompositeDisposable();
    }

    public void clear() {
        mCompositeDisposable.clear();
    }

    protected void addDisposable(@NonNull Disposable disposable) {
        mCompositeDisposable.add(disposable);
    }
}
