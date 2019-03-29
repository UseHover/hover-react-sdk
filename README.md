
# react-native-hover-react-sdk

This SDK is in alpha, and currently supports only the most basic Hover functionality: Getting the required permissions, starting a USSD session, getting the results of the session, parsing the result and passing the parsed result of any USSD or SMS response back. After initial set-up all coding can be done in javascript, however the setup does require your react project to include android native source, which means you need to use the react native cli, not expo.

For bug reports or feature requests you can submit an issue to this repo or contact us at support@usehover.com

## Installation

We will eventually add this to npm, but for now just clone this repo.

Inside this project directory run
  ```
  $ npm install --save-dev
  ```

Inside your react native project directory:
1. Run 
  ```
  $ npm install PATH_TO/react-native-hover-react-sdk --save-dev
  ```

2. Open your project's `package.json` and add `"react-native-hover-react-sdk": "file:PATH_TO/react-native-hover-react-sdk"` to the dependencies section.
  
3. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-hover-react-sdk'
  	project(':react-native-hover-react-sdk').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-hover-react-sdk/android')
  	```
    
4. Insert the following lines inside the allprojects > repositories block in `android/app/build.gradle`:
  ```
  maven { url 'http://maven.usehover.com/releases' }
  ```
    
5. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  ```
  implementation project(':react-native-hover-react-sdk')
  ```
  
6. Open `android/app/src/main/java/[...]/MainApplication.java`
  - Add `import com.hover.react.sdk.RNHoverReactSdkPackage;` to the imports at the top of the file
  - Add `new RNHoverReactSdkPackage()` to the list returned by the `getPackages()` method
  
7. Open `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.hover.react.sdk.RNHoverReactSdkModule;` to the imports at the top of the file
  - Add `RNHoverReactSdkModule.initializeHover(this.getApplicationContext());` at the end of the onCreate method


## Getting Started

In the javascript file where you want to use Hover, for example App.js:

```javascript
import { NativeModules, NativeEventEmitter } from 'react-native';
const RNHoverReactSdk = NativeModules.RNHoverReactSdk;
```
Inside your component:

To check if the Hover required permissions have been granted (READ_PHONE_STATE, RECEIVE_SMS, SYSTEM_ALERT_WINDOW, and BIND_ACCESSIBILITY_SERVICE:
```
var areGranted = await RNHoverReactSdk.hasAllPermissions();
console.log("granted: " + areGranted);
```

To start the Hover permission request helper:
```
  try {
    var areGranted = await RNHoverReactSdk.getPermission();
  } catch (e) {
    areGranted = false;
  }
  console.log("granted: " + areGranted);
```

To start a Hover request:
```
  try {
    extras = {"amount": "100", "recipient": "43214324"}; // Whatever variables you specified when creating your action
    var response = await RNHoverReactSdk.makeRequest(ACTION_ID, extras);
    console.log("got response: " + response.response_message);
    console.log("transaction uuid: " + response.uuid);
  } catch (e) {
    console.log("request failed or cancelled. Reason: " + e.message);
  }
```

If you have created a parser in your Hover dashboard then you need add your event listener BEFORE starting the Hover request. The best place for this is in your component's `componentWillMount` function:
```
async onHoverParserMatch(data) {
  console.log("received update for transaction with uuid: " + data.uuid);
}

componentWillMount() {
  const transactionEmitter = new NativeEventEmitter(RNHoverReactSdk)
  const subscription = transactionEmitter.addListener("transaction_update", (data) => this.onHoverParserMatch(data));
}
```
  
