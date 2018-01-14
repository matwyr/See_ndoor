package eu.warble.voice.navigation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
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
import java.util.*
import android.content.ActivityNotFoundException
import android.graphics.Color
import android.net.Uri
import android.view.HapticFeedbackConstants
import com.indoorway.android.common.sdk.model.Coordinates
import com.indoorway.android.common.sdk.model.IndoorwayNode
import com.indoorway.android.common.sdk.model.IndoorwayPosition
import com.indoorway.android.map.sdk.view.drawable.figures.DrawableCircle
import com.indoorway.android.map.sdk.view.drawable.layers.Layer
import com.indoorway.android.map.sdk.view.drawable.layers.MarkersLayer
import kotlinx.android.synthetic.main.loading_screen.*


class NavigationFragment : Fragment(), NavigationContract.View {
    override lateinit var presenter: NavigationContract.Presenter

    private val pathLayer: MarkersLayer by lazy { mapView.marker.addLayer(9f) }
    var lastPaths: List<IndoorwayNode>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.navigation_fragment, container, false)
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        presenter.start()
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
            clickScreen.bringToFront()
            clickScreen.setOnLongClickListener {
                presenter.recordVoice()
                clickScreen.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                return@setOnLongClickListener true
            }
        }else {
            mapView.bringToFront()
            clickScreen.setOnLongClickListener(null)
        }
    }

    override fun startActForResult(intent: Intent, requestCode: Int) {
        startActivityForResult(intent, requestCode)
    }

    override fun printPathAtMap(dots: List<IndoorwayNode>?) {
        //remove previous path
        lastPaths?.forEach { pathLayer.remove(it.id.toString()) }
        //add new path
        dots?.forEach {
            pathLayer.add(
                    DrawableCircle(
                            it.id.toString(),
                            0.4f, // radius in meters, eg. 0.4f
                            Color.RED, // circle background color, eg. Color.RED
                            Color.BLUE, // color of outline, eg. Color.BLUE
                            0.1f, // width of outline in meters, eg. 0.1f
                            it.coordinates // coordinates of circle center point
                    )
            )
        }
        lastPaths = dots
    }

    override fun printCurrentPosition(position: IndoorwayPosition) {
        mapView.position.setPosition(position, false)
    }

    override fun getMContext(): Context? {
        return context
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
        super.onResume()
        presenter.resume()
    }

    override fun onPause() {
        presenter.pause()
        super.onPause()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter.result(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        presenter.destroy()
        super.onDestroy()
    }

    companion object {
        fun newInstance() = NavigationFragment()
        val REQ_CODE_SPEECH_INPUT = 100
    }

}