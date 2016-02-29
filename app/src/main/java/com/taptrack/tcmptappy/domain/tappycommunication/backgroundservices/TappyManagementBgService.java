/*
 * Copyright (c) 2016. Papyrus Electronics, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * you may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.taptrack.tcmptappy.domain.tappycommunication.backgroundservices;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.operations.put.PutResult;
import com.taptrack.tcmptappy.application.TcmpTappyDemo;
import com.taptrack.tcmptappy.data.SavedTcmpMessage;
import com.taptrack.tcmptappy.domain.preferencepersistence.AppPreferenceService;
import com.taptrack.tcmptappy.domain.tappycommunication.TappyStatusService;
import com.taptrack.tcmptappy.domain.tappypersistence.ActiveTappiesService;
import com.taptrack.tcmptappy.tappy.ble.ParcelableTappyBleDeviceDefinition;
import com.taptrack.tcmptappy.tappy.ble.TappyBleDeviceDefinition;
import com.taptrack.tcmptappy.tappy.ble.service.IBleTappyMessageCallback;
import com.taptrack.tcmptappy.tappy.ble.service.IBleTappyServiceIPC;
import com.taptrack.tcmptappy.tappy.ble.service.IBleTappyStatusCallback;
import com.taptrack.tcmptappy.tcmp.MalformedPayloadException;
import com.taptrack.tcmptappy.tcmp.RawTCMPMessage;
import com.taptrack.tcmptappy.tcmp.TCMPMessage;
import com.taptrack.tcmptappy.tcmp.TCMPMessageParseException;
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.BasicNfcCommandLibrary;
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.responses.NdefFoundResponse;
import com.taptrack.tcmptappy.tcmp.common.CommandFamilyMessageResolver;
import com.taptrack.tcmptappy.tcmp.common.FamilyCodeNotSupportedException;
import com.taptrack.tcmptappy.tcmp.common.ResponseCodeNotSupportedException;
import com.taptrack.tcmptappy.utils.TimberSubscriber;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Inject;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class TappyManagementBgService extends Service {
    protected final IBinder binder = new TappyManagerBinder();

    protected AtomicBoolean launchNdef = new AtomicBoolean(false);
    private AtomicBoolean isBound = new AtomicBoolean(false);

    private AtomicLong lastUrlLaunched = new AtomicLong(0);
    private static final long URL_LAUNCH_THROTTLE = 300; //minimum amount of time between url launches

    public class TappyManagerBinder extends Binder {
        public TappyManagementBgService getService() {
            return TappyManagementBgService.this;
        }

        public void messageTappy(TappyBleDeviceDefinition deviceDefinition, TCMPMessage message) {
            TappyManagementBgService.this.sendMessage(deviceDefinition,message);
        }

        public void broadcastMessage(TCMPMessage message) {
            TappyManagementBgService.this.broadcastMessage(message);
        }

        public void connectTappy(TappyBleDeviceDefinition deviceDefinition) {
            Set<TappyBleDeviceDefinition> newDeviceSet = new HashSet<>(1);
            newDeviceSet.add(deviceDefinition);
            TappyManagementBgService.this.connectSet(newDeviceSet);
        }
    }

    protected IBleTappyServiceIPC tappyCommService;

    protected ServiceConnection tappyConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            tappyCommService = IBleTappyServiceIPC.Stub.asInterface(service);
            try {
                tappyCommService.disconnectAll();
            } catch (RemoteException ignored) {
            }
            registerTappyServiceCallbacks();
            connectAllActiveDevices();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            tappyCommService = null;
            clearStatusMap();
        }
    };

    @Inject
    StorIOContentResolver contentResolver;
    @Inject
    ActiveTappiesService activeTappiesService;
    @Inject
    AppPreferenceService preferenceService;
    @Inject
    TappyStatusService tappyStatusService;

    CompositeSubscription subscriptions;

    Set<TappyBleDeviceDefinition> activeDevices = new HashSet<>();
    CommandFamilyMessageResolver resolver;
    {
        resolver = new CommandFamilyMessageResolver();
        resolver.registerCommandLibrary(new BasicNfcCommandLibrary());
    }

    private final Map<String,Integer> tappyStatusMap = new ConcurrentHashMap<>();

    private Handler handler = new BleCommHandler(this);
    static class BleCommHandler extends Handler {
        public static final int MSG_BLE_TAPPY_MESSAGE = 1;
        public static final int MSG_BLE_TAPPY_STATUS = 2;

        public static final String KEY_DEVICE = "BLEDEVICE";
        public static final String KEY_MESSAGE_BYTES = "MSGBYTES";
        public static final String KEY_STATUS = "STATUSINT";

        private final WeakReference<TappyManagementBgService> serviceWeakReference;

        BleCommHandler(TappyManagementBgService service) {
            serviceWeakReference = new WeakReference<TappyManagementBgService>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            TappyManagementBgService service = serviceWeakReference.get();
            if(service != null) {
                switch(msg.what) {
                    case MSG_BLE_TAPPY_MESSAGE: {
                        Bundle bundle = msg.getData();
                        ParcelableTappyBleDeviceDefinition tappy = bundle.getParcelable(KEY_DEVICE);
                        byte[] message = bundle.getByteArray(KEY_MESSAGE_BYTES);
                        service.onTappyMessageReceived(tappy, message);
                        break;
                    }
                    case MSG_BLE_TAPPY_STATUS: {
                        Bundle bundle = msg.getData();
                        ParcelableTappyBleDeviceDefinition tappy = bundle.getParcelable(KEY_DEVICE);
                        int status = bundle.getInt(KEY_STATUS);
                        service.onTappyBleStatusReceived(tappy,status);
                        break;
                    }
                    default:
                        super.handleMessage(msg);
                }
            }
            else {
                super.handleMessage(msg);
            }
        }
    }

    IBleTappyMessageCallback messageCallback = new IBleTappyMessageCallback.Stub() {
        @Override
        public void onMessageReceived(ParcelableTappyBleDeviceDefinition device, byte[] message) throws RemoteException {
            Message msg = new Message();
            msg.what = BleCommHandler.MSG_BLE_TAPPY_MESSAGE;
            Bundle data = new Bundle();
            data.putParcelable(BleCommHandler.KEY_DEVICE,device);
            data.putByteArray(BleCommHandler.KEY_MESSAGE_BYTES, message);
            msg.setData(data);
            handler.sendMessage(msg);
        }
    };

    IBleTappyStatusCallback statusCallback = new IBleTappyStatusCallback.Stub() {

        @Override
        public void onTappyBleStatus(ParcelableTappyBleDeviceDefinition device, int status) throws RemoteException {
            Message msg = new Message();
            msg.what = BleCommHandler.MSG_BLE_TAPPY_STATUS;
            Bundle data = new Bundle();
            data.putParcelable(BleCommHandler.KEY_DEVICE,device);
            data.putInt(BleCommHandler.KEY_STATUS, status);
            msg.setData(data);
            handler.sendMessage(msg);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        TcmpTappyDemo.getAppComponent().inject(this);
        subscribeToApplicationServices();
    }

    protected void onTappyMessageReceived(ParcelableTappyBleDeviceDefinition device, byte[] message) {
        long time = System.currentTimeMillis();
        SavedTcmpMessage savedTcmpMessage = new SavedTcmpMessage(device.getName(), device.getAddress(), time, message);
        contentResolver.put()
                .object(savedTcmpMessage)
                .prepare()
                .asRxObservable()
                .subscribe(new UnsubscribeOnNextSubscriber<PutResult>());
        try {
            TCMPMessage parsed = resolver.parseResponse(new RawTCMPMessage(message));
            if(parsed instanceof NdefFoundResponse && launchNdef.get()) {
                    checkUrlAndLaunch((NdefFoundResponse) parsed);
            }
        } catch (MalformedPayloadException | TCMPMessageParseException | FamilyCodeNotSupportedException | ResponseCodeNotSupportedException ignored) {
            // we don't really care here, this is just a convenience features
        }
    }

    protected void onTappyBleStatusReceived(ParcelableTappyBleDeviceDefinition device, int status) {
        tappyStatusMap.put(device.getAddress(), status);
        publishStatusMap();
        Timber.i("Received status for %s: %d", device.getName(), status);
    }

    protected void clearStatusMap() {
        tappyStatusMap.clear();
        publishStatusMap();
    }

    protected void publishStatusMap() {
        tappyStatusService.publishNewStatusMap(tappyStatusMap);
    }

    protected void bindToCommunicator() {
        bindService(new Intent(this, TappyCommunicationBgService.class), tappyConnection, BIND_AUTO_CREATE);
        isBound.set(true);
    }

    private void connectAllActiveDevices() {
        connectSet(activeDevices);
    }

    private void connectSet(Set<TappyBleDeviceDefinition> devices) {
        for(TappyBleDeviceDefinition definition : devices) {
            if(tappyCommService != null) {
                try {
                    tappyCommService.connectDevice(new ParcelableTappyBleDeviceDefinition(definition));
                } catch (RemoteException e) {
                    Timber.e(e,null);
                }
            }
        }
    }

    private void disconnectSet(Set<TappyBleDeviceDefinition> devices) {
        for(TappyBleDeviceDefinition definition : devices) {
            if(tappyCommService != null) {
                try {
                    tappyCommService.disconnectDevice(new ParcelableTappyBleDeviceDefinition(definition));
                } catch (RemoteException e) {
                    Timber.e(e,null);
                }
            }
        }
    }

    private void refreshStatuses() {
        if(tappyCommService != null) {
            for(TappyBleDeviceDefinition deviceDefinition : activeDevices) {
                try {
                    tappyCommService.getTappyStatus(new ParcelableTappyBleDeviceDefinition(deviceDefinition),statusCallback);
                } catch (RemoteException ignored) {
                }
            }
        }
    }

    private void registerTappyServiceCallbacks() {
        if(tappyCommService != null) {
            try {
                tappyCommService.setMessageReceivedCallback(messageCallback);
                tappyCommService.setOnTappyStatusChangedListener(statusCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            refreshStatuses();
        }
    }

    protected void unbindFromCommunicator() {
        if(isBound.get()) {
            if(tappyCommService != null) {
                try {
                    tappyCommService.unregisterMessageReceivedCallback();
                    tappyCommService.unregisterUnparsableMessageReceivedCallback();
                    tappyCommService.unregisterOnTappyStatusChangedListener();
                } catch (RemoteException ignored) {
                    //its dying soon anyway
                }

                try {
                    tappyCommService.close();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            unbindService(tappyConnection);
            clearStatusMap();
            isBound.set(false);
        }
    }

    protected void activateCommunications() {
        //TODO: maybe some state to keep this from activating multiple times
        bindToCommunicator();
    }

    protected void deactivateCommunications() {
        //TODO: maybe add some state so this doesnt screw up if comm is deactivated
        // prior to service binding but after activation started
        unbindFromCommunicator();
    }

    protected void sendMessage(TappyBleDeviceDefinition deviceDefinition, TCMPMessage message) {
        byte[] messageBytes = message.toByteArray();
        long time = System.currentTimeMillis();
        SavedTcmpMessage savedTcmpMessage = new SavedTcmpMessage("","",time,messageBytes);
        contentResolver.put()
                .object(savedTcmpMessage)
                .prepare()
                .asRxObservable()
                .subscribe(new UnsubscribeOnNextSubscriber<PutResult>());
        if(tappyCommService != null) {
            try {
                tappyCommService.sendMessage(new ParcelableTappyBleDeviceDefinition(deviceDefinition), messageBytes);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    protected void broadcastMessage(TCMPMessage message) {
        byte[] messageBytes = message.toByteArray();
        long time = System.currentTimeMillis();
        SavedTcmpMessage savedTcmpMessage = new SavedTcmpMessage("", "", time, messageBytes);
        contentResolver.put()
                .object(savedTcmpMessage)
                .prepare()
                .asRxObservable()
                .subscribe(new UnsubscribeOnNextSubscriber<PutResult>());
        if(tappyCommService != null && activeDevices != null && activeDevices.size() != 0) {
            if(tappyCommService != null) {
                try {
                    tappyCommService.broadcastMessage(messageBytes);
                } catch (RemoteException e) {
                    Timber.e(e,null);
                }
            }
        }
    }

    protected void handleNewActiveDevices(Set<TappyBleDeviceDefinition> newDeviceSet) {
        Set<TappyBleDeviceDefinition> removedDevices = new HashSet<>(activeDevices);
        removedDevices.removeAll(newDeviceSet);
        disconnectSet(removedDevices);

        Set<TappyBleDeviceDefinition> addedDevices = new HashSet<>(newDeviceSet);
        addedDevices.removeAll(activeDevices);
        connectSet(addedDevices);

        this.activeDevices = newDeviceSet;
    }

    protected void handleNewLaunchNdefChange(boolean newLaunchNdef) {
        this.launchNdef.set(newLaunchNdef);
    }

    private void checkUrlAndLaunch(NdefFoundResponse parsed) {
        NdefMessage m = (parsed).getNdefMessage();
        NdefRecord[] records = m.getRecords();
        if (records.length != 0) {
            NdefRecord firstRecord = records[0];
            if (firstRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN &&
                    Arrays.equals(firstRecord.getType(), NdefRecord.RTD_URI)) {
                byte[] uriPayload = firstRecord.getPayload();
                if (uriPayload.length > 1) {
                    byte prefixByte = uriPayload[0];
                    String url = null;
                    switch (prefixByte) {
                        case 0x01:
                            url = "http://www." + new String(Arrays.copyOfRange(uriPayload, 1, uriPayload.length));
                            break;
                        case 0x02:
                            url = "https://www." + new String(Arrays.copyOfRange(uriPayload, 1, uriPayload.length));
                            break;
                        case 0x03:
                            url = "http://" + new String(Arrays.copyOfRange(uriPayload, 1, uriPayload.length));
                            break;
                        case 0x04:
                            url = "https://" + new String(Arrays.copyOfRange(uriPayload, 1, uriPayload.length));
                            break;
                    }
                    if (url != null) {
                        long currentTime = System.currentTimeMillis();
                        long previousTime = lastUrlLaunched.getAndSet(currentTime);
                        if((previousTime + URL_LAUNCH_THROTTLE) < currentTime) {
                            Intent launchUrlIntent = new Intent(Intent.ACTION_VIEW);
                            launchUrlIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            launchUrlIntent.setData(Uri.parse(url));
                            startActivity(launchUrlIntent);
                        }
                    }
                }
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unsubscribeFromApplicationServices();
        unbindService(tappyConnection);
    }

    protected void subscribeToApplicationServices() {
        Subscription activeTappySubscription = activeTappiesService
                .getActiveTappies()
                .subscribe(new TimberSubscriber<Set<TappyBleDeviceDefinition>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onNext(Set<TappyBleDeviceDefinition> tappyBleDeviceDefinitions) {
                        handleNewActiveDevices(tappyBleDeviceDefinitions);
                    }
                });

        Subscription communicationsActiveSubscription = preferenceService
                .getCommunicationActive()
                .subscribe(new TimberSubscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onNext(Boolean activate) {
                        if(activate) {
                            activateCommunications();
                        }
                        else {
                            deactivateCommunications();
                        }
                    }
                });

        Subscription launchNdefSubscription = preferenceService
                .getBackgroundNdefLaunch()
                .subscribe(new TimberSubscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onNext(Boolean launchNdef) {
                        handleNewLaunchNdefChange(launchNdef);
                    }
                });

        subscriptions = new CompositeSubscription(activeTappySubscription,
                communicationsActiveSubscription,
                launchNdefSubscription);
    }

    protected void unsubscribeFromApplicationServices() {
        if(subscriptions != null && !subscriptions.isUnsubscribed())
            subscriptions.unsubscribe();
        subscriptions = null;
    }

    protected final static class UnsubscribeOnNextSubscriber<T> extends TimberSubscriber<T> {

        @Override
        public void onCompleted() {

        }

        @Override
        public void onNext(T o) {
            unsubscribe();
        }
    }
}
