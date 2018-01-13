package eu.warble.voice.navigation

import com.indoorway.android.common.sdk.listeners.generic.Action0
import com.indoorway.android.common.sdk.listeners.generic.Action1
import eu.warble.voice.data.VoiceRepository

class NavigationPresenter(val voiceRepository: VoiceRepository, val navigationView: NavigationContract.View)
    : NavigationContract.Presenter {

    init {
        navigationView.presenter = this
    }

    override fun start() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }




}