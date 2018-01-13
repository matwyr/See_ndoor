package eu.warble.voice.navigation

import android.app.Activity.RESULT_OK
import android.content.Intent
import com.indoorway.android.common.sdk.listeners.generic.Action0
import com.indoorway.android.common.sdk.listeners.generic.Action1
import com.indoorway.android.common.sdk.model.IndoorwayObjectParameters
import com.indoorway.android.common.sdk.model.IndoorwayPosition
import com.indoorway.android.location.sdk.model.IndoorwayLocationSdkError
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import eu.warble.voice.util.Tools
import java.util.*
import android.support.v4.content.ContextCompat.startActivity
import android.content.ActivityNotFoundException
import android.net.Uri
import android.support.v4.app.ActivityCompat.startActivityForResult
import eu.warble.voice.R
import eu.warble.voice.navigation.NavigationFragment.Companion.REQ_CODE_SPEECH_INPUT


class NavigationPresenter(val navigationView: NavigationContract.View)
    : NavigationContract.Presenter {

    lateinit var textToSpeech: TextToSpeech

    init {
        navigationView.presenter = this
    }

    override fun start() {
        textToSpeech = TextToSpeech(navigationView.getMContext(), { status: Int ->
            if (status == TextToSpeech.SUCCESS) {
                val res = textToSpeech.setLanguage(Locale.US)
                if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED)
                    navigationView.showError("US Language is not supported")
            }else
                navigationView.showError("TTS Initialization Failed!")
        })
        saySomething("Determining location")
    }

    /**
     * Result will be on result method
     */
    override fun recordVoice() {
        navigationView.recordVoice()
    }

    override fun saySomething(toSay: String) {
        textToSpeech.speak(toSay, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun navigate(to: IndoorwayObjectParameters) {
        //val pathRepo = Injection.providePathRepository
        //val path = pathRepo.findPath(to)
    }

    override fun parseVoice(said: String?) {
        if (said != null) {
            when(said){
                "stop" -> NavigationService.stopNavigation()
                "repeat" -> saySomething("Please repeat")
                else -> {
                    val obj = Tools.checkObjectAvailable(said, NavigationService.mapObjects)
                    if(obj != null)
                        navigate(obj)
                    else
                        saySomething("Room is not existing")
                }
            }
        }
    }

    private fun onPositionChange(it: IndoorwayPosition) {

    }

    override fun result(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == NavigationFragment.REQ_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null){
            //return what user said
            val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            saySomething(result[0])
            //todo use this command to do following commands
            val command = Tools.recogniseRoom(result[0])
        }
    }

    private fun loadMapView(buildingUUID: String, mapUUID: String){
        navigationView.loadMap(buildingUUID, mapUUID,
                onMapLoadCompletedListener = Action1 {
                    NavigationService.onMapLoadCompletedListener.onAction(it)
                    navigationView.activateLongClickListener(true)
                    navigationView.showLoading(false)
                    navigationView.showMap(true)
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

    override fun destroy() {
        textToSpeech.stop();
        textToSpeech.shutdown();
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