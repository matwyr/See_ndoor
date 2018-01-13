package eu.warble.voice.navigation

import com.indoorway.android.common.sdk.listeners.generic.Action1
import com.indoorway.android.common.sdk.model.IndoorwayMap
import com.indoorway.android.common.sdk.model.IndoorwayNode
import com.indoorway.android.common.sdk.model.IndoorwayPosition
import com.indoorway.android.location.sdk.IndoorwayLocationSdk
import com.indoorway.android.location.sdk.model.IndoorwayLocationSdkError
import com.indoorway.android.location.sdk.model.IndoorwayLocationSdkState

object NavigationService {

    private lateinit var positionChangeListener: Action1<IndoorwayPosition>
    private lateinit var stateErrorListener: Action1<IndoorwayLocationSdkError>
    private lateinit var onStartListener: Action1<IndoorwayPosition?>
    private lateinit var paths: List<IndoorwayNode>

    val onMapLoadCompletedListener: Action1<IndoorwayMap> by lazy { Action1<IndoorwayMap> { onMapLoaded(it) } }

    private val onStateChangeListener: Action1<IndoorwayLocationSdkState> by lazy {
        Action1<IndoorwayLocationSdkState> {
            when(it.name){
                "LOCATING_FOREGROUND" -> onStartListener.onAction(latestPosition())
            }
        }
    }

    private fun onMapLoaded(indoorwayMap: IndoorwayMap) {
        this.paths = indoorwayMap.paths

    }

    fun isStarted() = latestPosition() != null

    fun start(positionChangeListener: Action1<IndoorwayPosition>,
              stateErrorListener: Action1<IndoorwayLocationSdkError>,
              onStartListener: Action1<IndoorwayPosition?>) {
        this.positionChangeListener = positionChangeListener
        this.stateErrorListener = stateErrorListener
        this.onStartListener = onStartListener
        IndoorwayLocationSdk.instance().position().onChange().register(positionChangeListener)
        IndoorwayLocationSdk.instance().state().onError().register(stateErrorListener)
        IndoorwayLocationSdk.instance().state().onChange().register(onStateChangeListener)
    }

    fun stop() {
        IndoorwayLocationSdk.instance().position().onChange().unregister(positionChangeListener)
        IndoorwayLocationSdk.instance().state().onError().unregister(stateErrorListener)
        IndoorwayLocationSdk.instance().state().onChange().unregister(onStateChangeListener)
    }

    fun latestPosition(): IndoorwayPosition? = IndoorwayLocationSdk.instance().position().latest()
}