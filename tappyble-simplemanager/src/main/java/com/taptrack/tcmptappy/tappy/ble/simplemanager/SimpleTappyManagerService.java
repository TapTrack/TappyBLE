package com.taptrack.tcmptappy.tappy.ble.simplemanager;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.taptrack.tcmptappy.tappy.ble.ParcelableTappyBleDeviceDefinition;
import com.taptrack.tcmptappy.tappy.ble.TappyBleDeviceDefinition;
import com.taptrack.tcmptappy.tappy.ble.TappyBleDeviceStatus;
import com.taptrack.tcmptappy.tappy.ble.service.IBleTappyMessageCallback;
import com.taptrack.tcmptappy.tappy.ble.service.IBleTappyServiceIPC;
import com.taptrack.tcmptappy.tappy.ble.service.IBleTappyStatusCallback;
import com.taptrack.tcmptappy.tappy.ble.service.TappyBleCommunicationsService;
import com.taptrack.tcmptappy.tcmp.RawTCMPMessage;
import com.taptrack.tcmptappy.tcmp.TCMPMessage;
import com.taptrack.tcmptappy.tcmp.TCMPMessageParseException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class SimpleTappyManagerService extends Service {
    private  static final String TAG = SimpleTappyManagerService.class.getName();
    public interface Intents {
        String ACTION_CONNECT = TAG+".CONNECT";
        String EXTRA_TAPPY_BLE_DEVICE_DEFINITION = ACTION_CONNECT+".DEVICE";

        String ACTION_DISCONNECT = TAG+".DISCONNECT";

        String ACTION_SEND_TCMP = TAG+".SEND";

        String ACTION_REQ_REBROADCAST_STATUS = TAG+".REBROADCAST_STATUS";

        String ACTION_ENABLE_AUTOMATIC_RECONNECT = TAG+".ENABLE_AUTO_RECONNECT";
        String ACTION_DISABLE_AUTOMATIC_RECONNECT = TAG+".DISABLE_AUTO_RECONNECT";

        String ACTION_SHUTDOWN = TAG+".SHUTDOWN";

        String ACTION_TCMP_RECEIVED = TAG+".TCMP_RECEIVED";
        String EXTRA_TCMP_BYTES = TAG+".TCMP_BYTES";

        String ACTION_NEW_STATUS = TAG+".NEW_STATUS";
        String EXTRA_DEVICE_STATUS = ACTION_NEW_STATUS+".STATUS";

        String ACTION_REMOTE_EXCEPTION = TAG+".ERROR";
        String EXTRA_REMOTE_EXCEPTION_MSG = ACTION_REMOTE_EXCEPTION+"MESSAGE";
        String EXTRA_REMOTE_EXCEPTION_TRACE = ACTION_REMOTE_EXCEPTION+"TRACE";
    }

    protected BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            handleIntent(intent);
        }
    };

    protected IBleTappyServiceIPC tappyCommService;
    protected boolean isBinding = false;

    protected ServiceConnection tappyConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            tappyCommService = IBleTappyServiceIPC.Stub.asInterface(service);
            registerTappyServiceCallbacks();
            connectActiveDevice();
            sendPendingData();
            requestStatusUpdate();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            tappyCommService = null;
        }
    };


    protected boolean autoReconnect = false;
    protected ParcelableTappyBleDeviceDefinition activeDevice;
    protected int tappyStatus;


    private Handler handler = new BleCommHandler(this);
    static class BleCommHandler extends Handler {
        public static final int MSG_BLE_TAPPY_MESSAGE = 1;
        public static final int MSG_BLE_TAPPY_STATUS = 2;
        public static final int MSG_INTENT_TO_FORWARD = 3;

        public static final String KEY_EXTRAS = "EXTRAS";
        public static final String KEY_ACTION = "ACTION";
        public static final String KEY_DEVICE = "BLEDEVICE";
        public static final String KEY_MESSAGE_BYTES = "MSGBYTES";
        public static final String KEY_STATUS = "STATUSINT";

        private final WeakReference<SimpleTappyManagerService> serviceWeakReference;

        BleCommHandler(SimpleTappyManagerService service) {
            serviceWeakReference = new WeakReference<SimpleTappyManagerService>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            SimpleTappyManagerService service = serviceWeakReference.get();
            if(service != null) {
                switch(msg.what) {
                    case MSG_BLE_TAPPY_MESSAGE: {
                        Bundle bundle = msg.getData();
                        ParcelableTappyBleDeviceDefinition tappy = bundle.getParcelable(KEY_DEVICE);
                        if(tappy != null) {
                            if (service.activeDevice != null &&
                                    service.activeDevice.getAddress().equals(tappy.getAddress())) {
                                byte[] message = bundle.getByteArray(KEY_MESSAGE_BYTES);
                                service.newTappyMessage(message);
                                break;
                            }
                        }
                        break;
                    }
                    case MSG_BLE_TAPPY_STATUS: {
                        Bundle bundle = msg.getData();
                        ParcelableTappyBleDeviceDefinition tappy = bundle.getParcelable(KEY_DEVICE);
                        if(tappy != null) {
                            if (service.activeDevice != null && service.activeDevice.getAddress().equals(tappy.getAddress())) {
                                int status = bundle.getInt(KEY_STATUS);
                                service.newBleStatus(status);
                                break;
                            }
                        }
                    }
                    case MSG_INTENT_TO_FORWARD: {
                        Bundle bundle = msg.getData();
                        if(bundle != null && bundle.containsKey(KEY_ACTION)) {
                            Intent intent = new Intent(bundle.getString(KEY_ACTION));
                            if(bundle.containsKey(KEY_EXTRAS)) {
                                intent.putExtras(bundle.getBundle(KEY_EXTRAS));
                            }
                            service.handleIntent(intent);
                        }
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

    protected ArrayDeque<TCMPMessage> pendingMessages = new ArrayDeque<>(1);

    protected Set<SimpleTappyListener> listeners = new CopyOnWriteArraySet<>();

    public class SimpleTappyManagerBinder extends Binder {
        public void connectTappy(TappyBleDeviceDefinition deviceDefinition) {
            Intent intent = new Intent(Intents.ACTION_CONNECT);
            intent.putExtra(Intents.EXTRA_TAPPY_BLE_DEVICE_DEFINITION,
                    new ParcelableTappyBleDeviceDefinition(deviceDefinition));
            forwardIntentThroughHandler(intent);
        }

        public void disconnectTappy() {
            Intent intent = new Intent(Intents.ACTION_DISCONNECT);
            forwardIntentThroughHandler(intent);
        }

        public void requestRebroadcastStatus() {
            Intent intent = new Intent(Intents.ACTION_REQ_REBROADCAST_STATUS);
            forwardIntentThroughHandler(intent);
        }

        public void shutdown() {
            Intent intent = new Intent(Intents.ACTION_SHUTDOWN);
            forwardIntentThroughHandler(intent);
        }

        public void setAutomaticReconnect(boolean shouldReconnect) {
            Intent intent;
            if(shouldReconnect)
                intent = new Intent(Intents.ACTION_ENABLE_AUTOMATIC_RECONNECT);
            else
                intent = new Intent(Intents.ACTION_DISABLE_AUTOMATIC_RECONNECT);
            forwardIntentThroughHandler(intent);
        }

        public void sendTcmpMessage(TCMPMessage message) {
            Intent intent = new Intent(Intents.ACTION_SEND_TCMP);
            intent.putExtra(Intents.EXTRA_TCMP_BYTES,message.toByteArray());
            forwardIntentThroughHandler(intent);
        }

        /**
         * Registers a SimpleTappyListener to receive feedback
         *
         * @param listener Listener to call when tappy status changes
         */
        public void registerSimpleTappyListener(SimpleTappyListener listener) {
            listeners.add(listener);
        }

        public void unregisterSimpleTappyListener(SimpleTappyListener listener) {
            listeners.remove(listener);
        }

        /**
         * Gets what the service currently thinks the status is.
         *
         * Note that this does not cross IPC into the communication service, so it has
         * the potential to be slightly out of date.
         * @return current device status
         */
        public int getLatestStatus() {
            return tappyStatus;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        bindService(new Intent(this, TappyBleCommunicationsService.class), tappyConnection, BIND_AUTO_CREATE);
        isBinding = true;

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intents.ACTION_CONNECT);
        filter.addAction(Intents.ACTION_DISCONNECT);
        filter.addAction(Intents.ACTION_SEND_TCMP);
        filter.addAction(Intents.ACTION_DISABLE_AUTOMATIC_RECONNECT);
        filter.addAction(Intents.ACTION_ENABLE_AUTOMATIC_RECONNECT);
        filter.addAction(Intents.ACTION_REQ_REBROADCAST_STATUS);
        filter.addAction(Intents.ACTION_SHUTDOWN);
        registerReceiver(receiver, filter);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new SimpleTappyManagerBinder();
    }

    protected void forwardIntentThroughHandler(Intent intent) {
        Message msg = new Message();
        msg.what = BleCommHandler.MSG_INTENT_TO_FORWARD;
        Bundle description = new Bundle();
        if(intent.getAction() != null) {
            description.putString(BleCommHandler.KEY_ACTION, intent.getAction());
        }
        if(intent.getExtras() != null) {
            description.putBundle(BleCommHandler.KEY_EXTRAS, intent.getExtras());
        }
        msg.setData(description);
        handler.sendMessage(msg);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterTappyServiceCallbacks();
        if(tappyCommService != null) {
            try {
                tappyCommService.disconnectAll();
            } catch (RemoteException e) {
                handleRemoteException("Error unbinding on destroy",e);
            }
        }
        if(!isBinding) {
            unbindService(tappyConnection);
        }
        unregisterReceiver(receiver);
    }


    protected void broadcastIntent(Intent intent) {
        sendBroadcast(intent);
    }

    protected void newBleStatus(int status) {
        int oldStatus = tappyStatus;
        tappyStatus = status;

        if(status == TappyBleDeviceStatus.READY) {
            sendPendingData();
        }

        if(status == TappyBleDeviceStatus.DISCONNECTED &&
                autoReconnect &&
                oldStatus != TappyBleDeviceStatus.DISCONNECTED) {
            connectActiveDevice();
        }


        Intent intent = new Intent(Intents.ACTION_NEW_STATUS);
        intent.putExtra(Intents.EXTRA_DEVICE_STATUS,status);

        Iterator<SimpleTappyListener> listenerIterator = listeners.iterator();
        while(listenerIterator.hasNext()) {
            listenerIterator.next().onNewStatus(status);
        }
        broadcastIntent(intent);
    }

    protected void newTappyMessage(byte[] data) {
        Intent intent = new Intent(Intents.ACTION_TCMP_RECEIVED);
        intent.putExtra(Intents.EXTRA_TCMP_BYTES,data);

        Iterator<SimpleTappyListener> listenerIterator = listeners.iterator();
        while(listenerIterator.hasNext()) {
            try {
                listenerIterator.next().onNewMessageReceived(new RawTCMPMessage(data));
            } catch (TCMPMessageParseException e) {
                Log.wtf(TAG,e);
            }
        }

        broadcastIntent(intent);
    }

    protected void registerTappyServiceCallbacks() {
        if(tappyCommService != null) {
            try {
                tappyCommService.setMessageReceivedCallback(messageCallback);
                tappyCommService.setOnTappyStatusChangedListener(statusCallback);
            } catch (RemoteException e) {
                handleRemoteException("Error registering callbacks",e);
            }
        }
    }

    protected void unregisterTappyServiceCallbacks() {
        if(tappyCommService != null) {
            try {
                tappyCommService.unregisterMessageReceivedCallback();
                tappyCommService.unregisterOnTappyStatusChangedListener();
            } catch (RemoteException e) {
                handleRemoteException("Error registering callbacks",e);
            }
        }
    }

    protected void connectActiveDevice() {
        if(activeDevice != null && tappyCommService != null) {
            try {
                tappyCommService.setConnectedDevices(Collections.singletonList(activeDevice));
            } catch (RemoteException e) {
                handleRemoteException("Error on connecting",e);
            }
        }
    }

    protected void disconnectDevice() {
        activeDevice = null;
        tappyStatus = TappyBleDeviceStatus.UNKNOWN;
        pendingMessages.clear();
        if(tappyCommService != null) {
            try {
                tappyCommService.disconnectAll();
            } catch (RemoteException e) {
                handleRemoteException("Exception on disconnecting",e);
            }
        }
    }

    protected void handleRemoteException(String msg, RemoteException e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();

        Log.e(TAG, msg, e);
        Intent intent = new Intent(Intents.ACTION_REMOTE_EXCEPTION);
        intent.putExtra(Intents.EXTRA_REMOTE_EXCEPTION_MSG,e.getMessage());
        intent.putExtra(Intents.EXTRA_REMOTE_EXCEPTION_TRACE, stackTrace);
        broadcastIntent(intent);

        Iterator<SimpleTappyListener> listenerIterator = listeners.iterator();
        while(listenerIterator.hasNext()) {
            listenerIterator.next().onRemoteException(e);
        }
    }

    protected void sendPendingData() {
        // TODO: make this do something
        if(pendingMessages.size() > 0 &&
                tappyCommService != null &&
                activeDevice != null &&
                tappyStatus == TappyBleDeviceStatus.READY) {
            Iterator<TCMPMessage> messageIterator = pendingMessages.descendingIterator();
            while(messageIterator.hasNext()) {
                TCMPMessage message = messageIterator.next();

                try {
                    tappyCommService.sendMessage(activeDevice,message.toByteArray());
                } catch (RemoteException e) {
                    handleRemoteException("Error sending message",e);
                }

                messageIterator.remove();
            }
        }
    }

    protected void requestStatusUpdate() {
        if(tappyCommService != null) {
            if(activeDevice == null) {
                newBleStatus(TappyBleDeviceStatus.UNKNOWN);
            }
            else {
                try {
                    tappyCommService.getTappyStatus(activeDevice,statusCallback);
                } catch (RemoteException e) {
                    handleRemoteException("Error on request status update",e);
                }
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            handleIntent(intent);
        }
        return START_REDELIVER_INTENT;
    }

    protected void handleIntent(Intent intent) {
        String action = intent.getAction();
        if(action == null)
            return;

        Bundle extras = intent.getExtras();

        if(action.equals(Intents.ACTION_CONNECT) &&
                extras != null &&
                extras.containsKey(Intents.EXTRA_TAPPY_BLE_DEVICE_DEFINITION)) {
            ParcelableTappyBleDeviceDefinition deviceDefinition =
                    extras.getParcelable(Intents.EXTRA_TAPPY_BLE_DEVICE_DEFINITION);
            connectTappy(deviceDefinition);
        }
        else if (action.equals(Intents.ACTION_DISCONNECT)) {
            disconnectDevice();
        }
        else if (action.equals(Intents.ACTION_DISABLE_AUTOMATIC_RECONNECT)) {
            setAutoReconnect(false);
        }
        else if (action.equals(Intents.ACTION_ENABLE_AUTOMATIC_RECONNECT)) {
            setAutoReconnect(true);
        }
        else if (action.equals(Intents.ACTION_REQ_REBROADCAST_STATUS)) {
            requestStatusUpdate();
        }
        else if (action.equals(Intents.ACTION_SHUTDOWN)) {
            requestShutdown();
        }
        else if (action.equals(Intents.ACTION_SEND_TCMP) &&
                extras != null &&
                extras.containsKey(Intents.EXTRA_TCMP_BYTES)) {
            sendData(extras.getByteArray(Intents.EXTRA_TCMP_BYTES));
        }
    }

    protected void requestShutdown() {
        stopSelf();
    }

    protected void connectTappy(ParcelableTappyBleDeviceDefinition deviceDefinition) {
        if(activeDevice != null && !deviceDefinition.getAddress().equals(deviceDefinition.getAddress())) {
            tappyStatus = TappyBleDeviceStatus.UNKNOWN;
            pendingMessages.clear();
        }
        activeDevice = deviceDefinition;
        connectActiveDevice();
    }

    protected void sendData(byte[] data) {
        try {
            RawTCMPMessage rawTCMPMessage = new RawTCMPMessage(data);
            pendingMessages.addFirst(rawTCMPMessage);
            sendPendingData();
        } catch (TCMPMessageParseException e) {
            Log.e(TAG,"Attempting to send invalid message",e);
        }
    }

    protected void setAutoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
    }
}
