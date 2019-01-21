/*
 * Copyright 2016-2018 huxizhijian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.huxizhijian.hhcomicviewer.util;

import org.huxizhijian.hhcomic.model.repository.bean.Resource;
import org.huxizhijian.hhcomicviewer.weight.MultipleStatusView;

import androidx.annotation.NonNull;

/**
 * 帮助处理{@link MultipleStatusView}各种状态的类
 *
 * @author huxizhijian
 * @date 2018/12/22
 */
public class StatusViewHelper {

    private StatusViewHelper() {
    }

    public static void holdResourceState(@Resource.State @NonNull String state, @NonNull HandleStateListener listener) {
        switch (state) {
            case Resource.SUCCESS:
                listener.onSuccess();
                break;
            case Resource.ERROR:
                listener.onError();
                break;
            case Resource.LOADING:
                listener.onLoading();
                break;
            case Resource.NO_NETWORK:
                listener.onNoNetwork();
                break;
            case Resource.EMPTY:
                listener.onEmpty();
                break;
            default:
                break;
        }
    }

    public interface HandleStateListener {

        void onSuccess();

        void onError();

        void onLoading();

        void onNoNetwork();

        void onEmpty();
    }

    /**
     * listener的一个默认实现
     */
    public static class HandleStateImpl implements HandleStateListener {

        private MultipleStatusView mStatusView;

        public HandleStateImpl(@NonNull MultipleStatusView statusView) {
            mStatusView = statusView;
        }

        @Override
        public void onSuccess() {
            mStatusView.showContent();
        }

        @Override
        public void onError() {
            mStatusView.showError();
        }

        @Override
        public void onLoading() {
            mStatusView.showLoading();
        }

        @Override
        public void onNoNetwork() {
            mStatusView.showNoNetwork();
        }

        @Override
        public void onEmpty() {
            mStatusView.showEmpty();
        }
    }
}
