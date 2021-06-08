package com.skoumal.grimoire.wand.wizard.sync

import android.app.Service
import android.content.AbstractThreadedSyncAdapter
import android.content.Intent
import android.os.IBinder

abstract class SyncService : Service() {

    protected var adapter: AbstractThreadedSyncAdapter? = null

    override fun onCreate() {
        super.onCreate()
        adapter = onCreateSyncAdapter()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return adapter?.syncAdapterBinder
    }

    protected abstract fun onCreateSyncAdapter(): AbstractThreadedSyncAdapter

}