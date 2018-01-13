package eu.warble.voice.navigation

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import eu.warble.voice.R
import eu.warble.voice.util.Injection
import eu.warble.voice.util.replaceFragmentInActivity

class NavigationActivity : AppCompatActivity() {

    private lateinit var navigationPresenter: NavigationPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.navigation_activity)

        val navigationFragment = supportFragmentManager.findFragmentById(R.id.contentFrame)
            as NavigationFragment? ?: NavigationFragment.newInstance().also {
            replaceFragmentInActivity(it, R.id.contentFrame)
        }

        navigationPresenter = NavigationPresenter(Injection.provideVoiceRepository(), navigationFragment)
    }

    override fun onResume() {
        super.onResume()
        navigationPresenter.resume()
    }

    override fun onPause() {
        navigationPresenter.pause()
        super.onPause()
    }
}