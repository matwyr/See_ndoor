package eu.warble.voice.util

import eu.warble.voice.data.VoiceService

object Injection {
    fun provideVoiceRepository(): VoiceService = VoiceService()
}