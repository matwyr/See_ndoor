package eu.warble.voice

import android.app.Application
import com.indoorway.android.common.sdk.IndoorwaySdk

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        IndoorwaySdk.initContext(this)
        IndoorwaySdk.configure("acdd0179-3ea5-421b-8035-e96009d61a77")
    }
}