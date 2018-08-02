package com.nd.hilauncherdev.plugin.navigation.activity;

import java.util.ArrayList;
import java.util.HashSet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ListView;

import com.android.dynamic.plugin.PluginUtil;
import com.nd.hilauncherdev.plugin.navigation.Card;
import com.nd.hilauncherdev.plugin.navigation.CardManager;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.util.AnalyticsConstant;
import com.nd.hilauncherdev.plugin.navigation.widget.CardAddAdapter;
import com.nd.hilauncherdev.plugin.navigation.widget.HeaderView;
import com.nd.hilauncherdev.plugin.navigation.widget.NavigationView2;

/**
 * 添加卡片<br/>
 *
 * @author chenzhihong_9101910
 */
public class CardAddActivity extends BaseActivity {

    private Context mContext;
    private ListView mCardList;
    private CardAddAdapter mCardAdapter;
    private View mCardAllAddedV;
    private View cardAddedNoticeV;
    private View viewAddedCardV;
    private boolean isShowManageButton = false;
    private HeaderView headView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        isShowManageButton = getIntent().getBooleanExtra("is_show_manage_btn", false);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.navigation_card_add);
        headView = (HeaderView) findViewById(R.id.head_view);
        headView.setTitle("添加卡片");
        initMenuOption();
        headView.setGoBackListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mCardList = (ListView) findViewById(R.id.navi_card_list);
        mCardAllAddedV = findViewById(R.id.navi_card_all_added);
        cardAddedNoticeV = findViewById(R.id.navi_card_added_notice);
        cardAddedNoticeV.setVisibility(View.GONE);
        cardAddedNoticeV.setOnClickListener(mClickListener);
    }


    private void initMenuOption() {

        if (isShowManageButton) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.card_manage_header,null);
            headView.replaceMenu(view);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
					try {
						Intent intent = new Intent(mContext, CardManageActivity.class);
						mContext.startActivity(intent);
						PluginUtil.invokeSubmitEvent(NavigationView2.activity, AnalyticsConstant.NAVIGATION_SCREEN_CARDS_PULL_REFRESH, "xgl");
					}catch (Exception e){
						e.printStackTrace();
					}

                }
            });
            headView.setMenuVisibility(View.VISIBLE);
        }
    }

    private OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.navi_card_added_notice:
				finish();
				break;

			default:
				break;
			}
		}
	};

	private void initCard() {
		ArrayList<Card> cardS = CardManager.getInstance().getCardSCanBeAdded(mContext);
		ArrayList<Card> curCardL = CardManager.getInstance().getCurrentCardS(mContext);
		HashSet<Integer> curCardS = new HashSet<Integer>();
		for (Card card : curCardL) {
			curCardS.add(card.id);
		}

		for (Card card : cardS) {
			if (curCardS.contains(card.id)) {
				card.isAdded = true;
			} else {
				card.isAdded = false;
			}
		}

		mCardAdapter = new CardAddAdapter(CardAddActivity.this, cardS);
		mCardAdapter.isFromSubAdd = false;
		mCardList.setAdapter(mCardAdapter);
		mCardAdapter.notifyDataSetChanged();
		if (cardS.size() > 0) {
			mCardList.setVisibility(View.VISIBLE);
			mCardAllAddedV.setVisibility(View.GONE);
		} else {
			mCardList.setVisibility(View.GONE);
			mCardAllAddedV.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onResume() {
		initCard();
		super.onResume();
	}

	public void showCardAddedNotice() {
		cardAddedNoticeV.setVisibility(View.VISIBLE);
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				cardAddedNoticeV.setVisibility(View.GONE);
			}
		}, 5000);
	}

	public Handler handler = new Handler() {

	};
}
