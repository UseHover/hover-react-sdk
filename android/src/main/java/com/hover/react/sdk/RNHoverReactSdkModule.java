package com.hover.react.sdk;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.hover.sdk.api.Hover;
import com.hover.sdk.permissions.PermissionActivity;

public class RNHoverReactSdkModule extends ReactContextBaseJavaModule {
	private final String TAG = "RNHoverReactSdkModule";
	private final int PERM_REQUEST = 0;

	private final ReactApplicationContext reactContext;
	private Promise mPermPromise;

	public RNHoverReactSdkModule(ReactApplicationContext reactContext) {
		super(reactContext);
		this.reactContext = reactContext;
		reactContext.addActivityEventListener(mActivityEventListener);
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
	public void getPermission(Promise promise) {
		Log.e(TAG, "getting permissions");
		Activity currentActivity = getCurrentActivity();
		if (currentActivity == null) {
			promise.reject("Activity doesn't exist");
			return;
		}

		mPermPromise = promise;
		try {
			currentActivity.startActivityForResult(
				new Intent(reactContext, PermissionActivity.class), PERM_REQUEST);
		} catch (Exception e) {
			mPermPromise.reject(e);
			mPermPromise = null;
		}
	}

	private void onPermissionResult(int resultCode) {
		if (resultCode == Activity.RESULT_CANCELED) {
			mPermPromise.reject("Denied");
		} else if (resultCode == Activity.RESULT_OK) {
			mPermPromise.resolve("Success!");
		}
		mPermPromise = null;
	}

	private final ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {
		@Override
		public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intent) {
			Log.e(TAG, "result: " + resultCode);
			Log.e(TAG, "mPermPromise: " + mPermPromise);
			if (requestCode == PERM_REQUEST && mPermPromise != null) {
				onPermissionResult(resultCode);
			}
		}
	};

	@ReactMethod
	public void showToast(String message) {
		Log.e(TAG, "toast called. " + message);
		Toast.makeText(getReactApplicationContext(), message, Toast.LENGTH_LONG).show();
	}

	@ReactMethod
	public void hasAllPermissions(Promise p) {
//		promise.resolve(true);
//		Hover.hasAllPerms(reactContext); // Added in 0.17.0
		p.resolve(Build.VERSION.SDK_INT < 23
			|| ((ContextCompat.checkSelfPermission(reactContext, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED &&
				ContextCompat.checkSelfPermission(reactContext, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
			&& ContextCompat.checkSelfPermission(reactContext, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED)
			&& Hover.isAccessibilityEnabled(reactContext) && Hover.isOverlayEnabled(reactContext));
	}
}