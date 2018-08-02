package com.nd.hilauncherdev.plugin.navigation.http;

import java.io.Serializable;

/**
 * 响应结果<br/>
 * 
 * @author chenzhihong_9101910
 * 
 */
public class ServerResultHeader implements Serializable {

	private static final long serialVersionUID = 5271302355916459053L;

	/** 无法访问到服务端的数据 */
	private boolean bNetworkProblem = false;

	/** 服务端的结果状态码 */
	private int resultCode = -1;

	/** 服务端的结果描述信息 */
	private String resultMessage;

	/** 接口响应内容的处理方式 0-原始内容 1- Gzip压缩 */
	private int bodyEncryptType = 0;

	/** 返回内容 */
	private String responseJson;

	/** 客户端数据缓存时间 单位：分钟 */
	private int clientCache = 0;

	/** 服务器时间 */
	private String serverTime;

	/**
	 * 请求是否成功
	 * 
	 * @return
	 */
	public boolean isRequestOK() {
		return !bNetworkProblem && resultCode == ResultCodeMap.SERVER_RESPONSE_CODE_SUCCESS;
	}

	public boolean isbNetworkProblem() {
		return bNetworkProblem;
	}

	public void setbNetworkProblem(boolean bNetworkProblem) {
		this.bNetworkProblem = bNetworkProblem;
	}

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultMessage() {
		return resultMessage;
	}

	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}

	public int getBodyEncryptType() {
		return bodyEncryptType;
	}

	public void setBodyEncryptType(int bodyEncryptType) {
		this.bodyEncryptType = bodyEncryptType;
	}

	public String getResponseJson() {
		return responseJson;
	}

	public void setResponseJson(String responseJson) {
		this.responseJson = responseJson;
	}

	public int getClientCache() {
		return clientCache;
	}

	public void setClientCache(int clientCache) {
		this.clientCache = clientCache;
	}

	public String getServerTime() {
		return serverTime;
	}

	public void setServerTime(String serverTime) {
		this.serverTime = serverTime;
	}

	@Override
	public String toString() {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("resultCode=" + resultCode + ";").append("resultMessage=" + resultMessage + ";");
		strBuf.append("responseJson=" + responseJson + ";");
		return strBuf.toString();
	}
}
