package com.lifegate.app.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import ca.allanwang.kau.utils.visible
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.lifegate.app.R
import com.lifegate.app.data.model.SlideModel
import com.smarteist.autoimageslider.SliderViewAdapter

/*
 *Created by Adithya T Raj on 30-06-2021
*/

class ImageSliderAdapter(
    val items: MutableList<SlideModel>, val context: Context, val listener: SliderClickListener? = null
    ) : SliderViewAdapter<ImageSliderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        return ViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.image_slider_layout_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val requestOptions = RequestOptions()
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)

        if (items[position].imagePath != 0 && items[position].imagePath != null) {
            holder?.llPreview?.visibility = View.GONE
            Glide.with(context)
                .load(items[position].imagePath)
                .apply(requestOptions)
                .into(holder.imageViewBackground)
        } else {

            if (items[position].imageUrl!=null && items[position].imageUrl?.contains(".mp4")!!){
                holder?.llPreview?.visibility = View.VISIBLE
                Glide.with(context)
                    .load(R.drawable.life_gate_logo)
                    .apply(requestOptions)
                    .into(holder.imageViewBackground)
            }else{
                holder?.llPreview?.visibility = View.GONE
                Glide.with(context)
                    .load(items[position].imageUrl)
                    .apply(requestOptions)
                    .into(holder.imageViewBackground)
            }

        }

        holder.textViewDescription.text = items[position].title

        holder.imageViewBackground.setOnClickListener {
            listener?.onSliderClick(items[position])
        }

    }

    override fun getCount(): Int {
        return items.size
    }

    class ViewHolder(itemView: View)
        : SliderViewAdapter.ViewHolder(itemView) {

        val imageViewBackground: AppCompatImageView = itemView.findViewById(R.id.iv_auto_image_slider)
        val llPreview: LinearLayout = itemView.findViewById(R.id.ll_preview)
        val imageGifContainer: ImageView = itemView.findViewById(R.id.iv_gif_container)
        val textViewDescription: TextView = itemView.findViewById(R.id.tv_auto_image_slider)

    }

    interface SliderClickListener {
        fun onSliderClick(item:SlideModel)
    }

}