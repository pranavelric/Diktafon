package com.voice.voicerecorder.ui.records

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.voice.voicerecorder.R
import com.voice.voicerecorder.adapters.VoiceRecordAdapter

import com.voice.voicerecorder.databinding.FragmentRecordBinding
import com.voice.voicerecorder.utils.RecordState
import com.voice.voicerecorder.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class RecordFragment : Fragment() {


    @Inject
    lateinit var voiceRecordAdapter: VoiceRecordAdapter

    private val recordListViewModel: RecordsListViewModel by lazy {
        ViewModelProvider(this).get(RecordsListViewModel::class.java)
    }

    private lateinit var binding: FragmentRecordBinding

    private var isPlaying: Boolean = false
    private var fileToPlay: File? = null

    private var mainHandler: Handler? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecordBinding.inflate(inflater, container, false)

        initializeData()
        subscribe()
        setData()
        setClickListeners()
        return binding.root
    }

    private fun initializeData() {
        mainHandler = Handler(Looper.getMainLooper())
        recordListViewModel.initMediaPlayer()
        recordListViewModel.recordList.observe(viewLifecycleOwner, { list ->
            voiceRecordAdapter.submitList(list)
        })

    }

    private fun setData() {

        binding.btmListLay.seekBar.setOnTouchListener { v, event -> true }

        binding.recordList.apply {
            adapter = voiceRecordAdapter
            setHasFixedSize(true)
        }

        recordListViewModel.progress.observe(viewLifecycleOwner, { progress ->

            binding.btmListLay.seekBar.progress = progress

        })


    }


    private fun subscribe() {

        recordListViewModel.recordState.observe(viewLifecycleOwner, { state ->
            when (state) {
                is RecordState.Playing -> {
                    isPlaying = true
                    changeButtonIcon(isPlaying)
                    setRecordTitle()
                    setPlayerStatus("Playing")


                    //pause player
                    binding.btmListLay.play.setOnClickListener {
                        recordListViewModel.pauseRecord()
                        enableSeekBar(false)
                    }
                    // previous song
                    binding.btmListLay.playPrev.setOnClickListener {
                        recordListViewModel.playAgain()
                        enableSeekBar(true)
                    }

                }
                is RecordState.Pause -> {

                    isPlaying = false
                    setPlayerStatus("Paused")
                    changeButtonIcon(false)
                    binding.btmListLay.play.setOnClickListener {
                        recordListViewModel.resumePlayingRecord()
                        enableSeekBar(true)
                    }

                }
                is RecordState.End -> {
                    isPlaying = false
                    enableSeekBar(false)
                    changeButtonIcon(isPlaying)
                    binding.btmListLay.seekBar.progress = 0

                    binding.btmListLay.play.setOnClickListener {
                        playNext()
                    }
                    binding.btmListLay.playNext.setOnClickListener {
                        playNext()
                    }
                    binding.btmListLay.playPrev.setOnClickListener {
                        playPrevious()
                    }
                    setPlayerStatus("Stopped")


                }
                is RecordState.Error -> {
                    state.exception.message?.let { context?.toast(it) }
                }


            }
        })


        binding.btmListLay.playNext.setOnClickListener {
            playNext()
        }
        binding.btmListLay.playPrev.setOnClickListener {
            playPrevious()
        }

    }

    private fun setPlayerStatus(s: String) {

        binding.btmListLay.mediaplayerStatus.text = s

    }


    private fun playNext() {

        recordListViewModel.playNext()
        binding.btmListLay.seekBar.max = recordListViewModel.recordDuration
        enableSeekBar(true)
        isPlaying = true

    }

    private fun playPrevious() {
        recordListViewModel.playPrevious()
        binding.btmListLay.seekBar.max = recordListViewModel.recordDuration
        enableSeekBar(true)
        isPlaying = true

    }

    private fun enableSeekBar(b: Boolean) {
        val run = object : Runnable {
            override fun run() {
                recordListViewModel.getLiveProgress()
                mainHandler?.postDelayed(this, 1000)
            }
        }
        if (b) {
            mainHandler?.post(run)
        } else {
            mainHandler?.removeCallbacksAndMessages(null)
        }
    }

    private fun setRecordTitle() {


        binding.btmListLay.filenameTv.text = recordListViewModel.getTitle()
    }

    private fun changeButtonIcon(playing: Boolean) {
        if (playing) {
            binding.btmListLay.play.setImageDrawable(context?.let {
                ActivityCompat.getDrawable(it, R.drawable.ic_baseline_pause_24)
            })
        } else {
            binding.btmListLay.play.setImageDrawable(context?.let {
                ActivityCompat.getDrawable(it, R.drawable.ic_baseline_play_arrow_24)
            })
        }
    }

    private fun setClickListeners() {

        voiceRecordAdapter.setOnVoiceItemClickListener { record, position ->


            lifecycleScope.launch {
                playWithFilepath(record.filePath, position)
            }

        }


    }

    private fun playWithFilepath(filePath: String, position: Int) {

        recordListViewModel.playRecord(filePath, position)
        binding.btmListLay.seekBar.max = recordListViewModel.recordDuration
        enableSeekBar(true)
        val behavior = BottomSheetBehavior.from(binding.btmListLay.root)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED


    }


}