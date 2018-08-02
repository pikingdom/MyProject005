package com.nd.hilauncherdev.plugin.navigation.bean;

import android.text.TextUtils;
import android.util.Log;

import com.nd.hilauncherdev.plugin.navigation.util.DateUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * 新闻内容的数据结构
 *
 * */
public class News {
	public final static  int TYPE_AD=3;
	public final static  int TYPE_BANNER_AD=4;
	public final static  int TYPE_S_NEWS=1;
	public final static  int TYPE_L_NEWS=2;
	public String name = "";
	//类型，1小图新图，2大图新闻，3，小图广告
	public int type;
	public String title;
	public String desc;
	public String imageUrl;
	public String linkUrl;
	public String publicDate;
	public String publicAuthor;

	//2017.09.05增加UC头新相关字段
	//UC广告展示上报url
	public String showImpressionUrl;
	//UC内容类型
	public int ucItemType = -1;
	//UC广告卡片风格类型
	public int ucStyleType = 0;
	//UC频道ID
	public long ucChannelId = -1;
	//UC文章ID
	public String ucAid;
	//UC推荐批次
	public String ucRecoId;

	public ArrayList<String> chapters = new ArrayList<>();
	public News()
	{

	}
	public News(JSONObject jo) {
		title=jo.optString("Title");
		linkUrl=jo.optString("Link");
		imageUrl =jo.optString("Image");
		publicDate=jo.optString("Date");
		if(!TextUtils.isEmpty(publicDate))
			publicDate = DateUtil.parseDateTimeString(publicDate);
		publicAuthor=jo.optString("Author");
		type=jo.optInt("Type");
		desc=jo.optString("Abstract");
		desc.trim();
		desc="    "+desc;
		//UC头条API相关属性
		showImpressionUrl = jo.optString("ShowImpressionUrl");
		ucItemType = jo.optInt("ItemType", -1);
		ucStyleType = jo.optInt("StyleType", 0);
		ucChannelId = jo.optLong("ChannelId", -1);
		ucAid = jo.optString("ItemId");
	}

	public static class NewsList {
		public ArrayList<News> mList;
		public long mNextNonce = -1;
		public int mNextPindx = -1;
		public boolean isLast=false;

		//UC推荐批次
		public String ucRecoId;
		//UC新闻发生时间
		public long ucftime;
	}

	public JSONObject toJson() {
		JSONObject jo = new JSONObject();
		try {
			jo.put("Title",title);
			jo.put("Link",linkUrl);
			jo.put("Image",imageUrl);
			jo.put("Date",publicDate);
			jo.put("Author",publicAuthor);
			jo.put("Type",type);
			jo.put("Abstract",desc);
			jo.put("ShowImpressionUrl", showImpressionUrl);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jo;
	}


	public static NewsList getNews(String url1) {
		/*
		//   String url = "http://iphone.myzaker.com/zaker/apps_telecom.php?for=test";
		//url = "http://iphone.myzaker.com/zaker/article_telecom.php?app_id=2&for=test";
		// url += "?for=test";

		String url="http://iphone.myzaker.com/zaker/article_telecom.php?app_id=660&for=test";
		HttpResponse httpResponse = null;
		HttpGet httpGet = new HttpGet(url);
		//InputStream inputStream = null;
		// 实体
		HttpEntity mHttpEntity = null;

		try {
			httpResponse = new DefaultHttpClient().execute(httpGet);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = httpResponse.getEntity();
				if (entity != null) {


					InputStream inputStream1 = entity.getContent();
					InputStream inputStream = new GZIPInputStream(new BufferedInputStream(inputStream1));

					StringBuilder builder = new StringBuilder();
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
					for (String s = bufferedReader.readLine(); s != null; s = bufferedReader.readLine()) {
						builder.append(s);
					}
					String result = builder.toString();
					String w = decodeUnicode(result);
					Log.d("AD", "Server return " + result);
					return parseNews(w);

				}


			}
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		return null;
	}


	public static NewsList  parseNews(String str) {
		ArrayList<News> newsList = new ArrayList<News>();
		Log.e("zhou","str"+str.substring(1590,1595));
		try {
			JSONObject jsonData = new JSONObject(str);
			JSONObject data = jsonData.getJSONObject("data");
			JSONArray list = data.getJSONArray("list");
			for (int i = 0; i < list.length(); i++) {
				JSONObject object = (JSONObject) list.get(i);
				String image = object.getString("thumbnail_pic");
				String title = object.getString("title");
				String pk = object.getString("pk");
				String url = object.getString("url");
				News info = new News();
				info.type=2;
				info.imageUrl = image;
				info.title=title;
				newsList.add(info);

			}


		} catch (Exception e) {
			e.printStackTrace();
		}
		NewsList list=new NewsList();
		list.mList=newsList;
		list.mNextNonce=-1;
		list.mNextPindx=-1;
		return list;

	}

	/**
	 * 是否是UC新闻源
	 * @return
     */
	public boolean isUCNews() {
		if (!TextUtils.isEmpty(ucAid) && !TextUtils.isEmpty(ucRecoId)) {
			return true;
		}
		return false;
	}

}
