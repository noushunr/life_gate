package com.lifegate.app.ui.activity

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.navArgs
import com.lifegate.app.R
import com.lifegate.app.data.model.SlideModel
import com.lifegate.app.databinding.ActivitySliderBinding
import com.lifegate.app.databinding.ActivityVideoBinding
import com.lifegate.app.utils.ImageSliderAdapter
import com.lifegate.app.utils.SliderAdapter
import com.lifegate.app.utils.fromJson
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView

class SliderActivity : AppCompatActivity(),ImageSliderAdapter.SliderClickListener {

    lateinit var binding: ActivitySliderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySliderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val safeArgs: SliderActivityArgs by navArgs()
        val json = safeArgs.json

        var homeSlideList = mutableListOf<SlideModel>()
        try {
            homeSlideList = fromJson(json)
            val adapter = ImageSliderAdapter(homeSlideList, this,this)
            binding.imageSlider.also {
                it.stopAutoCycle()
                it.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
                it.setIndicatorAnimation(IndicatorAnimationType.WORM)
                it.indicatorSelectedColor = Color.WHITE
                it.indicatorUnselectedColor = Color.GRAY
                it.setSliderAdapter(adapter)
                it.isAutoCycle = false
                it.stopAutoCycle()
            }
        } catch (e: Exception) {

        }
    }

    override fun onSliderClick(item:SlideModel) {

         if (item.imageUrl!=null){
            if (item?.imageUrl?.contains(".mp4")!!){
                var intent = Intent(this,VideoActivity::class.java)
                var args = Bundle()
                args.putString("video_url",item?.imageUrl!!)
                intent.putExtras(args)
                startActivity(intent)
            }
        }


    }
}