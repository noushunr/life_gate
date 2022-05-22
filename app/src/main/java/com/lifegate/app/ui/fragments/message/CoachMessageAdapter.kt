package com.lifegate.app.ui.fragments.message

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifegate.app.data.network.responses.CoachMessageApi.*
import com.lifegate.app.databinding.ListCoachAllMessageBinding
import com.lifegate.app.utils.clearHtmlTag
import com.lifegate.app.utils.setDayMonthDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Adithya T Raj.
 **/

class CoachMessageAdapter(
    val clickListener: (CoachMessageData) -> Unit
): RecyclerView.Adapter<CoachMessageAdapter.ViewHolder>() {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    private val diffCallBack = object : DiffUtil.ItemCallback<CoachMessageData>() {
        override fun areItemsTheSame(
            oldItem: CoachMessageData,
            newItem: CoachMessageData
        ): Boolean {
            return oldItem.message_id == newItem.message_id
        }

        override fun areContentsTheSame(
            oldItem: CoachMessageData,
            newItem: CoachMessageData
        ): Boolean {
            return oldItem.message == newItem.message
        }

    }

    private val differ = AsyncListDiffer(this, diffCallBack)

    fun submitList(list: List<CoachMessageData>) {
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
        holder.binding.listTitleTxt.text = "Coach : ${item.coach_fname} ${item.coach_lname}"
        holder.binding.listContentTxt.text = item.message?.clearHtmlTag()
        holder.binding.listDateTxt.text = setDayMonthDate(item.message_date.toString())
    }

    override fun getItemCount() = differ.currentList.size

    class ViewHolder(
        val binding: ListCoachAllMessageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListCoachAllMessageBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

    }

}