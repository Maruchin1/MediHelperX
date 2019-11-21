package com.example.medihelper.presentation.feature.launcher

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.example.medihelper.R
import com.example.medihelper.databinding.FragmentWelcomeBinding
import com.example.medihelper.presentation.framework.bind
import kotlinx.android.synthetic.main.fragment_welcome.*

class WelcomeFragment : Fragment() {

    private val directions by lazy { WelcomeFragmentDirections }

    fun onClickNavToMainPersonFragment() = findNavController().navigate(
        directions.toMainPersonFragment(),
        getSharedLogoExtras()
    )

    fun onClickNavToLoginFragment() = findNavController().navigate(
        directions.toLoginFragment(),
        getSharedLogoExtras()
    )

    fun onClickNavToPatronConnectFragment() = findNavController().navigate(
        directions.toPatronConnectFragment(),
        getSharedLogoExtras()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(LauncherTransitions()) {
            sharedElementEnterTransition = getWelcomeEnterShared(requireContext())
            enterTransition = getWelcomeEnter()
            exitTransition = getWelcomeExit()
            reenterTransition = getWelcomeReenter()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return bind<FragmentWelcomeBinding>(
            inflater = inflater,
            container = container,
            layoutResId = R.layout.fragment_welcome
        )
    }

    private fun getSharedLogoExtras() = FragmentNavigatorExtras(
        lay_logo to "lay_logo_option"
    )
}