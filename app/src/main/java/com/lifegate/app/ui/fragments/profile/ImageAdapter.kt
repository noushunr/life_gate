package com.lifegate.app.ui.fragments.profile;

import android.content.Context;
import android.net.Uri
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.lifegate.app.R
import com.lifegate.app.data.network.responses.CoachesListApi
import com.lifegate.app.utils.PRO_IMG_BASE_URL
import com.lifegate.app.utils.loadImage
import java.util.List;

/**
 * Created by Noushad N on 29-03-2022.
 */
class ImageAdapter(
    private var items: MutableList<String>?,
    val clickListener: (String) -> Unit
) :
    RecyclerView.Adapter<ImageAdapter.ImageUUploadViewHollder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageUUploadViewHollder {
        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false)
        return ImageUUploadViewHollder(
            view
        )
    }

    override fun onBindViewHolder(holder: ImageUUploadViewHollder, position: Int) {
        loadImage(holder?.ivUploads, PRO_IMG_BASE_URL + items?.get(position))
        holder.ivUploads.setOnClickListener {
            clickListener(items?.get(position)!!)
        }
    }

    override fun getItemCount(): Int {
        return items?.size!!
    }

    class ImageUUploadViewHollder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var ivUploads = itemView?.findViewById<AppCompatImageView>(R.id.iv_uploads)

    }

}
