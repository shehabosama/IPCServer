package com.base.ipcserver

class Client
    (
    var clientPackageName: String?,
    var clientProcessId: String,
    var clientData: String?,
    var ipcMethod: String
    )
    object RecentClient {
        var client: Client? = null
}