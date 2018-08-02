package com.nd.hilauncherdev.plugin.navigation.loader;

import android.content.Context;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.nd.hilauncherdev.kitset.util.reflect.NavigationKeepForReflect;
import com.nd.hilauncherdev.plugin.navigation.Card;
import com.nd.hilauncherdev.plugin.navigation.CardManager;
import com.nd.hilauncherdev.plugin.navigation.CardViewHelper;
import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;
import com.nd.hilauncherdev.plugin.navigation.bean.Book;
import com.nd.hilauncherdev.plugin.navigation.kit.AssetFileCache;
import com.nd.hilauncherdev.plugin.navigation.net.NetOpi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 书架卡片loader
 * Created by linliangbin on 2017/8/3 17:57.
 */

public class BookShelfLoader extends CardDataLoader {


    // 阅读历史展示个数
    public static final int HISTORY_ITEM_COUNT = 2;
    // 总的展示个数
    public static final int SHOW_ITEM_COUNT = 3;
    // 榜单推荐数据文件缓存
    public static final String NAVI_BOOK_SHELF_FILE = "navi_card_book_shelf";
    // 个性化推荐数据文件缓存
    private static final String NAVI_BOOK_SHELF_PERSONAL_FILE = "navi_card_book_shelf_personal";

    /**
     * 是否需要重新更新阅读数据
     */
    public static boolean mayNeedReloadRead = false;

    private ArrayList<Book> recommendList = new ArrayList<Book>();
    private int recommendIndex = 0;

    public static Card generateCard() {
        Card card = new Card();
        card.type = CardManager.CARD_BOOKSHELF_TYPE;
        card.id = CardManager.CARD_ID_BOOKSHELF;
        return card;
    }

