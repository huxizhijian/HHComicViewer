package org.huxizhijian.hhcomic.model.repository.bean;

import androidx.annotation.Nullable;
import androidx.annotation.StringDef;

/**
 * 返回值包装类
 *
 * @author huxizhijian
 * @date 2018/11/16
 */
public class Response<T> {

    public Response() {
    }

    public Response(T obj, @State String state) {
        this.obj = obj;
        this.state = state;
    }

    public Response(T obj, @State String state, String message, Exception exception) {
        this.obj = obj;
        this.state = state;
        this.message = message;
        this.exception = exception;
    }

    /**
     * 真正的返回值
     */
    public T obj;

    /**
     * 返回的状态
     */
    @State
    public String state;

    /**
     * 信息（中文）
     */
    @Nullable
    public String message;

    /**
     * 发生的异常
     */
    @Nullable
    public Throwable exception;

    public static final String SUCCESS_STATE = "SUCCESS_STATE";

    public static final String ERROR_STATE = "ERROR_STATE";

    public static final String EMPTY_STATE = "EMPTY_STATE";

    @StringDef({SUCCESS_STATE, ERROR_STATE, EMPTY_STATE})
    @interface State {
    }
}
