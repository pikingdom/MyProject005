package com.nd.hilauncherdev.plugin.navigation.loader;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.nd.hilauncherdev.plugin.navigation.Card;
import com.nd.hilauncherdev.plugin.navigation.CardManager;
import com.nd.hilauncherdev.plugin.navigation.CardViewHelper;
import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;
import com.nd.hilauncherdev.plugin.navigation.bean.FunnyBean;
import com.nd.hilauncherdev.plugin.navigation.http.LauncherHttpCommon;
import com.nd.hilauncherdev.plugin.navigation.http.ServerResultHeader;
import com.nd.hilauncherdev.plugin.navigation.net.UrlConstant;
import com.nd.hilauncherdev.plugin.navigation.util.FileUtil;
import com.nd.hilauncherdev.plugin.navigation.util.URLs;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

public class FunnyLoader extends CardDataLoader {
	private static final String NAVI_FILE_BIG = "navi_card_funny_big.txt";
	private static final String NAVI_FILE_SMALL = "navi_card_funny_small.txt";

	private static final String NAVI_CARD_PATH_BIG = NaviCardLoader.NAV_DIR + NAVI_FILE_BIG;
	private static final String NAVI_CARD_PATH_SMALL = NaviCardLoader.NAV_DIR + NAVI_FILE_SMALL;

	// 大图
	// 在当前页数据中的下标
	private static final String FUNNY_CARD_CUR_INDEX_BIG = "funny_card_cur_index_big";
	// 页码
    private static final String FUNNY_PAGE_INDEX_BIG = "funny_page_index_big";

	// 小图
	// 在当前页数据中的下标
	private static final String FUNNY_CARD_CUR_INDEX_SMALL = "funny_card_cur_index_small";
	// 页码
	private static final String FUNNY_PAGE_INDEX_SMALL = "funny_page_index_small";


	public int curIndex = 0;
    public static final int MAX_PAGE =50;
	public static final int PAGE_COUNT = 10;
	public static final int CACHE_START_LIMIT = PAGE_COUNT/2;

	//发下数据的数量最小单位
	//如果个数小，则不显示，避免换一批后无内容更新的问题

	public static final int MIN_ITEM_SMALL = 4;
	public static final int MIN_ITEM_BIG = 2;

	//当前大图列表
	private ArrayList<FunnyBean> curBigImgList = new ArrayList<FunnyBean>();
	//当前大图Index
	private int currentBigIndex = -1;
	//每次展示最少的项目
	public static final int BIG_IMG_COUNT_LEAST = 1;

	//当前小图列表
	private ArrayList<FunnyBean> curSmallImgList = new ArrayList<FunnyBean>();
	private int currentSmallIndex = -1;
	public static final int SMALL_IMG_COUNT_LEAST = 2;

	//缓存显示过半后只取一次数据，避免重复获取
	private boolean isBlockingRequestBig = false;
	private boolean isBlockingRequestSmall = false;

	/**
	 * 请求数据时的类型
	 */
	//只请求大图数据
	//请求类型
	public static final int REQUEST_TYPE_LARGE_ONLY = 1;
	//请求结果TAG
	public static final String LARGE_TAG = "TopicList";
	//请求的页大小
	public static final int LARGE_PAGE_SIZE = 4;
	//请求参数页码
	public static final String LARGE_PAGE_PARAM_INDEX = "PageIndex";
	//请求参数页大小
	public static final String LARGE_PAGE_PARAM_SIZE = "PageSize";


	//只请求小图数据
	public static final int REQUEST_TYPE_SMALL_ONLY = 2;
	public static final String SMALL_TAG = "SmallTopicList";
	public static final int SMALL_PAGE_SIZE = 8;
	public static final String SMALL_PAGE_PARAM_INDEX = "SmallPageIndex";
	public static final String SMALL_PAGE_PARAM_SIZE = "SmallPageSize";


	//请求大小图混合数据
	public static final int REQUEST_TYPE_MIX = 4;
	public static final int MIX_PAGE_SIZE = 10;
	public static final String MIX_PAGE_PARAM_INDEX = "MixPageIndex";
	public static final String MIX_PAGE_PARAM_SIZE = "MixPageSize";

