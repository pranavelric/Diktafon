package com.voice.voicerecorder.ui.main

import android.app.AlertDialog
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.voice.voicerecorder.R
import com.voice.voicerecorder.data.Record
import com.voice.voicerecorder.databinding.FragmentMainBinding
import com.voice.voicerecorder.ui.activities.MainActivity
import com.voice.voicerecorder.utils.RecordState
import com.voice.voicerecorder.utils.setFullScreenWithBtmNav
import com.voice.voicerecorder.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class MainFragment : Fragment() {

    private val viewModel: RecordViewModel by lazy {
        ViewModelProvider(this).get(RecordViewModel::class.java)
    }

    private var isRecording: Boolean = false
    private lateinit var binding: FragmentMainBinding
    private var title: String? = null
    private var filename: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)

        subscribe()
        setClickListeners()

        return binding.root
    }

    private fun subscribe() {
        viewModel.recordState.observe(viewLifecycleOwner, { recordState ->
            when (recordState) {
                is RecordState.Recording -> {

                    //disable the edit text
                    isRecording = true
                    switchButtonDrawble(isRecording)
                }
                is RecordState.Done<Record> -> {
                    isRecording = false
                    switchButtonDrawble(isRecording)

                    context?.toast("file Saved!")
                }
                is RecordState.Error -> {
                    // request focus and chronometer 0
                }
            }

        })
    }

    private fun switchButtonDrawble(recording: Boolean) {

        if (recording) {
            binding.recordBtn.setImageDrawable(
                activity?.let {
                    ContextCompat.getDrawable(it, R.drawable.ic_baseline_mic_off_24)
                }
            )
        } else {
            binding.recordBtn.setImageDrawable(
                activity?.let {
                    ContextCompat.getDrawable(it, R.drawable.ic_baseline_mic_24)
                }
            )
        }

    }

    private fun setClickListeners() {


        binding.list.setOnClickListener {

            if (isRecording) {
                showAlertDialog()
            } else {
                findNavController().navigate(R.id.action_mainFragment_to_recordFragment)
            }
        }

        binding.micCard.setOnClickListener { setRecordBtnClickListener() }
    }

    private fun showAlertDialog() {
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setPositiveButton(
            "Ok"
        ) { _, _ ->

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
        } else {
            startRecording()
        }
    }


    private fun stopRecording() {
        binding.timer.stop()
        binding.audioNameEt.text?.clear()
        binding.audioNameEt.isEnabled = true


        filename = if (title == null || title.isNullOrEmpty()) {
            filename
        } else {
            title + ".3gp"
        }


        viewModel.stopRecording(filename)
        binding.headingText.text = "Recording stopped, file saved"

    }

    private fun startRecording() {

        binding.timer.base = SystemClock.elapsedRealtime()
        binding.timer.start()
        binding.audioNameEt.isEnabled = false


        val filepath = activity?.getExternalFilesDir("/")?.absolutePath
        val formatter = SimpleDateFormat("yyyy_MM_dd_hh_ss", Locale.ENGLISH)
        title = binding.audioNameEt.text.toString()

        filename = if (title == null || title.isNullOrEmpty()) {
            "filename" + formatter.format(Date()) + ".3gp"
        } else {
            title + ".3gp"
        }




        binding.headingText.text = "Recording started for file: ${filename}"

        if (filepath != null) {
            viewModel.startRecording(filename, filepath)
        } else {
            context?.toast("filepath Error")
        }

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