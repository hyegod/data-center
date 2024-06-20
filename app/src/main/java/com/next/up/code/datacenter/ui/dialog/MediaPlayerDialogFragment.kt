package com.next.up.code.datacenter.ui.dialog

import android.app.Dialog
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.next.up.code.datacenter.R
import com.next.up.code.datacenter.databinding.FragmentMediaPlayerBinding
import java.util.Timer
import java.util.TimerTask


class MediaPlayerDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentMediaPlayerBinding

    private var startTime = 0L
    private var isPlaying = false
    private var time: Timer? = null
    private var elapsedTime = 0L

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(
            requireContext(), R.style.RoundedDialogStyle
        )
        setupDialogCategoryDetail(dialog)
        return dialog
    }

    private fun setupDialogCategoryDetail(dialog: Dialog) {
        binding = FragmentMediaPlayerBinding.inflate(layoutInflater)
        val view = binding.root
        dialog.setContentView(view)
        if (ContextCompat.checkSelfPermission(
                requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Jika izin belum diberikan, minta izin kepada pengguna
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        } else {

            setupMediaAudio()
        }
    }

    private fun setupMediaAudio() {

        binding.togglePlay.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val uri = arguments?.getString("path").toString()
                if (isPlaying) {
                    resumePlayback()
                } else {
                    startPlayback(uri)
                }

            } else {
                pausePlayback()
            }

        }
    }

    private fun startPlayback(url: String) {
        mediaPlayer = MediaPlayer()
        try {
            mediaPlayer?.setDataSource(url)
            mediaPlayer?.prepareAsync()

            mediaPlayer?.setOnPreparedListener { mp ->
                mp.start()
                if (!isPlaying) {
                    startTime = System.currentTimeMillis() - elapsedTime
                }
                isPlaying = true
                startTimer()
            }

            mediaPlayer?.setOnCompletionListener {
                resetPlayback()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun pausePlayback() {
        mediaPlayer?.pause()
        stopTimer()
    }

    private fun resumePlayback() {
        mediaPlayer?.seekTo(elapsedTime.toInt())
        mediaPlayer?.start()
        startTime = System.currentTimeMillis() - elapsedTime
        startTimer()
    }


    private fun startTimer() {
        time = Timer()
        time?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val currentTime = System.currentTimeMillis()
                elapsedTime = currentTime - startTime
                requireActivity().runOnUiThread {
                    binding.tvTime.text = formatTime(elapsedTime)
                }

                if (!mediaPlayer?.isPlaying!!) {
                    stopTimer()
                    resetPlayback()
                }
            }
        }, 0, 1000)
    }


    private fun stopTimer() {
        time?.cancel()
        time = null
    }

    private fun resetPlayback() {
        mediaPlayer?.seekTo(0)
        elapsedTime = 0
        isPlaying = false
        requireActivity().runOnUiThread {
            binding.tvTime.text = formatTime(elapsedTime)
            binding.togglePlay.isChecked = false
        }
    }

    private fun formatTime(time: Long): String {
        val seconds = time / 1000
        val minutes = seconds / 60
        val secondsFormatted = seconds % 60
        return String.format("%02d:%02d", minutes, secondsFormatted)
    }

    private fun stopPlayback() {
        mediaPlayer?.release()
        mediaPlayer = null
        stopTimer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopPlayback()
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                setupMediaAudio()
            } else {
                // Izin tidak diberikan, Anda dapat memberikan pesan kepada pengguna atau melakukan tindakan lain
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 123
    }
}