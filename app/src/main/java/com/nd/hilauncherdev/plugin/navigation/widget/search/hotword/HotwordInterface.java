package com.nd.hilauncherdev.plugin.navigation.widget.search.hotword;

import com.nd.hilauncherdev.plugin.navigation.bean.HotwordItemInfo;

import java.util.List;

/**
 * 热词工具类接口
 * Created by linliangbin on 2017/9/22 10:20.
 */

public interface HotwordInterface {

    /**
     * @desc 增加热词
     * @author linliangbin
     * @time 2017/9/22 13:46
     */
    public void appendHotwords(List<HotwordItemInfo> hotwordItemInfos);

    /**
     * @desc 获取下一个热词
     * @author linliangbin
     * @time 2017/9/22 13:46
     */
    public HotwordItemInfo popupNextHotword();

    /**
     * @desc 热词是否可用
     * @author linliangbin
     * @time 2017/9/22 13:46
     */
    public boolean isHotwordsAvailable();

    /**
     * @desc 服务端热词是否可用
     * @author linliangbin
     * @time 2017/10/12 11:11
     */
    public boolean isServerHotwordAvailable();

    /**
     * @desc 获取当前热词
     * @author linliangbin
     * @time 2017/9/22 13:46
     */
    public HotwordItemInfo getCurrentHotword();

}
