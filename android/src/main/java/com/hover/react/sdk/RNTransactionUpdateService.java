package com.hover.react.sdk;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.facebook.react.HeadlessJsTaskService;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.jstasks.HeadlessJsTaskConfig;
import com.facebook.react.modules.core.DeviceEventManagerModule;

public class RNTransactionUpdateService extends HeadlessJsTaskService {
	private final String TAG = "RNUpdateService";

	@Nullable
	protected HeadlessJsTaskConfig getTaskConfig(Intent intent) {
		Log.e(TAG, "running headless js task");
//		Bundle extras = intent.getExtras();
//		WritableNativeMap params = new WritableNativeMap();
//		params.putString("transaction_uuid", intent.getStringExtra("uuid"));
//		params.putString("text", intent.getStringExtra("response_message"));

		// FIXME: Is this ok? seems a bit odd, but the api for dev is easy
		try {
			getReactNativeHost().getReactInstanceManager().getCurrentReactContext()
				.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
				.emit("transaction_update", null);
		} catch (Exception e) { Log.e(TAG, e.getMessage()); }

		return null;
	}
}