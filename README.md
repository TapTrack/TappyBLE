# Impending Deprecation
This SDK will be deprecated in the near future for a new Android Tappy SDK that supports both TappyUSB and TappyBLE readers while offering a simplified API. The new version 4.x.x Tappy demo app now in the [Play Store](https://play.google.com/store/apps/details?id=com.taptrack.bletappyexample) is part of this SDK, so the app found in this repository will not match up with what you see currently published. If you are starting a new Android project using the Tappy and wish to base it on the new SDK, please contact TapTrack for access to the latest beta version.

# Taptrack Tappy BLE SDK
This project provides a standard SDK for interfacing with a TapTrack TappyBLE NFC reader. The 'app' module contains the source for the version 3.x.x editions of the TappyBLE example app found at 
https://play.google.com/store/apps/details?id=com.taptrack.bletappyexample

## Common Gradle Dependencies
```groovy
compile 'com.taptrack.tcmptappy:tappyble-scanner:0.9.4'
compile 'com.taptrack.tcmptappy:tappyble-service:0.9.4'
compile 'com.taptrack.tcmptappy:tappyble-simplemanager:0.9.4'
```

## Documentation
Please see the documentation in the wiki

## Basic Command Families

[0x0000h: System Command Family](https://github.com/TapTrack/System-Command-Family)

[0x0001h: Basic NFC Command Family](https://github.com/TapTrack/BasicNfc-Command-Family)

[0x0003h: MIFARE Classic Command Family](https://github.com/TapTrack/MifareClassic-Command-Family)

[0x0004h: Type 4 Command Family](https://github.com/TapTrack/Type4-Command-Family)
