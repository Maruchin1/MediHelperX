package com.maruchin.medihelper.presentation.framework

import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView

class BaseViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
    val view = binding.root

    fun bind(displayData: Any, handler: Any? = null, viewModel: ViewModel? = null) {
        binding.apply {
            setVariable(BR.displayData, displayData)
            handler?.let { setVariable(BR.handler, it) }
            viewModel?.let { setVariable(BR.viewModel, it) }
            binding.executePendingBindings()
        }
    }
}