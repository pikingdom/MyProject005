package com.nd.hilauncherdev.plugin.navigation.http;

/**
 * 下载状态<br>
 * Author: chenzhihong_9101910 Date:2014-12-16
 */
public class DownloadState {
	/**
	 * 正在下载
	 */
	public static final int STATE_DOWNLOADING = 0;

	/**
	 * 暂停
	 */
	public static final int STATE_PAUSE = 1;

	/**
	 * 取消
	 */
	public static final int STATE_CANCLE = 2;

	/**
	 * 完成,但未安装
	 */
	public static final int STATE_FINISHED = 3;

	/**
	 * 等待
	 */
	public static final int STATE_WAITING = 4;

	/**
	 * 已安装
	 */
	public static final int STATE_INSTALLED = 5;

	/**
	 * 无,从未进行过下载
	 */
	public static final int STATE_NONE = 6;

	/**
	 * 下载失败
	 */
	public static final int STATE_FAILED = 7;

}
