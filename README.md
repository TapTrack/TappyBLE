# Taptrack Tappy BLE SDK
This project provides a standard SDK for interfacing with a TapTrack TappyBLE NFC reader. The 'app' module contains the TappyBLE example app found at 
https://play.google.com/store/apps/details?id=com.taptrack.bletappyexample

## Common Gradle Dependencies
```groovy
compile 'com.taptrack.tcmptappy:tappyble-scanner:0.9.0'
compile 'com.taptrack.tcmptappy:tappyble-service:0.9.0'
compile 'com.taptrack.tcmptappy:tappyble-simplemanager:0.9.0'
```

## Documentation
Please see the documentation in the wiki

## Basic Command Families

[0x0000h: System Command Family](https://github.com/TapTrack/System-Command-Family)

[0x0001h: Basic NFC Command Family](https://github.com/TapTrack/BasicNfc-Command-Family)

[0x0003h: MIFARE Classic Command Family](https://github.com/TapTrack/MifareClassic-Command-Family)

[0x0004h: Type 4 Command Family](https://github.com/TapTrack/Type4-Command-Family)
