package com.dunn.instrument.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.dunn.instrument.tools.log.LogUtil;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        LogUtil.i("BootReceiver", "onReceive action = " + intent.getAction());
    }
}
