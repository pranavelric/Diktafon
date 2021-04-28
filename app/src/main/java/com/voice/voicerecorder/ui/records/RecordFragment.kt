package com.voice.voicerecorder.ui.records

import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.voice.voicerecorder.R
import com.voice.voicerecorder.adapters.VoiceRecordAdapter

import com.voice.voicerecorder.databinding.FragmentRecordBinding
import com.voice.voicerecorder.utils.RecordState
import com.voice.voicerecorder.utils.gone
import com.voice.voicerecorder.utils.toast
import com.voice.voicerecorder.utils.visible
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
            if(list.isEmpty()){
                binding.emptyLay.visible()
            }
            else{
                binding.emptyLay.gone()
            }
            voiceRecordAdapter.submitList(list)
        })
        setSwipeToDelete()

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
                        recordListViewModel.playPrevious()
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
                is RecordState.Message -> {
                    state.message.let {
                        context?.toast(it)
                    }
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


    fun setSwipeToDelete() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val record = voiceRecordAdapter.currentList[position]



                recordListViewModel.deleteRecord(record.filePath, record.id)

            }

            private val deleteIcon =
                ContextCompat.getDrawable(activity!!, R.drawable.ic_baseline_delete_24)
            private val intrinsicWidth = deleteIcon?.intrinsicWidth
            private val intrinsicHeight = deleteIcon?.intrinsicHeight
            private val backGround = ColorDrawable()
            private val backgroundColor = Color.parseColor("#f44336")
            private val clearPaint =
                Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val itemHeight = itemView.bottom - itemView.top
                val isCanceled = dX == 0f && !isCurrentlyActive

                if (isCanceled) {
                    clearCanvas(
                        c,
                        itemView.right + dX,
                        itemView.top.toFloat(),
                        itemView.right.toFloat(),
                        itemView.bottom.toFloat()
                    )

                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                    return

                }
                // Draw red background
                backGround.color = backgroundColor
                backGround.setBounds(
                    itemView.right + dX.toInt(),
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
                backGround.draw(c)
                // Calculate position of delete icon
                val deleteIconTop = itemView.top + (itemHeight - intrinsicHeight!!) / 2
                val deleteIconMargin = (itemHeight - intrinsicHeight) / 2
                val deleteIconLeft = itemView.right - deleteIconMargin - intrinsicWidth!!
                val deleteIconRight = itemView.right - deleteIconMargin
                val deleteIconBottom = deleteIconTop + intrinsicHeight
                // draw delete icon
                deleteIcon?.setBounds(
                    deleteIconLeft,
                    deleteIconTop,
                    deleteIconRight,
                    deleteIconBottom
                )
                deleteIcon?.draw(c)
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )


            }

            // private fun clearCanvas
            private fun clearCanvas(
                c: Canvas?,
                left: Float,
                top: Float,
                right: Float,
                bottom: Float
            ) {
                c?.drawRect(left, top, right, bottom, clearPaint)
            }


        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.recordList)
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