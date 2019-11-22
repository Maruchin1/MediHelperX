package com.example.medihelper.presentation.utils

import androidx.fragment.app.FragmentManager
import com.example.medihelper.presentation.dialogs.LoadingDialog

class LoadingScreen {

    private var loadingDialog: LoadingDialog? = null

    fun showLoadingScreen(fragmentManager: FragmentManager) {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog()
            loadingDialog?.show(fragmentManager)
        }
    }

    fun closeLoadingScreen() {
        if (loadingDialog != null) {
            loadingDialog?.dismiss()
            loadingDialog = null
        }
    }
}