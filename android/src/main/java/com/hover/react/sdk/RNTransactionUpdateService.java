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
				.emit("transaction_update", convertTinfoToWritableNativeMap(intent.getExtras()));
			Log.e(TAG, "done");
		} catch (Exception e) { Log.e(TAG, e.getMessage()); }

		return null;
	}

	private WritableNativeMap getDetails(Intent i) {
		WritableNativeMap params = new WritableNativeMap();
		params.putString("uuid", i.getStringExtra("uuid"));
		params.putString("action_id", i.getStringExtra("action_id"));
		params.putString("response_message", i.getStringExtra("response_message"));
		params.putString("status", i.getStringExtra("status"));
		params.putString("status_meaning", i.getStringExtra("status_meaning"));
		params.putString("status_description", i.getStringExtra("status_description"));
		params.putString("environment", i.getStringExtra("environment"));
		params.putString("sim_hni", i.getStringExtra("sim_hni"));
		params.putString("request_timestamp", i.getStringExtra("request_timestamp"));
		params.putString("update_timestamp", i.getStringExtra("update_timestamp"));

		params.putString("extras", i.getStringExtra("request_timestamp"));
		params.putString("ussd_messages", i.getStringExtra("update_timestamp"));

		params.putString("matched_parser_id", i.getStringExtra("matched_parser_id"));
		params.putString("message_type", i.getStringExtra("message_type"));
		params.putString("response_number", i.getStringExtra("response_number"));
		params.putString("regex", i.getStringExtra("regex"));

		return params;
	}

	private WritableNativeMap convertTinfoToWritableNativeMap(Bundle bundle) {
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

	private WritableNativeMap convertHashMapToWritableNative(HashMap<String, String> extras) {
		WritableNativeMap map = new WritableNativeMap();
		for (Map.Entry<String, String> entry : extras.entrySet())
			map.putString(entry.getKey(), entry.getValue());
		return map;
	}

	private WritableNativeArray convertArrToWritableNative(String[] messages) {
		WritableNativeArray arr = new WritableNativeArray();
		for (String msg: messages)
			arr.pushString(msg);
		return arr;
	}
}