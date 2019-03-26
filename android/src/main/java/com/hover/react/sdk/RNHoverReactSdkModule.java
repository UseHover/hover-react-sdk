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
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.hover.sdk.api.Hover;
import com.hover.sdk.api.HoverParameters;
import com.hover.sdk.permissions.PermissionActivity;

import io.sentry.Sentry;

public class RNHoverReactSdkModule extends ReactContextBaseJavaModule {
	private final String TAG = "RNHoverReactSdkModule";
	private final int PERM_REQUEST = 0, SESSION_REQUEST = 1;

	private final ReactApplicationContext reactContext;
	private Promise permPromise, sessionPromise;

	public RNHoverReactSdkModule(ReactApplicationContext reactContext) {
		super(reactContext);
		this.reactContext = reactContext;
		reactContext.addActivityEventListener(activityEventListener);
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

	@ReactMethod
	public void getPermission(Promise promise) {
		Log.e(TAG, "getting permissions");

		Activity currentActivity = getCurrent(promise);
		if (currentActivity != null) {
			try {
				permPromise = promise;
				currentActivity.startActivityForResult(
					new Intent(reactContext, PermissionActivity.class), PERM_REQUEST);
			} catch (Exception e) {
				permPromise.reject(e);
				permPromise = null;
			}
		}
	}

	private void onPermissionResult(int resultCode) {
		Log.e(TAG, "got permission result");
		if (resultCode == Activity.RESULT_CANCELED) {
			permPromise.reject("Denied");
		} else if (resultCode == Activity.RESULT_OK) {
			permPromise.resolve("Success!");
		}
		permPromise = null;
	}

	@ReactMethod
	public void makeRequest(String actionId, Promise p) {
		Activity currentActivity = getCurrent(p);
		if (currentActivity != null) {
			try {
				sessionPromise = p;
				startHover(currentActivity, actionId);
			} catch (Exception e) {
				sessionPromise.reject(e);
				sessionPromise = null;
			}
		}
	}

	private void startHover(Activity currentActivity, String actionId) {
		Log.e(TAG, "starting Hover");
		Intent i = new HoverParameters.Builder(currentActivity)
			.request(actionId)
			// .extra(“action_step_variable_name”, variable_value)
			.buildIntent();
		currentActivity.startActivityForResult(i, SESSION_REQUEST);
	}

	private void onSessionResult(int resultCode, Intent i) {
		Log.e(TAG, "got session result");
		if (resultCode == Activity.RESULT_CANCELED) {
			sessionPromise.reject("Denied");
		} else if (resultCode == Activity.RESULT_OK) {
			sessionPromise.resolve("Success!");
		}
		sessionPromise = null;
	}

	private final ActivityEventListener activityEventListener = new BaseActivityEventListener() {
		@Override
		public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intent) {
			Log.e(TAG, "got activity result");
			if (requestCode == PERM_REQUEST && permPromise != null) {
				onPermissionResult(resultCode);
			} else if (requestCode == SESSION_REQUEST && sessionPromise != null) {
				onSessionResult(resultCode, intent);
			}
		}
	};

	public void sendEvent(String event, WritableNativeMap params) {
		reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
			.emit(event, params);
	}

	private Activity getCurrent(Promise p) {
		Activity currentActivity = getCurrentActivity();
		if (currentActivity == null)
			p.reject("Activity doesn't exist");
		return  currentActivity;
	}

	private String getPackageName() {
		try {
			return reactContext.getApplicationContext().getPackageName();
		} catch (NullPointerException e) {
			Sentry.capture(e);
			return "fail";
		}
	}
}