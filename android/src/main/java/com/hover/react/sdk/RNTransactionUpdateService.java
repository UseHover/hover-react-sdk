package com.hover.react.sdk;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.facebook.react.HeadlessJsTaskService;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.jstasks.HeadlessJsTaskConfig;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RNTransactionUpdateService extends HeadlessJsTaskService {
	private final String TAG = "RNUpdateService";

	@Nullable
	protected HeadlessJsTaskConfig getTaskConfig(Intent intent) {
		Log.e(TAG, "running headless js task");

		// FIXME: Is this ok? seems a bit odd, but the api for dev is easy
		// Probably won't work when app/JS bridge is not running. What to do then?
		// Should dev just implement the android transaction receiver if they care?
		try {
			// Bundle extras = intent.getExtras();
			Log.e(TAG, "emitting event");
			getReactNativeHost().getReactInstanceManager().getCurrentReactContext()
				.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
				.emit("transaction_update", convertBundleToWritableNativeMap(intent.getExtras()));
			Log.e(TAG, "done");
		} catch (Exception e) { Log.e(TAG, e.getMessage()); }

		return null;
	}

	// These are the intent extras supplied by the Hover SDK
	//	uuid, action_id, response_message, status, status_meaning, status_description, environment,
	//	sim_hni, request_timestamp, update_timestamp, transaction_extras, ussd_messages,
	//	matched_parser_id, message_type, response_number, regex
	public static WritableNativeMap convertBundleToWritableNativeMap(Bundle bundle) {
		WritableNativeMap params = new WritableNativeMap();
		for (String key : bundle.keySet()) {
			if (bundle.get(key) != null) {
				try {
					if (key.equals("transaction_extras")) {
						//noinspection unchecked
						params.putMap(key, convertHashMapToWritableNative((HashMap<String, String>) bundle.get(key)));
					} else if (key.equals("ussd_messages")) {
						params.putArray(key, convertArrToWritableNative(bundle.getStringArray(key)));
					} else if (!bundle.get(key).toString().isEmpty())
						params.putString(key, bundle.get(key).toString());
				} catch (Exception ignore) { }
			}
		}
		return params;
	}

	public static WritableNativeMap convertHashMapToWritableNative(HashMap<String, String> extras) {
		WritableNativeMap map = new WritableNativeMap();
		for (Map.Entry<String, String> entry : extras.entrySet())
			map.putString(entry.getKey(), entry.getValue());
		return map;
	}

	public static WritableNativeArray convertArrToWritableNative(String[] messages) {
		WritableNativeArray arr = new WritableNativeArray();
		for (String msg: messages)
			arr.pushString(msg);
		return arr;
	}
}