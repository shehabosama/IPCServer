package com.base.ipcserver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.base.ipcserver.Constants.DATA
import com.base.ipcserver.Constants.PACKAGE_NAME
import com.base.ipcserver.Constants.PID

class IPCBroadCastReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        RecentClient.client = Client(
            p1?.getStringExtra(PACKAGE_NAME)!!,
            p1?.getStringExtra(PID)!!,
            p1?.getStringExtra(DATA)!!,
            "Receiver"
        )

    }
}