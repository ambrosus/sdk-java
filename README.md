# Ambrosus Java/Android SDK

The Ambrosus Java/Android development kit makes it easy for developers to interact with the Ambrosus Network via the Ambrosus Node API (https://ambrosus.docs.apiary.io/#). It is designed to encapsulate most of the Ambrosus Network and Node API implementation details and to also allow third-party developers to focus on the underlying business logic implementation of their respective solutions. 

When it comes to software requirements, both Java/Android SDK versions require Java 8. Meanwhile, the Android version is also compatible with Android API 19+ (Android 4.4).

This document itself, provides an overview of the core classes and key features of the Java/Android SDK. It functions to introduce the key concepts of the Ambrosus Network, and to also provide a step by step guide to creating, retrieving, and searching for assets and events (among other features). The document begins by explaining how to get started with Java 6+. Next, an overview of the key concepts within the Ambrosus Network is provided (for those in need of a more thorough introduction to the basic concepts of the Ambrosus Network please refer to: https://tech.ambrosus.com/#core). Third, the key features of the Java/Android SDK are explained: such features range from retrieving assets and events by ID’s to using custom data models. Finally, to conclude the Ambrosus Viewer is included as a Demo App for prospective developers to make use of. 

* [Getting started](#getting-started)
  * [Java 8](#java-8)
    * [Gradle](#gradle)
	* [Maven](#maven)
  * [Android](#android)
* [Overview of core classes and key concepts](#overview-of-core-classes-and-key-concepts)
  * [Network](#network)
  * [Asset](#asset)
  * [Event](#event)
  * [Private Key](#private-key)
* [Key features](#key-features)
  * [Retrieve asset/event by ID](#retrieve-assetevent-by-id)
  * [Search for assets/events satisfying provided criteria](#search-for-assetsevents-satisfying-provided-criteria)
  * [Fetching next pages of a search result (pagination support)](#fetching-next-pages-of-a-search-result)
  * [Create assets and events](#create-assets-and-events)
  * [Create event with limited access and query content of this event](#create-event-with-limited-access-and-query-content-of-this-event)
  * [Configure Node API endpoint](#configure-node-api-endpoint)
  * [Using custom data models](#using-custom-data-models)
  * [Example: Search for information about item which is marked with “3451080000324” EAN13 barcode.](#example-search-for-information-about-item-which-is-marked-with-3451080000324-ean13-barcode)
* [Demo app (Ambrosus Viewer)](#demo-app-ambrosus-viewer)

## Getting started

### Java 8

#### Gradle

```gradle
repositories {
    maven { url 'https://oss.sonatype.org/content/groups/staging' }  
}

dependencies {
    implementation 'com.ambrosus.sdk:core:0.0.1'    
}
```

#### Maven

**<TBD>**

### Android

Add staging repository to the project level (root) build.gradle script

```gradle
allprojects {
    repositories {
        maven {url 'https://oss.sonatype.org/content/groups/staging'}
    }
}
```

Enable support for Java 8 features in app (module) level build.gradle

```gradle
android {
    compileOptions {
        targetCompatibility JavaVersion.VERSION_1_8
        sourceCompatibility JavaVersion.VERSION_1_8
    }
}
```

Declare dependency:

```gradle
dependencies {
    implementation 'com.ambrosus.sdk:core-android:0.0.1'
}
```

## Overview of core classes and key concepts

### Network

https://github.com/ambrosus/sdk-java-new/blob/master/sdk/src/main/java/com/ambrosus/sdk/Network.java

is a core class responsible for communication with AMB-NET. It contains a number of get*(...), find*(...) and push*(...) methods  which can be used to retrieve/push data models from/to AMB-NET eg.:

```java
String assetId = "0x88181e5e517df33d71637b3f906df2e27759fdcbb38456a46544e42b3f9f00a2";
Network network = new Network();
NetworkCall<Asset> networkCall = network.getAsset(assetId);
```

Each of these methods returns an instance of NetworkCall<ResultType> interface. It provides the same behaviour as 
**Call** (https://square.github.io/retrofit/2.x/retrofit/retrofit2/Call.html) 

interface from Retrofit library. So you have to perform this network call to get an actual data model of ResultType. You can do it synchronously by calling NetworkCall.execute() method or asynchronously by passing an instance of **NetworkCallBack<ResultType>** to **NetworkCall.enqueue(NetworkCallBack<ResultType> callback)** method. E.g:

```java
//Synchronous execution   
 
try {
    Asset asset = networkCall.execute();
    System.out.println(asset.getSystemId());
} catch (Throwable t) {
    throw new RuntimeException(t);
}

//Asynchronous execution

networkCall.enqueue(new NetworkCallback<Asset>() {
    @Override
    public void onSuccess(@NonNull NetworkCall<Asset> call, @NonNull Asset asset) {
        //request was performed successfully
        System.out.println(asset.getSystemId());
    }

    @Override
    public void onFailure(@NonNull NetworkCall<Asset> call, @NonNull Throwable error) {
        //request failed because of (Throwable error)
    }
});
```

On Android, callbacks will be executed on the main thread. On the JVM, callbacks will happen on a thread responsible for network communication. 

All samples below will execute network calls synchronously in order to minimize the amount of sample code. It is important to keep in mind that you cannot execute these calls synchronously on the Android main thread because it would lead to  
**NetworkOnMainThreadException** (https://developer.android.com/reference/android/os/NetworkOnMainThreadException) 

### Asset

(https://github.com/ambrosus/sdk-java-new/blob/master/sdk/src/main/java/com/ambrosus/sdk/Asset.java)

Assets are the primary objects of analysis being monitored or traced over time; a stationary water sensor, a logistics pallet, a crate of milk, a steak, etc. As the ‘nouns’ of the system, Assets can represent an ingredient, product, package of products or any other type of container.
Importantly, an Asset functions as a handle of Events and possesses an idData structure containing the following pieces of information:

* The AMB-ID of the Asset 
* User Address
* Minimal Access Level Required to View the Private Data 
* Timestamp 
* Hash of the Data Field 

### Event

(https://github.com/ambrosus/sdk-java-new/blob/master/sdk/src/main/java/com/ambrosus/sdk/Event.java)

Events are registries of any change of state that has occurred to an Asset; the temperature, humidity, location, acceleration, quality check, etc. When registered in AMB-NET, an event will always contain the following array of JSON objects: 

* WHAT (relating to the AMB-ID of the Asset in Question)
* WHERE (the location of where the Event was taken from based upon latitude or longitude coordinates or GLN). 
* WHO (the device, application, or user that created the Event in question). 
* WHEN (a timestamp of the Event indicating when it was originally created). 
* WHY (indicating the business procedure and its purpose).

Each item in this array is an object of a some type identified by a string constant. You can get types of all available data objects with Event.getDataTypes() method. You can use Event.getDataObject(String type) to retrieve an object of a certain type. 

### Private Key

A private key is a hex string representation of a 32 byte sequences. In order to use a private key in the Ambrosus Ecosystem, you must first register an account (https://dashboard.hermes.ambrosus-test.com/signup) on the Ambrosus Network. With the private key required to create an Ambrosus Account, you can then start creating assets or events on the network. Additionally, you can also use this private key to create an AuthToken which will allow you to query content of events with restricted access.

Within the Java SDK itself, private keys are used to sign off on the content of Events, Assets and AuthTokens. Meanwhile, AMB-NET verifies these signatures with your public key when you try to push an event/asset to the network or query the content of protected events.

## Key features

### Retrieve asset/event by ID

```java
String assetId = "0x88181e5e517df33d71637b3f906df2e27759fdcbb38456a46544e42b3f9f00a2";

Asset asset = network.getAsset(assetId).execute();
System.out.println(asset.getSystemId());

final String eventId = "0x36fe3d701297e0ede30456241594f19b60c07ae4e629f5a11a944d46567efafe";

Event event = network.getEvent(eventId).execute();
System.out.println(event.getSystemId());
```

### Search for assets/events satisfying provided criteria
 
You can search for assets/events which match your criteria with **Network.findAssets(Query<Asset> query) / Network.findEvents(Query<Event> query)** methods. These methods return a **NetworkCall** instance of which the resultant type is defined as **SearchResult<Asset>** or **SearchResult<Event>** respectively. E.g:

```java
SearchResult<Event> searchResult 
= network.findEvents(new EventQueryBuilder().build()).execute();

SearchResult class represents a page of search results with up to 100 result data models. You can get a list of these data models with SearchResult.getValues() method:

List<Event> values = searchResult.getValues();

You can specify search criteria with EventQueryBuilder and AssetQueryBuilder classes, e.g:

Query<Event> anotherQuery = new EventQueryBuilder()
        .createdBy("0xFF1E60D7e4fe21C1817B8249C8cB8E52D1912665")
        .byDataObjectType("ambrosus.asset.harvested")
        .build();

searchResult = network.findEvents(anotherQuery).execute();

values = searchResult.getValues();
```

### Fetching next pages of a search result (pagination support)

When you query for events or assets with **Network.findEvents(Query<Event> query)** / **Network.findAssets(Query<Asset> query)** methods sdk will return to you only the first page of the overall search result. This page can contain up to 100 data models. If your search results contain more than 100 data models you can access the subsequent pages using **PageQueryBuilder**:

```java
Query<Event> query = new EventQueryBuilder().createdBy("0x9A3Db936c94523ceb1CcC6C90461bc34a46E9dfE").build();

SearchResult<Event> firstPage = network.findEvents(query).execute();
if(firstPage.getTotalPages() > 1) {
    PageQueryBuilder<Event> pageQueryBuilder = new PageQueryBuilder<>(firstPage);
    Query<? extends Event> secondPageQuery = pageQueryBuilder.getQueryForPage(firstPage.getPageIndex() + 1);
    SearchResult<Event> secondPage = network.findEvents(secondPageQuery).execute();
    System.out.println(secondPage);
}
```

### Create assets and events

You can create assets and events with the **Asset.Builder** and **Event.Builder** classes respectively. E.g:

```java
String privateKey = "<<Put your Private Key (link to private key section) here>>";

Asset asset = new Asset.Builder().createAsset(privateKey);

network.pushAsset(asset).execute();

JsonObject testData = new JsonObject();
testData.addProperty("testKey", "testValue");
testData.addProperty("anotherKey", "anotherValue");

Event.Builder builder = new Event.Builder(asset.getSystemId())
        .addData("custom", testData);

Event event = builder.createEvent(privateKey);
network.pushEvent(event).execute();
```

### Create event with limited access and query content of this event

You can restrict access to your events data by setting the accessLevel > 0:

```java
Event.Builder builder = new Event.Builder(asset.getSystemId())
        .setAccessLevel(1)
        .addData("custom", testData);

Event event = builder.createEvent(privateKey);

network.pushEvent(event).execute();
```

After such measures have been taken, it is not possible to get json data from this event until you are authorized as a holder of an account which was used to create the event or, conversely, if you are a holder of one of its child accounts:

```java
Event privateEvent = network.getEvent(event.getSystemId()).execute();

try {
    List<JsonObject> data = privateEvent.getRawData();
} catch (RestrictedDataAccessException e) {
   System.out.println(e.getMessage());
    //we get this exception because
    //of querying event with accessLevel > 0
    //without providing correct AuthToken for the network
 }
``` 

In order to authenticate oneself as an account holder you have to create an AuthToken instance and provide it for the **Network** class instance. This will allow you to get data for all events with access level within your designated range of [0; your account accessLevel]

```java
AuthToken authToken = AuthToken.create(privateKey, 1, TimeUnit.DAYS);
network.authorize(authToken);

privateEvent = network.getEvent(event.getSystemId()).execute();
//now you can get access to event data
System.out.println(privateEvent.getRawData());
```

### Configure Node API endpoint

It’s possible to use different network API endpoints. To do this, one must first create an instance of Configuration class, and then set the API endpoint for this instance with url(String url) method. Once this has been done, it is then possible to create an instance of Network class using the following configuration:

```java
Configuration configuration = new Configuration().url("https://hermes.ambrosus.com");
Network network = new Network(configuration);
```

Altogether, you can create several network instances linked to different API endpoints and use them to query data from different sources.

### Using custom data models

You can introduce your own data models by extending the generic Event model class. The Ambrosus development kit contains a set of helper classes: 

**NetworkCallAdapter** (https://github.com/ambrosus/sdk-java-new/blob/master/sdk/src/main/java/com/ambrosus/sdk/NetworkCallAdapter.java)

**SearchRequestAdapter**
(https://github.com/ambrosus/sdk-java-new/blob/master/sdk/src/main/java/com/ambrosus/sdk/SearchRequestAdapter.java)

**GenericEventQueryBuilder**
(https://github.com/ambrosus/sdk-java-new/blob/master/sdk/src/main/java/com/ambrosus/sdk/GenericEventQueryBuilder.java)

These classes might help you to build your own implementation of the Network class which operates with your own data models. You can use **AMBNetwork** class https://github.com/ambrosus/sdk-java-new/blob/master/sdk/src/main/java/com/ambrosus/sdk/model/AMBNetwork.java as a sample of such an implementation. We use this class in our demo apps and it operates with data models that we use for demos: 

**AMBAssetInfo** (JSON model)
https://github.com/ambrosus/sdk-java-new/blob/master/sdk/src/main/java/com/ambrosus/sdk/model/AMBAssetInfo.java

**AMBEvent**
https://github.com/ambrosus/sdk-java-new/blob/master/sdk/src/main/java/com/ambrosus/sdk/model/AMBEvent.java

### Example: Search for information about item which is marked with “3451080000324” EAN13 barcode.

Assumption: each item on the network has a corresponding Event which contains information about the item in the following json format:

(https://github.com/ambrosus/sdk-javascript/blob/master/docs-old/AmbrosusEventEntryTypes.md)

Using the generic Event model:

```java
String ean13barcode = "3451080000324";

//according to assumptions Event with information about item:
Query<Event> query = new EventQueryBuilder()
        //1. should contain data object of "ambrosus.asset.info" type
        .byDataObjectType("ambrosus.asset.info")
        //2. should contain data object with "identifiers.ean13" array
        //so querying for events which have "identifiers.ean13" array with ean13barcode value
        .byDataObjectField("identifiers.ean13", ean13barcode)
        .build();

SearchResult<Event> eventSearchResult = network.findEvents(query).execute();
Event item = eventSearchResult.getValues().get(0);
```

It is also possible to do the same thing using a generic Event model + **AssetInfoQueryBuilder** and Identifier classes which contain constants from the code above:

```java
Query<AMBAssetInfo> assetInfoQuery = new AssetInfoQueryBuilder()
        .byIdentifier(new Identifier(Identifier.EAN13, ean13barcode))
        .build();

eventSearchResult = network.findEvents(query).execute();
item = eventSearchResult.getValues().get(0);
```

Finally, it can also be done with an instance of **AMBNetwork** class which you can use to query AssetInfo model:

```java
AMBNetwork ambNetwork = new AMBNetwork();
SearchResult<AMBAssetInfo> assetInfoSearchResult = ambNetwork.findAssetInfo(assetInfoQuery).execute();
AMBAssetInfo assetInfo = assetInfoSearchResult.getValues().get(0);
```

## Demo app (Ambrosus Viewer)

The Ambrosus Viewer
(https://github.com/ambrosus/sdk-java-new/tree/master/samples/android/DemoApp)

is an Android application which allows users to scan a Barcode, QR Code, [or other 1D or 2D symbology - in development] and get details about an item moving through an industrial process.

By using the Ambrosus Viewer, any business, customer, or regulatory authority, has the opportunity to learn about the scanned item: its origins, quality controls (if applicable), and other details such as temperature, weight, creation date, and more. When combined into a single string of Events, it is possible to see a timeline detailing all things that have happened to the particular asset in question, from the date of its creation to it arrival at its end destination.