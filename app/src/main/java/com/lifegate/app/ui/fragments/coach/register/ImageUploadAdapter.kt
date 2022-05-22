package com.lifegate.app.ui.fragments.coach.register;

import android.content.Context;
import android.net.Uri
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.lifegate.app.R
import java.util.List;

/**
 * Created by Noushad N on 23-03-2022.
 */
class ImageUploadAdapter(
    private var mContext: Context,
    private var items: MutableList<Uri>?
) :
    RecyclerView.Adapter<ImageUploadAdapter.ImageUUploadViewHollder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageUUploadViewHollder {
        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false)
        return ImageUUploadViewHollder(
            view
        )
    }

    override fun onBindViewHolder(holder: ImageUUploadViewHollder, position: Int) {

        holder?.ivUploads?.setImageURI(items?.get(position))
    }

    override fun getItemCount(): Int {
        return items?.size!!
    }

    class ImageUUploadViewHollder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var ivUploads = itemView?.findViewById<ImageView>(R.id.iv_uploads)

    }

}
