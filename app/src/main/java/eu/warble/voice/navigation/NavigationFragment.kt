package eu.warble.voice.navigation

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.indoorway.android.common.sdk.listeners.generic.Action0
import com.indoorway.android.common.sdk.listeners.generic.Action1
import com.indoorway.android.common.sdk.model.IndoorwayPosition
import com.indoorway.android.location.sdk.model.IndoorwayLocationSdkError
import eu.warble.voice.R
import kotlinx.android.synthetic.main.navigation_fragment.*

class NavigationFragment : Fragment(), NavigationContract.View {

    override lateinit var presenter: NavigationContract.Presenter
    private val positionChangeListener: Action1<IndoorwayPosition> by lazy {
        Action1<IndoorwayPosition> { onPositionChange(it) }
    }

    private val stateErrorListener: Action1<IndoorwayLocationSdkError> by lazy {
        Action1<IndoorwayLocationSdkError> { onStateError(it) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.navigation_fragment, container, false)

        return view
    }

    private fun onPositionChange(it: IndoorwayPosition) {

    }

    private fun onStateError(it: IndoorwayLocationSdkError) {}

    companion object {
        fun newInstance() = NavigationFragment()
    }

}