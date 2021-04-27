package com.voice.voicerecorder.ui.records

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.voice.voicerecorder.adapters.VoiceRecordAdapter

import com.voice.voicerecorder.databinding.FragmentRecordBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class RecordFragment : Fragment() {


    @Inject
    lateinit var voiceRecordAdapter: VoiceRecordAdapter

    private lateinit var binding: FragmentRecordBinding
    private var allFiles = ArrayList<File>()
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

        voiceRecordAdapter.setOnVoiceItemClickListener { file, position ->

            //play

        }

    }


    private fun setData() {

        val filepath = activity?.getExternalFilesDir("/")?.absolutePath
        val file = File(filepath)
        if (file.listFiles() != null && file.listFiles().isNotEmpty())
            allFiles.addAll(file.listFiles()!!)


        voiceRecordAdapter.submitList(allFiles)

        binding.recordList.apply {
            adapter = voiceRecordAdapter
            setHasFixedSize(true)
        }


    }


}