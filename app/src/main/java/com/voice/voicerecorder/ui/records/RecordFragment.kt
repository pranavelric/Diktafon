package com.voice.voicerecorder.ui.records

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
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
    private var mediaPlayer: MediaPlayer? = null
    private var handler: Handler? = null
    private var runnable: Runnable? = null


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

        binding.btmListLay.play.setOnClickListener {
            if (isPlaying) {
                pauseAudio()

            } else {

                if (fileToPlay != null) {
                    resumeAudio()
                }

            }
        }


        binding.btmListLay.seekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                if (mediaPlayer != null) {
                    pauseAudio()
                }
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (mediaPlayer != null)
                    if (fileToPlay != null) {
                        val progress = seekBar?.progress
                        progress?.let { mediaPlayer!!.seekTo(it) }
                        resumeAudio()

                    }
            }

        })


    }

    private fun playRecording(file: File) {
        fileToPlay = file
        if (isPlaying) {
            stopPlayingVoiceItem()
            playVoiceItem(fileToPlay)
        } else {

            playVoiceItem(fileToPlay)
        }

    }


    fun stopPlayingVoiceItem() {
        isPlaying = false
        mediaPlayer?.stop()


        binding.btmListLay.play.setImageDrawable(context?.let {
            ActivityCompat.getDrawable(
                it,
                R.drawable.ic_baseline_play_arrow_24
            )
        })

        binding.btmListLay.mediaplayerStatus.text = "Stopped"

        runnable?.let { handler?.removeCallbacks(it) }

    }

    private fun playVoiceItem(file: File?) {

        isPlaying = true
        mediaPlayer = MediaPlayer().apply {
            setDataSource(file?.absolutePath)
            prepare()
        }


        val behavior = BottomSheetBehavior.from(binding.btmListLay.root)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED

        mediaPlayer?.start()
        binding.btmListLay.filenameTv.text = file?.name
        binding.btmListLay.mediaplayerStatus.text = "Playing"
        binding.btmListLay.play.setImageDrawable(context?.let {
            ActivityCompat.getDrawable(
                it,
                R.drawable.ic_baseline_pause_24
            )
        })


        mediaPlayer?.setOnCompletionListener { mp ->

            stopPlayingVoiceItem()
            binding.btmListLay.mediaplayerStatus.text = "Finished"

            binding.btmListLay.play.setImageDrawable(context?.let {
                ActivityCompat.getDrawable(
                    it,
                    R.drawable.ic_baseline_play_arrow_24
                )
            })
        }

        binding.btmListLay.seekBar.max = mediaPlayer?.duration!!
        handler = Handler()

        updateRunnable()
        handler?.postDelayed(runnable!!, 0)


    }

    private fun updateRunnable() {
        runnable = object : Runnable {
            override fun run() {
                mediaPlayer?.currentPosition?.let { binding.btmListLay.seekBar.setProgress(it) }
                handler?.postDelayed(this, 500)
            }

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

    private fun pauseAudio() {
        mediaPlayer?.pause()
        isPlaying = false


        binding.btmListLay.play.setImageDrawable(context?.let {
            ActivityCompat.getDrawable(
                it,
                R.drawable.ic_baseline_play_arrow_24
            )
        })

        runnable?.let { handler?.removeCallbacks(it) }

    }

    private fun resumeAudio() {



        if (mediaPlayer?.currentPosition!! < mediaPlayer?.duration!!) {
            mediaPlayer?.start()
            isPlaying = true

            binding.btmListLay.play.setImageDrawable(context?.let {
                ActivityCompat.getDrawable(
                    it,
                    R.drawable.ic_baseline_pause_24
                )
            })

            updateRunnable()
            runnable?.let { handler?.postDelayed(it, 0) }
        } else {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            binding.btmListLay.seekBar.progress = 0
            if (fileToPlay != null)
                playRecording(fileToPlay!!)
        }
    }


    override fun onResume() {
        super.onResume()

        if(fileToPlay!=null){
            playVoiceItem(fileToPlay)
           pauseAudio()
        }





    }


    override fun onStop() {
        super.onStop()

        stopPlayingVoiceItem()
        if(mediaPlayer!=null){
            mediaPlayer?.release()
        }
    }

}