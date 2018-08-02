package com.nd.hilauncherdev.plugin.navigation.widget.taobaoext.bannerview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dian91.ad.AdvertSDKManager;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.util.AsyncImageLoader;
import com.nostra13.universalimageloader.ex.ImageCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HorizontalScrollViewAdapter
{

	private Context mContext;
	private ShowListener showListener;
	private LayoutInflater mInflater;
	private List<AdvertSDKManager.AdvertInfo> mDatas= new ArrayList<>();

	private AsyncImageLoader mImageLoader;

	public HorizontalScrollViewAdapter(Context context, List<AdvertSDKManager.AdvertInfo> mDatas)
	{
		this.mContext = context;
		this.mImageLoader = new AsyncImageLoader();
		mInflater = LayoutInflater.from(context);
		if (mDatas != null)
			this.mDatas = mDatas;
	}
	public void setShowListener(ShowListener listener){
		this.showListener = listener;
	}
	public List<AdvertSDKManager.AdvertInfo> getAdapterData(){
		return this.mDatas;
	}
	public void setAdapterData(List<AdvertSDKManager.AdvertInfo> mDatas){
		this.mDatas = mDatas;
	}

	public int getCount()
	{
		return mDatas.size();
	}

	public Object getItem(int position)
	{
		return mDatas.get(position);
	}

	public long getItemId(int position)
	{
		return position;
	}

	public ViewGroup getView(int position, ViewGroup convertView, ViewGroup parent)
	{
		ViewHolder viewHolder = null;
		if (convertView == null)
		{
			viewHolder = new ViewHolder();
			convertView = (ViewGroup)mInflater.inflate(
					R.layout.activity_index_gallery_item, parent, false);
			viewHolder.mImg = (ImageView) convertView
					.findViewById(R.id.id_index_gallery_item_image);
			convertView.setTag(viewHolder);
		} else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (position<mDatas.size()&&position>=0) {
			final AdvertSDKManager.AdvertInfo bannerItem = mDatas.get(position);
			if (bannerItem.picUrl != null) {
				final ImageView mIvBanner = viewHolder.mImg;
				mIvBanner.setImageResource(R.drawable.taobao_banner_background);
				Drawable drawable = mImageLoader.loadDrawable(bannerItem.picUrl, new ImageCallback() {
					@Override
					public void imageLoaded(Drawable imageDrawable, String imageUrl, Map map) {
						if (imageDrawable != null) {
							if (showListener!=null){
								showListener.showEntity(bannerItem);
							}
							if (mIvBanner instanceof ImageView) {
								mIvBanner.setImageDrawable(imageDrawable);
							}
						}
					}
				});
				if (drawable != null) {
					mIvBanner.setImageDrawable(drawable);

					if (showListener != null)
						showListener.showEntity(bannerItem);
				}
			}
		}
		return convertView;
	}

	private class ViewHolder
	{
		ImageView mImg;
	}
	public interface ShowListener{
		public void showEntity(AdvertSDKManager.AdvertInfo entity);
	}
}
