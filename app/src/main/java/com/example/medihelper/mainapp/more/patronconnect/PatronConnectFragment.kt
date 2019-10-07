package com.example.medihelper.mainapp.more.patronconnect

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.medihelper.R
import com.example.medihelper.custom.AppFullScreenDialog
import com.example.medihelper.custom.bind
import com.example.medihelper.databinding.FragmentPatronConnectBinding
import com.example.medihelper.services.LoadingDialogService
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.fragment_patron_connect.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PatronConnectFragment : AppFullScreenDialog() {
    private val TAG = "PatronConnectFragment"

    private val viewModel: PatronConnectViewModel by viewModel()
    private val directions by lazy { PatronConnectFragmentDirections }
    private val loadingDialogService: LoadingDialogService by inject()

    fun onClickScanQrCode() {
        IntentIntegrator.forSupportFragment(this).apply {
            setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
            setPrompt("Zeskanuj kod z aplikacji opiekuna")
            setOrientationLocked(true)
        }.initiateScan()
    }

    fun onClickConfirm() = viewModel.loadProfileData()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return bind<FragmentPatronConnectBinding>(
            inflater = inflater,
            layoutResId = R.layout.fragment_patron_connect,
            container = container,
            viewModel = viewModel
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupKeyEditTexts()
        observerViewModel()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        Log.i(TAG, "csacResult = $result")
        result?.contents?.let { connectionKey ->
            viewModel.setConnectionKey(connectionKey)
        }
    }

    private fun observerViewModel() {
        viewModel.errorConnectionKeyLive.observe(viewLifecycleOwner, Observer { errorMessage ->
            if (errorMessage != null) {
                showSnackbar(errorMessage)
            }
        })
        viewModel.loadingInProgressLive.observe(viewLifecycleOwner, Observer { inProgress ->
            if (inProgress == true) {
                loadingDialogService.showLoadingDialog(childFragmentManager)
            } else {
                loadingDialogService.dismissLoadingDialog()
            }
        })
        viewModel.patronConnectSuccessfulAction.observe(viewLifecycleOwner, Observer {
            findNavController().navigate(directions.toConnectedPersonFragment())
            dismiss()
        })
        viewModel.patronConnectErrorLive.observe(viewLifecycleOwner, Observer { errorMessageId ->
            if (errorMessageId != null) {
                showSnackbar(resources.getString(errorMessageId))
            }
        })
    }

    private fun setupKeyEditTexts() {
        val etxList = listOf(etx_key_1, etx_key_2, etx_key_3, etx_key_4, etx_key_5, etx_key_6)
        for (i in etxList.indices) {
            if ((i + 1) < etxList.size) {
                addTextChangeListener(etxList[i], etxList[i + 1])
            }
        }
    }

    private fun addTextChangeListener(currEtx: TextInputEditText, nextEtx: TextInputEditText) {
        currEtx.addTextChangedListener {
            if (it.toString().length == 1) {
                currEtx.clearFocus()
                nextEtx.run {
                    requestFocus()
                    isCursorVisible = true
                }
            }
        }
    }

    private fun showSnackbar(message: String) = Snackbar.make(root_lay, message, Snackbar.LENGTH_SHORT).apply {
        animationMode = Snackbar.ANIMATION_MODE_SLIDE
    }.show()
}