package org.huxizhijian.hhcomic.model.repository.base;

import com.blankj.utilcode.util.NetworkUtils;
import com.bumptech.glide.load.HttpException;

import org.huxizhijian.core.util.HHLogger;
import org.huxizhijian.hhcomic.model.comic.HHComic;
import org.huxizhijian.hhcomic.model.comic.config.HHComicConfig;
import org.huxizhijian.hhcomic.model.comic.config.SourceConfig;
import org.huxizhijian.hhcomic.model.comic.service.source.base.SourceInfo;
import org.huxizhijian.hhcomic.model.repository.bean.Resource;
import org.json.JSONException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

    /**
     * 获取source info，即source类本身
     *
     * @param sourceKey source key
     * @param sourceLiveData 外部提供的live data
     */
    public void getSourceInfo(@NonNull String sourceKey, MutableLiveData<Resource<SourceInfo>> sourceLiveData) {
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
    protected <T> void successResult(@NonNull MutableLiveData<Resource<T>> liveData, @NonNull Object obj) {
        // 注意，setValue()仅能在主线程使用，如果想要在子线程也可以使用，调用postValue()
        liveData.setValue(Resource.success((T) obj));
    }

    protected <T> void emptyResult(@NonNull MutableLiveData<Resource<T>> liveData) {
        liveData.setValue(Resource.empty());
    }

    protected <T> void loadingResult(@NonNull MutableLiveData<Resource<T>> liveData) {
        liveData.setValue(Resource.loading(null));
    }

    protected <T> void errorResult(@NonNull MutableLiveData<Resource<T>> liveData, @Nullable Throwable throwable) {
        String message;
        if (throwable != null) {
            HHLogger.e("RxJava2 exception", throwable, throwable.getMessage());
            if (!NetworkUtils.isConnected()) {
                // 网络未连接
                liveData.setValue(Resource.noNetwork());
                return;
            } else if (throwable instanceof UnknownHostException) {
                message = "没有网络";
            } else if (throwable instanceof HttpException) {
                message = "网络请求错误";
            } else if (throwable instanceof SocketTimeoutException) {
                message = "网络连接超时";
            } else if (throwable instanceof JSONException
                    || throwable instanceof NullPointerException) {
                message = "解析错误";
            } else if (throwable instanceof ConnectException) {
                message = "连接失败";
            } else if (throwable instanceof IOException) {
                message = "数据读写错误";
            } else {
                message = "错误";
            }
        } else {
            message = "未知错误";
        }
        liveData.setValue(Resource.error(message, null, throwable));
    }

    public List<SourceConfig> getSourceConfigs() {
        return mSourceConfigs;
    }
}
