package com.next.up.code.datacenter.ui.setting.detail

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.github.drjacky.imagepicker.ImagePicker
import com.inyongtisto.myhelper.base.BaseFragment
import com.inyongtisto.myhelper.extension.toMultipartBody
import com.inyongtisto.myhelper.extension.toRequestBody
import com.inyongtisto.myhelper.extension.toastError
import com.inyongtisto.myhelper.extension.toastSuccess
import com.next.up.code.core.data.Resource
import com.next.up.code.core.session.BaseSharedPreferences
import com.next.up.code.datacenter.R
import com.next.up.code.datacenter.databinding.FragmentChangeProfileBinding
import com.next.up.code.datacenter.ui.setting.SettingViewModel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class ChangeProfileFragment : BaseFragment(), View.OnClickListener {

    private var _binding: FragmentChangeProfileBinding? = null
    private val binding get() = _binding
    private var root: View? = null
    private val viewModel: SettingViewModel by viewModel()


    private lateinit var sharedPreferences: SharedPreferences
    private var fileImage: File? = null
    private var token = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentChangeProfileBinding.inflate(layoutInflater)
        root = binding?.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupProfile()
        mainButton()
        preferencesSetup()
        token = sharedPreferences.getString(BaseSharedPreferences.PREF_TOKEN, "").toString()

    }

    private fun mainButton() {
        binding?.apply {
            btnChangeImage.setOnClickListener {
                pickImage()
            }
            toolbar.btnBack.setOnClickListener { requireActivity().onBackPressed() }
            toolbar.title.text = "Ubah Profil"

            btnSave.setOnClickListener(this@ChangeProfileFragment)
        }

    }

    private fun pickImage() {
        ImagePicker.with(requireActivity()).crop().maxResultSize(1080, 1080, true)
            .createIntentFromDialog { launcher.launch(it) }
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data!!
                fileImage = File(uri.path!!)
                Picasso.get().load(fileImage!!).into(binding?.ivProfile)
            }
        }

    private fun setupProfile() {
        viewModel.getUser().observe(viewLifecycleOwner) {
            binding?.apply {
                acFullName.setText(it.personResponsible)
                acNip.setText(it.nip)
                acEmail.setText(it.email)
                Picasso.get().load(it.picture)
                    .into(ivProfile)
            }
        }
    }

    private fun preferencesSetup() {
        sharedPreferences = requireContext().getSharedPreferences(
            BaseSharedPreferences.PREFERENCES_NAME,
            AppCompatActivity.MODE_PRIVATE
        )
    }

    private fun updateProfileWithoutPicture() {
        viewModel.updateProfile(
            "Bearer $token",
            null,
            "PUT".toRequestBody(),
            binding?.acFullName?.text.toString().toRequestBody(),
            binding?.acNip?.text.toString().toRequestBody()
        ).observe(viewLifecycleOwner) { responseData ->
            when (responseData) {
                is Resource.Success -> {
                    progress.dismiss()
                    toastSuccess("Berhasil di update")

                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.deleteAllUser()

                    }
                    viewModel.insertUserToDB("Bearer $token").observe(viewLifecycleOwner) {
                    }
                    requireActivity().onBackPressed()
                }

                is Resource.Loading -> {
                    progress.show()
                }

                is Resource.Error -> {
                    progress.dismiss()
                    toastError(responseData.message.toString())
                }
            }
        }
    }

    private fun updateProfileWithPicture() {
        viewModel.updateProfile(
            "Bearer $token",
            fileImage.toMultipartBody("picture"),
            "PUT".toRequestBody(),
            binding?.acFullName?.text.toString().toRequestBody(),
            binding?.acNip?.text.toString().toRequestBody()
        ).observe(viewLifecycleOwner) { responseData ->
            when (responseData) {
                is Resource.Success -> {
                    progress.dismiss()
                    toastSuccess("Berhasil di update")

                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.deleteAllUser()

                    }
                    viewModel.insertUserToDB("Bearer $token").observe(viewLifecycleOwner) {
                    }
                    requireActivity().onBackPressed()
                }

                is Resource.Loading -> {
                    progress.show()
                }

                is Resource.Error -> {
                    progress.dismiss()
                    toastError(responseData.message.toString())
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_save -> {
                if (fileImage != null) {
                    updateProfileWithPicture()
                } else {
                    updateProfileWithoutPicture()
                }
            }
        }
    }
}