package com.next.up.code.datacenter.ui.dialog

import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.inyongtisto.myhelper.extension.toastSuccess
import com.next.up.code.core.data.Resource
import com.next.up.code.core.session.BaseSharedPreferences
import com.next.up.code.datacenter.R
import com.next.up.code.datacenter.databinding.FragmentDialogAddFolderBinding
import com.next.up.code.datacenter.ui.file.FileViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class DialogEditFolderFragment : DialogFragment() {

    private lateinit var binding: FragmentDialogAddFolderBinding
    private val fileViewModel: FileViewModel by viewModel()

    private lateinit var sharedPreferences: SharedPreferences
    private var token = ""
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(
            requireContext(), R.style.RoundedDialogStyle
        )

        setupDialog(dialog)

        preferencesSetup()

        token = sharedPreferences.getString(BaseSharedPreferences.PREF_TOKEN, "").toString()
        setupMainButton()
        setupFolder()
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

    private fun setupMainButton() {
        binding.btnCancel.setOnClickListener { dismiss() }
        binding.btnSave.setOnClickListener { renameFolder() }
    }

    private fun setupFolder() {
        val folderName = arguments?.getString("folderName")
        binding.tvHeaderAddFolder.text = resources.getText(R.string.text_edit_folder)
        binding.edtFolder.setText(folderName)
    }

    private fun renameFolder() {
        val folderId = arguments?.getInt("folderId")
        fileViewModel.renameFolder("Bearer $token", folderId!!, binding.edtFolder.text.toString())
            .observe(this@DialogEditFolderFragment) { responseData ->
                when (responseData) {
                    is Resource.Success -> {
                        toastSuccess("Berhasil di Perbarui!")
                        CoroutineScope(Dispatchers.IO).launch {
                            fileViewModel.deleteAllFiles()
                        }
                        fileViewModel.getListFile("Bearer $token")
                            .observe(this@DialogEditFolderFragment) {
                            }

                        dismiss()
                        val fromFragment = arguments?.getString("fromFragment").toString()
                        if (fromFragment == "Property") {
                            requireActivity().onBackPressed()
                        }
                    }

                    is Resource.Loading -> {

                    }

                    is Resource.Error -> {

                    }
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        dismiss()
    }


}