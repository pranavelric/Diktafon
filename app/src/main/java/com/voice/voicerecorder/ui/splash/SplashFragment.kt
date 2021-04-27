package com.voice.voicerecorder.ui.splash

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.voice.voicerecorder.MainActivity
import com.voice.voicerecorder.R
import com.voice.voicerecorder.databinding.FragmentSplashBinding
import com.voice.voicerecorder.utils.CoroutinesHelper
import com.voice.voicerecorder.utils.hideSystemUI


class SplashFragment : Fragment() {

    private lateinit var binding: FragmentSplashBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashBinding.inflate(inflater, container, false)


        goToMain()

        return binding.root
    }

    private fun goToMain() {
        CoroutinesHelper.delayWithMain(2000L) {
            findNavController().navigate(R.id.action_splashFragment_to_mainFragment)
        }
    }

    override fun onStart() {
        super.onStart()
        (activity as MainActivity).hideSystemUI()


    }


}