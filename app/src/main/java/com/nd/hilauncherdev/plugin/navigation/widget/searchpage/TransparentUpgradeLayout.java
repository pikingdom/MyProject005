package com.nd.hilauncherdev.plugin.navigation.widget.searchpage;

import android.content.Context;
import android.util.AttributeSet;

import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.widget.upgrade.UpgradeLayout;

/**
 * 透明展示的更新条
 * Created by linliangbin on 2017/8/9 20:20.
 */

public class TransparentUpgradeLayout extends UpgradeLayout {
    public TransparentUpgradeLayout(Context context) {
        super(context);
    }

    public TransparentUpgradeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_transparent_upgrade_plugin;
    }
}
