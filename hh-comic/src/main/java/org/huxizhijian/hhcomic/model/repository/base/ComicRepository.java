package org.huxizhijian.hhcomic.model.repository.base;

import com.blankj.utilcode.util.NetworkUtils;
import com.bumptech.glide.load.HttpException;

import org.huxizhijian.core.util.HHLogger;
import org.huxizhijian.hhcomic.model.comic.HHComic;
import org.huxizhijian.hhcomic.model.comic.config.HHComicConfig;
import org.huxizhijian.hhcomic.model.comic.config.SourceConfig;
import org.huxizhijian.hhcomic.model.comic.service.source.base.SourceInfo;
import org.huxizhijian.hhcomic.model.repository.bean.Response;
import org.json.JSONException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 整合了model的comic部分的基础逻辑，以及对于返回值得便捷处理，和ComicViewModel配套使用
 *
 * @author huxizhijian
 * @date 2018/11/20
 */
public abstract class ComicRepository extends BaseRepository {

    private List<SourceConfig> mSourceConfigs;

    public ComicRepository() {
        super();
        mSourceConfigs = HHComic.getSourceConfigList();
    }

    public void getSourceInfo(@NonNull String sourceKey, @NonNull MutableLiveData<Response<SourceInfo>> sourceLiveData) {
        Disposable disposable = Flowable.create(emitter -> {
            emitter.onNext(HHComic.getSource(sourceKey));
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(source -> successResult(sourceLiveData, source),
                        throwable -> errorResult(sourceLiveData, throwable));
        addDisposable(disposable);
    }

    public HHComicConfig getConfigUtil() {
        return HHComic.config();
    }

    public HHComic.DatabaseGuide getDaoGuide() {
        return HHComic.db();
    }

    @SuppressWarnings("unchecked cast")
    protected <T> void successResult(@NonNull MutableLiveData<Response<T>> liveData, @NonNull Object obj) {
        Response<T> response = new Response<>((T) obj, Response.SUCCESS_STATE);
        // 注意，setValue()仅能在主线程使用，如果想要在子线程也可以使用，调用postValue()
        liveData.setValue(response);
    }

    protected <T> void emptyResult(@NonNull MutableLiveData<Response<T>> liveData) {
        Response<T> response = new Response<>(null, Response.EMPTY_STATE);
        response.message = "空结果";
        liveData.setValue(response);
    }

    protected <T> void errorResult(@NonNull MutableLiveData<Response<T>> liveData, @NonNull Throwable throwable) {
        HHLogger.e("RxJava2 exception in repository", throwable.getMessage());
        Response<T> response = new Response<>(null, Response.ERROR_STATE);
        response.exception = throwable;
        if (!NetworkUtils.isConnected()) {
            response.message = "网络未连接";
        } else if (throwable instanceof UnknownHostException) {
            response.message = "没有网络";
        } else if (throwable instanceof HttpException) {
            response.message = "网络请求错误";
        } else if (throwable instanceof SocketTimeoutException) {
            response.message = "网络连接超时";
        } else if (throwable instanceof JSONException
                || throwable instanceof NullPointerException) {
            response.message = "解析错误";
        } else if (throwable instanceof ConnectException) {
            response.message = "连接失败";
        } else if (throwable instanceof IOException) {
            response.message = "数据读写错误";
        } else {
            response.message = "错误";
        }
        liveData.setValue(response);
    }

    public List<SourceConfig> getSourceConfigs() {
        return mSourceConfigs;
    }
}
