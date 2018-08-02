package com.nd.hilauncherdev.plugin.navigation.loader;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.nd.hilauncherdev.plugin.navigation.Card;
import com.nd.hilauncherdev.plugin.navigation.CardViewHelper;
import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;
import com.nd.hilauncherdev.plugin.navigation.activity.BookDetailAct;
import com.nd.hilauncherdev.plugin.navigation.activity.BookSearchAct;
import com.nd.hilauncherdev.plugin.navigation.bean.Book;
import com.nd.hilauncherdev.plugin.navigation.http.LauncherHttpCommon;
import com.nd.hilauncherdev.plugin.navigation.http.LauncherLibUtil;
import com.nd.hilauncherdev.plugin.navigation.http.ServerResultHeader;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysisConstant;
import com.nd.hilauncherdev.plugin.navigation.util.FileUtil;
import com.nd.hilauncherdev.plugin.navigation.util.IntegralTaskIdContent;
import com.nd.hilauncherdev.plugin.navigation.util.LauncherCaller;
import com.nd.hilauncherdev.plugin.navigation.util.TelephoneUtil;
import com.nd.hilauncherdev.plugin.navigation.util.ThreadUtil;
import com.nd.hilauncherdev.plugin.navigation.util.URLs;
import com.nd.hilauncherdev.plugin.navigation.util.UrlUtilC;

public class BookLoader extends CardDataLoader{
	private static final String NAVI_BOOK_FILE = "navi_card_book.txt";
	private static final String NAVI_CARD_BOOK_PATH = NaviCardLoader.NAV_DIR + NAVI_BOOK_FILE;

	public static final int PAGE_BOOK_SIZE = 18;
	public int curPage = 1;
	public int bookNum = -1;

	@Override
	public String getCardShareImagePath() {
		return NaviCardLoader.NAV_DIR + "navigation_book_share.jpg";
	}

	@Override
	public String getCardShareImageUrl() {
		if(CommonLauncherControl.DX_PKG){
			return "http://pic.ifjing.com/rbpiczy/pic/2016/04/25/dd61dd153efc477aaa2f21723b04f074.jpg";
		}
		if(CommonLauncherControl.AZ_PKG){
			return "http://pic.ifjing.com/rbpiczy/pic/2016/05/19/524ac3f8b91c41c58c02e3c48738580e.png";
		}
		return "http://pic.ifjing.com/rbpiczy/pic/2016/03/11/00ec531bb81b4a3fafe45a5b086651f8.jpg";
	}

	@Override
	public String getCardShareWord() {
		return "想痛痛快快地看文，就来@" + CommonLauncherControl.LAUNCHER_NAME + "，感受神一般的更新速度～ ";
	}

	@Override
	public String getCardShareAnatics() {
		return "xsyd";
	}

	@Override
	public String getCardDetailImageUrl() {
		return "http://pic.ifjing.com/rbpiczy/pic/2016/03/11/8996e00fb1ee4b8da2809cef432cbcb7.png";
	}