    public static ArrayList<Book> parseBookList(String data) {
        ArrayList<Book> books = new ArrayList<Book>();
        if (TextUtils.isEmpty(data)) {
            return books;
        }

        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONArray jsonArray = jsonObject.optJSONArray("List");
            return parseBookList(jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return books;
    }

    public static ArrayList<Book> parseBookList(JSONArray jsonArray) {
        ArrayList<Book> books = new ArrayList<Book>();

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                String string = jsonArray.getString(i);
                JSONObject current = new JSONObject(string);
                if (current != null) {
                    Book book = new Book();
                    book.icon = current.optString("Thumb");
                    book.bookId = current.optString("Id");
                    book.name = current.optString("Name");
                    book.desc = current.optString("Desc");
                    book.author = current.optString("FromSource");
                    if (book.isAvailable()) {
                        books.add(book);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return books;
    }

    @Override
    public String getCardShareImagePath() {
        return NaviCardLoader.NAV_DIR + "navigation_book_share.jpg";
    }

    @Override
    public String getCardShareImageUrl() {
        if (CommonLauncherControl.DX_PKG) {
            return "http://pic.ifjing.com/rbpiczy/pic/2016/04/25/dd61dd153efc477aaa2f21723b04f074.jpg";
        }
        if (CommonLauncherControl.AZ_PKG) {
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
        return "xssj";
    }

    @Override
    public String getCardDetailImageUrl() {
        return "http://pic.ifjing.com/rbpiczy/pic/2016/03/11/8996e00fb1ee4b8da2809cef432cbcb7.png";
    }

    /**
     * @desc 将本地阅读数据和推荐数据按照2本本地+1本推荐数据方式组合
     * 本地数据不足时使用推荐数据填充
     * @author linliangbin
     * @time 2017/8/8 15:07
     */
    private ArrayList<Book> mergeList(ArrayList<Book> readHistory) {
        ArrayList<Book> resultList = new ArrayList<Book>();
        HashMap<String, String> resultIdMap = new HashMap<String, String>();
        if (readHistory == null || readHistory.isEmpty()) {
            for (int i = 0; i < recommendList.size() && resultList.size() < SHOW_ITEM_COUNT; i++) {
                Book current = recommendList.get((recommendIndex++) % recommendList.size());
                if (current == null) {
                    continue;
                }
                current.isRecommend = true;
                if (!resultIdMap.containsKey(current.bookId)) {
                    resultList.add(current);
                    resultIdMap.put(current.bookId, "");
                }
            }
            return resultList;
        }

        for (int i = 0; i < HISTORY_ITEM_COUNT && i < readHistory.size(); i++) {
            Book book = readHistory.get(i);
            book.isRecommend = false;
            resultList.add(book);
            resultIdMap.put(book.bookId, "");
        }

        if (recommendList != null) {
            for (int i = 0; i < recommendList.size() && resultList.size() < SHOW_ITEM_COUNT; i++) {
                Book current = recommendList.get((recommendIndex++) % recommendList.size());
                if (current == null) {
                    continue;
                }
                current.isRecommend = true;
                if (!resultIdMap.containsKey(current.bookId)) {
                    resultList.add(current);
                }
            }
        }


        if (resultList.size() < SHOW_ITEM_COUNT) {
            resultList.clear();
            resultList.addAll(readHistory);
        }

        return resultList;
    }

    private void updateCacheRecommendList(ArrayList<Book> recommend, ArrayList<Book> personalRecommend) {
        if ((recommend == null || recommend.isEmpty())
                && (personalRecommend == null || personalRecommend.isEmpty())) {
            return;
        }

        if (recommendList == null) {
            recommendList = new ArrayList<Book>();
        }
        recommendList.clear();

        HashMap<String, String> bookIds = new HashMap<String, String>();
        if (recommend != null && recommend.size() > 0) {
            for (int i = 0; i < recommend.size(); i++) {
                Book book = recommend.get(i);
                if (book != null && !bookIds.containsKey(book.bookId)) {
                    recommendList.add(book);
                    bookIds.put(book.bookId, "");
                }
            }
        }

        if (personalRecommend != null && personalRecommend.size() > 0) {
            for (int i = 0; i < personalRecommend.size(); i++) {
                Book book = personalRecommend.get(i);
                if (book != null && !bookIds.containsKey(book.bookId)) {
                    recommendList.add(book);
                    bookIds.put(book.bookId, "");
                }
            }
        }

    }

    /**
     * @param updateCacheIncase 缓存未空时是否获取访问接口
     * @param needRefreshData   是否强制更新数据并刷新
     * @desc
     * @author linliangbin
     * @time 2017/8/9 15:46
     */
    public void loadDataS(Context context, Card card, boolean needRefreshData, boolean updateCacheIncase, int showSize) {

        ArrayList<Book> readHistory = new ArrayList<Book>();
        String readData = "";
        try {
            readData = NavigationKeepForReflect.getLocalReadHistory();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        if (!TextUtils.isEmpty(readData)) {
            try {
                readHistory = parseBookList(new JSONArray(readData));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        /** 是否无缓存数据 */
        boolean emptyCache = false;

        /** 是否缓存数据已经全部展示完 */
        boolean cacheOut = false;

        String recommendData = AssetFileCache.getInstance(context).readByFile(NAVI_BOOK_SHELF_FILE);
        ArrayList<Book> bookList = parseBookList(recommendData);
        if (bookList == null || bookList.size() <= 0) {
            emptyCache = true;
            bookList = parseBookList(AssetFileCache.getInstance(context).readByAsset(NAVI_BOOK_SHELF_FILE));
        }

        String personalRecommendData = AssetFileCache.getInstance(context).readByFile(NAVI_BOOK_SHELF_PERSONAL_FILE);
        ArrayList<Book> personalBookList = null;
        try {
            personalBookList = parseBookList(new JSONArray(personalRecommendData));

        } catch (Exception e) {
            e.printStackTrace();
        }

        updateCacheRecommendList(bookList, personalBookList);
        refreshView(mergeList(readHistory));

        if (recommendList != null && recommendIndex > recommendList.size()) {
            recommendIndex = 0;
            cacheOut = true;
        }
        if ((emptyCache && updateCacheIncase) || cacheOut) {
            loadDataFromServer(context, card, true);
        }
    }

    @Override
    public void loadDataS(Context context, Card card, boolean needRefreshData, int showSize) {
        Log.i("llbeing", "BookLoader:loadDataS");
        loadDataS(context, card, needRefreshData, true, showSize);
    }

    @Override
    public void loadDataFromServer(Context context, Card card, boolean needRefreshView) {
        Log.i("llbeing", "BookLoader:loadDataFromServer");
        String data = NetOpi.loadRecommendBookList(context, 1, 30);
        ArrayList<Book> bookList = parseBookList(data);
        if (bookList != null && bookList.size() > 0) {
            if (needRefreshView) {
                loadDataS(context, card, false, false, 3);
            }
            AssetFileCache.getInstance(mContext).writeToFile(NAVI_BOOK_SHELF_FILE, data);
        }


        try {
            String personalRecommendData = NavigationKeepForReflect.getPersonalRecommend();
            ArrayList<Book> personalRecommendList = parseBookList(new JSONArray(personalRecommendData));
            if (personalRecommendList != null && personalRecommendList.size() > 0) {
                AssetFileCache.getInstance(mContext).writeToFile(NAVI_BOOK_SHELF_PERSONAL_FILE, personalRecommendData);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    @Override
    public <E> void refreshView(ArrayList<E> objList) {
        if (handler == null) {
            return;
        }

        Message msg = new Message();
        msg.what = CardViewHelper.MSG_LOAD_BOOKSHELF_SUC;
        msg.obj = objList;
        handler.sendMessage(msg);
    }

}