	private HashMap<String,ArrayList<FunnyBean>> preBeanMap = new HashMap<String,ArrayList<FunnyBean>>();
	
	/**
	 *  当前是否在使用默认数据展示
	    若使用默认数据展示，则重新获取数据
	 */
	private boolean isUsingDefaultData = false;

	@Override
	public String getCardShareImagePath() {
		return NaviCardLoader.NAV_DIR + "navigation_funny_share.jpg";
	}

	@Override
	public String getCardShareImageUrl() {
		if(CommonLauncherControl.DX_PKG){
			return UrlConstant.HTTP_PIC_IFJING_COM+"rbpiczy/pic/2016/09/19/29a44d39586f4d98b6c69c71c203c631.jpg";
		}
		if(CommonLauncherControl.AZ_PKG){
			return UrlConstant.HTTP_PIC_IFJING_COM+"rbpiczy/pic/2016/09/19/ec24791cf9484bdb9c54602e510f9fc9.jpg";
		}
		return UrlConstant.HTTP_PIC_IFJING_COM+"rbpiczy/pic/2016/06/19/899c0eb87e354ee38d4cfc04b6522083.jpg";
	}

	@Override
	public String getCardShareWord() {
		return "所有热爱生活的人，都能在这儿发现不同的乐趣  @" + CommonLauncherControl.LAUNCHER_NAME + " " ;
	}

	@Override
	public String getCardShareAnatics() {
		return "qfx";
	}

	@Override
	public String getCardDetailImageUrl() {
		return UrlConstant.HTTP_PIC_IFJING_COM+"rbpiczy/pic/2016/06/20/9665656912384e1cb22e195ae01ffd0f.png";
	}

