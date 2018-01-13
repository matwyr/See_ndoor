package eu.warble.voice.navigation

import com.indoorway.android.common.sdk.listeners.generic.Action0
import com.indoorway.android.common.sdk.listeners.generic.Action1
import com.indoorway.android.common.sdk.model.Coordinates
import com.indoorway.android.common.sdk.model.IndoorwayMap
import com.indoorway.android.common.sdk.model.IndoorwayNode
import com.indoorway.android.common.sdk.model.IndoorwayObjectParameters
import eu.warble.voice.BasePresenter
import eu.warble.voice.BaseView


interface NavigationContract {

    interface View: BaseView<Presenter>{
        fun showLoading(show: Boolean)
        fun showMap(show: Boolean)
        fun loadMap(buildingUUID: String, mapUUID: String,
                    onMapLoadCompletedListener: Action1<IndoorwayMap>,
                    onMapLoadFailedListener: Action0)
        fun showError(error: String)
        fun activateLongClickListener(activate: Boolean)
    }

    interface Presenter: BasePresenter {
        fun pause()
        fun resume()
        fun result(requestCode: Int, resultCode: Int)
        fun recordVoice()
        fun saySomething(toSay: String)
        fun navigate(to: IndoorwayObjectParameters)
        fun parseVoice(said: String?)
    }
}