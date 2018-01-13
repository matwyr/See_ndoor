package eu.warble.voice.navigation

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.indoorway.android.common.sdk.listeners.generic.Action0
import com.indoorway.android.common.sdk.listeners.generic.Action1
import com.indoorway.android.common.sdk.model.IndoorwayMap
import eu.warble.voice.R
import kotlinx.android.synthetic.main.navigation_fragment.*

class NavigationFragment : Fragment(), NavigationContract.View {
    override lateinit var presenter: NavigationContract.Presenter
    private lateinit var layout: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.navigation_fragment, container, false)
        layout = view
        return view
    }

    override fun loadMap(buildingUUID: String, mapUUID: String,
                         onMapLoadCompletedListener: Action1<IndoorwayMap>,
                         onMapLoadFailedListener: Action0) {
        mapView.onMapLoadCompletedListener = onMapLoadCompletedListener
        mapView.onMapLoadFailedListener = onMapLoadFailedListener
        mapView.load(buildingUUID, mapUUID)
    }

    override fun activateLongClickListener(activate: Boolean) {
        if (activate) {
            layout.setOnLongClickListener {
                presenter.recordVoice()
                return@setOnLongClickListener true
            }
        }else {
            layout.setOnLongClickListener(null)
        }
    }

    override fun showLoading(show: Boolean) {
        loading_screen.visibility = if(show) View.VISIBLE else View.GONE
    }

    override fun showMap(show: Boolean) {
        mapView.visibility = if(show) View.VISIBLE else View.GONE
    }

    override fun showError(error: String) {
        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
    }

    override fun onResume() {
        presenter.resume()
        super.onResume()
    }

    override fun onPause() {
        presenter.pause()
        super.onPause()
    }

    companion object {
        fun newInstance() = NavigationFragment()
    }

}