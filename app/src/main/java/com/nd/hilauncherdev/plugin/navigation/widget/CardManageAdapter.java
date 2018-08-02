package com.nd.hilauncherdev.plugin.navigation.widget;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nd.hilauncherdev.plugin.navigation.Card;
import com.nd.hilauncherdev.plugin.navigation.CardManager;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.activity.CardManageActivity;

/**
 * 管理卡片Adapter<br/>
 * 
 * @author chenzhihong_9101910
 * 
 */
public class CardManageAdapter extends BaseAdapter {
	public ArrayList<Card> mCardS = new ArrayList<Card>();

	private Context mCtx;
	private CardManageActivity act;
	private LayoutInflater mInflater;

	public CardManageAdapter(Context context, ArrayList<Card> cardS) {
		this.mCtx = context;
		this.act = (CardManageActivity) context;
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
			convertView = mInflater.inflate(R.layout.navigation_list_card_manage_item, null);
			viewHolder = new ViewHolder();
			viewHolder.cardDelV = convertView.findViewById(R.id.navi_list_card_manager_del_ic);
			viewHolder.cardDelImg = (ImageView) convertView.findViewById(R.id.navi_list_card_manager_del_img);
			viewHolder.cardMoveV = convertView.findViewById(R.id.navi_list_card_manager_move_ic);
			viewHolder.cardCannotMoveV = (ImageView) convertView.findViewById(R.id.navi_list_card_manager_cannot_move);
			viewHolder.nameV = (TextView) convertView.findViewById(R.id.navi_list_card_manager_name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (card.canBeDeleted) {
			viewHolder.cardDelImg.setVisibility(View.VISIBLE);
			viewHolder.cardDelImg.setImageResource(R.drawable.navi_card_del);
			viewHolder.cardDelV.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Card curCard = mCardS.get(position);
					if (curCard.canBeDeleted) {
						mCardS.remove(position);
						CardManageAdapter.this.notifyDataSetChanged();
						setChanged();
					}
				}
			});
		} else {
			viewHolder.cardDelImg.setVisibility(View.VISIBLE);
			viewHolder.cardDelImg.setImageResource(R.drawable.navi_card_del_disable);
		}

		if (card.canMoved) {
			viewHolder.cardMoveV.setVisibility(View.VISIBLE);
			viewHolder.cardCannotMoveV.setVisibility(View.GONE);
		} else {
			viewHolder.cardCannotMoveV.setVisibility(View.VISIBLE);
			viewHolder.cardMoveV.setVisibility(View.GONE);
		}

		viewHolder.nameV.setText(card.name);
		return convertView;
	}

	protected void setChanged() {
		if (!act.isChanged) {
			act.isChanged = true;
			CardManager.getInstance().setIsCardListChanged(act, true);
		}
	}

	private class ViewHolder {
		public View cardDelV;
		public ImageView cardDelImg;
		public View cardMoveV;
		public ImageView cardCannotMoveV;
		public TextView nameV;
	}

	public void swap(int from, int to) {
		if (from != to) {
			Card item = (Card) this.getItem(from);
			mCardS.remove(from);
			mCardS.add(to, item);
			notifyDataSetChanged();
			setChanged();
		}
	}
}
