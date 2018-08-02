package com.nd.hilauncherdev.plugin.navigation.widget.navigation;

/**
 * 零屏界面回调接口
 * Created by linliangbin on 2017/8/31 17:10.
 */

public interface PageActionInterface {

    /**
     * @desc 进入界面
     * @author linliangbin
     * @time 2017/8/31 17:12
     */
    public void onEnterPage();

    /**
     * @desc 离开界面
     * @author linliangbin
     * @time 2017/8/31 17:12
     */
    public void onLeavePage();

    /**
     * @desc 重新回到界面
     * @author linliangbin
     * @time 2017/8/31 17:12
     */
    public void onPageResume();

    /**
     * @desc 返回键回到界面
     * @author linliangbin
     * @time 2017/8/31 17:12
     */
    public void onBackToPage();


    /**
     * @desc 非阻塞方式刷新
     * @author linliangbin
     * @time 2017/9/22 14:26
     */
    public void onActionRefresh();

    /**
     * @desc 阻塞方式的刷新
     * 该方法会在线程中被调用
     * @author linliangbin
     * @time 2017/9/22 17:20
     */
    public void onActionRefreshSync();

    /**
     * @desc 每日任务中后台更新
     * @author linliangbin
     * @time 2017/9/22 18:07
     */
    public void doDailyUpdate();
}
