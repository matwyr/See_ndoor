package eu.warble.voice.util

import eu.warble.voice.data.VoiceRepository

object Injection {
    fun provideVoiceRepository(): VoiceRepository = VoiceRepository()
}