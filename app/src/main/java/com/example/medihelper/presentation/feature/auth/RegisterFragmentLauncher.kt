package com.example.medihelper.presentation.feature.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.medihelper.R
import com.example.medihelper.databinding.FragmentLauncherRegisterBinding
import com.example.medihelper.presentation.feature.launcher.LauncherOptionFragment
import com.example.medihelper.presentation.framework.bind
import com.example.medihelper.presentation.framework.showShortSnackbar
import com.example.medihelper.presentation.utils.LoadingScreen
import kotlinx.android.synthetic.main.fragment_launcher_register.*
import org.koin.android.ext.android.inject

class RegisterFragmentLauncher : LauncherOptionFragment() {

    private val viewModel: RegisterViewModel by inject()
    private val loadingScreen: LoadingScreen by inject()

    fun onClickConfirm() = viewModel.registerUser()

    fun onClickBack() = findNavController().popBackStack()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return bind<FragmentLauncherRegisterBinding>(
            inflater = inflater,
            container = container,
            layoutResId = R.layout.fragment_launcher_register,
            viewModel = viewModel
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.loadingInProgress.observe(viewLifecycleOwner, Observer { inProgress ->
            if (inProgress) {
                loadingScreen.showLoadingScreen(childFragmentManager)
            } else {
                loadingScreen.closeLoadingScreen()
            }
        })
        viewModel.errorRegister.observe(viewLifecycleOwner, Observer { errorMessage ->
            if (errorMessage == null) {
                findNavController().popBackStack()
            } else {
                showShortSnackbar(rootLayout = root_lay, message = errorMessage)
            }
        })
    }
}