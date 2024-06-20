package com.next.up.code.datacenter.ui.dialog

import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.inyongtisto.myhelper.extension.toastSuccess
import com.next.up.code.core.data.Resource
import com.next.up.code.core.session.BaseSharedPreferences
import com.next.up.code.datacenter.MainViewModel
import com.next.up.code.datacenter.R
import com.next.up.code.datacenter.databinding.FragmentDialogAddFolderBinding
import com.next.up.code.datacenter.ui.detail.DetailViewModel
import com.next.up.code.datacenter.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class DialogAddFolderFragment : DialogFragment() {

    private lateinit var binding: FragmentDialogAddFolderBinding
    private val viewModel: MainViewModel by viewModel()
    private val homeViewModel: HomeViewModel by viewModel()
    private val detailViewModel: DetailViewModel by viewModel()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(
            requireContext(), R.style.RoundedDialogStyle
        )

        setupDialog(dialog)

        preferencesSetup()

        val token = sharedPreferences.getString(BaseSharedPreferences.PREF_TOKEN, "").toString()
        setupMainButton(token)
        return dialog
    }

    private fun preferencesSetup() {
        sharedPreferences = requireContext().getSharedPreferences(
            BaseSharedPreferences.PREFERENCES_NAME,
            AppCompatActivity.MODE_PRIVATE
        )
    }

    private fun setupDialog(dialog: Dialog) {
        binding = FragmentDialogAddFolderBinding.inflate(layoutInflater)
        val view = binding.root
        dialog.setContentView(view)
    }

    private fun setupMainButton(token: String) {
        binding.btnSave.setOnClickListener {
            uploadFolder(token)
        }
        binding.btnCancel.setOnClickListener { dismiss() }
    }

    private fun uploadFolder(token: String) {
        var parentNameId = arguments?.getInt("parentNameId", 0)
        val folderName = binding.edtFolder.text.toString()
        val fromFragment = arguments?.getString("fromFragment").toString()
        if (parentNameId == 0) {
            parentNameId = null
        }
        if (folderName != null) {
            viewModel.uploadFolder("Bearer $token", folderName, parentNameId)
                .observe(this@DialogAddFolderFragment) { responseData ->
                    when (responseData) {
                        is Resource.Success -> {
                            dismiss()
                            toastSuccess("Berhasil menambahkan folder $folderName")
                            homeViewModel.getListFile("Bearer $token")
                                .observe(this@DialogAddFolderFragment) {
                                }


                        }

                        is Resource.Loading -> {
                        }

                        is Resource.Error -> {

                        }
                    }
                }
        } else {
            binding.edtFolder.error = "Harap isi folder"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dismiss()
    }


}