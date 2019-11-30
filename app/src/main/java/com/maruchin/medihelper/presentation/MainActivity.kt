package com.maruchin.medihelper.presentation

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Gravity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.maruchin.medihelper.R
import com.maruchin.medihelper.domain.deviceapi.NotificationApi
import com.maruchin.medihelper.domain.usecases.ServerConnectionUseCases
import com.maruchin.medihelper.presentation.feature.launcher.LauncherActivity
import com.maruchin.medihelper.presentation.framework.BaseActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject


class MainActivity : BaseActivity() {

    private val serverConnectionUseCases: ServerConnectionUseCases by inject()
    private val notificationApi: NotificationApi by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupBottomNav()
        notificationApi.enablePeriodicRemindersUpdate()
    }

    override fun onResume() {
        super.onResume()
        serverConnectionUseCases.enqueueServerSync()
    }

    override fun onPause() {
        super.onPause()
        serverConnectionUseCases.enqueueServerSync()
    }

    fun setMainColor(colorResID: Int) {
        window.statusBarColor = ContextCompat.getColor(this, colorResID)
        val states = arrayOf(
            intArrayOf(-android.R.attr.state_selected), // unchecked
            intArrayOf(android.R.attr.state_selected)  // pressed
        )
        val colors = intArrayOf(
            ContextCompat.getColor(this, R.color.colorTextTertiary),
            ContextCompat.getColor(this, colorResID)
        )
        val colorStateList = ColorStateList(states, colors)
        bottom_nav.run {
            itemIconTintList = colorStateList
            itemTextColor = colorStateList
        }
        bottom_nav.itemIconTintList = ColorStateList(states, colors)
    }

    fun showSnackbar(message: String) {
        val snackbar = Snackbar.make(frame_fragments, message, Snackbar.LENGTH_SHORT)
            .setAnimationMode(Snackbar.ANIMATION_MODE_FADE)
        val layoutParams = snackbar.view.layoutParams as CoordinatorLayout.LayoutParams
        layoutParams.anchorId = R.id.bottom_nav
        layoutParams.anchorGravity = Gravity.TOP
        layoutParams.gravity = Gravity.TOP
        snackbar.view.layoutParams = layoutParams
        snackbar.show()
    }

    fun navigateToOptions() {
        findNavController(R.id.nav_host_fragment).navigate(R.id.optionsFragment)
    }

    fun restartActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(intent)
    }

    fun restartApp() {
        val intent = Intent(this, LauncherActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setupBottomNav() {
        val navController = findNavController(R.id.nav_host_fragment)
        bottom_nav.setupWithNavController(navController)
    }
}
