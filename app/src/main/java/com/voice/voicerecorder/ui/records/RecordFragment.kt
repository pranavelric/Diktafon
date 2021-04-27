package com.voice.voicerecorder.ui.records

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.voice.voicerecorder.databinding.FragmentRecordBinding


class RecordFragment : Fragment() {



    private lateinit var binding: FragmentRecordBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecordBinding.inflate(inflater, container, false)
        setData()
        setClickListeners()
        return binding.root
    }

    private fun setClickListeners() {



    }



    private fun setData() {

    }


}