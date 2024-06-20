package com.next.up.code.datacenter

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.inyongtisto.myhelper.base.BaseActivity
import com.next.up.code.core.data.Resource
import com.next.up.code.core.session.BaseSharedPreferences
import com.next.up.code.datacenter.databinding.ActivityHomeBinding
import com.next.up.code.datacenter.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeActivity : BaseActivity() {
    private var _binding: ActivityHomeBinding? = null
    private val binding get() = _binding
    private var root: View? = null
    private val viewModel: HomeViewModel by viewModel()

    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityHomeBinding.inflate(layoutInflater)
        root = binding?.root
        setContentView(root)

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        val navController = findNavController(R.id.nav_host_home_fragment)

        navView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.documentsFragment || destination.id == R.id.imagesFragment
                || destination.id == R.id.audioFragment || destination.id == R.id.videosFragment
                || destination.id == R.id.viewerImageFragment || destination.id == R.id.driveFragment
                || destination.id == R.id.detailNewsFragment || destination.id == R.id.detailFileFragment
                || destination.id == R.id.changePasswordFragment || destination.id == R.id.changeProfileFragment
                || destination.id == R.id.viewerVideoFragment || destination.id == R.id.allLatestFileFragment
            ) {
                navView.visibility = View.GONE
            } else {
                navView.visibility = View.VISIBLE
            }
        }
        preferencesSetup()
        val token = sharedPreferences.getString(BaseSharedPreferences.PREF_TOKEN, "").toString()
        setupFile(token)
        getUser(token)
        getNews()

    }


    private fun preferencesSetup() {
        sharedPreferences = getSharedPreferences(
            BaseSharedPreferences.PREFERENCES_NAME,
            MODE_PRIVATE
        )
    }


    private fun getUser(token: String) {
        viewModel.insertUserToDB("Bearer $token").observe(this@HomeActivity) { responseData ->
            when (responseData) {
                is Resource.Success -> {
                    progress.dismiss()
                }

                is Resource.Loading -> {
                    progress.show()
                }

                is Resource.Error -> {
                    progress.dismiss()
                }
            }
        }
    }

    private fun setupFile(token: String) {
        viewModel.getListFile("Bearer $token").observe(this@HomeActivity) { responseData ->
            when (responseData) {
                is Resource.Success -> {
                    progress.dismiss()
                }

                is Resource.Loading -> {
                    progress.show()
                }

                is Resource.Error -> {
                    progress.dismiss()
                }

            }
        }
    }

    private fun getNews() {
        viewModel.getNews().observe(this) { responseData ->
            when (responseData) {
                is Resource.Success -> {
                    progress.dismiss()
                }

                is Resource.Loading -> {
                    progress.show()
                }

                is Resource.Error -> {
                    progress.dismiss()
                }

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}