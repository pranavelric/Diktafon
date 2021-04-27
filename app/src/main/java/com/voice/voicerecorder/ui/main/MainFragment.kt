package com.voice.voicerecorder.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.voice.voicerecorder.MainActivity
import com.voice.voicerecorder.R
import com.voice.voicerecorder.databinding.FragmentMainBinding
import com.voice.voicerecorder.utils.setFullScreenWithBtmNav


class MainFragment : Fragment() {
    private var isRecording: Boolean = false
    private lateinit var binding: FragmentMainBinding
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
            findNavController().navigate(R.id.action_mainFragment_to_recordFragment)
        }
        binding.micCard.setOnClickListener {
            setRecordBtnClickListener()
        }
    }


    private fun setRecordBtnClickListener() {
        if (isRecording) {
            binding.recordBtn.setImageDrawable(
                activity?.let {
                    ContextCompat.getDrawable(it, R.drawable.ic_baseline_mic_off_24)
                }
            )
            isRecording = false

        } else {

            isRecording = true

            binding.recordBtn.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(it, R.drawable.ic_baseline_mic_24)
            }
            )

        }
    }


    override fun onStart() {
        super.onStart()
        (activity as MainActivity).setFullScreenWithBtmNav()

    }

}