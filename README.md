# A Mirth Connect plugin to add cross channel searching and present a summary view of channels via a Settings tab

A [Mirth Connect](https://github.com/nextgenhealthcare/connect) plugin started from Kaur Palang's [sample plugin] (https://github.com/kpalang/mirth-sample-plugin).  Allows cross-channel searching and generates a jQuery-accordion style [nested summary] (https://github.com/mjtrac/mirth-sample-plugin/blob/master/sample-plugin-generated.html) of your channels.  Relevant non-boilerplate is in client/src/main/java/com/mitchtrachtenberg/mirthpluginsearchsummary/client

---

## Installation by building
1. [Install Java](https://www.javatpoint.com/javafx-how-to-install-java)
1. [Install Maven](https://www.javatpoint.com/how-to-install-maven)
1. Run `git clone https://github.com/mjtrac/mirth-searchsummary-plugin`
1. Copy  m2settings.xml to use as your local maven settings.xml, located in .m2 under your home directory, or include the <servers> material from m2settings.xml in your existing ~/.m2/settings.xml. (This is required to get permissions needed for Kaur's mirth-resources-api repo at github.)
1. Navigate to `mirth-searchsummary-plugin/`
1. Run `mvn install` to install dependencies to local cache (see below if you get an error message indicating you are Unauthorized to download from the github repo)
1. Run `mvn clean package` to verify the build works
1. Install the sample plugin by getting the `.zip` archive from `mirth-searchsummary-plugin/distribution/target`
---

## Installation of pre-built zip for Mirth Connect 4.3
1. `git clone https://github.com/mjtrac/mirth-searchsummary-plugin`
1. `cd mirth-searchsummary-plugin/distribution/searchsummaryplugin.zip
1. Run Mirth Connect 4.2.0 (this zip will only work with 4.2.0)
1. Go to the Extensions page in Mirth Connect 4.2.0
1. At the bottom of the extensions page, Browse to your downloaded copy of searchsummaryplugin.zip and then click Install
1. Restart the Mirth Connect Server and run the Administrator Launcher with the "-k" argument which will allow you to accept a self-signed certificate on the extension. 
## Usage
IMPORTANT: You MUST add server information to your Maven settings.xml file in your ~/.m2 folder in order to use Kaur's repository of various Mirth jars in building this plugin.

IMPORTANT: Once this extension is installed, the Mirth Connect Administrator Launcher will need to be run with the -k flag that allows you to accept self-signed certificates, as the provided plugin certificate is self-signed.  You will then need to accept the prompt the launcher displays when it encounters the plugin.

After the extension is installed into Mirth 4.2 (currently the only version checked is 4.2) 
Mirth will have a new Search/summary tab in Settings view.  
You may search for a complete property name (e.g.: port) or for a property value (e.g.: 6661).  All channels will be searched and you will be presented with a path to each reference (e.g.: channel(Channel with a Long Name)/sourceConnector/properties/listenerConnectorProperties/port/ = 6661).

![searchsummary1](https://user-images.githubusercontent.com/2815700/231273566-91373440-e178-4050-98db-a27e62b35013.png)

Using the Menu items in the Search/summary menu to the left, you may generate a temporary file with either text or html for all channels.
The html is generated to allow quick navigation.

![searchsummary2](https://user-images.githubusercontent.com/2815700/231273674-6d0454fb-b7df-4e0d-8959-5cd722cd9666.png)


**This repository showcases use of [mirth-plugin-maven-plugin](https://github.com/kpalang/mirth-plugin-maven-plugin) to generate `plugin.xml` file!**

- Any external libraries that you might want to use in the plugin at runtime, go into `libs/runtime/{type}`
- Any external libraries that you might want to use at compiletime, go into `libs/compiletime/{type}`


## IF YOU GET UNAUTHORIZED WHEN YOU TRY TO USE THE KPALANG GITHUB REPO
If you want to build this for your current version of Mirth, then you can manually install the current Mirth jar files into your local Maven 
repository with a command modeled on this one, one for each jar file that Maven cannot download from the kpalang repo.  
In this example command, the jar file is located in the Mirth Connect install tree's server-lib folder, but other required jar files may be located in other subfolders.  

Replace 4.3.0 below with the version number of Mirth you've installed, and replace the file, groupId, and artifactId as necessary. 

```
mvn install:install-file \
  -Dfile=<path to your local Mirth install>/server-lib/mirth-server.jar \
  -DgroupId=com.mirth.connect \
  -DartifactId=mirth-server \
  -Dversion=4.3.0 -Dpackaging=jar
```

The command should give a response that includes something like:
```
[INFO] Installing <path to your Mirth install>/server-lib/mirth-server.jar to <your home dir>/.m2/repository/com/mirth/connect/mirth-server/4.3.0/mirth-server-4.3.0.jar
```
To add additional versions, you can repeat the process with other versions of Mirth Connect, which can be downloaded from https://mirthdownloadarchive.s3.amazonaws.com/connect-downloads.html

## TODO
* Allow file selector saving of generated text and html.
* Add data type properties for filters and transformers.
* Investigate adding capability to channel view rather than settings.
* Enable retrieval via Mirth Connect API


## NOTE 
Although I am an instructor for NextGen, this work is completely independent of my job and any errors are my own.  Also, the Mirth Connect build system, Mirth Connect's dependencies, Java versioning, and Java build systems are all mysteries to me, so I've just tried to follow Kaur Palang's structure and guide, for which I am grateful.  

Mitch Trachtenberg, mjtrac at gmail dot com.
