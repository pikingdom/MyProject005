package com.nd.hilauncherdev.plugin.navigation.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.GridView;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.bean.GameBean;
import com.nd.hilauncherdev.plugin.navigation.loader.GameH5HistoryLoader;
import com.nd.hilauncherdev.plugin.navigation.widget.HeaderView;
import com.nd.hilauncherdev.plugin.navigation.widget.HistoryGameAdapter;
import java.util.ArrayList;

/**
 * Created by linliangbin_dian91 on 2015/11/9.
 * 游戏卡片H5游戏打开历史
 */
public class GameOpenHistoryAct extends BaseActivity {

    private Context mContext;
    private GridView historyGameGridView;
    private HistoryGameAdapter gridAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.navigation_card_game_history);
        HeaderView headView = (HeaderView) findViewById(R.id.head_view);
        headView.setTitle(this.getString(R.string.game_history));
        headView.setGoBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        historyGameGridView = (GridView) this.findViewById(R.id.game_h5_history_gridview);
        ArrayList<GameBean> beans = GameH5HistoryLoader.getOpenH5GameBean(mContext);
        if(beans.size() == 0){
            this.findViewById(R.id.layout_no_data).setVisibility(View.VISIBLE);
            this.findViewById(R.id.game_h5_history_gridview_layout).setVisibility(View.GONE);
            historyGameGridView.setVisibility(View.GONE);
        }else{
            this.findViewById(R.id.layout_no_data).setVisibility(View.GONE);
            this.findViewById(R.id.game_h5_history_gridview_layout).setVisibility(View.VISIBLE);
            historyGameGridView.setVisibility(View.VISIBLE);
            gridAdapter = new HistoryGameAdapter(mContext,beans);
            historyGameGridView.setAdapter(gridAdapter);
            gridAdapter.notifyDataSetChanged();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if(gridAdapter != null)
//            gridAdapter.releaseCache();
    }
}
