package com.nd.hilauncherdev.plugin.navigation.widget;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nd.hilauncherdev.plugin.navigation.Card;
import com.nd.hilauncherdev.plugin.navigation.CardManager;
import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.activity.BookDetailAct;
import com.nd.hilauncherdev.plugin.navigation.activity.BookListAct;
import com.nd.hilauncherdev.plugin.navigation.bean.Book;
import com.nd.hilauncherdev.plugin.navigation.loader.BookLoader;
import com.nd.hilauncherdev.plugin.navigation.util.AnalyticsConstant;
import com.nd.hilauncherdev.plugin.navigation.util.AsyncImageLoader;
import com.nd.hilauncherdev.plugin.navigation.util.CommonGlobal;
import com.nostra13.universalimageloader.ex.ImageCallback;

public class BookAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater layoutInflater;
	public Handler handler;
	public BookLoader bookLoader;

	private AsyncImageLoader asyncImageLoader;
	public ArrayList<Book> list = new ArrayList<>();
	public int layoutId = 0;
	public boolean toRead = true;
	public boolean isShowOp = false;
	public boolean isDelClick = false;

	private class ViewHolder {
		View allV;
		ImageView imageView;
		TextView nameV;
		TextView statusV;
		View delV;
	}

	public BookAdapter(Context context, AsyncImageLoader asyncImageLoader, ArrayList<Book> list, boolean toRead) {
		mContext = context;
		layoutInflater = LayoutInflater.from(mContext);
		this.asyncImageLoader = asyncImageLoader;
		this.list = list;
		this.toRead = toRead;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Book getItem(int position) {
		if (position < list.size()) {
			return list.get(position);
		}

		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		try {
			final Book item = getItem(position);
			if (null == item) {
				return new View(mContext);
			}

			if (convertView == null) {
				if (layoutId == 0) {
					convertView = layoutInflater.inflate(R.layout.book_card_item, null);
				} else {
					convertView = layoutInflater.inflate(R.layout.book_list_item, null);
				}
			}

			if (convertView.getTag() == null) {
				ViewHolder holder = new ViewHolder();
				holder.allV = convertView.findViewById(R.id.book_item_all);
				holder.imageView = (ImageView) convertView.findViewById(R.id.book_item_ic);
				holder.nameV = (TextView) convertView.findViewById(R.id.book_item_name);
				holder.statusV = (TextView) convertView.findViewById(R.id.book_item_chapter_status);
				holder.delV = convertView.findViewById(R.id.book_item_del);
				convertView.setTag(holder);
			}

			final ViewHolder viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.imageView.setImageResource(CommonLauncherControl.getJrttNewBigBackgroad());
			Drawable cachedImage = asyncImageLoader.loadDrawable(item.icon, new ImageCallback() {
				@Override
				public void imageLoaded(Drawable imageDrawable, String imageUrl, Map map) {
					if (imageDrawable != null && viewHolder.imageView != null && imageUrl.equals(item.icon)) {
						viewHolder.imageView.setImageDrawable(imageDrawable);
					}
				}
			});
			if (cachedImage != null) {
				viewHolder.imageView.setImageDrawable(cachedImage);
			}

			viewHolder.nameV.setText(item.name);
			String status = "";
			if (item.type == 0) {
				status += "已完结；";
			} else {
				status += "未完结；";
			}

			status += "共" + item.num + "章";
			viewHolder.statusV.setText(status);
			viewHolder.allV.setTag(item.url);
			viewHolder.allV.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (toRead) {
						BookLoader.toRead(mContext, item);
						if (mContext instanceof BookListAct) {
							((BookListAct) mContext).submitEvent(mContext, AnalyticsConstant.NAVIGATION_SCREEN_STORY_CARD_MY_COLUMN_DISTRIBUTE_EFFECT, item.bookId);
						}
					} else {
						Intent intent = new Intent(mContext, BookDetailAct.class);
						intent.putExtra("bookId", item.bookId);
						mContext.startActivity(intent);
					}
				}
			});
			if (isShowOp && viewHolder.delV != null) {
				viewHolder.delV.setVisibility(View.VISIBLE);
				viewHolder.delV.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						doDelMyBook(item);
					}
				});
			} else if (viewHolder.delV != null) {
				viewHolder.delV.setVisibility(View.GONE);
			}
			return convertView;
		} catch (Exception e) {
			return new View(mContext);
		}

	}

	protected void doDelMyBook(Book book) {
		BookLoader.delMyBook(mContext, book);
		CardManager.getInstance().setIsCardListChanged(mContext, true);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				bookLoader.handler = handler;
				bookLoader.loadDataS(mContext, new Card(), false, Integer.MAX_VALUE);
			}
		}, 300);
		isDelClick = true;
	}
}
