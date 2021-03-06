package eu.warble.voice.navigation

import android.app.Activity.RESULT_OK
import android.content.Intent
import com.indoorway.android.common.sdk.listeners.generic.Action0
import com.indoorway.android.common.sdk.listeners.generic.Action1
import com.indoorway.android.location.sdk.model.IndoorwayLocationSdkError
import android.speech.RecognizerIntent
import com.indoorway.android.common.sdk.model.*
import eu.warble.voice.R
import eu.warble.voice.data.VisitorDataSource
import eu.warble.voice.data.VoiceService
import eu.warble.voice.data.model.Direction
import eu.warble.voice.data.model.NodeInfo
import eu.warble.voice.util.PathTranslator
import eu.warble.voice.util.Tools


class NavigationPresenter(val navigationView: NavigationContract.View)
    : NavigationContract.Presenter {
    private var mapIsLoaded: Boolean = false

    companion object {
        val REQUEST_PERMISSION_CODE: Int = 234
    }

    init {
        navigationView.presenter = this
    }

    override fun start() {
        val context = navigationView.getMContext()
        if (context != null) {
            VoiceService.start(context, object : VoiceService.VoiceServiceCallback{
                override fun onStarted() {
                    VoiceService.speak(context.getString(R.string.determining_location))
                }
                override fun onError(error: String) {
                    navigationView.showError(error)
                }
            })
        }
    }

    /**
     * Result will be on result method
     */
    override fun recordVoice() {
        val context = navigationView.getMContext()
        if (context != null)
            VoiceService.recordVoice(context, startActivityForResult = {
                intent, requestCode ->  navigationView.startActForResult(intent, requestCode)
        })
    }

    override fun doVoiceCommand(command: String?) {
        if (command != null) {
            when(command){
                "stop" -> stopNavigation()
                "repeat" -> VoiceService.speak(getString(R.string.please_repeat))
                else -> {
                    if (command.startsWith("go")) {
                        val obj = Tools.checkObjectAvailable(command, NavigationService.mapObjects)
                        if (obj != null)
                            startNavigating(obj.centerPoint)
                        else
                            VoiceService.speak(getString(R.string.room_is_not_existing))
                    }else if (command.startsWith("find ")){
                        findVisitor(command)
                    }
                }
            }
        }
    }

    private fun findVisitor(command: String?){
        val name = command?.substringAfter("find ")
        if (name != null)
            VisitorDataSource.findPerson(name, object : VisitorDataSource.OnPersonFindListener{
                override fun found(visitorLocation: VisitorLocation?) {
                    val coordinates = visitorLocation?.position?.coordinates
                    val lastSeen = getString(R.string.person_last_seen_on) + " " +
                            Tools.dateToString(visitorLocation?.timestamp)
                    VoiceService.speak(lastSeen)
                    if (coordinates != null) {
                        startNavigating(coordinates)
                    } else
                        VoiceService.speak(getString(R.string.person_currently_not_available))
                }
                override fun notFound() {
                    VoiceService.speak(getString(R.string.person_not_found))
                }
            })
    }

    private fun startNavigating(coordinates: Coordinates) {
        if (NavigationService.navIsRunning)
            stopNavigation()
        val path = NavigationService.findPath(coordinates)
        printPathAtMap(path)
        NavigationService.navigablePath = PathTranslator.translate(path)
        NavigationService.navIsRunning = true
        val node = NavigationService.navigablePath?.iterator()?.next()
        if (node != null)
            VoiceService.speak(NavigationService.navigableNodeInfoToString(node.value))
    }

    private fun stopNavigation() {
        navigationView.printPathAtMap(null)
        NavigationService.navigablePath = null
        NavigationService.navIsRunning = false
    }

    private fun printPathAtMap(dots: List<IndoorwayNode>?){
        navigationView.printPathAtMap(dots)
    }

    private fun printCurrentPosition(position: IndoorwayPosition){
        navigationView.printCurrentPosition(position)
    }

    override fun result(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == NavigationFragment.REQ_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null){
            val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            VoiceService.speak(result[0])
            val command = Tools.recogniseCommand(result[0])
            doVoiceCommand(command)
        }
    }

    private fun loadMapView(buildingUUID: String, mapUUID: String){
        navigationView.loadMap(buildingUUID, mapUUID,
                onMapLoadCompletedListener = Action1 {
                    NavigationService.onMapLoadCompletedListener.onAction(it)
                    navigationView.activateLongClickListener(true)
                    navigationView.showLoading(false)
                    navigationView.showMap(true)
                    mapIsLoaded = true
                    VoiceService.speak(getString(R.string.init_tip))
                }, onMapLoadFailedListener = Action0 {
                    navigationView.showError(getString(R.string.error_map_load_failed))
                }
        )
    }

    override fun pause() {
        NavigationService.stop()
    }

    override fun resume() {
        NavigationService.start(
                positionChangeListener = Action1 { onPositionChange(it) } ,
                stateErrorListener = Action1 {onStateError(it)},
                onStartListener = Action1 { onLocationDetermined() }
        )
    }

    private fun onLocationDetermined(){
        if (!mapIsLoaded)
            loadMapView(NavigationService.latestNonNullPosition.buildingUuid,
                        NavigationService.latestNonNullPosition.mapUuid)
    }

    private var latestSaid: MutableMap.MutableEntry<IndoorwayNode, NodeInfo>? = null

    private fun onPositionChange(it: IndoorwayPosition) {
        NavigationService.latestNonNullPosition = it
        printCurrentPosition(it)
        val currentNode = NavigationService.getCurrentAtNode(it)
        if (NavigationService.navIsRunning && currentNode != null && currentNode != latestSaid) {
            if (currentNode.value.direction == Direction.FINISH) {
                stopNavigation()
                VoiceService.speak(NavigationService.navigableAtNodeInfoToString(currentNode.value))
                return
            }
            VoiceService.speak(NavigationService.navigableAtNodeInfoToString(currentNode.value))
            VoiceService.speak(NavigationService.navigableNodeInfoToString(currentNode.value))
            latestSaid = currentNode
        }
    }

    override fun destroy() {
        VoiceService.stop()
    }

    private fun getString(resId: Int): String {
        val context = navigationView.getMContext()
        return if (context != null) {
            context.getString(resId)
        } else {
            "null"
        }
    }

    private fun onStateError(error: IndoorwayLocationSdkError) {
        when(error) {
            IndoorwayLocationSdkError.BleNotSupported -> { VoiceService.speak(getString(R.string.your_device_is_not_supported)) }
            is IndoorwayLocationSdkError.MissingPermission -> {
                val permission = error.permission
                navigationView.requestPermissions(permission, REQUEST_PERMISSION_CODE)
            }
            IndoorwayLocationSdkError.BluetoothDisabled -> { VoiceService.speak(getString(R.string.enable_bluetooth)) }
            IndoorwayLocationSdkError.LocationDisabled -> { VoiceService.speak(getString(R.string.enable_location)) }
            IndoorwayLocationSdkError.UnableToFetchData -> { navigationView.showError("Network-related error, service will be restarted on network connection established") }
            IndoorwayLocationSdkError.NoRadioMaps -> { navigationView.showError("Measurements have to be taken in order to use location") }
        }
    }
}