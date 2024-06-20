package com.next.up.code.datacenter.ui.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.github.drjacky.imagepicker.ImagePicker
import com.next.up.code.core.session.BaseSharedPreferences
import com.next.up.code.datacenter.R
import com.next.up.code.datacenter.databinding.FragmentDialogAddBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class DialogAddFragment : DialogFragment(), View.OnClickListener {

    private lateinit var binding: FragmentDialogAddBinding
    private var file: File? = null
    private var token = ""
    private lateinit var sharedPreferences: SharedPreferences
    private var fromFragment = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(
            requireContext(), R.style.RoundedDialogStyle
        )

        setupDialog(dialog)
        preferencesSetup()

        token = sharedPreferences.getString(BaseSharedPreferences.PREF_TOKEN, "").toString()
        setupMainButton()
        fromFragment = arguments?.getString("fromFragment").toString()
        return dialog
    }

    private fun preferencesSetup() {
        sharedPreferences = requireContext().getSharedPreferences(
            BaseSharedPreferences.PREFERENCES_NAME, AppCompatActivity.MODE_PRIVATE
        )
    }

    private fun setupDialog(dialog: Dialog) {
        binding = FragmentDialogAddBinding.inflate(layoutInflater)
        val view = binding.root
        dialog.setContentView(view)
    }

    private fun setupMainButton() {

        binding.btnAddFolder.setOnClickListener(this@DialogAddFragment)
        binding.btnAddFile.setOnClickListener(this@DialogAddFragment)
        binding.btnTakePicture.setOnClickListener(this@DialogAddFragment)

    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data!!
                file = File(uri.path!!)
                uploadFile(file!!)

            }
        }

    private val launcherFile =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                if (uri != null) {
                    val fileDescriptor =
                        requireContext().contentResolver.openFileDescriptor(uri, "r")
                    if (fileDescriptor != null) {
                        val fileInputStream = FileInputStream(fileDescriptor.fileDescriptor)
                        val fileName = getFileName(uri)
                        val file = File(requireContext().cacheDir, fileName)
                        val fileOutputStream = FileOutputStream(file)
                        fileOutputStream.use { outputStream ->
                            fileInputStream.copyTo(outputStream)
                        }
                        fileDescriptor.close()
                        uploadFile(file)
                    }
                }
            }
        }

    private fun getFileName(uri: Uri): String {
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        val nameIndex = cursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor?.moveToFirst()
        val fileName = cursor?.getString(nameIndex ?: 0) ?: "temp_file"
        cursor?.close()
        return fileName
    }


    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        launcherFile.launch(intent)
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_add_folder -> {
                val parentNameId = arguments?.getInt("parentNameId", 0)
                val fromFragment = arguments?.getString("fromFragment")
                val bundle = Bundle()
                parentNameId?.let {
                    bundle.putInt("parentNameId", it)
                    bundle.putString("fromFragment", fromFragment)
                    val dialogAddFolderFragment = DialogAddFolderFragment()
                    dialogAddFolderFragment.arguments = bundle
                    val fragmentManager = requireFragmentManager()
                    fragmentManager.beginTransaction()
                        .add(dialogAddFolderFragment, "DialogAddFolderFragment").commit()
                    fragmentManager.beginTransaction().remove(this).commit()
                }
            }


            R.id.btn_add_file -> {
                openFilePicker()
            }

            R.id.btn_take_picture -> {
                launcher.launch(
                    ImagePicker.with(requireActivity()).crop().cameraOnly().createIntent()
                )
            }
        }
    }

    private fun uploadFile(file: File) {
        var parentNameId = arguments?.getInt("parentNameId", 0)
        if (parentNameId == 0) {
            parentNameId = null
        }
        val originalFileName = file.name
        val modifiedFileName = originalFileName.replace(" ", "_").lowercase()

        val uniqueFileName = addRandomNumberToFileName(modifiedFileName)
        val tempDir =
            requireContext().cacheDir
        val tempFile = File(tempDir, uniqueFileName)
        file.copyTo(tempFile)

        (parentFragment as? UploadFileListener)?.uploadFile(
            uniqueFileName,
            tempFile,
            parentNameId
        )

        dismiss()
    }

    interface UploadFileListener {
        fun uploadFile(fileName: String, filePart: File?, parentNameId: Int?)
    }

    private fun addRandomNumberToFileName(fileName: String): String {
        val randomValue = (0..1000).random() // Nilai acak antara 0 dan 1000
        return "$randomValue" + "_" + "$fileName"
    }


    override fun onDestroy() {
        super.onDestroy()
        launcher.unregister()
        launcherFile.unregister()
        file = null

    }


}