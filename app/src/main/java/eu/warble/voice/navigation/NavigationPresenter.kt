package eu.warble.voice.navigation

import com.indoorway.android.common.model.map.elements.IndoorObject
import com.indoorway.android.common.sdk.IndoorwaySdk
import com.indoorway.android.common.sdk.listeners.generic.Action0
import com.indoorway.android.common.sdk.listeners.generic.Action1
import com.indoorway.android.common.sdk.model.Coordinates
import com.indoorway.android.common.sdk.model.IndoorwayNode
import com.indoorway.android.common.sdk.model.IndoorwayObjectParameters
import com.indoorway.android.common.sdk.model.IndoorwayPosition
import com.indoorway.android.location.sdk.IndoorwayLocationSdk
import com.indoorway.android.location.sdk.model.IndoorwayLocationSdkError
import eu.warble.voice.data.VoiceRepository

class NavigationPresenter(val voiceRepository: VoiceRepository, val navigationView: NavigationContract.View)
    : NavigationContract.Presenter {

    var latestDestinationPoint: IndoorwayObjectParameters? = null

    private val positionChangeListener: Action1<IndoorwayPosition> by lazy {
        Action1<IndoorwayPosition> { onPositionChange(it) }
    }

    private val stateErrorListener: Action1<IndoorwayLocationSdkError> by lazy {
        Action1<IndoorwayLocationSdkError> { onStateError(it) }
    }

    init {
        navigationView.presenter = this
    }

    override fun start() {
        //saySomething("Tell where you want to go")

    }

    /**
     * Result will be on result method
     */
    override fun recordVoice() {
        //VoiceRepository.record
    }

    override fun saySomething(toSay: String) {

    }

    override fun navigate(to: String) {

    }

    override fun result(requestCode: Int, resultCode: Int) {

    }

    private fun currentPosition(): IndoorwayPosition? = IndoorwayLocationSdk.instance().position().latest()

    private fun onPositionChange(it: IndoorwayPosition) {

    }

    private fun startPositioningService() {
        IndoorwayLocationSdk.instance().position().onChange().register(positionChangeListener)
        IndoorwayLocationSdk.instance().state().onError().register(stateErrorListener)
    }

    private fun stopPositioningService() {
        IndoorwayLocationSdk.instance().position().onChange().unregister(positionChangeListener)
        IndoorwayLocationSdk.instance().state().onError().unregister(stateErrorListener)
    }

    override fun pause() {
        stopPositioningService()
    }

    override fun resume() {
        startPositioningService()
    }

    private fun onStateError(error: IndoorwayLocationSdkError) {
        when(error) {
            IndoorwayLocationSdkError.BleNotSupported -> { navigationView.showError("Bluetooth Low Energy is not supported") }
            is IndoorwayLocationSdkError.MissingPermission -> {navigationView.showError("Some permissions are missing") }
            IndoorwayLocationSdkError.BluetoothDisabled -> { navigationView.showError("Bluetooth is disabled") }
            IndoorwayLocationSdkError.LocationDisabled -> { navigationView.showError("Location is disabled") }
            IndoorwayLocationSdkError.UnableToFetchData -> { navigationView.showError("Network-related error, service will be restarted on network connection established") }
            IndoorwayLocationSdkError.NoRadioMaps -> { navigationView.showError("Measurements have to be taken in order to use location") }
        }
    }

    private fun ss(){
        IndoorwaySdk.instance()
                .map()
                .details("<building UUID>", "<map UUID>")
                .setOnCompletedListener(Action1 {

                })
                .setOnFailedListener(Action1 {
                    // handle error, original exception is given on e.getCause()
                })
                .execute()
    }
}