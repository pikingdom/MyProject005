package com.nd.hilauncherdev.plugin.navigation.widget;


/**
 * Created by linliangbin on 16-5-11.
 */
public interface PullToRefreshHeaderInterface {

    public void changeReleaserToRefreshState();

    public void changePullToRefreshState(boolean mIsBack);

    public void changeRefreshingState();

    public void changeDoneState();

    public void setAdapter();

    public void setRefreshTime();
}
