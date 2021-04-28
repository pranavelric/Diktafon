package com.voice.voicerecorder.ui.records

import android.media.MediaPlayer
import android.os.Handler
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.voice.voicerecorder.data.Record
import com.voice.voicerecorder.data.RecordRepository
import com.voice.voicerecorder.utils.RecordState
import kotlinx.coroutines.launch
import java.io.File

class RecordsListViewModel @ViewModelInject constructor(val recordRepository: RecordRepository) :
    ViewModel() {


    private var mediaPlayer: MediaPlayer? = null


    private val _progress: MutableLiveData<Int> = MutableLiveData()
    val progress: LiveData<Int> get() = _progress

    private var currentPosition: Int = -1
    var recordDuration: Int = 0

    val recordList: LiveData<List<Record>> = recordRepository.getAllRecords().asLiveData()

    private val _recordState: MutableLiveData<RecordState<Record>> = MutableLiveData()
    val recordState: LiveData<RecordState<Record>> get() = _recordState


    fun initMediaPlayer() {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setOnCompletionListener {
            _recordState.value = RecordState.End
        }
    }

    fun playRecord(filePath: String, position: Int) {
        currentPosition = position

        if (mediaPlayer?.isPlaying == true) {
            stopMediaPlayer()
            _recordState.value = RecordState.End
        }
        try {
            if (mediaPlayer == null)
                initMediaPlayer()

            Log.d("RRR", "playRecord: ${filePath} ${mediaPlayer == null}")

            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(filePath)
            mediaPlayer?.prepare()
            mediaPlayer?.start()
            recordDuration = mediaPlayer?.duration ?: 0

            _recordState.value = RecordState.Playing

        } catch (e: Exception) {

            _recordState.value = RecordState.Error(e)

        }


    }

    fun getLiveProgress() {
        _progress.value = mediaPlayer?.currentPosition
    }

    fun resumePlayingRecord() {

        progress.value?.let { mediaPlayer?.seekTo(it) }
        mediaPlayer?.start()
        if (mediaPlayer?.isPlaying == true) {
            _recordState.value = RecordState.Playing
        }

    }

    fun pauseRecord() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
        _recordState.value = RecordState.Pause
    }

    fun playNext() {
        recordList.value?.let { list ->
            if (!list.isNullOrEmpty()) {
                when (currentPosition) {
                    list.size - 1 -> {
                        playRecord(list[0].filePath, 0)
                    }
                    -1 -> {
                        playRecord(list[0].filePath, 0)
                    }
                    else -> {
                        val next = currentPosition + 1
                        playRecord(list[next].filePath, next)

                    }
                }
            }

        }
    }

    fun playPrevious() {
        recordList.value?.let { list ->
            if (!list.isNullOrEmpty()) {
                when (currentPosition) {
                    0 -> {
                        playRecord(list[list.size - 1].filePath, list.size - 1)
                    }
                    -1 -> {
                        playRecord(list[list.size - 1].filePath, list.size - 1)
                    }
                    else -> {
                        val next = currentPosition - 1
                        playRecord(list[next].filePath, next)

                    }
                }
            }

        }
    }


    fun playAgain() {

        recordList.value?.let { list ->
            if (!list.isNullOrEmpty()) {
                if (currentPosition != -1) {
                    playRecord(list[currentPosition].filePath, currentPosition)
                } else {
                    playNext()
                }
            }
        }
    }

    fun getTitle() = recordList.value?.get(currentPosition)?.title

    fun stopMediaPlayer() {
        mediaPlayer?.stop()
//        mediaPlayer?.release()
//        mediaPlayer = null
    }


    fun deleteRecord(filePath: String, id: Int) = viewModelScope.launch {
        recordRepository.delete(id)
        val fdelete: File = File(filePath)
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                _recordState.value = RecordState.Message("File deleted successfully")
            } else {
                _recordState.value = RecordState.Message("File not deleted")
            }
        }


    }


}