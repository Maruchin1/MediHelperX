package com.maruchin.medihelper.presentation.framework

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.maruchin.medihelper.BR

abstract class BaseBottomDialog<T : ViewDataBinding>(
    private val layoutResId: Int,
    private val collapsing: Boolean = false
) : BottomSheetDialogFragment() {
    abstract val TAG: String

    fun show(fragmentManager: FragmentManager) = show(fragmentManager, TAG)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: T = DataBindingUtil.inflate(inflater, layoutResId, container, false)
        return binding.apply {
            setVariable(BR.handler, this@BaseBottomDialog)
            lifecycleOwner = viewLifecycleOwner
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!collapsing) {
            setupFullExpand(view)
        }
    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        if (collapsing) {
            setupCollapsing(dialog)
        }
    }

    private fun setupCollapsing(dialog: Dialog) {
        dialog.setOnShowListener {
            val bottomDialog = it as BottomSheetDialog
            val bottomSheet = bottomDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            val behavior = BottomSheetBehavior.from(bottomSheet!!)

            behavior.peekHeight = (Resources.getSystem().displayMetrics.heightPixels) / 2
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun setupFullExpand(view: View) {
        view.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val dialog = dialog as BottomSheetDialog?
                val bottomSheet =
                    dialog!!.findViewById(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
                val behavior = BottomSheetBehavior.from(bottomSheet!!)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        })
    }
}