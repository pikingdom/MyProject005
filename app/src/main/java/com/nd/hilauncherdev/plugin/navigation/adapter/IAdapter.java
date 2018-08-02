package com.nd.hilauncherdev.plugin.navigation.adapter;

/**
 * 通用适配器接口
 * Created by chenxuyu_dian91 on 2016/2/23.
 */
public interface IAdapter {

    interface CallBack {
        /**
         * 完成
         */
        void onFinish();

        /**
         * 出错
         *
         * @param isFirstLoad 是否是第一次加载
         */
        void onError(boolean isFirstLoad);

        /**
         * 成功
         *
         * @param isNoMore        是否没有更多数据了
         * @param refreshDataSize 本次更新出来的数据集长度
         */
        void onSuccess(boolean isNoMore, int refreshDataSize);
    }

    /**
     * 刷新
     */
    void refresh(CallBack callBack);

    /**
     * 加载更多
     */
    void loadMore(CallBack callBack);

    /**
     * 还原
     */
    void restore();

    /**
     * 销毁
     */
    void onDestroy();

    /**
     * onResume
     */
    void onResume();

    /**
     * onPause
     */
    void onPause();
}
