package com.nd.hilauncherdev.plugin.navigation.widget;

import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.nd.hilauncherdev.plugin.navigation.Card;
import com.nd.hilauncherdev.plugin.navigation.CardManager;
import com.nd.hilauncherdev.plugin.navigation.CardViewHelper;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.activity.CardAddActivity;
import com.nd.hilauncherdev.plugin.navigation.activity.CardAddSubAct;
import com.nd.hilauncherdev.plugin.navigation.activity.CardDetailActivity;
import com.nd.hilauncherdev.plugin.navigation.util.AsyncImageLoader;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;
import com.nostra13.universalimageloader.ex.ImageCallback;

/**
 * 添加卡片Adapter<br/>
 * 
 * @author chenzhihong_9101910
 * 
 */
public class CardAddAdapter extends BaseAdapter {
	public ArrayList<Card> mCardS = new ArrayList<Card>();

	private Context mCtx;
	private LayoutInflater mInflater;
	private AsyncImageLoader asyncImageLoader = new AsyncImageLoader();
	public boolean isFromSubAdd = false;
	public boolean isViaNotify = false;
	public CardAddAdapter(Context context, ArrayList<Card> cardS) {
		this.mCtx = context;
		this.mCardS = cardS;
		this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return mCardS.size();
	}

	@Override
	public Object getItem(int position) {
		return mCardS.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		final Card card = (Card) getItem(position);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.navigation_list_card_add_item, null);
			viewHolder = new ViewHolder();
			viewHolder.allV = convertView.findViewById(R.id.navi_add_card_all);
			viewHolder.cardIV = (ImageView) convertView.findViewById(R.id.navi_add_card_ic);
			viewHolder.nameV = (TextView) convertView.findViewById(R.id.navi_list_card_name);
			viewHolder.descV = (TextView) convertView.findViewById(R.id.navi_list_card_desc);
			viewHolder.infoV = (TextView) convertView.findViewById(R.id.navi_list_card_info);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.infoV.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Card card = mCardS.get(position);
				if (card.showType == Card.SHOW_TYPE_SINGLE) {
					if (!card.isAdded) {
						card.isAdded = true;
						CardManager.getInstance().addCard(mCtx, card);
						CardManager.getInstance().addToAddedCardS(mCtx, card.id);
						CardManager.getInstance().setIsCardListChanged(mCtx, true);
						setAddedState((TextView) v);
						if (mCtx instanceof CardAddActivity) {
							((CardAddActivity) mCtx).showCardAddedNotice();
						} else if (mCtx instanceof CardAddSubAct) {
							((CardAddSubAct) mCtx).showCardAddedNotice();
						}
						if(isViaNotify)
							CardViewHelper.submitDefaultNotifyCancleAndAdd(mCtx,card,true);
					}
				} else if (card.showType == Card.SHOW_TYPE_GROUP) {
					Intent intent = new Intent();
					intent.setClass(mCtx, CardAddSubAct.class);
					intent.putExtra("cardType", card.type);
					mCtx.startActivity(intent);
					((Activity) mCtx).finish();
				}
			}
		});
		viewHolder.allV.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Card card = mCardS.get(position);
				if (!isFromSubAdd && card.showType == Card.SHOW_TYPE_SINGLE) {
					Intent intent = new Intent();
					intent.setClass(mCtx, CardDetailActivity.class);
					intent.putExtra(Card.IS_FROM_SUB_ADD, isFromSubAdd);
					intent.putExtra("cardType", card.type);
					intent.putExtra("cardId", card.id);
					mCtx.startActivity(intent);
					((Activity) mCtx).finish();
				} else if (card.showType == Card.SHOW_TYPE_GROUP) {
					Intent intent = new Intent();
					intent.setClass(mCtx, CardAddSubAct.class);
					intent.putExtra("cardType", card.type);
					mCtx.startActivity(intent);
					((Activity) mCtx).finish();
				}
			}
		});
		viewHolder.nameV.setText(card.name);
		viewHolder.descV.setText(card.desc);
		UiHelper.setCardIc(viewHolder.cardIV, card);
		Drawable drawable = asyncImageLoader.loadDrawable(card.smallImgUrl, new ImageCallback() {
			@Override
			public void imageLoaded(Drawable imageDrawable, String imageUrl, Map map) {
			}
		});
		if (drawable != null) {
			viewHolder.cardIV.setImageDrawable(drawable);
		}

		RelativeLayout.LayoutParams layoutParams = (LayoutParams) viewHolder.infoV.getLayoutParams();
		if (card.showType == Card.SHOW_TYPE_SINGLE) {
			layoutParams.width = ScreenUtil.dip2px(mCtx, 60);
			layoutParams.height = ScreenUtil.dip2px(mCtx, 29);
			if (card.isAdded) {
				setAddedState(viewHolder.infoV);
			} else {
				viewHolder.infoV.setText("添加");
				viewHolder.infoV.setTextColor(Color.parseColor("#ffffff"));
				viewHolder.infoV.setBackgroundResource(R.drawable.navi_card_add_btn);
			}
		} else if (card.showType == Card.SHOW_TYPE_GROUP) {
			layoutParams.width = ScreenUtil.dip2px(mCtx, 10);
			layoutParams.height = ScreenUtil.dip2px(mCtx, 15);
			viewHolder.infoV.setText("");
			viewHolder.infoV.setBackgroundResource(R.drawable.navi_right_go);
		}
		viewHolder.infoV.setLayoutParams(layoutParams);
		
		return convertView;
	}

	private void setAddedState(TextView tv) {
		tv.setText("已添加");
		tv.setTextColor(Color.parseColor("#ffffff"));
		tv.setBackgroundResource(R.drawable.navi_card_added_btn);
	}

	private class ViewHolder {
		public View allV;
		public ImageView cardIV;
		public TextView nameV;
		public TextView descV;
		public TextView infoV;
	}

}
