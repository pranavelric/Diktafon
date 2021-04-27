package com.voice.voicerecorder.utils

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MySharedPrefrenceHelper @Inject constructor(@ApplicationContext context: Context) {

    private val sp: SharedPreferences by lazy {
        context.getSharedPreferences(Constants.SHARED_PREFRENCES, 0)
    }

    private val editor = sp.edit()

    fun clearSession() {
        editor.clear()
        editor.commit()
    }

}