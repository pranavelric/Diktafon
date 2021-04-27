package com.voice.voicerecorder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.voice.voicerecorder.databinding.ActivityMainBinding
import com.voice.voicerecorder.utils.hideSystemUI
import com.voice.voicerecorder.utils.setFullScreenForNotch
import com.voice.voicerecorder.utils.setFullScreenWithBtmNav

class MainActivity : AppCompatActivity() {


    val navController: NavController by lazy {
        findNavController(R.id.fragment)
    }

  lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)



    }

    override fun onStart() {
        super.onStart()

        setFullScreenForNotch()
    }



    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, null)

    }
}