package com.maruchin.medihelper.presentation.framework

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import com.maruchin.medihelper.R
import com.maruchin.medihelper.presentation.LauncherActivity

abstract class BaseLauncherFragment<T : ViewDataBinding>(layoutResId: Int) : BaseFragment<T>(layoutResId) {

    protected val launcherActivity: LauncherActivity
        get() = requireActivity() as LauncherActivity

    fun setLightStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            launcherActivity.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    protected fun setStatusBarColor(colorId: Int) {
        launcherActivity.window?.statusBarColor = ContextCompat.getColor(requireContext(), colorId)
    }
}