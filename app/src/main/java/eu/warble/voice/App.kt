package eu.warble.voice

import android.app.Application
import com.indoorway.android.common.sdk.IndoorwaySdk

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        IndoorwaySdk.initContext(this)
        IndoorwaySdk.configure("a34e1447-c5d1-4c8e-b53d-33901f8b83ab")
    }
}