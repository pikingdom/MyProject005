package com.nd.hilauncherdev.plugin.navigation.http;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLHandshakeException;

import android.content.Context;
import android.os.Build;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NoHttpResponseException;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import com.nd.hilauncherdev.kitset.util.reflect.NavigationKeepForReflect;

import org.json.JSONObject;

/**
 * 网络操作类<br/>
 * 
 * @author chenzhihong_9101910
 * 
 */
public class LauncherHttpCommon {

	public static final String TAG = "LauncherHttpCommon";

	public static final String CHARSET_UTF_8 = org.apache.http.protocol.HTTP.UTF_8;

	public static final int MAX_REQUEST_RETRY_COUNTS = 1;

	public static final int CONNECTION_TIME_OUT = 10000;

	public static final int SOCKET_TIME_OUT = 10000;

	public static final String POST = "POST";

	public static final String GET = "GET";

	/** 重连时间间隔,2秒 */
	public final static int RETRY_SLEEP_TIME = 2000;
	/** 通用的Http响应头 */
	public static final String RESULT_CODE = "ResultCode";
	public static final String RESULT_MESSAGE = "ResultMessage";
	public static final String BODY_ENCRYPT_TYPE = "BodyEncryptType";
	public static final String CLIENT_CACHE = "ClientCache";
	public static final String SERVER_TIME = "ServerTime";
	public static final String REQUEST_KEY = "27B1F81F-1DD8-4F98-8D4B-6992828FB6E2";
	public static final String MT = "4";
	public static final String ProtocolVersion = utf8URLencode("1.0");

	/** 下载缓冲区 */
	public static int BUFFER_SIZE = 2048;
	public static String PID = "6";
	public static String DivideVersion;
	public static String SupPhone;
	public static String SupFirm;
	public static String IMEI;
	public static String IMSI;
	public static String CUID;

	/** URL地址 */
	private String url;

	/** 编码格式 */
	private String encoding = CHARSET_UTF_8;

	private ServerResultHeader csResult = new ServerResultHeader();

	public static String getPid(){
		try {
			return NavigationKeepForReflect.getCurrentPID()+"";
		}catch (Throwable t){
			t.printStackTrace();
		}
		return PID;
	}
	public LauncherHttpCommon(String url) {
		this.url = utf8URLencode(url);
	}

	public LauncherHttpCommon(String url, String encoding) {
		this.url = utf8URLencode(url);
		this.encoding = encoding;
	}

	private HttpRequestRetryHandler mReqRetryHandler = new HttpRequestRetryHandler() {
		@Override
		public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
			// we will try three times before getting connection
			if (executionCount >= MAX_REQUEST_RETRY_COUNTS) {
				// Do not retry if over max retry count
				return false;
			}
			if (exception instanceof NoHttpResponseException) {
				// Retry if the server dropped connection on us
				return true;
			}
			if (exception instanceof SSLHandshakeException) {
				// Do not retry on SSL handshake exception
				return false;
			}

			HttpRequest request = (HttpRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
			boolean idempotent = (request instanceof HttpEntityEnclosingRequest);
			if (!idempotent) {
				// Retry if the request is considered idempotent
				return true;
			}
			return false;

		}
	};

