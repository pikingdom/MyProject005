package com.nd.hilauncherdev.plugin.navigation;

import android.content.Context;
/**
 * 桌面-零屏间的调用接口
 * <p>
 * Created by linliangbin on 2016/9/13.
 */
public interface NavigationLauncherInterface {


    /**
     * 桌面onResume时回调，主要处理零屏屏设置发生改变时的处理
     */
    public void handleNavigationWhenLauncherOnResume();


    /**
     * 桌面onPause时回调，主要处理视频播放释放
     */
    public void handleNavigationWhenLauncherOnPause();


    /**
     * 桌面通过返回键进入零屏，目前主要处理返回键时进入零屏的动作
     */
    public void handleBackKeyToNavigation();
    
    
    /**
     * 通过反射，通知桌面已经滚动在第一屏
     *
     * @param ctx 参数必须是桌面的Launcher
     */
    public void setPageIndex(Context ctx, int index);


    /**
     * 通过反射，通知桌面当前在第几屏
     * 主要用于异常情况下初始化出问题的处理
     *
     * @param ctx 参数必须是桌面的Launcher
     */
    public void setPageCount(Context ctx, int index);


    public void jumpToPage(int which);

    /**
     * 这个方法会被桌面反射调用
     */
    public void scrollToPage(int which);


}
