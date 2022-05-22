package com.lifegate.app.ui.fragments.coach.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifegate.app.data.network.responses.CoachDetailsApi.*
import com.lifegate.app.databinding.ListCoachServicesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Adithya T Raj.
 **/

class CoachServiceAdapter(
    val clickListener: (CoachServices) -> Unit
): RecyclerView.Adapter<CoachServiceAdapter.ViewHolder>() {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    private val diffCallBack = object : DiffUtil.ItemCallback<CoachServices>() {
        override fun areItemsTheSame(
            oldItem: CoachServices,
            newItem: CoachServices
        ): Boolean {
            return oldItem.service_id == newItem.service_id
        }

        override fun areContentsTheSame(
            oldItem: CoachServices,
            newItem: CoachServices
        ): Boolean {
            return oldItem.service == newItem.service
        }

    }

    private val differ = AsyncListDiffer(this, diffCallBack)

    fun submitList(list: List<CoachServices>) {
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
        holder.binding.listCoachServiceTxt.text = item.service
    }

    override fun getItemCount() = differ.currentList.size

    class ViewHolder(
        val binding: ListCoachServicesBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListCoachServicesBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

    }

}