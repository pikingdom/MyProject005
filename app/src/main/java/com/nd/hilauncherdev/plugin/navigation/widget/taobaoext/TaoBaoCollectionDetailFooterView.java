package com.nd.hilauncherdev.plugin.navigation.widget.taobaoext;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;

import com.nd.hilauncherdev.plugin.navigation.R;

/**
 * Created by linxiaobin on 2017/9/12.
 */

public class TaoBaoCollectionDetailFooterView extends LinearLayout {
    public Button btnNext;

    public TaoBaoCollectionDetailFooterView(Context context) {
        super(context);
        init(context);
    }

    public TaoBaoCollectionDetailFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.taobao_collection_detail_footer, this);

        btnNext = (Button) findViewById(R.id.btn_tb_next_collection);
    }
}
