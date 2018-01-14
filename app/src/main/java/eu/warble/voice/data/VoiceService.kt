package eu.warble.voice.data

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import eu.warble.voice.R
import eu.warble.voice.navigation.NavigationFragment
import java.util.*

object VoiceService {
    var isStarted = false
    lateinit var textToSpeech: TextToSpeech

    fun start(context: Context, voiceServiceCallback: VoiceServiceCallback) {
        textToSpeech = TextToSpeech(context, { status: Int ->
            if (status == TextToSpeech.SUCCESS) {
                val res = textToSpeech.setLanguage(Locale.US)
                if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED)
                    voiceServiceCallback.onError(context.getString(R.string.error_language_not_supported))
                else {
                    isStarted = true
                    voiceServiceCallback.onStarted()
                }
            }else
                voiceServiceCallback.onError(context.getString(R.string.error_tts_init_failed))
        })
    }

    fun speak(toSay: String) {
        if (isStarted)
            textToSpeech.speak(toSay, TextToSpeech.QUEUE_ADD, null, null)
    }

    fun recordVoice(context: Context, startActivityForResult: (intent: Intent, requestCode: Int) -> Unit) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, context.getString(R.string.speech_prompt))
        try {
            startActivityForResult(intent, NavigationFragment.REQ_CODE_SPEECH_INPUT)
        } catch (a: ActivityNotFoundException) {
            val appPackageName = "com.google.android.googlequicksearchbox"
            try {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)))
            } catch (anfe: android.content.ActivityNotFoundException) {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)))
            }
        }
    }

    /**
     * Call on onDestroy()
     */
    fun stop(){
        textToSpeech.stop()
        textToSpeech.shutdown()
    }

    interface VoiceServiceCallback {
        fun onStarted()
        fun onError(error: String)
    }
}
