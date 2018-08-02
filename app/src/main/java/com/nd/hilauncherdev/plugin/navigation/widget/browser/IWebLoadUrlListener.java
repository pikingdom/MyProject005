package com.nd.hilauncherdev.plugin.navigation.widget.browser;

/**
 * @Description: </br>
 * @author: cxy </br>
 * @date: 2017年10月26日 20:31.</br>
 * @update: </br>
 */

public interface IWebLoadUrlListener {

    void onPageStarted();

    void onPageFinished();

    void onReceivedError(int errorCode, String description, String failingUrl);
}
