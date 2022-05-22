package com.lifegate.app.ui.fragments.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifegate.app.data.network.responses.NotificationApi.NotificationData
import com.lifegate.app.databinding.ListNotificationBinding
import com.lifegate.app.utils.clearHtmlTag
import com.lifegate.app.utils.setDayMonthDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Adithya T Raj.
 **/

class NotificationAdapter(
    val clickListener: (NotificationData) -> Unit
): RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    private val diffCallBack = object : DiffUtil.ItemCallback<NotificationData>() {
        override fun areItemsTheSame(
            oldItem: NotificationData,
            newItem: NotificationData
        ): Boolean {
            return oldItem.notification_title == newItem.notification_title
        }

        override fun areContentsTheSame(
            oldItem: NotificationData,
            newItem: NotificationData
        ): Boolean {
            return oldItem.notification_content == newItem.notification_content
        }

    }

    private val differ = AsyncListDiffer(this, diffCallBack)

    fun submitList(list: List<NotificationData>) {
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
        holder.binding.listNotificationTitleTxt.text = item.notification_title
        holder.binding.listNotificationContentTxt.text = item.notification_content?.clearHtmlTag()
        holder.binding.listNotificationDateTxt.text = setDayMonthDate(item.notification_date.toString())
    }

    override fun getItemCount() = differ.currentList.size

    class ViewHolder(
        val binding: ListNotificationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListNotificationBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

    }

}