package eu.warble.voice.util

import com.indoorway.android.common.sdk.model.Coordinates
import com.indoorway.android.common.sdk.model.IndoorwayObjectParameters
import java.util.*

object Tools {

    /**
     * Parse the voice input to commands
     * @param text input text from voice recognition
     * @return return the hall we want navigate to or a command
     */
    fun recogniseCommand(text: String): String {
        val speech: List<String> = text.split(" ")
        return when(speech.first()){
            "go" -> text
            "find" -> text
            "stop" -> "stop"
            else -> "repeat"
        }
    }

    fun checkObjectAvailable(said: String, objList: List<IndoorwayObjectParameters>): IndoorwayObjectParameters? {
        var res: IndoorwayObjectParameters? = null
        val saidL = said.toLowerCase()
        objList.forEach {
            val name = it.name as String
            if (saidL.contains(name.toLowerCase()))
                res = it
        }
        return res
    }
}