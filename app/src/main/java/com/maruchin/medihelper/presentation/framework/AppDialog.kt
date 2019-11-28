package com.maruchin.medihelper.presentation.framework

import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.maruchin.medihelper.R

abstract class AppDialog : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL,  R.style.AppTheme_Dialog)
    }

    override fun onStart() {
        super.onStart()
        dialog?.apply {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            window?.setLayout(width, height)
        }
    }
}