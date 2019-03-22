
# react-native-hover-react-sdk

## Getting started

`$ npm install react-native-hover-react-sdk --save`

### Mostly automatic installation

`$ react-native link react-native-hover-react-sdk`

### Manual installation


#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.hover.react.sdk.RNHoverReactSdkPackage;` to the imports at the top of the file
  - Add `new RNHoverReactSdkPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-hover-react-sdk'
  	project(':react-native-hover-react-sdk').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-hover-react-sdk/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-hover-react-sdk')
  	```


## Usage
```javascript
import RNHoverReactSdk from 'react-native-hover-react-sdk';

// TODO: What to do with the module?
RNHoverReactSdk;
```
  