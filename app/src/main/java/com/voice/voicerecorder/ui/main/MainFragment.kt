package com.voice.voicerecorder.ui.main

import android.app.AlertDialog
import android.content.DialogInterface
import android.media.MediaRecorder
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.voice.voicerecorder.ui.activities.MainActivity
import com.voice.voicerecorder.R
import com.voice.voicerecorder.databinding.FragmentMainBinding
import com.voice.voicerecorder.utils.setFullScreenWithBtmNav
import java.text.SimpleDateFormat
import java.util.*


class MainFragment : Fragment() {
    private var isRecording: Boolean = false
    private lateinit var binding: FragmentMainBinding

    private var mediaRecorder: MediaRecorder? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)

        setClickListeners()

        return binding.root
    }

    private fun setClickListeners() {


        binding.list.setOnClickListener {

            if (isRecording) {
                showAlertDialog()
            } else {
                findNavController().navigate(R.id.action_mainFragment_to_recordFragment)
            }

        }

        binding.micCard.setOnClickListener {
            setRecordBtnClickListener()
        }
    }

    private fun showAlertDialog() {
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setPositiveButton(
            "Ok"
        ) { dialog, which ->

            stopRecording()
            findNavController().navigate(R.id.action_mainFragment_to_recordFragment)
            isRecording = false
        }
        dialogBuilder.setNegativeButton("Cancel", null)

        val dialog = dialogBuilder.create()

        dialog.setTitle("Audio still recording")
        dialog.setMessage("Are you sure you want to stop recording?")
        dialog.show()

    }


    private fun setRecordBtnClickListener() {
        if (isRecording) {


            stopRecording()

            binding.recordBtn.setImageDrawable(
                activity?.let {
                    ContextCompat.getDrawable(it, R.drawable.ic_baseline_mic_24)
                }
            )
            isRecording = false

        } else {


            if ((activity as MainActivity).checkPermissions()) {
                isRecording = true
                startRecording()
                binding.recordBtn.setImageDrawable(activity?.let {
                    ContextCompat.getDrawable(it, R.drawable.ic_baseline_mic_off_24)
                }
                )

            }

        }
    }

    private fun stopRecording() {


        binding.timer.stop()
        mediaRecorder?.stop()
        mediaRecorder?.release()
        mediaRecorder = null
        binding.headingText.text = "Recording stopped, file saved"
        binding.recordBtn.setImageDrawable(activity?.let {
            ContextCompat.getDrawable(it, R.drawable.ic_baseline_mic_24)
        }
        )
        isRecording = false

    }

    private fun startRecording() {

        binding.timer.base = SystemClock.elapsedRealtime()
        binding.timer.start()


        val filepath = activity?.getExternalFilesDir("/")?.absolutePath
        val formatter = SimpleDateFormat("yyyy_MM_dd_hh_ss", Locale.ENGLISH)
        val filename = "filename" + formatter.format(Date()) + ".3gp"


        binding.headingText.text = "Recording started for file: ${filename}"


        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile("$filepath/$filename")
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        }
        try {
            mediaRecorder?.prepare()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mediaRecorder?.start()
    }


    override fun onStart() {
        super.onStart()
        (activity as MainActivity).setFullScreenWithBtmNav()

    }

    override fun onStop() {
        super.onStop()
        if (isRecording)
            stopRecording()
    }
}