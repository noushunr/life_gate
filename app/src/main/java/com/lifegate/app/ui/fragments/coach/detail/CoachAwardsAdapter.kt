package com.lifegate.app.ui.fragments.coach.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifegate.app.data.network.responses.CoachDetailsApi.*
import com.lifegate.app.databinding.ListCoachAwardsBinding
import com.lifegate.app.utils.loadImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Adithya T Raj.
 **/

class CoachAwardsAdapter(
    val clickListener: (CoachAward) -> Unit
): RecyclerView.Adapter<CoachAwardsAdapter.ViewHolder>() {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    private val diffCallBack = object : DiffUtil.ItemCallback<CoachAward>() {
        override fun areItemsTheSame(
            oldItem: CoachAward,
            newItem: CoachAward
        ): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(
            oldItem: CoachAward,
            newItem: CoachAward
        ): Boolean {
            return oldItem.award == newItem.award
        }

    }

    private val differ = AsyncListDiffer(this, diffCallBack)

    fun submitList(list: List<CoachAward>) {
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
        loadImage(holder.binding.listCoachAwardsImg, item.award)
    }

    override fun getItemCount() = differ.currentList.size

    class ViewHolder(
        val binding: ListCoachAwardsBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListCoachAwardsBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

    }

}