package com.base.ipcserver

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.os.Process
import android.text.TextUtils
import com.base.ipcserver.Constants.CONNECTION_COUNT
import com.base.ipcserver.Constants.DATA
import com.base.ipcserver.Constants.PACKAGE_NAME
import com.base.ipcserver.Constants.PID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


class IPCServerService: Service() {
    companion object{
        var connectionCount:Int = 0
        val NOT_SENT = "not sent"
    }


    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    private val mMessager = Messenger(IncomingHandler())
    internal inner class IncomingHandler : Handler(){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val receivedBundle = msg.data
            RecentClient.client = Client(
                receivedBundle.getString(PACKAGE_NAME),
                receivedBundle.getInt(PID).toString(),
                receivedBundle.getString(DATA),
                "Messenger")
            // Send message to the client. The message contains server info
            val message = Message.obtain(this@IncomingHandler,0)
            val bundle = Bundle()
            bundle.putInt(CONNECTION_COUNT,connectionCount)
            bundle.putInt(PID, Process.myPid())
            message.data = bundle
            // The service can save the msg.replyTo object as a local variable
            // so that it can send a message to the client at any time

            msg.replyTo.send(message)
        }
    }

    private val binder = object :IIPCExample.Stub(){

        override fun getPid(): Int {
            return Process.myPid()
        }

        override fun getConnectionCount(): Int {
           return IPCServerService.connectionCount
        }

        override fun setDisplayedValue(packageName: String?, pid: Int, data: String?) {
            val clientData = if (data == null || TextUtils.isEmpty(data)) NOT_SENT else data

            RecentClient.client = Client(packageName?: NOT_SENT,pid.toString(),clientData,"AIDL")
        }

    }
    override fun onBind(p0: Intent?): IBinder? {
        connectionCount++
        return when(p0?.action){
            "aidexample1"-> binder
            "messengerexample"-> mMessager.binder
            else -> null
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        RecentClient.client = null
        return super.onUnbind(intent)
    }

    override fun onCreate() {
        super.onCreate()

    }
}