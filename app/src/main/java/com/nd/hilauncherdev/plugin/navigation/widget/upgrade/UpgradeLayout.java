package com.nd.hilauncherdev.plugin.navigation.widget.upgrade;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.UpgradeHelper;
import com.nd.hilauncherdev.plugin.navigation.http.DownloadHelper;
import com.nd.hilauncherdev.plugin.navigation.http.DownloadState;
import com.nd.hilauncherdev.plugin.navigation.util.TelephoneUtil;
import com.nd.hilauncherdev.plugin.navigation.widget.navigation.BaseNavigationSearchView;

/**
 * Created by linliangbin on 2017/6/8 21:01.
 */

public class UpgradeLayout extends RelativeLayout implements View.OnClickListener {


    private Context context;
    private View naviUpdateAllV;
    private TextView updateInfoV;
    private View updateDownloadV;
    private View updatePauseV;
    private View updateCancelV;
    private Animation enterAnimation;
    private int downloadState = -1;
    private CancelCallback cancelCallback;
    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DownloadHelper.MSG_DOWNLOADING:
                    setUpgradeDownloading(msg);
                    break;
                case DownloadHelper.MSG_DOWNLOAD_PAUSE:
                    setUpgradeDownloadPause();
                    break;
                case DownloadHelper.MSG_DOWNLOAD_CANCEL:
                    setUpgradeDownloadCancel();
                    break;
                case DownloadHelper.MSG_DOWNLOAD_FINISHED:
                    setUpgradeDownloadFinished();
                    break;

                default:
                    break;
            }
        }
    };
    private BroadcastReceiver progressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            UpgradeHelper.onDownloadProgressReceive(context, handler, intent);
        }
    };
    public UpgradeLayout(Context context) {
        super(context);
        init();
    }

    public UpgradeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setCancelCallback(CancelCallback cancelCallback) {
        this.cancelCallback = cancelCallback;
    }

    public int getLayoutId() {
        return R.layout.layout_upgrade_plugin;
    }

    private void init() {
        this.context = getContext();
        LayoutInflater.from(getContext()).inflate(getLayoutId(), this);
        initUpdateV();
    }

    private void initUpdateV() {
        naviUpdateAllV = findViewById(R.id.navi_plugin_update);
        updateInfoV = (TextView) findViewById(R.id.navi_plugin_update_info);
        updateDownloadV = findViewById(R.id.navi_download_v);
        updatePauseV = findViewById(R.id.navi_pause_v);
        updatePauseV.setOnClickListener(this);
        updateCancelV = findViewById(R.id.navi_cancel_v);
        updateCancelV.setOnClickListener(this);
    }

    protected void setUpgradeDownloadFinished() {
        updateDownloadV.setVisibility(View.GONE);
        updateCancelV.setVisibility(View.GONE);
        updatePauseV.setVisibility(View.GONE);

        //3s 后自动重启桌面
        final Handler handler = new Handler() {
            int currentTime = 3;

            @Override
            public void handleMessage(Message msg) {
                updateInfoV.setText(String.format("更新完毕，%d 秒后重启桌面加载更新...", currentTime--));
                if (currentTime < 0) {
                    System.exit(0);
                }
                sendEmptyMessageDelayed(0, 1000);
            }
        };
        handler.sendEmptyMessage(0);
        naviUpdateAllV.setOnClickListener(this);
    }

    protected void setUpgradeDownloadCancel() {
        naviUpdateAllV.setVisibility(View.GONE);
        if (cancelCallback != null) {
            cancelCallback.onCancleUpgrade();
        }
    }

    protected void setUpgradeDownloadPause() {
        updateDownloadV.setVisibility(View.VISIBLE);
        updateCancelV.setVisibility(View.VISIBLE);
        updatePauseV.setVisibility(View.GONE);
    }

    protected void setUpgradeDownloading(Message msg) {
        if (downloadState == DownloadState.STATE_PAUSE) {
            return;
        }

        if (msg.obj != null && ((int) msg.obj) != 0) {
            updateInfoV.setText("卡片更新中..." + String.valueOf(msg.obj) + "%");
        }

        updateDownloadV.setVisibility(View.GONE);
        updateCancelV.setVisibility(View.VISIBLE);
        updatePauseV.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        if (v != null) {
            switch (v.getId()) {

                case R.id.navi_pause_v:
                    DownloadHelper.pauseDownloadTask(getContext(), UpgradeHelper.id);
                    downloadState = DownloadState.STATE_PAUSE;
                    setUpgradeDownloadPause();
                    break;
                case R.id.navi_cancel_v:
                    DownloadHelper.cancelDownloadTask(getContext(), UpgradeHelper.id);
                    setUpgradeDownloadCancel();
                    break;

                case R.id.navi_plugin_update:
                    System.exit(0);
                    break;

            }
        }
    }

    public void upgradePlugin(final String url, final int ver, final boolean isWifiAutoDownload) {
        if (TelephoneUtil.isWifiEnable(getContext()) && isWifiAutoDownload) {
            naviUpdateAllV.setVisibility(View.GONE);
            UpgradeHelper.upgradePlugin(context, url, ver);
            updateDownloadV.setVisibility(View.GONE);
            downloadState = DownloadState.STATE_DOWNLOADING;

        } else {

            naviUpdateAllV.setVisibility(View.VISIBLE);
            updateDownloadV.setVisibility(View.VISIBLE);
            updateInfoV.setText(getContext().getResources().getString(R.string.navigation_upgrade_tips));
            updateCancelV.setVisibility(View.GONE);
            updatePauseV.setVisibility(View.GONE);
            context.registerReceiver(progressReceiver, new IntentFilter(context.getPackageName() + DownloadHelper.ACTION_PROGRESS_CHANGED));
            updateDownloadV.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TelephoneUtil.isNetworkAvailable(BaseNavigationSearchView.activity)) {
                        Toast.makeText(BaseNavigationSearchView.activity, "更新失败，网络连接好像出了点问题哦", Toast.LENGTH_SHORT).show();
                    }

                    if (downloadState == DownloadState.STATE_PAUSE) {
                        DownloadHelper.continueDownloadTask(context, UpgradeHelper.id);
                    } else {
                        UpgradeHelper.upgradePlugin(context, url, ver);
                    }

                    updateDownloadV.setVisibility(View.GONE);
                    updatePauseV.setVisibility(View.VISIBLE);
                    updateInfoV.setText("卡片更新中...");
                    downloadState = DownloadState.STATE_DOWNLOADING;
                }
            });

        }
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        try {
            context.unregisterReceiver(progressReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public interface CancelCallback {
        public void onCancleUpgrade();
    }
}
