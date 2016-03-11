package com.taptrack.tcmptappy.tappy.ble.simplemanager;

import android.os.RemoteException;

import com.taptrack.tcmptappy.tcmp.TCMPMessage;

public interface SimpleTappyListener {
    void onNewMessageReceived(TCMPMessage message);
    void onNewStatus(int status);
    void onRemoteException(RemoteException e);
}
