
package com.hover.react.sdk;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

public class RNHoverReactSdkModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  public RNHoverReactSdkModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNHoverReactSdk";
  }
}