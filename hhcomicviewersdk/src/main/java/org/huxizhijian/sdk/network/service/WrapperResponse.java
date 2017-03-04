package org.huxizhijian.sdk.network.service;


import org.huxizhijian.sdk.network.service.convert.Convert;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by huxizhijian on 2016/11/19.
 */

public class WrapperResponse extends NormalResponse<byte[]> {

    private NormalResponse mNormalResponse;
    private List<Convert> mConvertList;

    public WrapperResponse(NormalResponse normalResponse, List<Convert> convertList) {
        this.mNormalResponse = normalResponse;
        this.mConvertList = convertList;
    }

    @Override
    public void success(NormalRequest request, byte[] data) {
        for (Convert convert : mConvertList) {
            if (convert.isCanParse(request.getContentType())) {
                try {
                    Object object = convert.parse(new String(data), getType());
                    mNormalResponse.success(request, object);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void fail(int errorCode, String errorMsg) {

    }

    public Type getType() {
        Type type = mNormalResponse.getClass().getGenericSuperclass();
        Type[] paramType = ((ParameterizedType) type).getActualTypeArguments();
        return paramType[0];
    }
}
