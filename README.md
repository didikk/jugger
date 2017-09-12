Jugger
=======

A library to control your Android Apps.

Feature:
1. Check if update is available.
2. Force your users to use the latest version.
3. Shut down your apps for several time.


Download
--------

Because this project is using firebase, so you need to add google-service classpath. Add the following code to your build.gradle project level.
```groovy
dependencies {
  // Another dependencies
  classpath 'com.google.gms:google-services:3.1.0'
}
```
Then, in your module Gradle file (usually the app/build.gradle), add the following code:
```groovy
repositories {
    maven { url "https://jitpack.io" }
}

dependencies {
    compile 'com.github.didikk:jugger:1.0.1'

}
// ADD THIS AT THE BOTTOM
apply plugin: 'com.google.gms.google-services'
```


Introduction
--------

1. Create Firebase project http://firebase.google.com 
2. Firebase will give you google-services.json file. Paste this file into your projects root directory.
3. In the left menu choose remote config and add these parameters.
4. Add these parameters

| Parameter Key  | Default Value |
| ------------- | ------------- |
| is_active  | Is your apps active (Ex: false/true)  |
| app_message  | Message if your apps is inactive for a reason (Ex: App is under maintenance, please wait for a moment.)  |
| update_message  | Message if newer version is available (Ex: Newer version is available, please update apps to get the best service.)  |
| is_mandatory  | Force your users to update to the latest version (Ex: false/true)  |
| version_code  | Version code of your apps (Ex: 12)  |

5. Publish Changes (Top right corner)

Usage
--------

###### Basic Usage

```java
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Jugger.with(this)
                .setPositiveTextColor("#3F51B5")
                .setNegativeTextColor("#FF4081")
                .check();
    }
}
```

