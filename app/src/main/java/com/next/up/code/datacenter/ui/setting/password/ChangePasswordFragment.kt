package com.next.up.code.datacenter.ui.setting.password

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.inyongtisto.myhelper.base.BaseFragment
import com.inyongtisto.myhelper.extension.toastError
import com.inyongtisto.myhelper.extension.toastInfo
import com.inyongtisto.myhelper.extension.toastSuccess
import com.next.up.code.core.data.Resource
import com.next.up.code.core.data.api.response.request.UpdatePasswordRequest
import com.next.up.code.core.session.BaseSharedPreferences
import com.next.up.code.datacenter.databinding.FragmentChangePasswordBinding
import com.next.up.code.datacenter.ui.setting.SettingViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class ChangePasswordFragment : BaseFragment() {

    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding
    private var root: View? = null
    private val viewModel: SettingViewModel by viewModel()
    private lateinit var sharedPreferences: SharedPreferences
    private var token = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentChangePasswordBinding.inflate(layoutInflater)
        root = binding?.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainButton()
        preferencesSetup()
        token = sharedPreferences.getString(BaseSharedPreferences.PREF_TOKEN, "").toString()
        binding?.btnSave?.setOnClickListener {
            if (binding?.edtPassword?.text?.isNotEmpty() == true && binding?.edtPasswordOld?.text?.isNotEmpty() == true && binding?.edtConfirm?.text?.isNotEmpty() == true) {
                updatePassword()
            } else {
                toastInfo("Harap isi dulu!")
            }
        }
    }


    private fun preferencesSetup() {
        sharedPreferences = requireContext().getSharedPreferences(
            BaseSharedPreferences.PREFERENCES_NAME,
            AppCompatActivity.MODE_PRIVATE
        )
    }

    private fun updatePassword() {
        val request = UpdatePasswordRequest(
            binding?.edtPasswordOld?.text?.toString().toString(),
            binding?.edtPassword?.text?.toString().toString(),
            binding?.edtConfirm?.text?.toString().toString(),

            )
        viewModel.updatePassword("Bearer $token", request)
            .observe(viewLifecycleOwner) { responseData ->
                when (responseData) {
                    is Resource.Success -> {
                        progress.dismiss()
                        toastSuccess("Berhasil mengubah password!")
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

    private fun mainButton() {
        binding?.apply {
            toolbar.title.text = "Ubah Password"
            toolbar.btnBack.setOnClickListener { requireActivity().onBackPressed() }
        }
    }
}