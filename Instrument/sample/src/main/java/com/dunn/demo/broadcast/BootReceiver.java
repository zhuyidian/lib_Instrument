package com.dunn.demo.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.i("BootReceiver", "onReceive action = " + intent.getAction());
    }
}
