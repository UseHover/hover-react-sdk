package com.hover.react.sdk;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.facebook.react.HeadlessJsTaskService;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.jstasks.HeadlessJsTaskConfig;

public class RNTransactionUpdateService extends HeadlessJsTaskService {
	private final String TAG = "RNUpdateService";

	@Nullable
	protected HeadlessJsTaskConfig getTaskConfig(Intent intent) {
		Log.e(TAG, "running headless js task");
		Bundle extras = intent.getExtras();
		WritableNativeMap params = new WritableNativeMap();
		params.putString("transaction_uuid", intent.getStringExtra("uuid"));
		params.putString("text", intent.getStringExtra("response_message"));
		// ReactInstanceManager reactInstanceManager = getReactNativeHost().getReactInstanceManager();
		// ReactContext reactContext = reactInstanceManager.getCurrentReactContext();
		
		// reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("transaction_update", params);

		return new HeadlessJsTaskConfig("TransactionUpdate", params, 5000, true);
	}
}