package eu.warble.voice.navigation

import com.indoorway.android.common.sdk.listeners.generic.Action1
import com.indoorway.android.common.sdk.model.*
import com.indoorway.android.location.sdk.IndoorwayLocationSdk
import com.indoorway.android.location.sdk.model.IndoorwayLocationSdkError
import com.indoorway.android.location.sdk.model.IndoorwayLocationSdkState
import eu.warble.voice.util.AStarSearch

object NavigationService {
    private lateinit var positionChangeListener: Action1<IndoorwayPosition>
    private lateinit var stateErrorListener: Action1<IndoorwayLocationSdkError>
    private lateinit var onStartListener: Action1<IndoorwayPosition?>
    lateinit var latestNonNullPosition: IndoorwayPosition
    lateinit var paths: List<IndoorwayNode>
    lateinit var mapObjects: List<IndoorwayObjectParameters>

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
        this.mapObjects = indoorwayMap.objects
    }

    fun findPath(to: IndoorwayObjectParameters): List<IndoorwayNode> {
        return AStarSearch(paths).findPath (
                findClosestNode(latestNonNullPosition.coordinates), findClosestNode(to.centerPoint))
    }

    fun start(positionChangeListener: Action1<IndoorwayPosition> ,stateErrorListener: Action1<IndoorwayLocationSdkError>,
              onStartListener: Action1<IndoorwayPosition?>) {
        this.stateErrorListener = stateErrorListener
        this.onStartListener = onStartListener
        this.positionChangeListener = positionChangeListener
        IndoorwayLocationSdk.instance().position().onChange().register(positionChangeListener)
        IndoorwayLocationSdk.instance().state().onError().register(stateErrorListener)
        IndoorwayLocationSdk.instance().state().onChange().register(onStateChangeListener)
    }

    fun stop() {
        IndoorwayLocationSdk.instance().position().onChange().unregister(positionChangeListener)
        IndoorwayLocationSdk.instance().state().onError().unregister(stateErrorListener)
        IndoorwayLocationSdk.instance().state().onChange().unregister(onStateChangeListener)
    }

    private fun findClosestNode(coordinates: Coordinates): IndoorwayNode {
        var minDistance = Double.MAX_VALUE
        var closestNode: IndoorwayNode = paths.first()
        paths.forEach {
            val distance = it.coordinates.getDistanceTo(coordinates)
            if (distance < minDistance) {
                minDistance = distance
                closestNode = it
            }
        }
        return closestNode
    }

    private fun latestPosition(): IndoorwayPosition? = IndoorwayLocationSdk.instance().position().latest()
}