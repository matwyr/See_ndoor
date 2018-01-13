package eu.warble.voice.navigation

import com.indoorway.android.common.sdk.model.Coordinates
import com.indoorway.android.common.sdk.model.IndoorwayNode
import eu.warble.voice.BasePresenter
import eu.warble.voice.BaseView


interface NavigationContract {

    interface View: BaseView<Presenter>{
        fun showLoading(show: Boolean)
        fun showMap(show: Boolean)
        fun showError(error: String)
    }

    interface Presenter: BasePresenter {
        fun pause()
        fun resume()
        fun result(requestCode: Int, resultCode: Int)
        fun recordVoice()
        fun saySomething(toSay: String)
        fun navigate(from: IndoorwayNode, to: IndoorwayNode)
    }

}