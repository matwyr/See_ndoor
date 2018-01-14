package eu.warble.voice.navigation

import com.indoorway.android.common.sdk.listeners.generic.Action1
import com.indoorway.android.common.sdk.model.*
import com.indoorway.android.location.sdk.IndoorwayLocationSdk
import com.indoorway.android.location.sdk.model.IndoorwayLocationSdkError
import com.indoorway.android.location.sdk.model.IndoorwayLocationSdkState
import eu.warble.voice.data.model.Direction
import eu.warble.voice.data.model.NodeInfo
import eu.warble.voice.util.AStarSearch

object NavigationService {
    private lateinit var positionChangeListener: Action1<IndoorwayPosition>
    private lateinit var stateErrorListener: Action1<IndoorwayLocationSdkError>
    private lateinit var onStartListener: Action1<IndoorwayPosition?>
    lateinit var latestNonNullPosition: IndoorwayPosition
    lateinit var paths: List<IndoorwayNode>
    lateinit var mapObjects: List<IndoorwayObjectParameters>
    var navigablePath: LinkedHashMap<IndoorwayNode, NodeInfo>? = null
    var navIsRunning: Boolean = false

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

    fun navigableNodeInfoToString(nodeInfo: NodeInfo): String{
        return if (nodeInfo.direction != Direction.FINISH)
            "In ${nodeInfo.distance.toInt()} metres turn ${nodeInfo.direction.name}"
        else
            "In ${nodeInfo.distance.toInt()} will be destination point"
    }

    fun navigableAtNodeInfoToString(nodeInfo: NodeInfo): String{
        return if (nodeInfo.direction != Direction.FINISH)
            "Turn ${nodeInfo.direction.name}"
        else
            "Finish point"
    }

    private fun latestPosition(): IndoorwayPosition? = IndoorwayLocationSdk.instance().position().latest()

    fun atNode(it: IndoorwayPosition): Boolean {
        val node = navigablePath?.iterator()?.next()
        if (node != null){
            if (it.coordinates.getDistanceTo(node.key.coordinates) <= 2){
                return true
            }
        }
        return false
    }
}