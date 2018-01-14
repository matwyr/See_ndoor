package eu.warble.voice.navigation

import android.content.Context
import android.content.Intent
import com.indoorway.android.common.sdk.listeners.generic.Action0
import com.indoorway.android.common.sdk.listeners.generic.Action1
import com.indoorway.android.common.sdk.model.*
import eu.warble.voice.BasePresenter
import eu.warble.voice.BaseView


interface NavigationContract {

    interface View: BaseView<Presenter>{
        fun getMContext(): Context?
        fun showLoading(show: Boolean)
        fun showMap(show: Boolean)
        fun printPathAtMap(dots: List<IndoorwayNode>?)
        fun printCurrentPosition(position: IndoorwayPosition)
        fun loadMap(buildingUUID: String, mapUUID: String,
                    onMapLoadCompletedListener: Action1<IndoorwayMap>,
                    onMapLoadFailedListener: Action0)
        fun showError(error: String)
        fun activateLongClickListener(activate: Boolean)
        fun startActForResult(intent: Intent, requestCode: Int)
        fun requestPermissions(permission: String, REQUEST_PERMISSION_CODE: Int)
    }

    interface Presenter: BasePresenter {
        fun pause()
        fun resume()
        fun result(requestCode: Int, resultCode: Int, data: Intent?)
        fun doVoiceCommand(command: String?)
        fun destroy()
        fun recordVoice()
    }
}