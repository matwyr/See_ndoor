package eu.warble.voice.navigation

import com.indoorway.android.common.sdk.model.Coordinates
import java.util.*

object NavigationRecorder {
    private var coordinatesList = LinkedList<Coordinates>()


    fun start() {

    }

    /**
     * Call in onResume()
     */
    fun resume() {

    }

    /**
     * Call in onPause()
     */
    fun pause() {

    }

    fun stop(): List<Coordinates> {
        return coordinatesList
    }
}