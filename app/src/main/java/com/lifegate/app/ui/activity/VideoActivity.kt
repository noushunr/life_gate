package com.lifegate.app.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.navArgs
import com.lifegate.app.databinding.ActivityVideoBinding
import java.lang.Exception

class VideoActivity : AppCompatActivity() {

    lateinit var binding: ActivityVideoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            val safeArgs: VideoActivityArgs by navArgs()
            var url = safeArgs.url
            if (url?.isEmpty()){
                if (intent?.extras!=null){
                    url = intent?.extras?.getString("video_url")!!
                }
            }

            binding.andExoPlayerView.setSource(url)
        }catch (e:Exception){

        }

    }
}