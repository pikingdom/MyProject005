package com.nd.hilauncherdev.plugin.navigation.loader;

import android.content.Context;
import android.text.TextUtils;

import com.nd.hilauncherdev.plugin.navigation.CardManager;
import com.nd.hilauncherdev.plugin.navigation.bean.TextBean;
import com.nd.hilauncherdev.plugin.navigation.bean.VideoWallpaperBean;
import com.nd.hilauncherdev.plugin.navigation.bean.WallpaperBean;
import com.nd.hilauncherdev.plugin.navigation.bean.WallpaperResultBean;
import com.nd.hilauncherdev.plugin.navigation.net.NetOpi;
import com.nd.hilauncherdev.plugin.navigation.util.FileUtil;
import com.nd.hilauncherdev.plugin.navigation.util.Global;
import com.nd.hilauncherdev.plugin.navigation.util.ThreadUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 * Created by linliangbin on 2017/5/10 13:46.
 */

public class WallpaperDataLoader {

    private static final String NAVI_FILE_WALLPAPER = "navi_wallpaper_text.txt";
    private static final String NAVI_PATH_WALLPAPER = NaviCardLoader.NAV_DIR + NAVI_FILE_WALLPAPER;


    private static WallpaperDataLoader instance = null;
    private Context context;

    private int textIndex = 0;
    private int picIndex = -1;
    private LinkedList<WallpaperBean> wallpaperList;
    private LinkedList<TextBean> textList;

    private boolean isUsingDefault = false;

    public WallpaperDataLoader(Context context) {
        this.context = context;
        initData();
    }

    public synchronized static WallpaperDataLoader getInstance(Context context) {
        if (instance == null) {
            instance = new WallpaperDataLoader(context);
        }
        return instance;
    }

    private int getSafeTextIndex() {
        if (textIndex < 0 || textIndex >= textList.size()) {
            if(textList.size() > 0){
                textIndex = textList.size() - 1;
            }else{
                textIndex = 0;
            }
            return textIndex;
        }
        return textIndex;
    }

    private int getSafePicIndex() {
        if(picIndex == -1){
            picIndex = CardManager.getInstance().getLastPicIndex(context);
        }
        if (picIndex < 0 || picIndex >= wallpaperList.size()) {
            if(wallpaperList.size() > 0){
                picIndex = wallpaperList.size() - 1;
            }else{
                picIndex = 0;
            }
            return picIndex;
        }
        return picIndex;
    }


    public TextBean popText() {
        if (textList == null || textList.size() == 0) {
            initData();
        }

        if (textList != null && textList.size() > 0) {
            TextBean textBean = textList.get(getSafeTextIndex());
            textIndex++;
            if (textIndex >= textList.size()) {
                CardManager.getInstance().setLastTextId(context, textBean.textId);
                refreshDataMemoryAndFile();
            }
            return textBean;
        }
        if (isUsingDefault) {
            refreshDataMemoryAndFile();
        }
        return null;
    }

    public WallpaperBean popWallpaper() {
        if (wallpaperList == null || wallpaperList.size() == 0) {
            initData();
        }

        if (wallpaperList != null && wallpaperList.size() > 0) {
            WallpaperBean wallpaperBean = wallpaperList.get(getSafePicIndex());
            picIndex++;
            CardManager.getInstance().setLastPicIndex(context,picIndex);
            if (picIndex >= wallpaperList.size()) {
                CardManager.getInstance().setLastPicId(context, wallpaperBean.picId);
                refreshDataMemoryAndFile();
            }
            return wallpaperBean;
        }
        return null;
    }

    private String doReadAsset() {
        return FileUtil.readFromAssetsFile(context, NAVI_FILE_WALLPAPER);
    }


    private boolean isCurrentEmptyData() {

        if (textList == null || textList.size() == 0) {
            return true;
        }
        if (wallpaperList == null || wallpaperList.size() == 0) {
            return true;
        }
        return false;
    }

    private void initData() {

        String localStr = FileUtil.readFileContent(NAVI_PATH_WALLPAPER);

        if (TextUtils.isEmpty(localStr)) {
            localStr = doReadAsset();
            isUsingDefault = true;
        }

        WallpaperResultBean wallpaperResultBean = parseResultFromData(localStr);

        if (!wallpaperResultBean.isTextListAvailable() || !wallpaperResultBean.isWallpaperListAvailable()) {
            localStr = doReadAsset();
            isUsingDefault = true;
            wallpaperResultBean = parseResultFromData(localStr);
            if (isCurrentEmptyData()) {
                updateCacheVideo(wallpaperResultBean);
            }
        } else {
            updateCacheVideo(wallpaperResultBean);
        }

    }

    private void updateCacheVideo(final WallpaperResultBean wallpaperResultBean) {

        if (wallpaperResultBean == null) {
            return;
        }
        if (wallpaperResultBean.isTextListAvailable()) {
            if (textList == null) {
                textList = new LinkedList<TextBean>();
            }
            textList.clear();
            textList.addAll(wallpaperResultBean.textList);
            textIndex = 0;
        }
        if (wallpaperResultBean.isWallpaperListAvailable()) {
            if (wallpaperList == null) {
                wallpaperList = new LinkedList<WallpaperBean>();
            }
            wallpaperList.clear();
            wallpaperList.addAll(wallpaperResultBean.wallpaperList);
            picIndex = CardManager.getInstance().getLastPicIndex(context);
        }

    }

    public WallpaperResultBean parseResultFromData(String data) {


        WallpaperResultBean resultBean = new WallpaperResultBean();

        try {
            String resString = data;
            JSONObject jsonObject = new JSONObject(resString);

            JSONArray jsonArray = jsonObject.optJSONArray("VideoPaperList");
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject tempObject = (JSONObject) jsonArray.get(i);
                    VideoWallpaperBean videoWallpaperBean = new VideoWallpaperBean(tempObject);
                    resultBean.addVideoBean(videoWallpaperBean);
                }
            }

            jsonArray = jsonObject.optJSONArray("PicList");
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject tempObject = (JSONObject) jsonArray.get(i);
                    WallpaperBean wallpaperBean = new WallpaperBean(tempObject);
                    resultBean.addWallpaperBean(wallpaperBean);
                }
            }

            jsonArray = jsonObject.optJSONArray("TextList");
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject tempObject = (JSONObject) jsonArray.get(i);
                    TextBean textBean = new TextBean(tempObject);
                    resultBean.addTextBean(textBean);
                }
            }


            return resultBean;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultBean;
    }


    public void refreshDataMemoryAndFile() {
        ThreadUtil.executeMore(new Runnable() {
            @Override
            public void run() {
                if (loadSaveData()) {
                    Global.runInMainThread(new Runnable() {
                        @Override
                        public void run() {
                            initData();
                        }
                    });
                }

            }
        });
    }

    public boolean loadSaveData() {

        try {
            String data = NetOpi.loadWallpaperAndText(context, 1, CardManager.getInstance().getLastPicId(context),
                    CardManager.getInstance().getLastTextId(context));
            if (TextUtils.isEmpty(data)) {
                return false;
            }
            FileUtil.writeFile(NAVI_PATH_WALLPAPER, data, false);
            CardManager.getInstance().setLastPicIndex(context,0);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;


    }

}
