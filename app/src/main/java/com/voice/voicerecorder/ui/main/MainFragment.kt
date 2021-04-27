package com.voice.voicerecorder.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.voice.voicerecorder.MainActivity
import com.voice.voicerecorder.R
import com.voice.voicerecorder.databinding.FragmentMainBinding
import com.voice.voicerecorder.utils.setFullScreenWithBtmNav


class MainFragment : Fragment() {

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
    }


    override fun onStart() {
        super.onStart()
        (activity as MainActivity).setFullScreenWithBtmNav()

    }

}