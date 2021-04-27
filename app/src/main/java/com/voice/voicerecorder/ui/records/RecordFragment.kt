package com.voice.voicerecorder.ui.records

import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.voice.voicerecorder.R
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
    private var isPlaying: Boolean = false
    private var fileToPlay: File? = null
    private lateinit var mediaPlayer: MediaPlayer

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

            playRecording(file)
        }



    }

    private fun playRecording(file: File) {
        if (isPlaying) {
            stopPlayingVoiceItem()
            playVoiceItem(fileToPlay)
        } else {
            fileToPlay = file
            playVoiceItem(fileToPlay)
        }

    }


    fun stopPlayingVoiceItem() {
        isPlaying = false
        mediaPlayer.stop()
        mediaPlayer.release()

        binding.btmListLay.play.setImageDrawable(context?.let {
            ActivityCompat.getDrawable(
                it,
                R.drawable.ic_baseline_play_arrow_24
            )
        })

        binding.btmListLay.mediaplayerStatus.text = "Stopped"


    }

    private fun playVoiceItem(file: File?) {

        isPlaying = true
        mediaPlayer = MediaPlayer().apply {
            setDataSource(file?.absolutePath)
            prepare()
        }


        val behavior = BottomSheetBehavior.from(binding.btmListLay.root)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED

        mediaPlayer.start()
        binding.btmListLay.filenameTv.text = file?.name
        binding.btmListLay.mediaplayerStatus.text = "Playing"
        binding.btmListLay.play.setImageDrawable(context?.let {
            ActivityCompat.getDrawable(
                it,
                R.drawable.ic_baseline_pause_24
            )
        })


        mediaPlayer.setOnCompletionListener { mp ->

            stopPlayingVoiceItem()
            binding.btmListLay.mediaplayerStatus.text = "Finished"

            binding.btmListLay.play.setImageDrawable(context?.let {
                ActivityCompat.getDrawable(
                    it,
                    R.drawable.ic_baseline_play_arrow_24
                )
            })

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