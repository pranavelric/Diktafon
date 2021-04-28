package com.voice.voicerecorder.ui.main

import android.media.MediaRecorder
import android.os.Environment
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voice.voicerecorder.data.Record
import com.voice.voicerecorder.data.RecordRepository
import com.voice.voicerecorder.utils.RecordState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class RecordViewModel @ViewModelInject constructor(val repository: RecordRepository) :
    ViewModel() {

    private val _recordState: MutableLiveData<RecordState<Record>> = MutableLiveData()

    val recordState: LiveData<RecordState<Record>> get() = _recordState

    private var mediaRecorder: MediaRecorder? = null

    //    private var isRecording:Boolean = false
    private lateinit var filePath: String
    private var filename: String = ""

    fun startRecording(title: String?, filepath: String) {

        filePath = filepath

        viewModelScope.launch {
            record(title)
            _recordState.value = RecordState.Recording
        }

    }

    private fun record(title: String?) {


        filename = title!!


        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile("${filePath}/$filename")
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        }
        try {
            mediaRecorder?.prepare()
            mediaRecorder?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }


    fun stopRecording(title: String?) {

        viewModelScope.launch {
            val record = stopRecord(title)
            _recordState.value = RecordState.Done(record)
        }
    }

    private fun stopRecord(title: String?): Record {

        try {
            mediaRecorder?.stop()
            mediaRecorder?.release()
            mediaRecorder = null
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val f = title ?: filename


        val record = Record(title = filename, filePath = "$filePath/$f")

        viewModelScope.launch {
            repository.insertRecord(record)
        }
        return record
    }


}