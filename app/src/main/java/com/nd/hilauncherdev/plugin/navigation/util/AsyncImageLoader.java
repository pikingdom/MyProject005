package com.nd.hilauncherdev.plugin.navigation.util;


import java.io.File;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiskCache;
import com.nostra13.universalimageloader.core.DefaultConfigurationFactory;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.ex.ImageCallback;
import com.nostra13.universalimageloader.utils.L;

/**
 * 使用线程池为每个异步加载的图片提供服务<br/>
 * 
 * @author chenzhihong_9101910
 * 
 */
public class AsyncImageLoader {

	/**
	 * 图片缓存配置参数
	 */
	//TODO 为方便测试 暂时调整为10M
	public static final int DEFAULT_DISKCACHE_SIZE = 200*1024*1024;//默认sd卡的缓存设置为100M
	public static final int DISK_FILE_CACHE_COUNT = 750; //默认缓存文件数量


	public AsyncImageLoader() {

	}

	/**
	 * 初始化图片缓存配置
	 * @param mContext
	 */
	public static void initImageLoaderConfig(Context mContext) {
		try {
			if (!ImageLoader.getInstance().isInited()) {
				File file = new File(Global.CACHES_HOME_IMAGE_LOADER);
				if (!file.exists()) {
					file.mkdirs();
				}
				LruDiskCache lruDiskCache = new LruDiskCache(file, null, DefaultConfigurationFactory.createFileNameGenerator(), AsyncImageLoader.DEFAULT_DISKCACHE_SIZE, AsyncImageLoader.DISK_FILE_CACHE_COUNT);
				ImageLoaderConfiguration imageLoaderConfiguration =
						new ImageLoaderConfiguration.Builder(mContext.getApplicationContext())
								.diskCache(lruDiskCache).build();
				ImageLoader.getInstance().init(imageLoaderConfiguration);
				L.disableLogging();

			}
		}catch (Exception e){
			e.printStackTrace();
		}

	}


	/**
	 * 阻塞加载图片方式并带缓存
	 * @param imageUrl
	 * @return
	 */
	public Drawable loadDrawable(String imageUrl) {
		try {
			return new BitmapDrawable(ImageLoader.getInstance().loadImageSync(imageUrl));
		}catch (Exception e){
			e.printStackTrace();
		}
		return  null;
	}

	/**
	 * 只获取缓存中的drawable
	 * @param imageUrl
	 * @return
	 */
	public Drawable loadDrawableOnlyCache(String imageUrl){
		try {
			return  ImageLoader.getInstance().loadDrawableIfExistInMemory(imageUrl);
		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 异步方式加载图片
	 * @param imageUrl
	 * @param imageCallback
	 * @return
	 */
	public Drawable loadDrawable(String imageUrl, final com.nostra13.universalimageloader.ex.ImageCallback imageCallback) {
		try {
			return loadDrawable(false, imageUrl, imageCallback);
		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
	

	public void showDrawable(final String image, final ImageView imageView, final ImageView.ScaleType imageScaleType, final int loadingRes){
		showDrawable(image,imageView,imageScaleType,loadingRes,ImageView.ScaleType.FIT_XY);
	}
	
	/**
	 * 根据给定的参数展示图片
	 * @param image 展示图片地址
	 * @param imageView 展示的imageview
	 * @param imageScaleType imageview 的缩放类型
	 * @param loadingRes imageview loading图片
	 * @param loadingScaleType 加载图片的显示类型   
	 */
	public void showDrawable(final String image, final ImageView imageView, final ImageView.ScaleType imageScaleType, final int loadingRes , final ImageView.ScaleType loadingScaleType){
		try {
			Drawable drawable = loadDrawable(false, image, new ImageCallback() {
				@Override
				public void imageLoaded(Drawable imageDrawable, String imageUrl, Map extraParams) {
					if(imageDrawable != null && !TextUtils.isEmpty(imageUrl) && imageUrl.equals(image)){
						try {
							imageView.setScaleType(imageScaleType);
						}catch (Throwable t){
							
						}
						imageView.setImageDrawable(imageDrawable);
					}
				}
			});

			if(drawable != null){
				try {
					imageView.setScaleType(imageScaleType);
				}catch (Throwable t){
					t.printStackTrace();
				}
				imageView.setImageDrawable(drawable);
			}else{
				try {
					imageView.setScaleType(loadingScaleType);
				}catch (Throwable t){
					t.printStackTrace();
				}
				imageView.setImageResource(loadingRes);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public Drawable loadDrawable(final boolean newFormatFileName,final String imageUrl, final com.nostra13.universalimageloader.ex.ImageCallback imageCallback) {
		try {
			return ImageLoader.getInstance().loadDrawable(imageUrl,imageCallback);
		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}



}
