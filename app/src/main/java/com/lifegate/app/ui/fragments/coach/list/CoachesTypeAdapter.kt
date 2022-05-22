package com.lifegate.app.ui.fragments.coach.list

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifegate.app.data.network.responses.CoachesTypeApi.*
import com.lifegate.app.databinding.ListCoachesTypeBinding
import com.lifegate.app.utils.loadImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Adithya T Raj.
 **/

class CoachesTypeAdapter(
    val clickListener: (CoachesType) -> Unit
): RecyclerView.Adapter<CoachesTypeAdapter.ViewHolder>() {

    private var viewPosition: Int? = null

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    private val diffCallBack = object : DiffUtil.ItemCallback<CoachesType>() {
        override fun areItemsTheSame(
            oldItem: CoachesType,
            newItem: CoachesType
        ): Boolean {
            return oldItem.coach_type_id == newItem.coach_type_id
        }

        override fun areContentsTheSame(
            oldItem: CoachesType,
            newItem: CoachesType
        ): Boolean {
            return oldItem.type == newItem.type
        }

    }

    private val differ = AsyncListDiffer(this, diffCallBack)

    fun submitList(list: List<CoachesType>) {
        adapterScope.launch {
            withContext(Dispatchers.Main) {
                differ.submitList(list.toList())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.binding.listCoachTypeCardView.setOnClickListener {
            clickListener(item)
            viewPosition = position
            notifyDataSetChanged()
        }

        when (viewPosition) {
            position -> holder.binding.listCoachTypeCardView.setCardBackgroundColor(
                Color.parseColor("#37B7FE")
            )
            else -> holder.binding.listCoachTypeCardView.setCardBackgroundColor(
                Color.parseColor("#FFFFFFFF")
            )
        }

        holder.binding.listCoachTypeNameTxt.text = item.type
        loadImage(holder.binding.listCoachTypeIconImg, item.type_icon)
    }

    override fun getItemCount() = differ.currentList.size

    class ViewHolder(
        val binding: ListCoachesTypeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListCoachesTypeBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

    }

}