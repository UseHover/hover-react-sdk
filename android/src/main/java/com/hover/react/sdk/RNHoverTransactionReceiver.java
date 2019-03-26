package com.hover.react.sdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.facebook.react.HeadlessJsTaskService;

public class RNHoverTransactionReceiver extends BroadcastReceiver {
	private final String TAG = "RNHoverReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e(TAG, "received Transaction broadcast");

		Intent i = new Intent(context, RNTransactionUpdateService.class);
		i.putExtras(intent);
		context.startService(i);
		HeadlessJsTaskService.acquireWakeLockNow(context);
	}
}