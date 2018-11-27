package org.huxizhijian.hhcomic.viewmodel.base;

import android.app.Application;

import org.huxizhijian.hhcomic.model.repository.base.BaseRepository;

import java.lang.reflect.ParameterizedType;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

/**
 * 基础ViewModel类
 *
 * @author huxizhijian
 * @date 2018/11/15
 */
public class BaseViewModel<T extends BaseRepository> extends AndroidViewModel {

    protected T mRepository;

    public BaseViewModel(@NonNull Application application) {
        super(application);
        mRepository = getNewInstance(this);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // 取消所有订阅
        if (mRepository != null) {
            mRepository.clear();
        }
    }

    /**
     * 使用反射实例化第一个泛型对象
     *
     * @return T的Class类型
     */
    @SuppressWarnings("unchecked cast")
    private T getNewInstance(Object object) {
        if (object != null) {
            try {
                return ((Class<T>) ((ParameterizedType) (object.getClass()
                        .getGenericSuperclass())).getActualTypeArguments()[0])
                        .newInstance();
            } catch (InstantiationException | IllegalAccessException | ClassCastException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
