package eu.warble.voice.navigation

import com.indoorway.android.common.sdk.listeners.generic.Action0
import com.indoorway.android.common.sdk.listeners.generic.Action1
import com.indoorway.android.common.sdk.model.IndoorwayObjectParameters
import com.indoorway.android.common.sdk.model.IndoorwayPosition
import com.indoorway.android.location.sdk.model.IndoorwayLocationSdkError
import eu.warble.voice.data.VoiceRepository

class NavigationPresenter(val voiceRepository: VoiceRepository, val navigationView: NavigationContract.View)
    : NavigationContract.Presenter {

    var mapViewLoaded = false

    init {
        navigationView.presenter = this
    }

    override fun start() {
        saySomething("Determining location")
    }

    /**
     * Result will be on result method
     */
    override fun recordVoice() {
        //VoiceRepository.record
    }

    override fun saySomething(toSay: String) {

    }

    override fun navigate(to: IndoorwayObjectParameters) {
        //val pathRepo = Injection.providePathRepository
        //val path = pathRepo.findPath(to)
    }

    override fun parseVoice(said: String?) {
        if (said != null) {
            when(said){
                "stop" -> {

                }
                "repeat" -> {
                    saySomething("Please repeat")
                }
                else -> {
                    //val object = Tools.checkObjectAvailable(said)
                    //if(object!= null){
                    //     navigate(object)
                    //}else{
                    //     saySomething("Room is not existing")
                    //}
                }
            }
        }
    }

    private fun onPositionChange(it: IndoorwayPosition) {

    }

    override fun result(requestCode: Int, resultCode: Int) {

    }

    private fun loadMapView(buildingUUID: String, mapUUID: String){
        navigationView.loadMap(buildingUUID, mapUUID,
                onMapLoadCompletedListener = Action1 {
                    NavigationService.onMapLoadCompletedListener.onAction(it)
                    navigationView.activateLongClickListener(true)
                    saySomething("If you want to find room, press the screen and just say go, and room number")
                }, onMapLoadFailedListener = Action0 {
                    navigationView.showError("MapLoadFailed")
                }
        )
    }

    override fun pause() {
        NavigationService.stop()
    }

    override fun resume() {
        NavigationService.start(
                positionChangeListener = Action1 {onPositionChange(it)},
                stateErrorListener = Action1 {onStateError(it)},
                onStartListener = Action1 { onLocationDetermined(it) }
        )
    }

    private fun onLocationDetermined(position: IndoorwayPosition?){
        if (position != null){
            loadMapView(position.buildingUuid, position.mapUuid)
        }else
            navigationView.showError("Indoorway error -> latest position is null," +
                    " but STATE = LOCATING_FOREGROUND")
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