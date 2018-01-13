package eu.warble.voice.navigation

import eu.warble.voice.BasePresenter
import eu.warble.voice.BaseView


interface NavigationContract {

    interface View: BaseView<Presenter>{

    }

    interface Presenter: BasePresenter {

    }
}