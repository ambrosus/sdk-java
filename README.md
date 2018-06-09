# Ambrosus Android SDK

The Ambrosus Android SDK makes it easy for Android App Developers to get back data from the [Ambrosus API](https://ambrosus.docs.apiary.io) (AMB-NET), and build their own interfaces for displaying Assets and Events.

The Ambrosus Android SDK is currently written in Kotlin. It's as well compatible with Java code and can be used in Java based Android projects. 

Supports Android 5 and above
Supports Java and Kotlin

* [Integration](#integration)
* [Overview](#overview)
* [Usage](#usage)
* [Sample Application (Ambrosus Viewer)](#sample-application-ambrosus-viewer)
* [Ambrosus Viewer Support](#ambrosus-viewer-support)
* [Sample Symbologies](#sample-symbologies)

## Integration

To start using the SDK you should link the sdk project in your gradle file:

```gradle
include ':ambrosussdk'
```

If you have copied the sdk in `ambrosussdk` folder.

You have to download and copy the sdk fodler in your project.

## Overview

The SDK is composed of couple main files. It uses Retrofit-2 to handle the communication with [Ambrosus API](https://ambrosus.docs.apiary.io).

`AMBModel.kt` - is located in `src/java/com/ambrosus/ambrosussdk/model` folder. It defines the main classes which are storing the data retrieved from the API.

* AMBModel - base class
* AMBAsset - particular asset
* AMBEvent - particular event

`AMBDataStore.kt` - is a class which implements a singleton pattern. It can be used to keep local cache of the fetched object.

`AMBNetwork.kt` - is located in `src/java/com/ambrosus/ambrosussdk/network` folder. The `AMBNetwork` class which is responsible to do communication with the backend.

`AMBService.kt` - is an interface which defines the endpoints that are available on the backend.  

There is one utility file `SectionFormatter.kt` which defines `Section` a separate element that stores information and `SectionFormatter` which can convert the JSON from the server to a list of `Section`s.

## Usage

To start using the Ambrosus SDK within a project you should import the specific classes
```kotlin
//TBD
```

To get back an asset from the API you can make a call like the following using an EAN8 code:

```kotlin
AMBNetwork.instance.requestAsset("data[identifiers.ean8]", "96385074", { 
//it is AMBAsset or null
})
```

or using the assetId 

```kotlin
AMBNetwork.instance.requestAsset("0x74d3723909b15275791d1d0366c9627ee4c6e4f9982f31233d0dd6c054e5b664", { 
//it is AMBAsset or null
})
```

A single Asset in the Ambrosus SDK has many events associated with it. All events will be fetched for you and added to the asset.

There is no separate function which can be used to fetch only the events.

# Sample Application (Ambrosus Viewer)

Ambrosus Viewer is an Android application that uses the Ambrosus API <https://dev.ambrosus.com> combined with scanning technology to allow users to scan a Bar Code, QR Code, [or other 1D or 2D symbology - in development] and get details about an item moving through a supply chain.

Learn about scanned item, origins and other details such as temperature, weight, creation date, and more. See a timeline detailing all things that happened to the asset from the date of its creation to it arriving in stores. 

To use the scanner in the Ambrosus Viewer you need a [Scandit](https://scandit.com) API key, you can sign up for a 30 day trial here:
https://ssl.scandit.com/customers/new?p=test  

The key can be replaced inside the `ViewerFragment.kt` on line 245:
```kotlin
private const val sScanditSdkAppKey = "[YOUR SCANDIT KEY HERE]"
```

## Supported OS & SDK Versions

* Supports Android 5 and above devices which have BLE.
* Requires Camera permission enabled in order to scan codes
* Requires internet permission to send the data to the API.
* Capable of scanning codes with the following symbologies:
* UPCE, UPC12, EAN8, EAN13, CODE 39, CODE 128, ITF, QR, DATAMATRIX

## Sample Symbologies

To see details about sample assets with the Ambrosus Viewer, scan any of the following codes from the app:

|   EAN-8   |   EAN-13   |     QR     |
| --------- | ---------------------------------- | ---------- |
| &emsp;&emsp;![EAN-8 Sample](https://i.imgur.com/m7QZIaS.png)   | &emsp;&emsp;![EAN-13 Sample](https://i.imgur.com/1HXwtPr.png) | &emsp;&emsp;![QR Sample](https://i.imgur.com/JfEUGo8.png)&emsp;&emsp;
|  <a href="https://gateway-test.ambrosus.com/events?data[type]=ambrosus.asset.identifier&data[identifiers.ean8]=96385074" target="_blank">Generic Asset</a>&emsp;  | <a href="https://gateway-test.ambrosus.com/events?data[type]=ambrosus.asset.identifier&data[identifiers.ean13]=6942507312009" target="_blank">Guernsey Cow</a>&emsp;&emsp; | &emsp;&emsp;&emsp;&emsp;<a href="https://gateway-test.ambrosus.com/assets/0x4c289b68b5bb1a098a4aa622b84d6f523e02fc9346a3a0a99efdfd8a96ba56df" target="_blank">Ibuprofen Batch 200mg</a>&emsp;&emsp;
