package com.example.medihelper.mainapp.schedule


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.medihelper.R
import com.example.medihelper.databinding.FragmentMedicinePlanListHostBinding
import kotlinx.android.synthetic.main.fragment_medicine_plan_list_host.*

class MedicinePlanListHostFragment : Fragment() {

    private lateinit var viewModel: ScheduleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.run {
            viewModel = ViewModelProviders.of(this).get(ScheduleViewModel::class.java)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentMedicinePlanListHostBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_medicine_plan_list_host, container, false)
        binding.viewModel = viewModel
        binding.handler = this
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupTabs()
    }

    private fun setupToolbar() {
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    private fun setupTabs() {
        view_pager_tabs.adapter = MedicinePlanListPagerAdapter()
        tab_layout.setupWithViewPager(view_pager_tabs)
    }

    // Inner classes
    inner class MedicinePlanListPagerAdapter : FragmentPagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        private val pagesList = listOf(
            MedicinePlanListFragment().apply { medicinePlanType = ScheduleViewModel.MedicinePlanType.ONGOING},
            MedicinePlanListFragment().apply { medicinePlanType = ScheduleViewModel.MedicinePlanType.ENDED }
        )

        override fun getCount(): Int {
            return pagesList.size
        }

        override fun getItem(position: Int): Fragment {
            return pagesList[position]
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return pagesList[position].medicinePlanType?.pageTitle
        }

    }
}