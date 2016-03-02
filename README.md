# Taptrack Tappy BLE SDK
This project provides a standard SDK for interfacing with a TapTrack TappyBLE
NFC reader.

## Searching for Tappies
In order to connect to and communicate with a Tappy, we first must acquire a
`TappyBleDeviceDefinition` for the Tappy. The easiest way to do this is using
the provided `TappyBleScanner` in the `tappyble-scanner` module. The Gradle
dependency for this module is
````groovy
compile 'com.taptrack.tcmptappy:tappyble-scanner:0.2.0'
````

````java
public class SearchActivity extends Activity {
  Map<String,TappyBleDeviceDefinition> devices = new HashMap<>();

  TappyBleFoundListener listener = new TappyBleFoundListener() {
    public void onTappyBleFound(TappyBleDeviceDefinition definition) {
      devices.put(definition.getAddress(),definition);
    }
  };

  TappyBleScanner scanner;

  private static final int COARSE_REQUEST_CODE = 1;
  private static final int REQUEST_ENABLE_BT = 1;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    promptForPermissions();

    scanner = TappyBleScanner.get();
  }

  @Override
  public void onResume() {
    super.onResume();
    scanner.registerTappyBleFoundListener(listener);
    scanner.startScan();
  }

  @Override
  public void onPause() {
    super.onPause();
    scanner.unregisterTappyBleFoundListener(listener);
    scanner.stopScan();
  }

  protected void promptForPermissions() {
    //prompt to enable bluetooth if it is off
    BluetoothAdapter adapter = BluetoothManager.get().getAdapter();
    if (adapter == null || !adapter.isEnabled()) {
      Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    //prompt for location runtime permission
    int permissionCheck = ContextCompat.checkSelfPermission(activity,
        Manifest.permission.ACCESS_COARSE_LOCATION);
    if(permissionCheck != PackageManager.PERMISSION_GRANTED) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    COARSE_REQUEST_CODE);
                  }
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
      if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
          Toast.makeText(activity,  "Bluetooth must be enabled", Toast.LENGTH_SHORT).show();
          activity.finish();
          return;
      }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
      if(requestCode == COARSE_REQUEST_CODE)
      {
          if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
              hasCoarsePermission = true;
          }
          else {
              Toast.makeText(activity, "Location is needed to get search results on Marshmallow", Toast.LENGTH_SHORT).show();
              activity.finish();
          }
      }
  }

}
````

## Connecting to a Tappy
It is recommended to use the provided `TappyBleCommunicationsService` in order to connect to
and communicate with Tappies
````groovy
compile 'com.taptrack.tcmptappy:tappyble-service:0.2.0'
````

````xml
 <service
   android:name="com.taptrack.tcmptappy.tappy.ble.service.TappyBleCommunicationsService"
                 android:process=":tcmpcomm"
                 android:exported="false" />
````
Notice that the service is intended to run in a separate process. While you can
run the communication service in your application process for debugging,
you may experience some UI jank if you do so. If you wish to run the communication
service in your application's process, make sure to send requests to it off of the
UI thread.

## Communicating with a Tappy
For most Tappy use cases, you should probably use one of the provided command
family modules.
````groovy
// basic NFC/NDEF operations like writing to tags
compile 'com.taptrack.tcmptappy:commandfamily-basicnfc:0.2.0'
// tappyBle commands such as GetBattery
compile 'com.taptrack.tcmptappy:commandfamily-system:0.2.0'
````

If you wish to write a custom command family, feel free to include the base
command family module
````groovy
compile 'com.taptrack.tcmptappy:commandfamily-common:0.2.0'
````

If you do not wish to use or implement a full command family, you should extend
from the basic `TCMPMessage` class included in the `message-common` module.
````groovy
compile 'com.taptrack.tcmptappy:message-common:0.2.0'
````

## Connecting to multiple Tappies
The provided `TappyBleCommunicationsService` supports connecting to and communicating with an arbitrary
number of Tappies. However, the actual number of Tappies you are able to maintain
a connection with in practice depends on capabilities of the Android device you are
using, so your mileage may vary.
