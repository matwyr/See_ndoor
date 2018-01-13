package eu.warble.voice.navigation

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eu.warble.voice.R

class NavigationFragment : Fragment(), NavigationContract.View {
    override lateinit var presenter: NavigationContract.Presenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.navigation_fragment, container, false)

        view.setOnLongClickListener {
            presenter.recordVoice()
            return@setOnLongClickListener true
        }

        return view
    }

    override fun showLoading(show: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showMap(show: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showError(error: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        fun newInstance() = NavigationFragment()
    }

}