# A Mirth Connect plugin to present a summary view of channels via a Settings tab

A [Mirth Connect](https://github.com/nextgenhealthcare/connect) plugin started from Kaur Palang's [sample plugin] (https://github.com/kpalang/mirth-sample-plugin).  Allows cross-channel searching and generates a jQuery-accordion style [nested summary] (https://github.com/mjtrac/mirth-sample-plugin/blob/master/sample-plugin-generated.html) of your channels

---

## Installation
1. [Install Java](https://www.javatpoint.com/javafx-how-to-install-java)
1. [Install Maven](https://www.javatpoint.com/how-to-install-maven)
1. Run `git clone https://github.com/mjtrac/mirth-searchsummary-plugin`
1. Navigate to `mirth-searchsummary-plugin/`
1. Run `mvn install` to install dependencies to local cache
1. Run `mvn clean package` to verify the build works
1. Install the sample plugin by getting the `.zip` archive from `mirth-searchsummary-plugin/distribution/target`
---

## Usage

After the extension is installed into Mirth 4.2 (currently the only version checked is 4.2) you will have a new tab in your Settings view.  
You may search for a complete property name (e.g.: port) or for a property value (e.g.: 6661).  All channels will be searched and you will be presented with a path to each reference (e.g.: channel(Channel with a Long Name)/sourceConnector/properties/listenerConnectorProperties/port/ = 6661).

![searchsummary1](https://user-images.githubusercontent.com/2815700/231273566-91373440-e178-4050-98db-a27e62b35013.png)

Using the Menu items in the Search/summary menu to the left, you may generate a temporary file with either text or html for all channels.
The html is generated to allow quick navigation.
![searchsummary2](https://user-images.githubusercontent.com/2815700/231273674-6d0454fb-b7df-4e0d-8959-5cd722cd9666.png)






**This repository showcases use of [mirth-plugin-maven-plugin](https://github.com/kpalang/mirth-plugin-maven-plugin) to generate `plugin.xml` file!**

- Any external libraries that you might want to use in the plugin at runtime, go into `libs/runtime/{type}`
- Any external libraries that you might want to use at compiletime, go into `libs/compiletime/{type}`

---
## TODO
* Searchability

