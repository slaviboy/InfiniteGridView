# InfiniteGridView Android
Simple library for creating infinite grid written in Kotlin

<img alt="cookie monster" src="https://github.com/slaviboy/InfiniteGridView/blob/master/screens/image11.png">
 
## About
The library contains a InfiniteGridView that is used to create a infinite grid that can have transformations applied to it, such as rotation, translation and scaling using the finger gestures made by the user. It is generating, only the lines that are visible by the user and displayed in the view. Lines are generated in real time using simple math algorithms that can be found on the official [Wiki](https://github.com/slaviboy/InfiniteGridView/wiki) page.

[![Platform](https://img.shields.io/badge/platform-android-green.svg)](http://developer.android.com/index.html)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21)
[![Download](https://img.shields.io/badge/version-0.1.0-blue)](https://github.com/slaviboy/InfiniteGridView/releases)

## Add to your project

Add the jitpack maven repository
```
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
``` 
Add the dependency
```
dependencies {
  implementation 'com.github.slaviboy:InfiniteGridView:v0.1.0'
}
```

## How to use
 
To create a InfiniteGridView just include the code below in your main activity layout file.

```xml
<com.slaviboy.infinitegridview.InfiniteGridView
     android:layout_width="match_parent"
     android:layout_height="match_parent"
     android:background="#46ADE8"
     app:horizontalNormalLinesColor="#ffffff"
     app:horizontalThickLinesColor="#ffffff"
     app:verticalNormalLinesColor="#ffffff"
     app:verticalThickLinesColor="#ffffff" />
```
For supported properties and more about how to customize the grid, check the section on the [Wiki](https://github.com/slaviboy/InfiniteGridView/wiki#properties) page.
