package com.next.up.code.datacenter.ui.setting

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.inyongtisto.myhelper.base.BaseFragment
import com.next.up.code.core.session.BaseSharedPreferences
import com.next.up.code.datacenter.MainActivity
import com.next.up.code.datacenter.R
import com.next.up.code.datacenter.databinding.FragmentSettingBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingFragment : BaseFragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding
    private var root: View? = null
    private val viewModel: SettingViewModel by viewModel()

    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSettingBinding.inflate(layoutInflater)
        root = binding?.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUser()
        preferencesSetup()
        mainButton()
    }

    private fun mainButton() {
        binding?.apply {
            btnLogout.setOnClickListener {
                logout()
            }
            cvChangePassword.setOnClickListener {
                findNavController().navigate(R.id.action_menu_setting_to_changePasswordFragment)
            }
            cvChange.setOnClickListener {
                findNavController().navigate(R.id.action_menu_setting_to_changeProfileFragment)
            }
        }
    }

    private fun preferencesSetup() {
        sharedPreferences = requireActivity().getSharedPreferences(
            BaseSharedPreferences.PREFERENCES_NAME, AppCompatActivity.MODE_PRIVATE
        )
    }

    private fun setUser() {
        viewModel.getUser().observe(viewLifecycleOwner) { data ->
            binding?.apply {
                tvFullName.text = data?.personResponsible
                tvEmail.text = data?.email
                Picasso.get().load(data?.picture).error(R.drawable.ic_profile_pic)
                    .placeholder(R.drawable.ic_profile_pic).into(ivProfile)
            }
        }
    }

    private fun logout() {

        AlertDialog.Builder(requireContext()).setTitle("Pemberitahuan")
            .setMessage("Apakah Anda yakin ingin logout?")
            .setPositiveButton("Ya") { dialog: DialogInterface?, which: Int ->
                sharedPreferences.edit().clear().commit()
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.deleteAllUser()
                    viewModel.deleteAllFiles()
                }
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finishAffinity()
            }.setNegativeButton("Tidak", null).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}