	@Override
	public void loadDataS(Context context, final Card card, boolean needRefreshData, int showSize) {
		ArrayList<Book> bookList = new ArrayList<Book>();
		NaviCardLoader.createBaseDir();
		String localStr = FileUtil.readFileContent(NAVI_CARD_BOOK_PATH);
		if (TextUtils.isEmpty(localStr)) {
			if (!new File(NAVI_CARD_BOOK_PATH).exists()) {
				FileUtil.copyPluginAssetsFile(context, NAVI_BOOK_FILE, NaviCardLoader.NAV_DIR, NAVI_BOOK_FILE);
				localStr = FileUtil.readFileContent(NAVI_CARD_BOOK_PATH);
			}
		}

		try {
			JSONObject jo = new JSONObject(localStr);
			JSONArray bookSJa = new JSONArray(jo.getString("bookList"));
			bookList.clear();
			for (int i = 0; i < bookSJa.length() && i < showSize; i++) {
				Book book = new Book(bookSJa.getJSONObject(i));
				bookList.add(book);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		refreshView(bookList);
	}

	public void loadRecommendBookS(final Context context, final Handler handler) {
		ArrayList<Book> bookList = new ArrayList<Book>();
		JSONObject paramsJO = new JSONObject();
		try {
			paramsJO.put("Pn", curPage);
			paramsJO.put("Ps", PAGE_BOOK_SIZE);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String jsonParams = paramsJO.toString();
		HashMap<String, String> paramsMap = new HashMap<String, String>();
		LauncherHttpCommon.addGlobalRequestValue(paramsMap, context, jsonParams);
		LauncherHttpCommon httpCommon = new LauncherHttpCommon(URLs.PANDAHOME_BASE_URL + "action.ashx/otheraction/9009");
		ServerResultHeader csResult = httpCommon.getResponseAsCsResultPost(paramsMap, jsonParams);
		if (csResult == null) {
			return;
		}

		curPage++;
		try {
			JSONObject jo = new JSONObject(csResult.getResponseJson());
			bookNum = jo.optInt("bookNumber");
			JSONArray bookSJa = new JSONArray(jo.getString("bookList"));
			bookList.clear();
			for (int i = 0; i < bookSJa.length(); i++) {
				Book book = new Book(bookSJa.getJSONObject(i));
				if (book.num > 0) {
					bookList.add(book);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (bookList.size() > 0) {
			Message msg = new Message();
			msg.what = CardViewHelper.MSG_LOAD_RECOMMEND_BOOK_SUC;
			msg.obj = bookList;
			handler.sendMessage(msg);
		}
	}

	public void loadBook(Context context, String bookId, Handler handler) {
		JSONObject paramsJO = new JSONObject();
		try {
			paramsJO.put("Bkid", bookId);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String jsonParams = paramsJO.toString();
		HashMap<String, String> paramsMap = new HashMap<String, String>();
		LauncherHttpCommon.addGlobalRequestValue(paramsMap, context, jsonParams);
		LauncherHttpCommon httpCommon = new LauncherHttpCommon(URLs.PANDAHOME_BASE_URL + "action.ashx/otheraction/9010");
		ServerResultHeader csResult = httpCommon.getResponseAsCsResultPost(paramsMap, jsonParams);
		if (csResult == null) {
			return;
		}

		Book book = null;
		try {
			JSONObject jo = new JSONObject(csResult.getResponseJson());
			book = new Book(jo);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (book != null) {
			Message msg = new Message();
			msg.what = BookDetailAct.MSG_LOAD_BOOK_SUC;
			msg.obj = book;
			handler.sendMessage(msg);
		}
	}

	@SuppressWarnings("deprecation")
	public void searchBookS(Context context, Handler handler, String key) {
		ArrayList<Book> bookList = new ArrayList<Book>();
		JSONObject paramsJO = new JSONObject();
		try {
			paramsJO.put("Keyword", URLEncoder.encode(key));
			paramsJO.put("Pn", curPage);
			paramsJO.put("Ps", PAGE_BOOK_SIZE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String jsonParams = paramsJO.toString();
		HashMap<String, String> paramsMap = new HashMap<String, String>();
		LauncherHttpCommon.addGlobalRequestValue(paramsMap, context, jsonParams);
		String url = URLs.PANDAHOME_BASE_URL + "action.ashx/otheraction/9011";
		LauncherHttpCommon httpCommon = new LauncherHttpCommon(url);
		ServerResultHeader csResult = httpCommon.getResponseAsCsResultPost(paramsMap, jsonParams);
		if (csResult == null) {
			return;
		}

		curPage++;
		try {
			JSONObject jo = new JSONObject(csResult.getResponseJson());
			bookNum = jo.optInt("bookNumber");
			JSONArray bookSJa = new JSONArray(jo.getString("bookList"));
			bookList.clear();
			for (int i = 0; i < bookSJa.length(); i++) {
				Book book = new Book(bookSJa.getJSONObject(i));
				if (book.num > 0) {
					bookList.add(book);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Message msg = new Message();
		msg.what = BookSearchAct.MSG_SEARCH_BOOK_SUC;
		msg.obj = bookList;
		handler.sendMessage(msg);
	}

	public static void toRead(final Context context, final Book book) {
		// Toast.makeText(context, "正在加载阅读页面", Toast.LENGTH_SHORT).show();
		ThreadUtil.executeMore(new Runnable() {
			@Override
			public void run() {
				String cuid = LauncherLibUtil.md5Hex(TelephoneUtil.getIMEI(context));
				String chapterId = getReadRecord(context, book.bookId, cuid);
				String url = UrlUtilC.setUrlParam(book.url, "uid", cuid);
				url = UrlUtilC.setUrlParam(url, "crid", chapterId);
				LauncherCaller.openUrl(context, "", url, IntegralTaskIdContent.NAVIGATION_STORY,
						CvAnalysisConstant.NAVIGATION_SCREEN_INTO, CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_BOOK_CARD_CLICK,
						CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_BOOK_RES_ID,CvAnalysisConstant.RESTYPE_LINKS);
			}
		});
	}

	public static void addToMyBook(Context context, Book book) {
		ArrayList<Book> bookList = new ArrayList<Book>();
		String localStr = FileUtil.readFileContent(NAVI_CARD_BOOK_PATH);
		try {
			JSONObject jo = new JSONObject(localStr);
			JSONArray bookSJa = new JSONArray(jo.getString("bookList"));
			bookList.clear();
			for (int i = 0; i < bookSJa.length(); i++) {
				Book localBook = new Book(bookSJa.getJSONObject(i));
				bookList.add(localBook);
			}

			bookList.add(0, book);
			JSONObject saveJo = new JSONObject();
			JSONArray saveJa = new JSONArray();
			for (Book saveBook : bookList) {
				JSONObject bookJo = saveBook.toJson();
				saveJa.put(bookJo);
			}

			saveJo.put("bookList", saveJa.toString());
			FileUtil.writeFile(NAVI_CARD_BOOK_PATH, saveJo.toString(), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void delMyBook(Context mContext, Book book) {
		String localStr = FileUtil.readFileContent(NAVI_CARD_BOOK_PATH);
		ArrayList<Book> bookList = new ArrayList<Book>();
		try {
			JSONObject jo = new JSONObject(localStr);
			JSONArray bookSJa = new JSONArray(jo.getString("bookList"));
			bookList.clear();
			for (int i = 0; i < bookSJa.length(); i++) {
				Book localBook = new Book(bookSJa.getJSONObject(i));
				bookList.add(localBook);
			}

			for (int i = 0; i < bookList.size(); i++) {
				if (bookList.get(i).bookId.equals(book.bookId)) {
					bookList.remove(i);
					break;
				}
			}

			JSONObject saveJo = new JSONObject();
			JSONArray saveJa = new JSONArray();
			for (Book saveBook : bookList) {
				JSONObject bookJo = saveBook.toJson();
				saveJa.put(bookJo);
			}

			saveJo.put("bookList", saveJa.toString());
			FileUtil.writeFile(NAVI_CARD_BOOK_PATH, saveJo.toString(), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean isBookExist(Book book) {
		String localStr = FileUtil.readFileContent(NAVI_CARD_BOOK_PATH);
		try {
			JSONObject jo = new JSONObject(localStr);
			JSONArray bookSJa = new JSONArray(jo.getString("bookList"));
			for (int i = 0; i < bookSJa.length(); i++) {
				Book localBook = new Book(bookSJa.getJSONObject(i));
				if (localBook.bookId.equals(book.bookId)) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public static String getReadRecord(Context context, String bookId, String uid) {
		String chapterId = "1";
		JSONObject paramsJO = new JSONObject();
		String jsonParams = paramsJO.toString();
		HashMap<String, String> paramsMap = new HashMap<String, String>();
		LauncherHttpCommon.addGlobalRequestValue(paramsMap, context, jsonParams);
		String url = "http://ks.baidu.com/app91/getreadhistory?uid=" + uid;
		LauncherHttpCommon httpCommon = new LauncherHttpCommon(url);
		ServerResultHeader csResult = httpCommon.getResponseAsCsResultPost(paramsMap, jsonParams);
		if (csResult == null) {
			return chapterId;
		}

		String res = csResult.getResponseJson();
		try {
			JSONArray ja = new JSONObject(res).getJSONArray("bookList");
			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo = ja.getJSONObject(i);
				String resBookId = String.valueOf(jo.optInt("bookId"));
				if (bookId.equals(resBookId)) {
					return String.valueOf(jo.optInt("chapterId"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return chapterId;
	}

	@Override
	public void loadDataFromServer(Context context, Card card, boolean needRefreshView) {
		
	}

	@Override
	public <E> void refreshView(ArrayList<E> objList) {
		if (handler == null) {
			return;
		}
		
		Message msg = new Message();
		msg.what = CardViewHelper.MSG_LOAD_MY_BOOK_SUC;
		msg.obj = objList;
		handler.sendMessage(msg);
	}
}
