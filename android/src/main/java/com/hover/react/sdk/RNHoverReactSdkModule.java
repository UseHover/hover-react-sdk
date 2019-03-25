package com.hover.react.sdk;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.UiThreadUtil;
import com.hover.sdk.api.Hover;
import com.hover.sdk.permissions.PermissionActivity;

public class RNHoverReactSdkModule extends ReactContextBaseJavaModule {
	private final String TAG = "RNHoverReactSdkModule";

	private final ReactApplicationContext reactContext;

	public RNHoverReactSdkModule(ReactApplicationContext reactContext) {
		super(reactContext);
		this.reactContext = reactContext;
	}

	@Override
	public String getName() {
		return "RNHoverReactSdk";
	}

	@ReactMethod
	public static void initializeHover(Context c) {
		Hover.initialize(c);
	}

	@ReactMethod
	public void getPermission() {
		Log.d(TAG, "getting permissions");
		Intent i = new Intent(reactContext, PermissionActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//    UiThreadUtil.runOnUiThread(new Runnable() {
		reactContext.startActivity(i);
	}
}