package com.wearablesensor.aura.device_pairing;

import android.content.Context;
import java.util.Observable;
import android.util.Log;

import com.wearablesensor.aura.device_pairing.notifications.DevicePairingConnectedNotification;
import com.wearablesensor.aura.device_pairing.notifications.DevicePairingDisconnectedNotification;

/**
 * Created by lecoucl on 31/03/17.
 */

public class DevicePairingService extends Observable{
    private final String TAG = this.getClass().getSimpleName();
    protected Context mContext;

    protected String mPairedDeviceName;
    protected String mPairedDeviceAddress;

    protected Boolean mPaired;


    public DevicePairingService(Context iContext){
        mContext = iContext;

        mPairedDeviceName = null;
        mPairedDeviceAddress = null;

        mPaired = false;
    }

    public void automaticPairing(){
        Log.d(TAG, "Start automatic Pairing");
    }

    public void startPairing(){
        Log.d(TAG, "start Pairing with Device: " + mPairedDeviceName + " - "+ mPairedDeviceAddress);

        this.setChanged();
        this.notifyObservers(new DevicePairingConnectedNotification(mPairedDeviceName, mPairedDeviceAddress));
    }

    public void endPairing(){
        Log.d(TAG, "end Pairing");
        mPairedDeviceName = null;
        mPairedDeviceAddress = null;

        this.setChanged();
        this.notifyObservers(new DevicePairingDisconnectedNotification());

    }

    public Boolean isPaired(){
        return mPaired;
    }
}
