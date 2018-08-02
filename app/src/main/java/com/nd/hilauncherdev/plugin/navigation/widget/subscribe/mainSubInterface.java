package com.nd.hilauncherdev.plugin.navigation.widget.subscribe;

/**
 * Created by linliangbin on 16-7-12.
 */
public interface mainSubInterface {
    /**
     * 根据主listview 选中的状态更新从listview 的内容
     * @param siteArrayString
     */
    public void scrollSubList(String siteArrayString,boolean isLocalCate);
    
    /**
     * 从listview 触发刷新主listview 内容
     */
    public void updateMainList();
}