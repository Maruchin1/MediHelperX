package com.example.medihelper.mainapp.medickit


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide

import com.example.medihelper.R
import com.example.medihelper.databinding.FragmentAddMedicineBinding
import com.example.medihelper.localdatabase.entities.MedicineType
import com.example.medihelper.mainapp.MainActivity
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.android.synthetic.main.fragment_add_medicine.*


class AddMedicineFragment : Fragment() {
    private val TAG = AddMedicineFragment::class.simpleName
    private val REQUEST_IMAGE_CAPTURE = 1

    private lateinit var viewModel: AddMedicineViewModel
    private lateinit var medicineTypeAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.run {
            viewModel = ViewModelProviders.of(this).get(AddMedicineViewModel::class.java)
            medicineTypeAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return bindLayout(inflater, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMainActivity()
        observeMedicineTypeList()
        observeTmpFile()
        setupSpinMedicineType()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val photoFile = viewModel.tmpPhotoFileLive.value
            Glide.with(this)
                .load(photoFile)
                .centerCrop()
                .into(img_photo)
        }
    }

    fun onClickBack(view: View) {
        findNavController().popBackStack()
    }

    fun onClickPhoto(view: View) {
        activity?.let {
            val intent = viewModel.takePhotoIntent(it)
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
    }

    fun onClickExpireDate(view: View) {
        context?.let {
            viewModel.showExpireDateDialogPicker(it)
        }
    }

    private fun bindLayout(inflater: LayoutInflater, container: ViewGroup?): View {
        val binding: FragmentAddMedicineBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_medicine, container, false)
        binding.viewModel = viewModel
        binding.handler = this
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    private fun setupMainActivity() {
        activity?.let {
            (it as MainActivity).run {
                val fab = findViewById<ExtendedFloatingActionButton>(R.id.btn_floating_action)
                fab.apply {
                    setIconResource(R.drawable.round_save_white_48)
                    text = "Zapisz"
                    extend()
                    setOnClickListener { saveNewMedicine() }
                }
            }
        }
    }

    private fun saveNewMedicine() {
        context?.let {
            val medicineAdded = viewModel.saveNewMedicine(it)
            if (medicineAdded) {
                findNavController().popBackStack()
                viewModel.resetViewModel()
            }
        }
    }

    private fun observeDefMedicineId() {
        viewModel.defaultMedicineIdLive.observe(viewLifecycleOwner, Observer {  })
    }

    private fun observeTmpFile() {
        viewModel.tmpPhotoFileLive.observe(viewLifecycleOwner, Observer {  })
    }

    private fun setupSpinMedicineType() {
        spin_medicine_type.adapter = medicineTypeAdapter
        spin_medicine_type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) { }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.setMedicineType(position)
            }
        }
    }

    private fun observeMedicineTypeList() {
        viewModel.medicineTypesListLive.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "medicineTypesList change = $it")
            it?.let { list -> setMedicineTypeSpinnerItems(list) }
        })
    }

    private fun setMedicineTypeSpinnerItems(list: List<MedicineType>) {
        val medicineTypesNamesList = ArrayList<String>()
        list.forEach {
            medicineTypesNamesList.add(it.typeName)
        }
        medicineTypeAdapter.apply {
            clear()
            addAll(medicineTypesNamesList)
            notifyDataSetChanged()
        }
    }

    companion object {
        var capturedPhotoUri: Uri? = null
    }
}