	private ResponseHandler<String> mResponseHandler = new ResponseHandler<String>() {
		public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {

			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				getCommonServerResult(response);
				int bodyEncryptType = csResult.getBodyEncryptType();
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					if (bodyEncryptType == 0) {// 原始内容
						String res = null;
						try {
							res = new String(EntityUtils.toByteArray(entity), encoding);
						} catch (UnsupportedEncodingException ex) {
							ex.printStackTrace();
							res = new String(EntityUtils.toByteArray(entity));
						}
						entity.consumeContent();
						return res;
					} else if (bodyEncryptType == 1) {// GZIP
						String res = "";
						InputStream is = null;
						BufferedInputStream bis = null;
						try {
							is = entity.getContent();
							bis = new BufferedInputStream(is);
							bis.mark(2); // 取前两个字节
							byte[] header = new byte[2];
							int result = bis.read(header);
							bis.reset(); // reset输入流到开始位置

							if (result != -1 && getShort(header, 0) == GZIPInputStream.GZIP_MAGIC) {
								is = new GZIPInputStream(bis);
							} else {
								is = bis;
							}
							res = parse(entity, is, encoding);
						} catch (UnsupportedEncodingException ex) {
							res = parse(entity, is, HTTP.DEFAULT_CONTENT_CHARSET);
							ex.printStackTrace();
						} catch (Exception ex) {
							ex.printStackTrace();
						} finally {
							entity.consumeContent();
							if (is != null)
								is.close();
							if (bis != null)
								bis.close();
						}
						return res;
					}
				}
			}
			return null;
		}
	};

	/**
	 * 添加接口通用参数
	 * 
	 * @param paramsMap
	 * @param ctx
	 * @param jsonParams
	 */
	public static void addGlobalRequestValue(HashMap<String, String> paramsMap, Context ctx, String jsonParams) {
		if (paramsMap == null)
			return;
		if (jsonParams == null) {
			jsonParams = "";
		}
		try {
			if (null == DivideVersion)
				DivideVersion = utf8URLencode(LauncherLibUtil.getDivideVersion(ctx));
			if (null == SupPhone)
				SupPhone = utf8URLencode(Build.MODEL);
			if (null == SupFirm)
				SupFirm = utf8URLencode(Build.VERSION.RELEASE);
			if (null == IMEI)
				IMEI = utf8URLencode(LauncherLibUtil.getIMEI(ctx));
			if (null == IMSI)
				IMSI = utf8URLencode(LauncherLibUtil.getIMSI(ctx));
			if (null == CUID)
				CUID = URLEncoder.encode(LauncherLibUtil.getCUID(ctx), "UTF-8");

			String branchName = "";
			try {
				branchName = NavigationKeepForReflect.getCustomLauncherBranchName();
				branchName = utf8URLencode(branchName);
			} catch (Throwable e) {
				e.printStackTrace();
			}
			String sessionID = "";
			paramsMap.put("PID", getPid());
			paramsMap.put("MT", MT);
			paramsMap.put("DivideVersion", DivideVersion);
			paramsMap.put("SupPhone", SupPhone);
			paramsMap.put("SupFirm", SupFirm);
			paramsMap.put("IMEI", IMEI);
			paramsMap.put("IMSI", IMSI);
			paramsMap.put("SessionId", sessionID);
			paramsMap.put("CUID", CUID);// 通用用户唯一标识 NdAnalytics.getCUID(ctx)
			paramsMap.put("ProtocolVersion", ProtocolVersion);
			paramsMap.put("BranchName", branchName);
			String Sign = LauncherLibUtil.md5Hex(getPid() + MT + DivideVersion + SupPhone + SupFirm + IMEI + IMSI + sessionID + CUID + ProtocolVersion + jsonParams + REQUEST_KEY + branchName);
			paramsMap.put("Sign", Sign);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DefaultHttpClient getDefaultHttpClient() {
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIME_OUT);
		HttpConnectionParams.setSoTimeout(params, SOCKET_TIME_OUT);
		DefaultHttpClient client = new DefaultHttpClient(params);
		client.setHttpRequestRetryHandler(mReqRetryHandler);
		return client;
	}

	public void abortConnection(final HttpRequestBase hrb, final HttpClient httpclient) {
		if (hrb != null) {
			hrb.abort();
		}
		if (httpclient != null) {
			httpclient.getConnectionManager().shutdown();
		}
	}

	public void setUrl(String url) {
		this.url = utf8URLencode(url);
	}

	/**
	 * 网易新闻阅读数据投递需要使用json格式 的header
	 * @param headerParamsMap
	 * @param jsonParams
	 * @return
	 */
	public ServerResultHeader getResponseAsCsResultPostJson(HashMap<String, String> headerParamsMap, String jsonParams) {

		csResult.setbNetworkProblem(false);
		HttpPost request = null;
		HttpClient client = null;
		String responseStr = null;
		try {
			request = new HttpPost(url);
			if (null != headerParamsMap) {
				for (String key : headerParamsMap.keySet()) {
					request.addHeader(key, headerParamsMap.get(key));
				}
			}

			if (null != jsonParams) {
				StringEntity entity = new StringEntity(jsonParams, HTTP.UTF_8);
				request.setEntity(entity);
			}
			request.addHeader("Content-Type","application/json");
			client = getDefaultHttpClient();
			responseStr = client.execute(request, mResponseHandler);
		} catch (Exception ex) {
			csResult.setbNetworkProblem(true);
			ex.printStackTrace();
		} finally {
			abortConnection(request, client);
		}
		csResult.setResponseJson(responseStr);
		return csResult;
	}


	public ServerResultHeader getResponseAsCsResultPost(HashMap<String, String> headerParamsMap, String jsonParams) {

		csResult.setbNetworkProblem(false);
		HttpPost request = null;
		HttpClient client = null;
		String responseStr = null;
		try {
			request = new HttpPost(url);
			if (null != headerParamsMap) {
				for (String key : headerParamsMap.keySet()) {
					request.addHeader(key, headerParamsMap.get(key));
				}
			}

			if (null != jsonParams) {
				StringEntity entity = new StringEntity(jsonParams, HTTP.UTF_8);
				request.setEntity(entity);
			}
			client = getDefaultHttpClient();
			responseStr = client.execute(request, mResponseHandler);
		} catch (Exception ex) {
			csResult.setbNetworkProblem(true);
			ex.printStackTrace();
		} finally {
			abortConnection(request, client);
		}
		csResult.setResponseJson(responseStr);
		return csResult;
	}


	/**
	 * get the response as string,using method GET
	 * @param paramsMap request parameter
	 * @return response string,null if request failed
	 */
	public String getResponseAsStringGET() {
		HttpGet request = null;
		HttpClient client = null;
		String responseStr = null;
		try {
			String getUrl = url;
			getUrl = utf8URLencode(getUrl);
			request = new HttpGet(getUrl);
			client = getDefaultHttpClient();
			responseStr = client.execute(request, mResponseHandler);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			abortConnection(request, client);
		}
		return responseStr;
	}


	public String getResponseAsStringPost(HashMap<String, String> headerParamsMap, String jsonParams) {

		csResult.setbNetworkProblem(false);
		HttpPost request = null;
		HttpClient client = null;
		String responseStr = null;
		try {
			request = new HttpPost(url);

			if (null != headerParamsMap) {
				for (String key : headerParamsMap.keySet()) {
					request.addHeader(key, headerParamsMap.get(key));
				}
			}
			if (null != jsonParams) {
				StringEntity entity = new StringEntity(jsonParams);
				entity.setContentEncoding(CHARSET_UTF_8);
				request.setEntity(entity);
			}
			client = getDefaultHttpClient();
			responseStr = client.execute(request, mResponseHandler);
		} catch (Exception ex) {
			csResult.setbNetworkProblem(true);
			ex.printStackTrace();
		} finally {
			abortConnection(request, client);
		}
		csResult.setResponseJson(responseStr);
		return csResult.getResponseJson();
	}

	public static String utf8URLencode(String url) {
		StringBuffer result = new StringBuffer();
		if (url != null)
			for (int i = 0; i < url.length(); i++) {
				char c = url.charAt(i);
				if ((c >= 0) && (c <= 255)) {
					result.append(c);
				} else {
					byte[] b = new byte[0];
					try {
						b = Character.toString(c).getBytes(CHARSET_UTF_8);
					} catch (Exception e) {
						e.printStackTrace();
					}
					for (int j = 0; j < b.length; j++) {
						int k = b[j];
						if (k < 0)
							k += 256;
						result.append("%" + Integer.toHexString(k).toUpperCase());
					}
				}
			}
		return result.toString();
	}

	private static String parse(final HttpEntity entity, InputStream instream, final String charset) throws IOException, ParseException {
		if (entity == null) {
			throw new IllegalArgumentException("HTTP entity may not be null");
		}

		if (instream == null) {
			return "";
		}

		if (entity.getContentLength() > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("HTTP entity too large to be buffered in memory");
		}
		int i = (int) entity.getContentLength();
		if (i < 0) {
			i = 4096;
		}

		Reader reader = new InputStreamReader(instream, charset);
		CharArrayBuffer buffer = new CharArrayBuffer(i);
		try {
			char[] tmp = new char[1024];
			int l;
			while ((l = reader.read(tmp)) != -1) {
				buffer.append(tmp, 0, l);
			}
		} finally {
			reader.close();
		}
		return buffer.toString();
	}

	private static int getShort(byte[] buffer, int off) {
		return (buffer[off] & 0xFF) | ((buffer[off + 1] & 0xFF) << 8);
	}

	/**
	 * 头部数据解析
	 * 
	 * @param response
	 */
	private void getCommonServerResult(HttpResponse response) {
		Header head = response.getFirstHeader(RESULT_CODE);
		if (head != null) {
			csResult.setResultCode(StrToInt(head.getValue()));
		}
		head = response.getFirstHeader(RESULT_MESSAGE);
		if (head != null) {
			csResult.setResultMessage(coverHeadResultMessge(head.getValue()));
		}
		head = response.getFirstHeader(BODY_ENCRYPT_TYPE);
		if (head != null) {
			csResult.setBodyEncryptType(StrToInt(head.getValue()));
		}
		head = response.getFirstHeader(CLIENT_CACHE);
		if(head != null){
			csResult.setClientCache(StrToInt(head.getValue()));
		}
		head = response.getFirstHeader(SERVER_TIME);
		if(head != null){
			csResult.setServerTime(head.getValue());
		}
	}

	private String coverHeadResultMessge(String srcMessage) {
		try {
			return URLDecoder.decode(srcMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return srcMessage;
	}

	private int StrToInt(String sStr) {
		try {
			return Integer.parseInt(sStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
}
