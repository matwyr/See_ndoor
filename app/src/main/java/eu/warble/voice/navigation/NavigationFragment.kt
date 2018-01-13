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
import android.net.Uri


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

    override fun mString(resId: Int): String {
        return getString(resId)
    }

    override fun recordVoice() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt))
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT)
        } catch (a: ActivityNotFoundException) {
            val appPackageName = "com.google.android.googlequicksearchbox"
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)))
            } catch (anfe: android.content.ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)))
            }
        }
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