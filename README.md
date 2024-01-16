# flutter_adyen_dropin_plugin

Note: This library is not official from Adyen.

Flutter plugin to integrate with the Android and iOS libraries of Adyen.
This library enables you to open the **Drop-in** method of Adyen with just calling one function.

* [Adyen drop-in Android](https://docs.adyen.com/checkout/android/drop-in)
* [Adyen drop-in iOS](https://docs.adyen.com/checkout/ios/drop-in)

The Plugin supports 3dSecure v2 and one time payment. It was not tested in a recurring payment scenario.

## Prerequisites

### Credentials
#### You need to have the following information:
* clientKey (from Adyen)
* amount & currency 
* sessionData (e.g userId)
* Adyen Environment (TEST, LIVE_EUROPE etc..)
* countryCode (de-DE, en-US etc..)
* session info (all response after request [/sessions](https://docs.adyen.com/api-explorer/Checkout/71/post/sessions)) use for Android
* sessionData (after request [/sessions](https://docs.adyen.com/api-explorer/Checkout/71/post/sessions) you can get it)

## Setup

### Android

In the MainActivity.java in your application extend FlutterFragmentActivity and add these line to onCreate, this allows adyen to get the Activity of your app.

```
@Override
protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    AdyenSetup.setActivity(this);
    AdyenSetup.setLauncherActivity(this);
}
``` 

#### Build Gradle
You need to set minSdkVersion and targetSdkVersion, compileSdkVersion (required by latest Adyen)

```  
android {
    ...
    compileSdkVersion 34

    defaultConfig {
        ...
        minSdkVersion 24
        targetSdkVersion 34
    }
}
```

### iOS
You need to add a URL_SCHEME if you do not have one yet.

[Here is how to add one.](https://developer.apple.com/documentation/uikit/inter-process_communication/allowing_apps_and_websites_to_link_to_your_content/defining_a_custom_url_scheme_for_your_app)

#### Target
You need to change your app target to iOS 13 or higher (required by latest Adyen)


## Flutter Implementation
To start a Payment you need to call the plugin like so:

```
String dropInStatus = '';
try {
    dropInStatus = await FlutterAdyenDropIn.openDropIn(
    sessionData: "<YOUR-SESSION-DATA>",
    clientKey: "<YOUR-CLIENT-KEY>",
    currency: "USD",
    value: 1000,
    sessionId: "<YOUR-SESSION-ID>",
    countryCode: "en-US",
    sessionInfo: {"FIELD": "<YOUR-SESSION-INFO>"},
    );
} catch (e) {
    print(e);
}
```
## Payment Status
All the status of the payment you can receive. Check [here](https://docs.adyen.com/payment-methods/cash-app-pay/android-drop-in/#show-result) for more detail

```
PAYMENT_CANCELED (when you close the pop up)
Authorised
Cancelled
Error
Refused
```