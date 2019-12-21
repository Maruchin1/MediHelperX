package com.maruchin.medihelper.presentation.feature.profiles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.leochuan.CarouselLayoutManager
import com.leochuan.CenterSnapHelper
import com.leochuan.ScaleLayoutManager
import com.maruchin.medihelper.R
import com.maruchin.medihelper.databinding.DialogProfileBinding
import com.maruchin.medihelper.domain.model.MedicinePlanItem
import com.maruchin.medihelper.domain.model.ProfileItem
import com.maruchin.medihelper.presentation.dialogs.ConfirmDialog
import com.maruchin.medihelper.presentation.feature.calendar.CalendarFragmentDirections
import com.maruchin.medihelper.presentation.framework.*
import kotlinx.android.synthetic.main.dialog_profile.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileDialog : BaseBottomDialog<DialogProfileBinding>(R.layout.dialog_profile, collapsing = true) {
    override val TAG: String
        get() = "ProfileDialog"

    private val viewModel: ProfileViewModel by viewModel()
    private val directions by lazy { CalendarFragmentDirections }

    fun onClickAddProfile() {
        requireParentFragment().findNavController().navigate(
            directions.toAddEditProfileFragment(
                editProfileId = null
            )
        )
    }

    fun onClickEditProfile() {
        requireParentFragment().findNavController()
            .navigate(
                directions.toAddEditProfileFragment(
                    editProfileId = viewModel.selectedProfileId
                )
            )
    }

    fun onClickDeleteProfile() {
        ConfirmDialog(
            title = "Usuń profil",
            message = "Wybrany profil zostanie usunięty, wraz z jego wpisami w kalendarzu. Czy chcesz kontynuować?",
            iconResId = R.drawable.round_delete_black_36
        ).apply {
            setColorPrimary(viewModel.colorPrimary.value)
            setOnConfirmClickListener {
                viewModel.deleteProfile()
            }
        }.show(childFragmentManager)
    }

    fun onClickMedicinePlanDetails(medicinePlanId: String) {
        requireParentFragment().findNavController().navigate(
            directions.toMedicinePlanDetailsFragment(
                medicinePlanId
            )
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.bindingViewModel = viewModel
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupProfileRecyclerView()
        setupMedicinesPlansRecyclerView()
        setupToolbarMenu()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.selectedProfilePosition.observe(viewLifecycleOwner, Observer { position ->
            recycler_view_profile.scrollToPosition(position)
        })
        viewModel.mainProfileSelected.observe(viewLifecycleOwner, Observer { mainSelected ->
            setEditDeleteProfileEnabled(!mainSelected)
        })
        viewModel.profileItemsAvailable.observe(viewLifecycleOwner, Observer {
            root_lay.beginDelayedFade()
        })
        viewModel.medicinesPlansAvailable.observe(viewLifecycleOwner, Observer {
            lay_medicines_plans.beginDelayedFade()
        })
    }

    private fun setupProfileRecyclerView() {
        recycler_view_profile.apply {
            adapter = ProfileAdapter()
            layoutManager = ScaleLayoutManager.Builder(requireContext(), 0)
                .setMinScale(0.75f)
                .setOrientation(CarouselLayoutManager.HORIZONTAL)
                .build()
            CenterSnapHelper().attachToRecyclerView(this)
        }
        setupProfileSelectedListener()
    }

    private fun setupProfileSelectedListener() {
        recycler_view_profile.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val layoutManager = recyclerView.layoutManager as ScaleLayoutManager
                    val selectedPosition = layoutManager.currentPosition
                    viewModel.selectProfile(selectedPosition)
                }
            }
        })
    }

    private fun setupMedicinesPlansRecyclerView() {
        recycler_view_medicine_plan.apply {
            adapter = MedicinePlanAdapter()
        }
    }

    private fun setupToolbarMenu() {
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.btn_add -> onClickAddProfile()
                R.id.btn_edit -> onClickEditProfile()
                R.id.btn_delete -> onClickDeleteProfile()
            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun setEditDeleteProfileEnabled(enabled: Boolean) {
        with(toolbar.menu) {
            val btnDelete = findItem(R.id.btn_delete)
            if (btnDelete.isVisible != enabled) {
                toolbar.beginDelayedTransition()
                btnDelete.isVisible = enabled
            }
        }
    }

    private inner class ProfileAdapter : BaseRecyclerAdapter<ProfileItem>(
        layoutResId = R.layout.rec_item_profile,
        lifecycleOwner = viewLifecycleOwner,
        itemsSource = viewModel.profileItems
    ) {
        override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
            val item = itemsList[position]
            holder.bind(item, this@ProfileDialog)
        }
    }

    private inner class MedicinePlanAdapter : BaseRecyclerAdapter<MedicinePlanItem>(
        layoutResId = R.layout.rec_item_medicine_plan,
        lifecycleOwner = viewLifecycleOwner,
        itemsSource = viewModel.medicinesPlans,
        areItemsTheSameFun = { oldItem, newItem -> oldItem.medicinePlanId == newItem.medicinePlanId }
    ) {
        override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
            val item = itemsList[position]
            holder.bind(item, this@ProfileDialog, viewModel = viewModel)
        }
    }
}