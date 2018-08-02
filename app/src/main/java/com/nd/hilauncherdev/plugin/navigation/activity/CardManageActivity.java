package com.nd.hilauncherdev.plugin.navigation.activity;

import java.util.ArrayList;
import java.util.HashSet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ListView;

import com.nd.hilauncherdev.plugin.navigation.Card;
import com.nd.hilauncherdev.plugin.navigation.CardManager;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.util.AnalyticsConstant;
import com.nd.hilauncherdev.plugin.navigation.util.LauncherBranchController;
import com.nd.hilauncherdev.plugin.navigation.widget.CardManageAdapter;
import com.nd.hilauncherdev.plugin.navigation.widget.DragSortListView;
import com.nd.hilauncherdev.plugin.navigation.widget.HeaderView;

/**
 * 卡片管理界面<br/>
 * 
 * @author chenzhihong_9101910
 * 
 */
public class CardManageActivity extends BaseActivity {

	private Context mContext;
	private DragSortListView mCardDownList;
	private ListView mCardUpList;
	private CardManageAdapter mCardUpAdapter;
	private CardManageAdapter mCardDownAdapter;
	public boolean isChanged = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.navigation_card_manage);
		HeaderView headView = (HeaderView) findViewById(R.id.head_view);
		headView.setTitle("管理卡片");
		headView.setGoBackListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		if(LauncherBranchController.isNavigationForCustomLauncher()){
			headView.setMenuVisibility(View.INVISIBLE);
		}else{
			headView.setMenuVisibility(View.VISIBLE);
			headView.setMenuListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						Intent intent = new Intent();
						intent.setClassName(mContext, "com.nd.hilauncherdev.launcher.navigation.settings.SettingsActivity");
						mContext.startActivity(intent);
						CardManageActivity.this.submitEvent(mContext, AnalyticsConstant.ZERO_SCREEN_SETTING);
						finish();
					}catch (Exception e){
						e.printStackTrace();
					}

				}
			});
		}
		mCardUpList = (ListView) findViewById(R.id.navi_card_up_list);
		mCardUpList.setVisibility(View.GONE);
		mCardDownList = (DragSortListView) findViewById(R.id.navi_card_down_list);
		initCard();
	}

	private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			mCardDownAdapter.swap(from, to);
		}
	};

	private void initCard() {
		ArrayList<Card> currentCardS = CardManager.getInstance().getCurrentCardS(mContext);
		ArrayList<Card> cardUpS = CardManager.getUpCardS(currentCardS);
		mCardUpAdapter = new CardManageAdapter(CardManageActivity.this, cardUpS);
		mCardUpList.setAdapter(mCardUpAdapter);
		ArrayList<Card> cardDownS = CardManager.getDownCardS(currentCardS);
		HashSet<Integer> implementCardIdM = CardManager.getImplementCardIdM();
		ArrayList<Card> cardsDowns = new ArrayList<Card>();
		for(Card card:cardDownS){
			if(implementCardIdM.contains(new Integer(card.id))){
				cardsDowns.add(card);
			}
		}

		mCardDownAdapter = new CardManageAdapter(CardManageActivity.this, cardsDowns);
		mCardDownList.setAdapter(mCardDownAdapter);
		mCardDownList.setDropListener(onDrop);
	}

	@Override
	protected void onPause() {
		CardManager.getInstance().saveCardS(this, mCardUpAdapter.mCardS, mCardDownAdapter.mCardS);
		super.onPause();
	}
}