	@Override
	public void loadDataS(Context context, final Card card, boolean needRefreshData, int showSize) {
		mContext = context;
		//当前是否已经更新界面数据
		boolean hasRefreshView= false;

		//不需要刷新数据（如置顶卡片），直接使用之前缓存的数据
		if(!needRefreshData && preBeanMap != null &&
				preBeanMap.get("big") != null && preBeanMap.get("big").size() >= BIG_IMG_COUNT_LEAST &&
				preBeanMap.get("small") != null && preBeanMap.get("small").size() >= SMALL_IMG_COUNT_LEAST){
			refresh(preBeanMap);
			return;
		}

		// 当前获取的要显示的数组
		ArrayList<FunnyBean> tempBigImgList = new ArrayList<FunnyBean>();
		ArrayList<FunnyBean> tempSmallImgList = new ArrayList<FunnyBean>();

		if(curBigImgList.size() <= 0){
			curBigImgList.clear();
			try {
				NaviCardLoader.createBaseDir();
				//从本地缓存获取数据
				curBigImgList = loadFunnyBeansCommon(context, NAVI_FILE_BIG, NAVI_CARD_PATH_BIG, LARGE_TAG);
			}catch (Exception e){
				e.printStackTrace();
			}
		}

		if(curSmallImgList.size() <= 0){
			curSmallImgList.clear();
			try {
				NaviCardLoader.createBaseDir();
				//从本地缓存获取数据
				curSmallImgList = loadFunnyBeansCommon(context,NAVI_FILE_SMALL,NAVI_CARD_PATH_SMALL,SMALL_TAG);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if(currentBigIndex == -1){
			currentBigIndex = getFunnyCardCurIndexBig(context);
		}
		if(currentSmallIndex == -1){
			currentSmallIndex = getFunnyCardCurIndexSmall(context);
		}
		
		assembleData(mContext,tempBigImgList,tempSmallImgList);

		//使用当前内存缓存数据刷新界面
		if(tempBigImgList.size() >= BIG_IMG_COUNT_LEAST && tempSmallImgList.size() >= SMALL_IMG_COUNT_LEAST){
			refresh(tempBigImgList,tempSmallImgList);
		}

		if(isUsingDefaultData){
			isUsingDefaultData = false;
			loadDataFromServer(context,card,true);	
		}else {
			//判断是否需要重新获取数据
			boolean needRefreshLarge = false, needRefreshSmall = false;
			if (!isBlockingRequestBig && needLoadBigData()) {
				needRefreshLarge = true;
				isBlockingRequestBig = true;
			}
			if (!isBlockingRequestSmall && needLoadBigData()) {
				needRefreshSmall = true;
				isBlockingRequestSmall = true;
			}
			loadDataFromServer(context, needRefreshLarge, needRefreshSmall);
		}
	}

	/**
	 * 同步本地文件缓存到内存中
	 */
	private void refreshMemoryCacheForLarge(Context context){
		curBigImgList.clear();
		FunnyBean funnyBean = new FunnyBean();
		synchronized (FunnyBean.class){

		}
		funnyBean.initDataFromJson(new JSONObject());
		try {
			NaviCardLoader.createBaseDir();
			//从本地缓存获取数据
			curBigImgList = loadFunnyBeansCommon(context,NAVI_FILE_BIG,NAVI_CARD_PATH_BIG,LARGE_TAG);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 同步本地文件缓存到内存中
	 */
	private void refreshMemoryCacheForSmall(Context context){
		curSmallImgList.clear();
		try {
			NaviCardLoader.createBaseDir();
			//从本地缓存获取数据
			curSmallImgList = loadFunnyBeansCommon(context,NAVI_FILE_SMALL,NAVI_CARD_PATH_SMALL,SMALL_TAG);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据目前的数据组装显示数据
	 * @param tempBigImgList
	 * @param tempSmallImgList
	 * @return
	 */
	private void assembleData(Context context,ArrayList<FunnyBean> tempBigImgList,ArrayList<FunnyBean> tempSmallImgList){
		try {
			HashMap<String,ArrayList<FunnyBean>> map = new HashMap<String,ArrayList<FunnyBean>>();
			boolean[] result = new boolean[]{false,false};
			
			if(currentBigIndex < 0 || currentBigIndex >= curBigImgList.size()) {
				currentBigIndex = 0;
			}
			if(curBigImgList.size() >= 1 && tempBigImgList.size() == 0){
				tempBigImgList.add(curBigImgList.get(currentBigIndex));
				currentBigIndex ++;
			}
			
			if(currentBigIndex >= curBigImgList.size()){
				refreshMemoryCacheForLarge(context);
				result[0] = true;
				isBlockingRequestBig = false;
				currentBigIndex = 0;
				
			}
			setFunnyCardCurIndexBig(context,currentBigIndex);
			
			if(currentSmallIndex < 0 || currentSmallIndex >= curSmallImgList.size()){
				currentSmallIndex = 0;
			}
			for(int count = 0;tempSmallImgList.size() != SMALL_IMG_COUNT_LEAST && count < SMALL_IMG_COUNT_LEAST;count++){
				if(curSmallImgList.size() > 0){
					tempSmallImgList.add(curSmallImgList.get(currentSmallIndex % curSmallImgList.size()));
				}else{
					break;
				}
				currentSmallIndex ++;
				if(currentSmallIndex >= curSmallImgList.size()){
					refreshMemoryCacheForSmall(context);
					result[1] = true;
					isBlockingRequestSmall = false;
					currentSmallIndex = 0;
				}
			}
			setFunnyCardCurIndexSmall(context,currentSmallIndex);
		}catch (Exception e){
			e.printStackTrace();
		}
		
	}

	/**
	 * 从缓存加载数据
	 * @param context
	 * @param assetName
	 * @param cachePath
	 * @param tag
	 * @return
	 */
	private ArrayList<FunnyBean> loadFunnyBeansCommon(Context context,String assetName,String cachePath,String tag){
		ArrayList<FunnyBean> beans = new ArrayList<FunnyBean>();
		String localStr = FileUtil.readFileContent(cachePath);

		try {
			if (TextUtils.isEmpty(localStr)) {
				if(FileUtil.copyPluginAssetsFile(context, assetName, NaviCardLoader.NAV_DIR, assetName)){
					localStr = FileUtil.readFileContent(cachePath);
				}
				if(TextUtils.isEmpty(localStr))
					localStr = FileUtil.readFromAssetsFile(context,assetName);
				//标记当前是否在展示默认数据
				isUsingDefaultData = true;
			}
		}catch (Exception e){
			e.printStackTrace();
		}


		try {
			JSONObject jobig = new JSONObject(localStr);
			JSONArray jabig = jobig.getJSONArray(tag);
			if (jabig.length() < 0) {
				if(FileUtil.copyPluginAssetsFile(context, assetName, NaviCardLoader.NAV_DIR, assetName)){
					localStr = FileUtil.readFileContent(cachePath);
				}
				if(TextUtils.isEmpty(localStr))
					localStr = FileUtil.readFromAssetsFile(context,assetName);
				
				//标记当前是否在展示默认数据
				isUsingDefaultData = true;
				jobig = new JSONObject(localStr);
				jabig = jobig.getJSONArray(tag);
			}

			for(int i = 0; i < jabig.length(); i ++){
				JSONObject jsonObject = (JSONObject) jabig.get(i);
				FunnyBean funnyBean = new FunnyBean();
				funnyBean.initDataFromJson(jsonObject);
				beans.add(funnyBean);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return beans;
	}


	/**
	 * 判断当前是否需要更新缓存数据
	 * 目前缓存显示过半时获取新缓存
	 * @return
	 */
	private boolean needLoadBigData(){

		if(curBigImgList == null)
			return true;
		if(curBigImgList.size() == 0)
			return true;
		if((float)currentBigIndex / curBigImgList.size() >= 0.5f){
			return true;
		}
		return false;
	}

	private boolean needLoadSmallData(){

		if(curSmallImgList == null)
			return true;
		if(curSmallImgList.size() == 0)
			return true;

		if((float)currentSmallIndex / curSmallImgList.size() >= 0.5f){
			return true;
		}
		return false;
	}


	private void loadDataFromServer(Context context,boolean requestLarge,boolean requestSmall){

		ArrayList<RequestBean> requestBeans = new ArrayList<RequestBean>();
		int indexBig = getPageIndexBig(context);
		int indexSmall = getPageIndexSmall(context);
		if(indexBig <= 0 || indexBig > MAX_PAGE){
			indexBig = 1;
		}
		if(indexSmall <= 0 || indexSmall > MAX_PAGE){
			indexSmall = 1;
		}

		RequestBean largeRequestBean = new RequestBean();
		largeRequestBean.type = REQUEST_TYPE_LARGE_ONLY;
		largeRequestBean.tag = LARGE_TAG;
		largeRequestBean.minSize = MIN_ITEM_BIG;
		largeRequestBean.path = NAVI_CARD_PATH_BIG;
		largeRequestBean.indexSp = FUNNY_PAGE_INDEX_BIG;
		largeRequestBean.requestIndex = indexBig;

		RequestBean smallRequestBean = new RequestBean();
		smallRequestBean.type = REQUEST_TYPE_SMALL_ONLY;
		smallRequestBean.tag = SMALL_TAG;
		smallRequestBean.minSize = MIN_ITEM_SMALL;
		smallRequestBean.path = NAVI_CARD_PATH_SMALL;
		smallRequestBean.indexSp = FUNNY_PAGE_INDEX_SMALL;
		smallRequestBean.requestIndex = indexSmall;

		//同时请求大小图数据
		if(requestLarge && requestSmall){
			requestBeans.add(largeRequestBean);
			requestBeans.add(smallRequestBean);
			loadDataFromServerCommon(context,requestBeans);
			return;
		}
		//只请求大图数据
		if (requestLarge) {
			requestBeans.add(largeRequestBean);
			loadDataFromServerCommon(context,requestBeans);
			return;
		}

		//只请求小图数据
		if (requestSmall) {
			requestBeans.add(smallRequestBean);
			loadDataFromServerCommon(context,requestBeans);
			return;
		}
	}


	class RequestBean{
		//请求的类型
		int type=0;
		//请求数据使用的TAG
		String tag = "";
		//请求数据的最小长度
		int minSize = 1;
		//缓存文件路径
		String path = "";
		//保存页码的SP
		String indexSp = "";
		//请求的页码
		int requestIndex = 1;
	}

	private void loadDataFromServerCommon(Context context,ArrayList<RequestBean> requestBeans){

		int requestType = 0;
		int indexBig=0,indexSmall=0,indexMix=0;

		for(int i=0;i<requestBeans.size();i++){
			RequestBean requestBean = requestBeans.get(i);
			requestType += requestBean.type;
			switch (requestBean.type){
				case REQUEST_TYPE_LARGE_ONLY:
					indexBig = requestBean.requestIndex;
					break;
				case REQUEST_TYPE_SMALL_ONLY:
					indexSmall = requestBean.requestIndex;
					break;
				case REQUEST_TYPE_MIX:
					indexMix = requestBean.requestIndex;
					break;

			}
		}

		try {
			String resString = requestData(context, indexBig,indexSmall,indexMix, requestType);
			if (!TextUtils.isEmpty(resString)) {
				JSONObject resJo = new JSONObject(resString);
				for(int i =0;i<requestBeans.size();i++){
					RequestBean requestBean = requestBeans.get(i);
					JSONArray bigJa = resJo.getJSONArray(requestBean.tag);
					if (bigJa != null && bigJa.length() >= requestBean.minSize) {
						JSONObject jsonObject = new JSONObject();
						jsonObject.put(requestBean.tag, bigJa);
						FileUtil.writeFile(requestBean.path, jsonObject.toString(), false);
						setPageIndex(context, requestBean.indexSp, ++requestBean.requestIndex);
					} else {
						setPageIndex(context, requestBean.indexSp, 1);
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	@Override
	public void loadDataFromServer(final Context context, final Card card, final boolean needRefreshView) {
		try {
			loadDataFromServer(context, true, true);
			if (needRefreshView) {
				isBlockingRequestBig = false;
				isBlockingRequestSmall = false;
				curBigImgList.clear();
				currentBigIndex = 0;
				setFunnyCardCurIndexBig(context,0);
				curSmallImgList.clear();
				currentSmallIndex = 0;
				setFunnyCardCurIndexSmall(context,0);
				/**
				 *
				 SD卡不可读写的情况下,出现循环调用问题
				 */
				
				curBigImgList.clear();
				
				try {
					NaviCardLoader.createBaseDir();
					//从本地缓存获取数据
					curBigImgList = loadFunnyBeansCommon(context, NAVI_FILE_BIG, NAVI_CARD_PATH_BIG, LARGE_TAG);
				}catch (Exception e){
					e.printStackTrace();
				}
				
				curSmallImgList.clear();
				try {
					NaviCardLoader.createBaseDir();
					//从本地缓存获取数据
					curSmallImgList = loadFunnyBeansCommon(context,NAVI_FILE_SMALL,NAVI_CARD_PATH_SMALL,SMALL_TAG);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				// 当前获取的要显示的数组
				ArrayList<FunnyBean> tempBigImgList = new ArrayList<FunnyBean>();
				ArrayList<FunnyBean> tempSmallImgList = new ArrayList<FunnyBean>();
				
				assembleData(mContext,tempBigImgList,tempSmallImgList);
				
				//使用当前内存缓存数据刷新界面
				if(tempBigImgList.size() >= BIG_IMG_COUNT_LEAST && tempSmallImgList.size() >= SMALL_IMG_COUNT_LEAST){
					refresh(tempBigImgList,tempSmallImgList);
				}
				
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		
	}

	/**
	 * 通知界面更新
	 * @param big
	 * @param small
	 */
	private void refresh(ArrayList<FunnyBean> big,ArrayList<FunnyBean> small){
		HashMap<String,ArrayList<FunnyBean>> map = new HashMap<String,ArrayList<FunnyBean>>();
		map.put("big", big);
		map.put("small", small);
		refresh(map);
	}

	private void refresh(HashMap<String,ArrayList<FunnyBean>> map ){
		if(handler == null){
			return;
		}
		Message msg = new Message();
		msg.what = CardViewHelper.MSG_LOAD_FUNNY_SUC;
		msg.obj = map;
		handler.sendMessage(msg);
		preBeanMap = map;
	}

	@Override
	public <E> void refreshView(ArrayList<E> objList) {

	}


	public void setFunnyCardCurIndexBig(Context context, int value) {
		SharedPreferences sp = context.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
		sp.edit().putInt(FUNNY_CARD_CUR_INDEX_BIG, value).commit();
	}

	public int getFunnyCardCurIndexBig(Context context) {
		SharedPreferences sp = context.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
		return sp.getInt(FUNNY_CARD_CUR_INDEX_BIG, 0);
	}


    public void setPageIndexBig(Context context, int value) {
        SharedPreferences sp = context.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putInt(FUNNY_PAGE_INDEX_BIG, value).commit();
    }

    public int getPageIndexBig(Context context) {
        SharedPreferences sp = context.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
        return sp.getInt(FUNNY_PAGE_INDEX_BIG, 1);
    }

	public void setFunnyCardCurIndexSmall(Context context, int value) {
		SharedPreferences sp = context.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
		sp.edit().putInt(FUNNY_CARD_CUR_INDEX_SMALL, value).commit();
	}

	public int getFunnyCardCurIndexSmall(Context context) {
		SharedPreferences sp = context.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
		return sp.getInt(FUNNY_CARD_CUR_INDEX_SMALL, 0);
	}


	public void setPageIndexSmall(Context context, int value) {
		SharedPreferences sp = context.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
		sp.edit().putInt(FUNNY_PAGE_INDEX_SMALL, value).commit();
	}

	public int getPageIndexSmall(Context context) {
		SharedPreferences sp = context.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
		return sp.getInt(FUNNY_PAGE_INDEX_SMALL, 1);
	}

	public void setPageIndex(Context context, String spName ,int value){
		SharedPreferences sp = context.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
		sp.edit().putInt(spName, value).commit();
	}


	/**
	 * 根据给定的参数请求接口数据
	 * @param mContext
	 * @param largeIndex 大图页码
	 * @param smallIndex 小图页码
	 * @param mixIndex 不区分大小图的页码
	 * @param type 请求的类型
	 * @return
	 */
	public static final String requestData(Context mContext,int largeIndex,int smallIndex,int mixIndex,int type){

		try {
			JSONObject paramsJO = new JSONObject();
			switch (type){
				case REQUEST_TYPE_SMALL_ONLY+REQUEST_TYPE_LARGE_ONLY:
					paramsJO.put(LARGE_PAGE_PARAM_INDEX, largeIndex);
					paramsJO.put(LARGE_PAGE_PARAM_SIZE, LARGE_PAGE_SIZE);
					paramsJO.put(SMALL_PAGE_PARAM_INDEX, smallIndex);
					paramsJO.put(SMALL_PAGE_PARAM_SIZE, SMALL_PAGE_SIZE);
					break;
				case REQUEST_TYPE_SMALL_ONLY:
					paramsJO.put(SMALL_PAGE_PARAM_INDEX, smallIndex);
					paramsJO.put(SMALL_PAGE_PARAM_SIZE, SMALL_PAGE_SIZE);
					break;
				case REQUEST_TYPE_LARGE_ONLY:
					paramsJO.put(LARGE_PAGE_PARAM_INDEX, largeIndex);
					paramsJO.put(LARGE_PAGE_PARAM_SIZE, LARGE_PAGE_SIZE);
					break;
				case REQUEST_TYPE_MIX:
					paramsJO.put(MIX_PAGE_PARAM_INDEX, mixIndex);
					paramsJO.put(MIX_PAGE_PARAM_SIZE, MIX_PAGE_SIZE);
			}

			paramsJO.put("Type",type);

			String jsonParams = paramsJO.toString();
			HashMap<String, String> paramsMap = new HashMap<String, String>();
			LauncherHttpCommon.addGlobalRequestValue(paramsMap, mContext, jsonParams);
			LauncherHttpCommon httpCommon = new LauncherHttpCommon(URLs.PANDAHOME_BASE_URL + "action.ashx/themeaction/4062");
			ServerResultHeader csResult = httpCommon.getResponseAsCsResultPost(paramsMap, jsonParams);
			if (csResult == null || !csResult.isRequestOK()) {
				return "";
			}

			String resString = csResult.getResponseJson();
			return resString;

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}

}
