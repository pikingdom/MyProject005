package com.nd.hilauncherdev.plugin.navigation.adapter;

/**
 * description: 通用数据加载回调接口<br/>
 * author: chenxuyu_dian91<br/>
 * date: 2016/3/1<br/>
 */
public interface ILoadDataCallBack<T> {
    void onFinish();

    void onSuccess(T data, boolean isLastPage);

    void onFailure();
}
