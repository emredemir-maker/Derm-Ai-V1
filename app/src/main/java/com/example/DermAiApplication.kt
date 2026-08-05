package com.example

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck

class DermAiApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            FirebaseApp.initializeApp(this)
            val firebaseAppCheck = FirebaseAppCheck.getInstance()
            AppCheckInitializer.initialize(firebaseAppCheck)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
