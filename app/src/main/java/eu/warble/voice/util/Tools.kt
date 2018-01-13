package eu.warble.voice.util

import com.indoorway.android.common.sdk.model.IndoorwayObjectParameters

object Tools {

    /**
     * Parse the voice input to commands
     * @param text input text from voice recognition
     * @return return the hall we want navigate to or a command
     */
    fun recogniseRoom(text: String): String {
        val speech: List<String> = text.split(" ")
        for (i in speech.indices) {
            if (speech[i] == "go") {
                return if (i != speech.size - 1) {
                    if (speech[i + 1] == "stop")
                        "repeat"
                    else
                        speech[i + 1]
                } else
                    "repeat"
            } else if (speech[i] == "stop") {
                return "stop"
            }
        }
        return "repeat"
    }

    fun checkObjectAvailable(said: String, objList: List<IndoorwayObjectParameters>): IndoorwayObjectParameters? {
        var res: IndoorwayObjectParameters? = null
        objList.forEach {
            if (said == it.name)
                res = it
        }
        return res
    }
}