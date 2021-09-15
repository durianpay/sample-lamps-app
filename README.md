# Durianpay Android Sample App

This repository demonstrates how to integrate Durianpay's Android SDK.

The documentation to our Android SDK is available[here](https://durianpay.id/docs/mobile/android-native/)

## Integration

The checkout procedure is present in ProductActivity.kt file

Add the following lines in project level `android/build.gradle` file to add a repository to get the android sdk:
```
allprojects {
    repositories {
        maven {
            name = "AndroidPackages"
            url = uri("https://maven.pkg.github.com/PavanDevara/durianpaysdk")
            credentials {
                username = "PavanDevara"
                password = "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"
            }
        }
    }
}
```

Add this line to your app's build.gradle inside the dependencies section:
```
implementation 'id.durianpay.android:durianpaysdk:1.3.1@aar'
```

Check again on all .gradle and ProductActivity.kt and Constants.kt file for secret keys