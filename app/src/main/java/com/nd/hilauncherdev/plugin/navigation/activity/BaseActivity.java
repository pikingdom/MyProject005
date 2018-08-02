package com.nd.hilauncherdev.plugin.navigation.activity;

import android.os.Bundle;

import com.android.dynamic.plugin.PluginActivity;
import com.nd.hilauncherdev.plugin.navigation.util.Global;
import com.nd.hilauncherdev.plugin.navigation.util.reflect.ReflectInvoke;

/**
 * 基类Activity
 * 
 * @author chenzhihong_9101910
 * 
 */
public class BaseActivity extends PluginActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Global.initGlobalHandler();
		ReflectInvoke.initImageLoaderConfig(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}

}
