package eu.warble.voice.navigation

import android.app.Activity.RESULT_OK
import android.content.Intent
import com.indoorway.android.common.sdk.listeners.generic.Action0
import com.indoorway.android.common.sdk.listeners.generic.Action1
import com.indoorway.android.common.sdk.model.IndoorwayObjectParameters
import com.indoorway.android.common.sdk.model.IndoorwayPosition
import com.indoorway.android.location.sdk.model.IndoorwayLocationSdkError
import android.speech.RecognizerIntent
import com.indoorway.android.common.sdk.model.IndoorwayNode
import eu.warble.voice.R
import eu.warble.voice.data.VoiceService
import eu.warble.voice.util.Tools


class NavigationPresenter(val navigationView: NavigationContract.View)
    : NavigationContract.Presenter {
    private var mapIsLoaded: Boolean = false

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

    override fun parseVoiceCommand(said: String?) {
        if (said != null) {
            when(said){
                "stop" -> stopNavigation()
                "repeat" -> VoiceService.speak(getString(R.string.please_repeat))
                else -> {
                    val obj = Tools.checkObjectAvailable(said, NavigationService.mapObjects)
                    if(obj != null)
                        navigate(obj)
                    else
                        VoiceService.speak(getString(R.string.room_is_not_existing))
                }
            }
        }
    }

    private fun navigate(obj: IndoorwayObjectParameters) {
        val path = NavigationService.findPath(obj)
        printPathAtMap(path)
    }

    private fun stopNavigation() {
        navigationView.printPathAtMap(null)

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
            parseVoiceCommand(command)
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

    private fun onPositionChange(it: IndoorwayPosition) {
        NavigationService.latestNonNullPosition = it
        printCurrentPosition(it)
    }

    override fun destroy() {
        VoiceService.stop()
    }

    private fun getString(resId: Int): String {
        val context = navigationView.getMContext()
        return if (context != null){
            context.getString(resId)
        }else{
            "null"
        }
    }

    private fun onStateError(error: IndoorwayLocationSdkError) {
        when(error) {
            IndoorwayLocationSdkError.BleNotSupported -> { navigationView.showError("Bluetooth Low Energy is not supported") }
            is IndoorwayLocationSdkError.MissingPermission -> { navigationView.showError("Some permissions are missing") }
            IndoorwayLocationSdkError.BluetoothDisabled -> { navigationView.showError("Bluetooth is disabled") }
            IndoorwayLocationSdkError.LocationDisabled -> { navigationView.showError("Location is disabled") }
            IndoorwayLocationSdkError.UnableToFetchData -> { navigationView.showError("Network-related error, service will be restarted on network connection established") }
            IndoorwayLocationSdkError.NoRadioMaps -> { navigationView.showError("Measurements have to be taken in order to use location") }
        }
    }
}