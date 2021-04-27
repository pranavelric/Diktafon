package com.voice.voicerecorder.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object CoroutinesHelper {

    fun delayWithMain(delayTime: Long, work: () -> Unit) {

        CoroutineScope(Dispatchers.Main).launch {
            delay(delayTime)
            work()
        }

    }

}