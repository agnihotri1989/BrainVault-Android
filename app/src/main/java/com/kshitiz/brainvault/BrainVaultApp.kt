package com.kshitiz.brainvault

import android.app.Application
import com.kshitiz.brainvault.auth.TokenManager
import com.kshitiz.brainvault.network.RetrofitInstance

class BrainVaultApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // 👇 Init RetrofitInstance with TokenManager at app startup
        val tokenManager = TokenManager(this)
        RetrofitInstance.init(tokenManager)
    }
}